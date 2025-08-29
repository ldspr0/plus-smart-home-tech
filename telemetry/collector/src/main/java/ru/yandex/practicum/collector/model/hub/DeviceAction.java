package ru.yandex.practicum.collector.model.hub;

import lombok.Getter;
import lombok.ToString;
import ru.yandex.practicum.collector.model.enums.DeviceActionType;

@Getter
@ToString(callSuper = true)
public class DeviceAction {

    private String sensorId;
    private DeviceActionType type;
    private int value;
}
