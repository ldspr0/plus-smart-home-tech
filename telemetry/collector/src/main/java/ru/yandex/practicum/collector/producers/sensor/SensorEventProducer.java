package ru.yandex.practicum.collector.producers.sensor;

import ru.yandex.practicum.collector.model.sensor.SensorEvent;

public interface SensorEventProducer {
    void builder(SensorEvent event);
}
