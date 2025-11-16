package ru.yandex.practicum.interactionapi.feign;

import org.springframework.cloud.openfeign.FeignClient;
import ru.yandex.practicum.interactionapi.interfaces.DeliveryOperations;

@FeignClient(name = "delivery", path = "/api/v1/delivery")
public interface DeliveryClient extends DeliveryOperations {
}