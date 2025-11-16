package ru.yandex.practicum.interactionapi.interfaces;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import ru.yandex.practicum.interactionapi.dto.PageableDto;
import ru.yandex.practicum.interactionapi.dto.ProductDto;
import ru.yandex.practicum.interactionapi.enums.ProductCategory;
import ru.yandex.practicum.interactionapi.enums.QuantityState;
import ru.yandex.practicum.interactionapi.request.SetProductQuantityStateRequest;

import java.util.UUID;

public interface ShoppingStoreOperations {

    @GetMapping
    Page<ProductDto> getProducts(@RequestParam(name = "category") ProductCategory productCategory,
                                 PageableDto pageableDto);

    @PutMapping
    ProductDto createNewProduct(@RequestBody @Valid ProductDto productDto);

    @PostMapping
    ProductDto updateProduct(@RequestBody @Valid ProductDto productDto);

    @PostMapping("/removeProductFromStore")
    Boolean removeProductFromStore(@RequestBody @NotNull UUID productId);

    @PostMapping("/quantityState")
    Boolean setProductQuantityState(@RequestBody(required = false) SetProductQuantityStateRequest setProductQuantityStateRequest,
                                    @RequestParam(required = false) UUID productId,
                                    @RequestParam(required = false) QuantityState quantityState);

    @GetMapping("/{productId}")
    ProductDto getProduct(@PathVariable @NotNull UUID productId);
}
