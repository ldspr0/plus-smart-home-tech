package ru.yandex.practicum.shoppingstore.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.interactionapi.dto.PageableDto;
import ru.yandex.practicum.interactionapi.enums.QuantityState;
import ru.yandex.practicum.interactionapi.request.SetProductQuantityStateRequest;
import ru.yandex.practicum.interactionapi.dto.ProductDto;
import ru.yandex.practicum.interactionapi.enums.ProductCategory;
import ru.yandex.practicum.shoppingstore.service.ShoppingStoreService;

import java.util.Objects;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/shopping-store")
@RequiredArgsConstructor
public class ShoppingStoreController {

    private final ShoppingStoreService shoppingStoreService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<ProductDto> getProducts(@RequestParam(name = "category") ProductCategory productCategory,
                                        PageableDto pageableDto) {
        log.info("Get Products with pagination.");
        return shoppingStoreService.getProducts(productCategory, pageableDto);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductDto createNewProduct(@RequestBody @Valid ProductDto productDto) {
        log.info("Create new Product. {}", productDto);
        return shoppingStoreService.createNewProduct(productDto);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public ProductDto updateProduct(@RequestBody @Valid ProductDto productDto) {
        log.info("Update the Product. {}", productDto);
        return shoppingStoreService.updateProduct(productDto);
    }

    @PostMapping("/removeProductFromStore")
    @ResponseStatus(HttpStatus.OK)
    public Boolean removeProductFromStore(@RequestBody @NotNull UUID productId) {
        log.info("Remove Product from the Store. {}", productId);
        return shoppingStoreService.removeProductFromStore(productId);
    }

    @PostMapping("/quantityState")
    @ResponseStatus(HttpStatus.OK)
    public Boolean setProductQuantityState(@RequestBody(required = false) SetProductQuantityStateRequest setProductQuantityStateRequest,
                                           @RequestParam(required = false) UUID productId,
                                           @RequestParam(required = false) QuantityState quantityState) {

        SetProductQuantityStateRequest request = Objects.requireNonNullElseGet(setProductQuantityStateRequest, () -> new SetProductQuantityStateRequest(productId, quantityState));
        log.info("Set Product quantity state. {}", request);
        return shoppingStoreService.setProductQuantityState(request);
    }

    @GetMapping("/{productId}")
    @ResponseStatus(HttpStatus.OK)
    public ProductDto getProduct(@PathVariable @NotNull UUID productId) {
        log.info("Get Product by Id: {}", productId);
        return shoppingStoreService.getProduct(productId);
    }
}