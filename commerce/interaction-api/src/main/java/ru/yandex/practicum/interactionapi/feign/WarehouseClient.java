package ru.yandex.practicum.interactionapi.feign;

import org.springframework.cloud.openfeign.FeignClient;
import ru.yandex.practicum.interactionapi.circuitBreaker.WarehouseClientFallback;
import ru.yandex.practicum.interactionapi.interfaces.WarehouseOperations;

@FeignClient(name = "warehouse", path = "/api/v1/warehouse", fallback = WarehouseClientFallback.class)
public interface WarehouseClient extends WarehouseOperations {
}