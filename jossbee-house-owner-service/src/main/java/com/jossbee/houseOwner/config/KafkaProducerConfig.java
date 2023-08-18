package com.jossbee.houseOwner.config;

import com.jossbee.houseOwner.dto.HouseDto;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

import static org.apache.kafka.clients.producer.ProducerConfig.*;

@Configuration
public class KafkaProducerConfig {

    @Value("${house_topic}")
    private String houseTopicName;

    @Value("${kafka.zookeeper-address}")
    private String zookeeperAddress;

    @Bean
    public NewTopic houseTopic() {
        return TopicBuilder.name(houseTopicName)
                .build();
    }

    @Bean
    public ProducerFactory<String, HouseDto> houseProducerFactory() {
        Map<String, Object> configProps = new HashMap<>();

        configProps.put(BOOTSTRAP_SERVERS_CONFIG, zookeeperAddress);
        configProps.put(KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, HouseDto> kafkaHouseDtoTemplate() {
        return new KafkaTemplate<>(houseProducerFactory());
    }
}
