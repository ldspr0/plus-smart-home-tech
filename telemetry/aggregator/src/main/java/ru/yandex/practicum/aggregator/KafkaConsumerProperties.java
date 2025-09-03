package ru.yandex.practicum.aggregator;

import lombok.Getter;
import lombok.Setter;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
@ConfigurationProperties(prefix = "spring.kafka.consumer")
@Getter
@Setter
public class KafkaConsumerProperties {

    private String clientId;
    private String groupId;
    private String bootstrapServers;
    private String keyDeserializer;
    private String valueDeserializer;
    private String enableAutoCommit;
    private String autoOffsetReset = "earliest";

    @Bean
    public KafkaConsumer<String, SpecificRecordBase> kafkaConsumer() {
        Properties properties = new Properties();

        if (bootstrapServers != null) {
            properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        }
        if (groupId != null) {
            properties.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        }
        if (keyDeserializer != null) {
            properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, keyDeserializer);
        }
        if (valueDeserializer != null) {
            properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, valueDeserializer);
        }
        if (enableAutoCommit != null) {
            properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, enableAutoCommit);
        }
        if (autoOffsetReset != null) {
            properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);
        }

        // Обязательные значения по умолчанию
        properties.putIfAbsent(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.putIfAbsent(ConsumerConfig.GROUP_ID_CONFIG, "aggregator-group");
        properties.putIfAbsent(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringDeserializer");
        properties.putIfAbsent(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                "io.confluent.kafka.serializers.KafkaAvroDeserializer");
        properties.putIfAbsent(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");

        return new KafkaConsumer<>(properties);
    }
}