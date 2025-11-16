package ru.yandex.practicum.order.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.interactionapi.dto.OrderDto;
import ru.yandex.practicum.interactionapi.interfaces.OrderOperations;
import ru.yandex.practicum.interactionapi.request.CreateNewOrderRequest;
import ru.yandex.practicum.interactionapi.request.ProductReturnRequest;
import ru.yandex.practicum.order.service.OrderService;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
public class OrderController implements OrderOperations {

    private final OrderService orderService;

    @Override
    @GetMapping
    public List<OrderDto> getClientOrders(@RequestParam String username,
                                          @RequestParam(defaultValue = "0") Integer page,
                                          @RequestParam(defaultValue = "10") Integer size) {
        try {
            log.info("Get client orders: {}", username);
            return orderService.getClientOrders(username, page, size);
        } catch (Exception e) {
            log.error("Error with getClientOrders.");
            throw e;
        }
    }

    @Override
    @PutMapping
    public OrderDto createNewOrder(@RequestBody @Valid CreateNewOrderRequest newOrderRequest) {
        try {
            log.info("Create new order: {}", newOrderRequest);
            return orderService.createNewOrder(newOrderRequest);
        } catch (Exception e) {
            log.error("Error with createNewOrder.");
            throw e;
        }
    }

    @Override
    @PostMapping("/return")
    public OrderDto productReturn(@RequestBody @Valid ProductReturnRequest returnRequest) {
        try {
            log.info("Product return: {}", returnRequest);
            return orderService.productReturn(returnRequest);
        } catch (Exception e) {
            log.error("Error with productReturn.");
            throw e;
        }
    }

    @Override
    @PostMapping("/payment")
    public OrderDto payment(@RequestBody UUID orderId) {
        try {
            log.info("Payment: {}", orderId);
            return orderService.payment(orderId);
        } catch (Exception e) {
            log.error("Error with payment.");
            throw e;
        }
    }

    @Override
    @PostMapping("/payment/failed")
    public OrderDto paymentFailed(@RequestBody UUID orderId) {
        try {
            log.info("Payment failed info: {}", orderId);
            return orderService.paymentFailed(orderId);
        } catch (Exception e) {
            log.error("Error with paymentFailed.");
            throw e;
        }
    }

    @Override
    @PostMapping("/delivery")
    public OrderDto delivery(@RequestBody UUID orderId) {
        try {
            log.info("Delivery: {}", orderId);
            return orderService.delivery(orderId);
        } catch (Exception e) {
            log.error("Error with delivery.");
            throw e;
        }
    }

    @Override
    @PostMapping("/delivery/failed")
    public OrderDto deliveryFailed(@RequestBody UUID orderId) {
        try {
            log.info("Delivery failed info: {}", orderId);
            return orderService.deliveryFailed(orderId);
        } catch (Exception e) {
            log.error("Error with deliveryFailed.");
            throw e;
        }
    }

    @Override
    @PostMapping("/completed")
    public OrderDto complete(@RequestBody UUID orderId) {
        try {
            log.info("Order completion: {}", orderId);
            return orderService.complete(orderId);
        } catch (Exception e) {
            log.error("Error with complete.");
            throw e;
        }
    }

    @Override
    @PostMapping("/calculate/total")
    public OrderDto calculateTotalCost(@RequestBody UUID orderId) {
        try {
            log.info("Calculate total: {}", orderId);
            return orderService.calculateTotalCost(orderId);
        } catch (Exception e) {
            log.error("Error with calculateTotalCost.");
            throw e;
        }
    }

    @Override
    @PostMapping("/calculate/delivery")
    public OrderDto calculateDeliveryCost(@RequestBody UUID orderId) {
        try {
            log.info("Calculate delivery: {}", orderId);
            return orderService.calculateDeliveryCost(orderId);
        } catch (Exception e) {
            log.error("Error with calculateDeliveryCost.");
            throw e;
        }
    }

    @Override
    @PostMapping("/assembly")
    public OrderDto assembly(@RequestBody UUID orderId) {
        try {
            log.info("Order assembly: {}", orderId);
            return orderService.assembly(orderId);
        } catch (Exception e) {
            log.error("Error with assembly.");
            throw e;
        }
    }

    @Override
    @PostMapping("/assembly/failed")
    public OrderDto assemblyFailed(@RequestBody UUID orderId) {
        try {
            log.info("Assembly failed info: {}", orderId);
            return orderService.assemblyFailed(orderId);
        } catch (Exception e) {
            log.error("Error with assemblyFailed.");
            throw e;
        }
    }
}