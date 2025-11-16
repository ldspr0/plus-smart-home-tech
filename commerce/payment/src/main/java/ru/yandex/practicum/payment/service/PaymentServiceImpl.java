package ru.yandex.practicum.payment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.interactionapi.dto.OrderDto;
import ru.yandex.practicum.interactionapi.dto.PaymentDto;
import ru.yandex.practicum.interactionapi.dto.ProductDto;
import ru.yandex.practicum.interactionapi.enums.PaymentState;
import ru.yandex.practicum.interactionapi.feign.OrderClient;
import ru.yandex.practicum.interactionapi.feign.ShoppingStoreClient;
import ru.yandex.practicum.payment.exception.NoPaymentFoundException;
import ru.yandex.practicum.payment.exception.NotEnoughInfoInOrderToCalculateException;
import ru.yandex.practicum.payment.mapper.PaymentMapper;
import ru.yandex.practicum.payment.model.Payment;
import ru.yandex.practicum.payment.repository.PaymentRepository;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentMapper paymentMapper;
    private final PaymentRepository paymentRepository;
    private final ShoppingStoreClient shoppingStoreClient;
    private final OrderClient orderClient;

    private final String MESSAGE_NOT_INFORMATION = "Not enough information for calculating.";
    private final String MESSAGE_PAYMENT_NOT_FOUND = "Payment is not found.";

    @Override
    public PaymentDto createPayment(OrderDto orderDto) {
        checkOrder(orderDto);
        Payment payment = Payment.builder()
                .orderId(orderDto.getOrderId())
                .totalPayment(convertToBigDecimal(orderDto.getTotalPrice()))
                .deliveryTotal(convertToBigDecimal(orderDto.getDeliveryPrice()))
                .productsTotal(convertToBigDecimal(orderDto.getProductPrice()))
                .feeTotal(getTax(convertToBigDecimal(orderDto.getTotalPrice())))
                .status(PaymentState.PENDING)
                .build();
        return paymentMapper.toPaymentDto(paymentRepository.save(payment));
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalCost(OrderDto orderDto) {
        if (orderDto.getDeliveryPrice() == null) {
            throw new NotEnoughInfoInOrderToCalculateException(MESSAGE_NOT_INFORMATION);
        }

        BigDecimal productPrice = convertToBigDecimal(orderDto.getProductPrice());
        BigDecimal deliveryPrice = convertToBigDecimal(orderDto.getDeliveryPrice());
        BigDecimal tax = getTax(productPrice);

        return productPrice.add(tax).add(deliveryPrice);
    }

    @Override
    public void paymentRefund(UUID uuid) {
        Payment payment = paymentRepository.findById(uuid).orElseThrow(
                () -> new NoPaymentFoundException(MESSAGE_PAYMENT_NOT_FOUND));
        payment.setStatus(PaymentState.SUCCESS);
        orderClient.payment(payment.getOrderId());
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal productCost(OrderDto orderDto) {
        Map<UUID, Long> products = orderDto.getProducts();
        if (products == null) {
            throw new NotEnoughInfoInOrderToCalculateException(MESSAGE_NOT_INFORMATION);
        }

        BigDecimal totalCost = BigDecimal.ZERO;

        for (Map.Entry<UUID, Long> entry : products.entrySet()) {
            ProductDto product = shoppingStoreClient.getProduct(entry.getKey());
            BigDecimal productPrice = convertToBigDecimal(product.getPrice());
            BigDecimal quantity = BigDecimal.valueOf(entry.getValue());

            BigDecimal productTotal = productPrice.multiply(quantity);
            totalCost = totalCost.add(productTotal);
        }

        return totalCost;
    }

    @Override
    public void paymentFailed(UUID uuid) {
        Payment payment = paymentRepository.findById(uuid).orElseThrow(
                () -> new NoPaymentFoundException(MESSAGE_PAYMENT_NOT_FOUND));
        payment.setStatus(PaymentState.FAILED);
        orderClient.paymentFailed(payment.getOrderId());
    }

    private void checkOrder(OrderDto orderDto) {
        if (orderDto.getDeliveryPrice() == null) {
            throw new NotEnoughInfoInOrderToCalculateException(MESSAGE_NOT_INFORMATION);
        }
        if (orderDto.getProductPrice() == null) {
            throw new NotEnoughInfoInOrderToCalculateException(MESSAGE_NOT_INFORMATION);
        }
        if (orderDto.getTotalPrice() == null) {
            throw new NotEnoughInfoInOrderToCalculateException(MESSAGE_NOT_INFORMATION);
        }
    }

    private BigDecimal getTax(BigDecimal totalPrice) {
        if (totalPrice == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal taxRate = new BigDecimal("0.10"); // 10%
        return totalPrice.multiply(taxRate);
    }

    private BigDecimal convertToBigDecimal(Double value) {
        return value != null ? BigDecimal.valueOf(value) : BigDecimal.ZERO;
    }
}