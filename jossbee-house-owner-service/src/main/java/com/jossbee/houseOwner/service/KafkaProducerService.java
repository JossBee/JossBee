package com.jossbee.houseOwner.service;

import com.jossbee.houseOwner.dto.HouseDto;
import com.jossbee.houseOwner.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {

    @Value("${house_topic}")
    private String houseTopicName;

    private final KafkaTemplate<String, HouseDto> kafkaHouseDtoTemplate;

    public void publishHouseDtoToKafka(HouseDto houseDto, String updateType) {

        houseDto.setUpdateType(updateType);

        ProducerRecord<String, HouseDto> houseDtoProducerRecord = new ProducerRecord<>(houseTopicName, houseDto.getId(), houseDto);

        try {
            SendResult<String, HouseDto> sendResult = kafkaHouseDtoTemplate.send(houseDtoProducerRecord).get();
            log.info("Successfully sent house data to Kafka, data: {}", sendResult.getProducerRecord().value());
        } catch (Exception ex) {
            log.error("Failed to send house data to Kafka: {}", ex.getMessage(), ex);
            throw new ServiceException("Failed to publish house data to Kafka, error occurred. Error is: " + ex.getMessage());
        }

        houseDto.setUpdateType(null);
    }
}
