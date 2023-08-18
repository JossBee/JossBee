package com.jossbee.houseOwner.config;

import com.jossbee.houseOwner.dto.HouseDto;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    @Bean
    public ProducerFactory<String, HouseDto> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();

        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "pkc-41p56.asia-south1.gcp.confluent.cloud:9092");
        configProps.put("key.serializer", StringSerializer.class.getName());
        configProps.put("value.serializer", JsonSerializer.class.getName());
        configProps.put(SaslConfigs.SASL_MECHANISM, "PLAIN");
        configProps.put(SaslConfigs.SASL_JAAS_CONFIG, "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"EYJ4BVM3A6YUTRS6\" password=\"plEHr04UtmxdTD1ZgcwOr2SAr52cZAGzaeqUX/dPaWN+Qz/V9bP5DsGl4Y9IvzI4\";");
        configProps.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_SSL");
        configProps.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 45000);

        return new DefaultKafkaProducerFactory<>(configProps);
    }


    @Bean
    public KafkaTemplate<String, HouseDto> kafkaHouseDtoTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
