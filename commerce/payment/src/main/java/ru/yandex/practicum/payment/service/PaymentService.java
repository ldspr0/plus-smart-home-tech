package ru.yandex.practicum.payment.service;

import ru.yandex.practicum.interactionapi.dto.OrderDto;
import ru.yandex.practicum.interactionapi.dto.PaymentDto;

import java.math.BigDecimal;
import java.util.UUID;

public interface PaymentService {

    PaymentDto createPayment(OrderDto orderDto);

    BigDecimal getTotalCost(OrderDto orderDto);

    void paymentRefund(UUID uuid);

    BigDecimal productCost(OrderDto orderDto);

    void paymentFailed(UUID uuid);
}