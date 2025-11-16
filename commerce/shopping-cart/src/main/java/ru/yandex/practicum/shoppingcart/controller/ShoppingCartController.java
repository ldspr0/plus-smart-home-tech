package ru.yandex.practicum.shoppingcart.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.interactionapi.dto.BookedProductsDto;
import ru.yandex.practicum.interactionapi.interfaces.ShoppingCartOperations;
import ru.yandex.practicum.interactionapi.request.ChangeProductQuantityRequest;
import ru.yandex.practicum.interactionapi.dto.ShoppingCartDto;
import ru.yandex.practicum.shoppingcart.service.ShoppingCartService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/shopping-cart")
@RequiredArgsConstructor
public class ShoppingCartController implements ShoppingCartOperations {
    private final ShoppingCartService shoppingCartService;

    @Override
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ShoppingCartDto getShoppingCart(@RequestParam String username) {
        log.info("Getting shopping cart info for user. {}", username);
        return shoppingCartService.getShoppingCart(username);
    }

    @Override
    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public ShoppingCartDto addProductToShoppingCart(@RequestParam String username,
                                                    @RequestBody Map<UUID, Long> request) {
        log.info("Add product to a cart for user. {}", username);
        return shoppingCartService.addProductToShoppingCart(username, request);
    }

    @Override
    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    public void deactivateCurrentShoppingCart(@RequestParam String username) {
        log.info("Shopping cart deactivation for a user. {}", username);
        shoppingCartService.deactivateCurrentShoppingCart(username);
    }

    @Override
    @PostMapping("/remove")
    @ResponseStatus(HttpStatus.OK)
    public ShoppingCartDto removeFromShoppingCart(@RequestParam String username,
                                                  @RequestBody List<UUID> request) {
        log.info("Remove items from a cart. {}", username);
        return shoppingCartService.removeFromShoppingCart(username, request);
    }

    @Override
    @PostMapping("/change-quantity")
    @ResponseStatus(HttpStatus.OK)
    public ShoppingCartDto changeProductQuantity(@RequestParam String username,
                                                 @RequestBody @Valid ChangeProductQuantityRequest requestDto) {
        log.info("Change quantity of items in a cart. {}", username);
        return shoppingCartService.changeProductQuantity(username, requestDto);
    }

    @Override
    @PostMapping("/booking")
    @ResponseStatus(HttpStatus.CREATED)
    public BookedProductsDto bookingProducts(@RequestParam String username) {
        log.info("Booking products for a user. {}", username);
        return shoppingCartService.bookingProducts(username);
    }
}