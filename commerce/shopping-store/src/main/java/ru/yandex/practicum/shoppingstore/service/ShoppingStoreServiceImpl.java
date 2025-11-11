package ru.yandex.practicum.shoppingstore.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.interactionapi.request.SetProductQuantityStateRequest;
import ru.yandex.practicum.interactionapi.dto.PageableDto;
import ru.yandex.practicum.interactionapi.dto.ProductDto;
import ru.yandex.practicum.interactionapi.enums.ProductCategory;
import ru.yandex.practicum.interactionapi.enums.ProductState;
import ru.yandex.practicum.shoppingstore.exception.ProductNotFoundException;
import ru.yandex.practicum.shoppingstore.mapper.ProductMapper;
import ru.yandex.practicum.shoppingstore.model.Product;
import ru.yandex.practicum.shoppingstore.repository.ShoppingStoreRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ShoppingStoreServiceImpl implements ShoppingStoreService {
    private final ShoppingStoreRepository shoppingStoreRepository;
    private final ProductMapper productMapper;

    @Transactional(readOnly = true)
    public Page<ProductDto> getProducts(ProductCategory productCategory, PageableDto pageableDto) {

        int page = (pageableDto != null && pageableDto.getPage() != null) ? pageableDto.getPage() : 0;
        int size = (pageableDto != null && pageableDto.getSize() != null) ? pageableDto.getSize() : 20;
        List<String> sortParams = (pageableDto != null) ? pageableDto.getSort() : Collections.emptyList();

        Sort sort = createSortFromRequest(sortParams);
        Pageable pageRequest = PageRequest.of(page, size, sort);

        Page<Product> productPage = shoppingStoreRepository.findAllByProductCategory(productCategory, pageRequest);

        return productPage.map(productMapper::productToProductDto);
    }

    @Override
    public ProductDto createNewProduct(ProductDto productDto) {
        Product newProduct = productMapper.productDtoToProduct(productDto);
        return productMapper.productToProductDto(shoppingStoreRepository.save(newProduct));
    }

    @Override
    public ProductDto updateProduct(ProductDto productDto) {
        Product oldProduct = shoppingStoreRepository.findByProductId(productDto.getProductId())
                .orElseThrow(() -> new ProductNotFoundException(
                        String.format("Error, Product with id %s is not found", productDto.getProductId()))
                );
        Product newProduct = productMapper.productDtoToProduct(productDto);
        newProduct.setProductId(oldProduct.getProductId());
        return productMapper.productToProductDto(shoppingStoreRepository.save(newProduct));
    }

    @Override
    public boolean removeProductFromStore(UUID productId) {
        Product product = shoppingStoreRepository.findByProductId(productId).orElseThrow(
                () -> new ProductNotFoundException(String.format("Error, Product with id %s is not found", productId))
        );
        product.setProductState(ProductState.DEACTIVATE);
        return true;
    }

    @Override
    public boolean setProductQuantityState(SetProductQuantityStateRequest setProductQuantityStateRequest) {
        Product product = shoppingStoreRepository.findByProductId(setProductQuantityStateRequest.getProductId())
                .orElseThrow(
                        () -> new ProductNotFoundException(String.format("Error, Product with id %s is not found", setProductQuantityStateRequest.getProductId()))
                );
        product.setQuantityState(setProductQuantityStateRequest.getQuantityState());
        return true;
    }

    @Override
    public ProductDto getProduct(UUID productId) {
        Product product = shoppingStoreRepository.findByProductId(productId).orElseThrow(
                () -> new ProductNotFoundException(String.format("Error, Product with id %s is not found", productId))
        );
        return productMapper.productToProductDto(product);
    }

    private Sort createSortFromRequest(List<String> sortParams) {
        if (sortParams == null || sortParams.isEmpty()) {
            return Sort.by(Sort.Direction.ASC, "productName"); // default
        }

        List<Sort.Order> orders = new ArrayList<>();
        for (String sortParam : sortParams) {
            if (sortParam.contains(",")) {
                String[] parts = sortParam.split(",");
                String field = parts[0].trim();
                Sort.Direction direction = parts.length > 1 ?
                        Sort.Direction.fromString(parts[1].trim()) : Sort.Direction.ASC;
                orders.add(new Sort.Order(direction, field));
            } else {
                orders.add(new Sort.Order(Sort.Direction.ASC, sortParam.trim()));
            }
        }
        return Sort.by(orders);
    }
}