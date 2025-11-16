package ru.yandex.practicum.warehouse.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.interactionapi.dto.AddressDto;
import ru.yandex.practicum.interactionapi.dto.BookedProductsDto;
import ru.yandex.practicum.interactionapi.dto.ShoppingCartDto;
import ru.yandex.practicum.interactionapi.request.AddProductToWarehouseRequest;
import ru.yandex.practicum.interactionapi.request.AssemblyProductsForOrderRequest;
import ru.yandex.practicum.interactionapi.request.NewProductInWarehouseRequest;
import ru.yandex.practicum.interactionapi.request.ShippedToDeliveryRequest;
import ru.yandex.practicum.warehouse.mapper.BookingMapper;
import ru.yandex.practicum.warehouse.model.Address;
import ru.yandex.practicum.warehouse.exception.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.warehouse.exception.ProductInShoppingCartLowQuantityInWarehouseException;
import ru.yandex.practicum.warehouse.exception.ProductNotFoundInWarehouseException;
import ru.yandex.practicum.warehouse.exception.SpecifiedProductAlreadyInWarehouseException;
import ru.yandex.practicum.warehouse.mapper.WarehouseMapper;
import ru.yandex.practicum.warehouse.model.Booking;
import ru.yandex.practicum.warehouse.model.Warehouse;
import ru.yandex.practicum.warehouse.repository.BookingRepository;
import ru.yandex.practicum.warehouse.repository.WarehouseRepository;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class WarehouseServiceImpl implements WarehouseService {

    private final WarehouseRepository warehouseRepository;
    private final WarehouseMapper warehouseMapper;
    private final BookingMapper bookingMapper;
    private final BookingRepository bookingRepository;

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void newProductInWarehouse(NewProductInWarehouseRequest newProductInWarehouseRequest) {
        warehouseRepository.findById(newProductInWarehouseRequest.getProductId()).ifPresent(warehouse -> {
            throw new SpecifiedProductAlreadyInWarehouseException("Error, specified product is already exists.");
        });
        Warehouse warehouse = warehouseMapper.toWarehouse(newProductInWarehouseRequest);
        warehouseRepository.save(warehouse);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void shippedToDelivery(ShippedToDeliveryRequest deliveryRequest) {
        Booking booking = bookingRepository.findByOrderId(deliveryRequest.getOrderId()).orElseThrow(
                () -> new NoSpecifiedProductInWarehouseException("Information about product is not found."));
        booking.setDeliveryId(deliveryRequest.getDeliveryId());
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void acceptReturn(Map<UUID, Long> products) {
        List<Warehouse> warehousesItems = warehouseRepository.findAllById(products.keySet());
        for (Warehouse warehouse : warehousesItems) {
            warehouse.setQuantity(warehouse.getQuantity() + products.get(warehouse.getProductId()));
        }
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public BookedProductsDto checkProductQuantityEnoughForShoppingCart(ShoppingCartDto shoppingCartDto) {
        Map<UUID, Long> products = shoppingCartDto.getProducts();
        Set<UUID> cartProductIds = products.keySet();
        Map<UUID, Warehouse> warehouseProducts = warehouseRepository.findAllById(cartProductIds)
                .stream()
                .collect(Collectors.toMap(Warehouse::getProductId, Function.identity()));

        Set<UUID> productIds = warehouseProducts.keySet();
        cartProductIds.forEach(id -> {
            if (!productIds.contains(id)) {
                throw new ProductNotFoundInWarehouseException("Error, Product is not found in Warehouse.");
            }
        });
        products.forEach((key, value) -> {
            if (warehouseProducts.get(key).getQuantity() < value) {
                throw new ProductInShoppingCartLowQuantityInWarehouseException("Error, not enough quantity in Warehouse.");
            }
        });

        return getBookedProducts(warehouseProducts.values(), products);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public BookedProductsDto assemblyProductsForOrder(AssemblyProductsForOrderRequest assemblyProductsForOrder) {
        Booking booking = bookingRepository.findById(assemblyProductsForOrder.getShoppingCartId()).orElseThrow(
                () -> new RuntimeException(String.format("Shopping cart %s not found", assemblyProductsForOrder.getShoppingCartId()))
        );

        Map<UUID, Long> productsInBooking = booking.getProducts();
        List<Warehouse> productsInWarehouse = warehouseRepository.findAllById(productsInBooking.keySet());
        productsInWarehouse.forEach(warehouse -> {
            if (warehouse.getQuantity() < productsInBooking.get(warehouse.getProductId())) {
                throw new ProductInShoppingCartLowQuantityInWarehouseException("Error, there is not enough quantity in warehouse.");
            }
        });
        for (Warehouse warehouse : productsInWarehouse) {
            warehouse.setQuantity(warehouse.getQuantity() - productsInBooking.get(warehouse.getProductId()));
        }
        booking.setOrderId(assemblyProductsForOrder.getOrderId());
        return bookingMapper.toBookedProductsDto(booking);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void addProductToWarehouse(AddProductToWarehouseRequest addProductToWarehouseRequest) {
        Warehouse warehouse = warehouseRepository.findById(addProductToWarehouseRequest.getProductId()).orElseThrow(
                () -> new NoSpecifiedProductInWarehouseException("ProductId: " + addProductToWarehouseRequest.getProductId() + " is not found in Warehouse.")
        );
        warehouse.setQuantity(warehouse.getQuantity() + addProductToWarehouseRequest.getQuantity());
    }

    @Override
    public AddressDto getWarehouseAddress() {
        String address = Address.CURRENT_ADDRESS;
        return AddressDto.builder()
                .country(address)
                .city(address)
                .street(address)
                .house(address)
                .flat(address)
                .build();
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public BookedProductsDto bookingProducts(ShoppingCartDto shoppingCartDto) {
        Map<UUID, Long> products = shoppingCartDto.getProducts();
        List<Warehouse> productsInWarehouse = warehouseRepository.findAllById(products.keySet());
        productsInWarehouse.forEach(warehouse -> {
            if (warehouse.getQuantity() < products.get(warehouse.getProductId())) {
                throw new ProductInShoppingCartLowQuantityInWarehouseException(
                        "Product: " + warehouse.getProductId() + " is sold out");
            }
        });

        double deliveryVolume = productsInWarehouse.stream()
                .map(v -> v.getDimension().getDepth() * v.getDimension().getWidth()
                          * v.getDimension().getHeight())
                .mapToDouble(Double::doubleValue)
                .sum();

        double deliveryWeight = productsInWarehouse.stream()
                .map(Warehouse::getWeight)
                .mapToDouble(Double::doubleValue)
                .sum();

        boolean fragile = productsInWarehouse.stream()
                .anyMatch(Warehouse::getFragile);

        Booking newBooking = Booking.builder()
                .shoppingCartId(shoppingCartDto.getShoppingCartId())
                .deliveryVolume(deliveryVolume)
                .deliveryWeight(deliveryWeight)
                .fragile(fragile)
                .products(products)
                .build();
        Booking booking = bookingRepository.save(newBooking);
        return bookingMapper.toBookedProductsDto(booking);
    }

    private BookedProductsDto getBookedProducts(Collection<Warehouse> productList,
                                                Map<UUID, Long> cartProducts) {
        return BookedProductsDto.builder()
                .fragile(productList.stream().anyMatch(Warehouse::getFragile))
                .deliveryWeight(productList.stream()
                        .mapToDouble(p -> p.getWeight() * cartProducts.get(p.getProductId()))
                        .sum())
                .deliveryVolume(productList.stream()
                        .mapToDouble(p ->
                                p.getDimension().getWidth() * p.getDimension().getHeight() * p.getDimension().getDepth() * cartProducts.get(p.getProductId()))
                        .sum())
                .build();
    }

}