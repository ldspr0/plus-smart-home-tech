package ru.yandex.practicum.collector.producers.hub;

import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.collector.model.enums.DeviceType;
import ru.yandex.practicum.collector.producers.KafkaProducer;
import ru.yandex.practicum.collector.model.hub.HubEvent;
import ru.yandex.practicum.collector.model.hub.DeviceAddedEvent;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

@Component
public class DeviceAddedProducer extends BaseHubProducer {
    public DeviceAddedProducer(KafkaProducer producer) {
        super(producer);
    }

    @Override
    public SpecificRecordBase toAvro(HubEvent hubEvent) {
        DeviceAddedEvent event = (DeviceAddedEvent) hubEvent;

        return HubEventAvro.newBuilder()
                .setHubId(hubEvent.getHubId())
                .setTimestamp(hubEvent.getTimestamp())
                .setPayload(new DeviceAddedEventAvro(event.getId(), mapToDeviceTypeAvro(event.getDeviceType())))
                .build();
    }

    private DeviceTypeAvro mapToDeviceTypeAvro(DeviceType deviceType) {
        DeviceTypeAvro type = null;

        switch (deviceType) {
            case LIGHT_SENSOR -> type = DeviceTypeAvro.LIGHT_SENSOR;
            case MOTION_SENSOR -> type = DeviceTypeAvro.MOTION_SENSOR;
            case SWITCH_SENSOR -> type = DeviceTypeAvro.SWITCH_SENSOR;
            case CLIMATE_SENSOR -> type = DeviceTypeAvro.CLIMATE_SENSOR;
            case TEMPERATURE_SENSOR -> type = DeviceTypeAvro.TEMPERATURE_SENSOR;
        }
        return type;
    }
}
