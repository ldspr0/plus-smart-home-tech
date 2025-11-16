package ru.yandex.practicum.delivery.service;

import ru.yandex.practicum.interactionapi.dto.DeliveryDto;
import ru.yandex.practicum.interactionapi.dto.OrderDto;

import java.math.BigDecimal;
import java.util.UUID;

public interface DeliveryService {

    DeliveryDto planDelivery(DeliveryDto deliveryDto);

    void deliverySuccessful(UUID deliveryId);

    void deliveryPicked(UUID deliveryId);

    void deliveryFailed(UUID deliveryId);

    BigDecimal deliveryCost(OrderDto orderDto);
}