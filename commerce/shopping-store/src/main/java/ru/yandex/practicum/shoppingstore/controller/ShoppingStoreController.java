package ru.yandex.practicum.shoppingstore.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.interactionapi.dto.PageableDto;
import ru.yandex.practicum.interactionapi.request.SetProductQuantityStateRequest;
import ru.yandex.practicum.interactionapi.dto.ProductDto;
import ru.yandex.practicum.interactionapi.enums.ProductCategory;
import ru.yandex.practicum.shoppingstore.service.ShoppingStoreService;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/shopping-store")
@RequiredArgsConstructor
public class ShoppingStoreController {

    private final ShoppingStoreService shoppingStoreService;

    @GetMapping
    public List<ProductDto> getProducts(@RequestParam ProductCategory productCategory,
                                        @Valid PageableDto pageableDto) {
        log.info("Get Products with pagination.");
        return shoppingStoreService.getProducts(productCategory, pageableDto);
    }

    @PutMapping
    public ProductDto createNewProduct(@RequestBody @Valid ProductDto productDto) {
        log.info("Create new Product. {}", productDto);
        return shoppingStoreService.createNewProduct(productDto);
    }

    @PostMapping
    public ProductDto updateProduct(@RequestBody @Valid ProductDto productDto) {
        log.info("Update the Product. {}", productDto);
        return shoppingStoreService.updateProduct(productDto);
    }

    @PostMapping("/removeProductFromStore")
    public Boolean removeProductFromStore(@RequestBody @NotNull UUID productId) {
        log.info("Remove Product from the Store. {}", productId);
        return shoppingStoreService.removeProductFromStore(productId);
    }

    @PostMapping("/quantityState")
    public Boolean setProductQuantityState(@RequestBody @Valid SetProductQuantityStateRequest setProductQuantityStateRequest) {
        log.info("Set Product quantity state. {}", setProductQuantityStateRequest);
        return shoppingStoreService.setProductQuantityState(setProductQuantityStateRequest);
    }

    @GetMapping("/{productId}")
    public ProductDto getProduct(@PathVariable @NotNull UUID productId) {
        log.info("Get Product by Id: {}", productId);
        return shoppingStoreService.getProduct(productId);
    }
}