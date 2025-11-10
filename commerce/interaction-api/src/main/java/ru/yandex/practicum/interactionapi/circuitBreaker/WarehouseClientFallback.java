package ru.yandex.practicum.interactionapi.circuitBreaker;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.interactionapi.dto.AddressDto;
import ru.yandex.practicum.interactionapi.dto.BookedProductsDto;
import ru.yandex.practicum.interactionapi.dto.ShoppingCartDto;
import ru.yandex.practicum.interactionapi.feign.WarehouseClient;
import ru.yandex.practicum.interactionapi.request.AddProductToWarehouseRequest;
import ru.yandex.practicum.interactionapi.request.NewProductInWarehouseRequest;

@Component
public class WarehouseClientFallback implements WarehouseClient {
    public final String FALLBACK_MESSAGE = "Fallback response: service is temporarily unavailable.";


    @Override
    public void newProductInWarehouse(NewProductInWarehouseRequest requestDto) {
        throw new WarehouseServerUnavailable(FALLBACK_MESSAGE);
    }

    @Override
    public BookedProductsDto checkProductQuantityEnoughForShoppingCart(ShoppingCartDto shoppingCartDto) {
        throw new WarehouseServerUnavailable(FALLBACK_MESSAGE);
    }

    @Override
    public void addProductToWarehouse(AddProductToWarehouseRequest requestDto) {
        throw new WarehouseServerUnavailable(FALLBACK_MESSAGE);
    }

    @Override
    public AddressDto getAddress() {
        throw new WarehouseServerUnavailable(FALLBACK_MESSAGE);
    }

    @Override
    public BookedProductsDto bookingProducts(ShoppingCartDto shoppingCartDto) {
        throw new WarehouseServerUnavailable(FALLBACK_MESSAGE);
    }
}