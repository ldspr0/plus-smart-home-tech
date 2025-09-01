package ru.yandex.practicum.collector.producers.hub;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.collector.model.enums.HubEventType;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class HubEventProducerConfig {
    private final DeviceAddedProducer deviceAddedBuilder;
    private final DeviceRemovedProducer deviceRemovedBuilder;
    private final ScenarioAddedProducer scenarioAddedBuilder;
    private final ScenarioRemovedProducer scenarioRemovedBuilder;

    @Bean
    public Map<HubEventType, HubEventProducer> getHubEventBuilders() {
        Map<HubEventType, HubEventProducer> hubEventBuilders = new HashMap<>();

        hubEventBuilders.put(HubEventType.DEVICE_ADDED, deviceAddedBuilder);
        hubEventBuilders.put(HubEventType.DEVICE_REMOVED, deviceRemovedBuilder);
        hubEventBuilders.put(HubEventType.SCENARIO_ADDED, scenarioAddedBuilder);
        hubEventBuilders.put(HubEventType.SCENARIO_REMOVED, scenarioRemovedBuilder);

        return hubEventBuilders;
    }
}
