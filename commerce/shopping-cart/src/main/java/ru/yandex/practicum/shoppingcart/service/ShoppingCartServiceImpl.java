package ru.yandex.practicum.shoppingcart.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.interactionapi.dto.BookedProductsDto;
import ru.yandex.practicum.interactionapi.feign.WarehouseClient;
import ru.yandex.practicum.interactionapi.request.ChangeProductQuantityRequest;
import ru.yandex.practicum.interactionapi.dto.ShoppingCartDto;
import ru.yandex.practicum.shoppingcart.exception.NoProductsInShoppingCartException;
import ru.yandex.practicum.shoppingcart.exception.NotAuthorizedUserException;
import ru.yandex.practicum.shoppingcart.exception.ProductInShoppingCartIsNotInWarehouse;
import ru.yandex.practicum.shoppingcart.mapper.ShoppingCartMapper;
import ru.yandex.practicum.shoppingcart.model.ShoppingCart;
import ru.yandex.practicum.shoppingcart.repository.ShoppingCartRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final ShoppingCartMapper shoppingCartMapper;
    @Lazy
    private final WarehouseClient warehouseClient;

    @Override
    @Transactional(readOnly = true)
    public ShoppingCartDto getShoppingCart(String username) {
        checkUsername(username);
        ShoppingCart shoppingCart = shoppingCartRepository.findByUsername(username);
        return shoppingCartMapper.toShoppingCartDto(shoppingCart);
    }

    @Override
    public ShoppingCartDto addProductToShoppingCart(String username, Map<UUID, Long> request) {
        checkUsername(username);
        ShoppingCart shoppingCart = shoppingCartRepository.findByUsername(username);

        if (shoppingCart == null) {
            shoppingCart = ShoppingCart.builder()
                    .username(username)
                    .products(new HashMap<>())
                    .active(true)
                    .build();
        }

        for (Map.Entry<UUID, Long> entry : request.entrySet()) {
            shoppingCart.getProducts().put(entry.getKey(),
                    shoppingCart.getProducts().getOrDefault(entry.getKey(), 0L) + entry.getValue());
        }

        return shoppingCartMapper.toShoppingCartDto(shoppingCartRepository.save(shoppingCart));
    }

    @Override
    public void deactivateCurrentShoppingCart(String username) {
        checkUsername(username);
        ShoppingCart shoppingCart = shoppingCartRepository.findByUsername(username);
        if (shoppingCart == null) {
            throw new ProductInShoppingCartIsNotInWarehouse("Shopping cart not found for user: " + username);
        }
        shoppingCart.setActive(false);
        shoppingCartRepository.save(shoppingCart);
    }

    @Override
    public ShoppingCartDto removeFromShoppingCart(String username, List<UUID> productsToRemove) {
        checkUsername(username);
        ShoppingCart shoppingCart = shoppingCartRepository.findByUsername(username);
        if (shoppingCart == null || shoppingCart.getProducts().isEmpty()) {
            throw new NoProductsInShoppingCartException("User " + username + " doesn't have products in a cart.");
        }

        for (UUID productId : productsToRemove) {
            shoppingCart.getProducts().remove(productId);
        }

        shoppingCartRepository.save(shoppingCart);
        return shoppingCartMapper.toShoppingCartDto(shoppingCart);
    }

    @Override
    public ShoppingCartDto changeProductQuantity(String username, ChangeProductQuantityRequest requestDto) {
        checkUsername(username);
        ShoppingCart shoppingCart = shoppingCartRepository.findByUsername(username);
        if (shoppingCart == null || shoppingCart.getProducts().isEmpty()) {
            throw new NoProductsInShoppingCartException("User " + username + " doesn't have products in a cart.");
        }

        if (shoppingCart.getProducts().containsKey(requestDto.getProductId())) {
            shoppingCart.getProducts().put(requestDto.getProductId(), requestDto.getNewQuantity());
        } else {
            throw new NoProductsInShoppingCartException("Product not found in cart: " + requestDto.getProductId());
        }

        shoppingCartRepository.save(shoppingCart);
        return shoppingCartMapper.toShoppingCartDto(shoppingCart);
    }

    @Override
    public BookedProductsDto bookingProducts(String username) {
        checkUsername(username);
        ShoppingCart shoppingCart = shoppingCartRepository.findByUsername(username);
        return warehouseClient.bookingProducts(shoppingCartMapper.toShoppingCartDto(shoppingCart));
    }

    private void checkUsername(String username) {
        if (username == null || username.isEmpty()) {
            throw new NotAuthorizedUserException("Username shouldn't be empty.");
        }
    }
}