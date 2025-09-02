package ru.yandex.practicum.collector.producers.hub;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.collector.producers.KafkaProducer;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioRemovedEventProto;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;

@Component
public class ScenarioRemovedProducer extends BaseHubProducer {
    public ScenarioRemovedProducer(KafkaProducer producer) {
        super(producer);
    }

    @Override
    public HubEventAvro toAvro(HubEventProto hubEvent) {
        ScenarioRemovedEventProto scenarioRemovedEvent = hubEvent.getScenarioRemoved();

        return HubEventAvro.newBuilder()
                .setHubId(hubEvent.getHubId())
                .setTimestamp(mapTimestampToInstant(hubEvent))
                .setPayload(new ScenarioRemovedEventAvro(scenarioRemovedEvent.getName()))
                .build();
    }

    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.SCENARIO_REMOVED;
    }
}