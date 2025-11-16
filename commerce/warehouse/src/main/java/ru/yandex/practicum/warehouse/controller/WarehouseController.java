package ru.yandex.practicum.warehouse.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.interactionapi.dto.AddressDto;
import ru.yandex.practicum.interactionapi.dto.BookedProductsDto;
import ru.yandex.practicum.interactionapi.dto.ShoppingCartDto;
import ru.yandex.practicum.interactionapi.interfaces.WarehouseOperations;
import ru.yandex.practicum.interactionapi.request.AddProductToWarehouseRequest;
import ru.yandex.practicum.interactionapi.request.AssemblyProductsForOrderRequest;
import ru.yandex.practicum.interactionapi.request.NewProductInWarehouseRequest;
import ru.yandex.practicum.interactionapi.request.ShippedToDeliveryRequest;
import ru.yandex.practicum.warehouse.service.WarehouseService;

import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/warehouse")
@RequiredArgsConstructor
public class WarehouseController implements WarehouseOperations {
    private final WarehouseService warehouseService;

    @Override
    @PutMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void newProductInWarehouse(@RequestBody @Valid NewProductInWarehouseRequest requestDto) {
        log.info("Add new Product in Warehouse: {}", requestDto);
        warehouseService.newProductInWarehouse(requestDto);
    }

    @Override
    @PostMapping("/check")
    @ResponseStatus(HttpStatus.OK)
    public BookedProductsDto checkProductQuantityEnoughForShoppingCart(@RequestBody @Valid ShoppingCartDto shoppingCartDto) {
        log.info("Check quantity of Products for a cart: {}", shoppingCartDto);
        return warehouseService.checkProductQuantityEnoughForShoppingCart(shoppingCartDto);
    }

    @Override
    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    public void addProductToWarehouse(@RequestBody @Valid AddProductToWarehouseRequest requestDto) {
        log.info("Add Product to Warehouse: {}", requestDto);
        warehouseService.addProductToWarehouse(requestDto);
    }

    @Override
    @GetMapping("/address")
    @ResponseStatus(HttpStatus.OK)
    public AddressDto getWarehouseAddress() {
        log.info("Get address.");
        return warehouseService.getWarehouseAddress();
    }

    @Override
    @PostMapping("/shipped")
    public void shippedToDelivery(ShippedToDeliveryRequest deliveryRequest) {
        log.info("Shipped Delivery: {}", deliveryRequest);
        warehouseService.shippedToDelivery(deliveryRequest);
    }

    @Override
    @PostMapping("/return")
    public void acceptReturn(@RequestBody Map<UUID, Long> products) {
        log.info("Accept Return: {}", products);
        warehouseService.acceptReturn(products);
    }

    @Override
    @PostMapping("/assembly")
    public BookedProductsDto assemblyProductForOrder(@RequestBody @Valid AssemblyProductsForOrderRequest assemblyProductsForOrder) {
        log.info("Prepare product for order: {}", assemblyProductsForOrder);
        return warehouseService.assemblyProductsForOrder(assemblyProductsForOrder);
    }

    @Override
    @PostMapping("/booking")
    public BookedProductsDto bookingProducts(@RequestBody @Valid ShoppingCartDto shoppingCartDto) {
        log.info("Booking of products for cart: {}", shoppingCartDto);
        return warehouseService.bookingProducts(shoppingCartDto);
    }
}