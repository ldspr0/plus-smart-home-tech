package ru.yandex.practicum.collector.producers.sensor;

import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

public interface SensorEventProducer {

    void builder(SensorEventProto sensorEvent);

    SensorEventProto.PayloadCase getMessageType();
}