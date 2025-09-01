package ru.yandex.practicum.collector.model.hub;

import lombok.Getter;
import lombok.ToString;
import ru.yandex.practicum.collector.model.enums.ConditionOperation;
import ru.yandex.practicum.collector.model.enums.ConditionType;

@Getter
@ToString(callSuper = true)
public class ScenarioCondition {

    private String sensorId;
    private ConditionType type;
    private ConditionOperation operation;
    private int value;
}
