package ru.yandex.practicum.collector.producers.hub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import ru.yandex.practicum.collector.producers.KafkaProducer;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

import java.time.Instant;

@Slf4j
@RequiredArgsConstructor
public abstract class BaseHubProducer implements HubEventProducer {

    private final KafkaProducer producer;

    @Value("${topic.telemetry-hubs}")

    private String topic;

    @Override
    public void builder(HubEventProto event) {
        var contract = toAvro(event);
        log.info("Send hub event {}", contract);
        producer.send(toAvro(event), event.getHubId(), mapTimestampToInstant(event), topic);
    }

    public Instant mapTimestampToInstant(HubEventProto event) {
        return Instant.ofEpochSecond(event.getTimestamp().getSeconds(), event.getTimestamp().getNanos());
    }

    public abstract HubEventAvro toAvro(HubEventProto hubEvent);

    public abstract HubEventProto.PayloadCase getMessageType();
}