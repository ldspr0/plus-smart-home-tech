package ru.yandex.practicum.collector.producers.sensor;

import lombok.RequiredArgsConstructor;
import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.beans.factory.annotation.Value;
import ru.yandex.practicum.collector.producers.KafkaProducer;
import ru.yandex.practicum.collector.model.sensor.SensorEvent;

@RequiredArgsConstructor
public abstract class BaseSensorProducer implements SensorEventProducer {

    private final KafkaProducer producer;

    @Value("${topic.telemetry-sensors}")
    private String topic;

    @Override
    public void builder(SensorEvent event) {
        producer.send(toAvro(event), event.getHubId(), event.getTimestamp(), topic);
    }

    public abstract SpecificRecordBase toAvro(SensorEvent sensorEvent);
}
