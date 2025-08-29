package ru.yandex.practicum.collector.producers.hub;

import lombok.RequiredArgsConstructor;
import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.beans.factory.annotation.Value;
import ru.yandex.practicum.collector.model.hub.HubEvent;
import ru.yandex.practicum.collector.producers.KafkaProducer;

@RequiredArgsConstructor
public abstract class BaseHubProducer implements HubEventProducer {

    private final KafkaProducer producer;

    @Value("${topic.telemetry-hubs}")
    private String topic;

    @Override
    public void builder(HubEvent event) {
        producer.send(toAvro(event), event.getHubId(), event.getTimestamp(), topic);
    }

    public abstract SpecificRecordBase toAvro(HubEvent hubEvent);
}
