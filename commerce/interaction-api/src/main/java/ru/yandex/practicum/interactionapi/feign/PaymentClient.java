package ru.yandex.practicum.interactionapi.feign;

import org.springframework.cloud.openfeign.FeignClient;
import ru.yandex.practicum.interactionapi.interfaces.PaymentOperations;

@FeignClient(name = "payment", path = "/api/v1/payment")
public interface PaymentClient extends PaymentOperations {
}