package ru.yandex.practicum.delivery.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.delivery.service.DeliveryService;
import ru.yandex.practicum.interactionapi.dto.DeliveryDto;
import ru.yandex.practicum.interactionapi.dto.OrderDto;
import ru.yandex.practicum.interactionapi.interfaces.DeliveryOperations;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/delivery")
@RequiredArgsConstructor
public class DeliveryController implements DeliveryOperations {

    private final DeliveryService deliveryService;


    @Override
    @PutMapping
    public DeliveryDto planDelivery(@RequestBody @Valid DeliveryDto deliveryDto) {
        try {
            log.info("Plan new Delivery: {}", deliveryDto);
            return deliveryService.planDelivery(deliveryDto);
        } catch (Exception e) {
            log.error("Error with planDelivery.");
            throw e;
        }
    }

    @Override
    @PostMapping("/successful")
    public void deliverySuccessful(@RequestBody UUID deliveryId) {
        try {
            log.info("Delivery successful info: {}", deliveryId);
            deliveryService.deliverySuccessful(deliveryId);
        } catch (Exception e) {
            log.error("Error with deliverySuccessful.");
            throw e;
        }
    }

    @Override
    @PostMapping("/picked")
    public void deliveryPicked(@RequestBody UUID deliveryId) {
        try {
            log.info("Delivery pick info: {}", deliveryId);
            deliveryService.deliveryPicked(deliveryId);
        } catch (Exception e) {
            log.error("Error with deliveryPicked.");
            throw e;
        }
    }

    @Override
    @PostMapping("/failed")
    public void deliveryFailed(@RequestBody UUID deliveryId) {
        try {
            log.info("Delivery fail info: {}", deliveryId);
            deliveryService.deliveryFailed(deliveryId);
        } catch (Exception e) {
            log.error("Error with deliveryFailed.");
            throw e;
        }
    }

    @Override
    @PostMapping("/cost")
    public Double deliveryCost(@RequestBody @Valid OrderDto orderDto) {
        try {
            log.info("Delivery cost calculation: {}", orderDto);
            return deliveryService.deliveryCost(orderDto);
        } catch (Exception e) {
            log.error("Error with deliveryCost.");
            throw e;
        }
    }
}