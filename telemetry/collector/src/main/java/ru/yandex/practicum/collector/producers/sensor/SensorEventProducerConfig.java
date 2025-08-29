package ru.yandex.practicum.collector.producers.sensor;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.collector.model.enums.SensorEventType;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class SensorEventProducerConfig {
    private final ClimateSensorProducer climateSensorBuilder;
    private final LightSensorProducer lightSensorBuilder;
    private final MotionSensorProducer motionSensorBuilder;
    private final SwitchSensorProducer switchSensorBuilder;
    private final TemperatureSensorProducer temperatureSensorBuilder;

    @Bean
    public Map<SensorEventType, SensorEventProducer> getSensorEventBuilders() {
        Map<SensorEventType, SensorEventProducer> sensorEventBuilders = new HashMap<>();

        sensorEventBuilders.put(SensorEventType.SWITCH_SENSOR_EVENT, switchSensorBuilder);
        sensorEventBuilders.put(SensorEventType.CLIMATE_SENSOR_EVENT, climateSensorBuilder);
        sensorEventBuilders.put(SensorEventType.LIGHT_SENSOR_EVENT, lightSensorBuilder);
        sensorEventBuilders.put(SensorEventType.MOTION_SENSOR_EVENT, motionSensorBuilder);
        sensorEventBuilders.put(SensorEventType.TEMPERATURE_SENSOR_EVENT, temperatureSensorBuilder);

        return sensorEventBuilders;
    }
}
