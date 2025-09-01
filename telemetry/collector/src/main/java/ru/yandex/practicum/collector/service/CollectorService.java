package ru.yandex.practicum.collector.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.collector.producers.hub.HubEventProducer;
import ru.yandex.practicum.collector.producers.sensor.SensorEventProducer;
import ru.yandex.practicum.collector.model.enums.HubEventType;
import ru.yandex.practicum.collector.model.enums.SensorEventType;
import ru.yandex.practicum.collector.model.hub.HubEvent;
import ru.yandex.practicum.collector.model.sensor.SensorEvent;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CollectorService {

    private final Map<SensorEventType, SensorEventProducer> sensorEventBuilders;
    private final Map<HubEventType, HubEventProducer> hubEventBuilders;

    public void collectSensorEvent(SensorEvent sensor) {
        log.info("Start collect sensor event" + sensor.toString());
        sensorEventBuilders.get(sensor.getType()).builder(sensor);
    }

    public void collectHubEvent(HubEvent hub) {
        log.info("Start collect hub event" + hub.toString());
        hubEventBuilders.get(hub.getType()).builder(hub);
    }
}