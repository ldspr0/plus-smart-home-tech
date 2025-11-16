package ru.yandex.practicum.payment.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.interactionapi.dto.OrderDto;
import ru.yandex.practicum.interactionapi.dto.PaymentDto;
import ru.yandex.practicum.interactionapi.interfaces.PaymentOperations;
import ru.yandex.practicum.payment.service.PaymentService;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
public class PaymentController implements PaymentOperations {

    private final PaymentService paymentService;

    @Override
    @PostMapping
    public PaymentDto createPayment(@RequestBody @Valid OrderDto orderDto) {
        try {
            log.info("Create payment request: {}", orderDto);
            return paymentService.createPayment(orderDto);
        } catch (Exception e) {
            log.error("Error with createPayment.");
            throw e;
        }
    }

    @Override
    @PostMapping("/totalCost")
    public BigDecimal getTotalCost(@RequestBody @Valid OrderDto orderDto) {
        try {
            log.info("Get total cost: {}", orderDto);
            return paymentService.getTotalCost(orderDto);
        } catch (Exception e) {
            log.error("Error with getTotalCost.");
            throw e;
        }
    }

    @Override
    @PostMapping("/refund")
    public void paymentRefund(@RequestBody UUID orderId) {
        try {
            log.info("Payment refund: {}", orderId);
            paymentService.paymentRefund(orderId);
        } catch (Exception e) {
            log.error("Error with paymentRefund.");
            throw e;
        }
    }

    @Override
    @PostMapping("/productCost")
    public BigDecimal productCost(@RequestBody @Valid OrderDto orderDto) {
        try {
            log.info("Product Cost calc: {}", orderDto);
            return paymentService.productCost(orderDto);
        } catch (Exception e) {
            log.error("Error with productCost.");
            throw e;
        }
    }

    @Override
    @PostMapping("/failed")
    public void paymentFailed(@RequestBody UUID orderId) {
        try {
            log.info("Payment fail: {}", orderId);
            paymentService.paymentFailed(orderId);
        } catch (Exception e) {
            log.error("Error with paymentFailed.");
            throw e;
        }
    }
}