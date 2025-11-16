package ru.yandex.practicum.delivery.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.delivery.exception.NoDeliveryFoundException;
import ru.yandex.practicum.delivery.mapper.DeliveryMapper;
import ru.yandex.practicum.delivery.model.Delivery;
import ru.yandex.practicum.delivery.repository.DeliveryRepository;
import ru.yandex.practicum.interactionapi.dto.AddressDto;
import ru.yandex.practicum.interactionapi.dto.DeliveryDto;
import ru.yandex.practicum.interactionapi.dto.OrderDto;
import ru.yandex.practicum.interactionapi.enums.DeliveryState;
import ru.yandex.practicum.interactionapi.feign.OrderClient;
import ru.yandex.practicum.interactionapi.feign.WarehouseClient;
import ru.yandex.practicum.interactionapi.request.ShippedToDeliveryRequest;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class DeliveryServiceImpl implements DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final DeliveryMapper deliveryMapper;
    private final OrderClient orderClient;
    private final WarehouseClient warehouseClient;

    private static final BigDecimal BASERATE = new BigDecimal("5.0");
    private static final BigDecimal FRAGILE_SURCHARGE_RATE = new BigDecimal("0.20");
    private static final BigDecimal WEIGHT_RATE = new BigDecimal("0.30");
    private static final BigDecimal VOLUME_RATE = new BigDecimal("0.20");
    private static final BigDecimal STREET_SURCHARGE_RATE = new BigDecimal("0.20");
    private static final String ADDRESS1 = "ADDRESS_1";
    private static final String ADDRESS2 = "ADDRESS_2";
    private final String MESSAGE_DELIVERY_NOT_FOUND = "Delivery is not found.";

    @Override
    public DeliveryDto planDelivery(DeliveryDto deliveryDto) {
        Delivery delivery = deliveryMapper.toDelivery(deliveryDto);
        delivery.setDeliveryState(DeliveryState.CREATED);
        return deliveryMapper.toDeliveryDto(deliveryRepository.save(delivery));
    }

    @Override
    public void deliverySuccessful(UUID deliveryId) {
        Delivery delivery = deliveryRepository.findById(deliveryId).orElseThrow(
                () -> new NoDeliveryFoundException(MESSAGE_DELIVERY_NOT_FOUND));
        delivery.setDeliveryState(DeliveryState.DELIVERED);
        orderClient.complete(delivery.getOrderId());
    }

    @Override
    public void deliveryPicked(UUID deliveryId) {
        Delivery delivery = deliveryRepository.findById(deliveryId).orElseThrow(
                () -> new NoDeliveryFoundException(MESSAGE_DELIVERY_NOT_FOUND));
        delivery.setDeliveryState(DeliveryState.IN_PROGRESS);
        orderClient.assembly(delivery.getOrderId());
        ShippedToDeliveryRequest deliveryRequest = new ShippedToDeliveryRequest(
                delivery.getOrderId(), delivery.getDeliveryId());
        warehouseClient.shippedToDelivery(deliveryRequest);
    }

    @Override
    public void deliveryFailed(UUID deliveryId) {
        Delivery delivery = deliveryRepository.findById(deliveryId).orElseThrow(
                () -> new NoDeliveryFoundException(MESSAGE_DELIVERY_NOT_FOUND));
        delivery.setDeliveryState(DeliveryState.FAILED);
        orderClient.assemblyFailed(delivery.getOrderId());
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal deliveryCost(OrderDto orderDto) {
        Delivery delivery = deliveryRepository.findByOrderId(orderDto.getOrderId()).orElseThrow(
                () -> new NoDeliveryFoundException(MESSAGE_DELIVERY_NOT_FOUND));

        AddressDto warehouseAddress = warehouseClient.getWarehouseAddress();

        BigDecimal addressCost = switch (warehouseAddress.getCity()) {
            case ADDRESS1 -> BASERATE;
            case ADDRESS2 -> BASERATE.multiply(new BigDecimal("2"));
            default ->
                    throw new IllegalStateException(String.format("Unexpected value: %s", warehouseAddress.getCity()));
        };

        BigDecimal deliveryCost = BASERATE.add(addressCost);

        if (orderDto.getFragile()) {
            deliveryCost = deliveryCost.add(deliveryCost.multiply(FRAGILE_SURCHARGE_RATE));
        }

        deliveryCost = deliveryCost.add(
                BigDecimal.valueOf(orderDto.getDeliveryWeight()).multiply(WEIGHT_RATE)
        );

        deliveryCost = deliveryCost.add(
                BigDecimal.valueOf(orderDto.getDeliveryVolume()).multiply(VOLUME_RATE)
        );

        if (!warehouseAddress.getStreet().equals(delivery.getToAddress().getStreet())) {
            deliveryCost = deliveryCost.add(deliveryCost.multiply(STREET_SURCHARGE_RATE));
        }

        return deliveryCost;
    }
}