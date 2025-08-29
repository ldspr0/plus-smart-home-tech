package ru.yandex.practicum.collector.producers.hub;

import ru.yandex.practicum.collector.model.hub.HubEvent;

public interface HubEventProducer {

    void builder(HubEvent hubEvent);
}
