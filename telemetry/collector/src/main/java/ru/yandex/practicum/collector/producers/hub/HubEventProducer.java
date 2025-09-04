package ru.yandex.practicum.collector.producers.hub;

import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;

public interface HubEventProducer {

    void builder(HubEventProto hubEvent);

    HubEventProto.PayloadCase getMessageType();
}