package ru.yandex.practicum.collector.producers.sensor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import ru.yandex.practicum.collector.producers.KafkaProducer;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

import java.time.Instant;

@Slf4j
@RequiredArgsConstructor
public abstract class BaseSensorProducer implements SensorEventProducer {

    private final KafkaProducer producer;

    @Value("${topic.telemetry-sensors}")

    private String topic;

    @Override
    public void builder(SensorEventProto event) {
        var contract = toAvro(event);
        log.info("Send sensor event {}", contract);
        producer.send(contract, event.getHubId(), mapTimestampToInstant(event), topic);
    }

    public Instant mapTimestampToInstant(SensorEventProto event) {
        return Instant.ofEpochSecond(event.getTimestamp().getSeconds(), event.getTimestamp().getNanos());
    }

    public abstract SensorEventAvro toAvro(SensorEventProto sensorEvent);

    public abstract SensorEventProto.PayloadCase getMessageType();
}