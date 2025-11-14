package ru.yandex.practicum.shoppingstore.service;

import lombok.RequiredArgsConstructor;
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

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ShoppingStoreServiceImpl implements ShoppingStoreService {
    private final ShoppingStoreRepository shoppingStoreRepository;
    private final ProductMapper productMapper;

    @Transactional(readOnly = true)
    public Page<ProductDto> getProducts(ProductCategory productCategory, PageableDto pageableDto) {

        Sort sort = Sort.by(Sort.Direction.DESC, "productName");
        Pageable pageRequest = PageRequest.of(pageableDto.getPage(), pageableDto.getSize(), sort);

        Page<Product> productPage = shoppingStoreRepository.findAllByProductCategory(productCategory, pageRequest);

        return productPage.map(productMapper::productToProductDto);
    }

    @Override
    @Transactional
    public ProductDto createNewProduct(ProductDto productDto) {
        Product newProduct = productMapper.productDtoToProduct(productDto);
        return productMapper.productToProductDto(shoppingStoreRepository.save(newProduct));
    }

    @Override
    @Transactional
    public ProductDto updateProduct(ProductDto productDto) {
        Product oldProduct = checkProductById(productDto.getProductId());
        Product newProduct = productMapper.productDtoToProduct(productDto);
        newProduct.setProductId(oldProduct.getProductId());
        return productMapper.productToProductDto(shoppingStoreRepository.save(newProduct));
    }

    @Override
    @Transactional
    public boolean removeProductFromStore(UUID productId) {
        Product product = checkProductById(productId);
        product.setProductState(ProductState.DEACTIVATE);
        return true;
    }

    @Override
    @Transactional
    public boolean setProductQuantityState(SetProductQuantityStateRequest setProductQuantityStateRequest) {
        Product product = shoppingStoreRepository.findByProductId(setProductQuantityStateRequest.getProductId())
                .orElseThrow(
                        () -> new ProductNotFoundException(String.format("Error, Product with id %s is not found", setProductQuantityStateRequest.getProductId()))
                );
        product.setQuantityState(setProductQuantityStateRequest.getQuantityState());
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDto getProduct(UUID productId) {
        Product product = checkProductById(productId);
        return productMapper.productToProductDto(product);
    }

    private Product checkProductById(UUID productId) {
        return shoppingStoreRepository.findByProductId(productId).orElseThrow(
                () -> new ProductNotFoundException(String.format("Error, Product with id %s is not found", productId))
        );
    }
}