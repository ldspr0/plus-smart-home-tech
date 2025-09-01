package ru.yandex.practicum.collector.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.collector.model.hub.HubEvent;
import ru.yandex.practicum.collector.model.sensor.SensorEvent;
import ru.yandex.practicum.collector.service.CollectorService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
@Slf4j
public class CollectorController {

    private final CollectorService collectorService;

    @PostMapping("/sensors")
    public void collectSensorEvent(@Valid @RequestBody SensorEvent sensor) {
        try {
            log.info("Sensor event collector");
            collectorService.collectSensorEvent(sensor);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @PostMapping("/hubs")
    public void collectHubEvent(@Valid @RequestBody HubEvent hub) {
        try {
            log.info("Hub Event collector");
            collectorService.collectHubEvent(hub);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
