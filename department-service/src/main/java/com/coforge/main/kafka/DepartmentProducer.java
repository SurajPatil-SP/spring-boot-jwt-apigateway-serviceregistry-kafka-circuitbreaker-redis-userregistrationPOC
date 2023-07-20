package com.coforge.main.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import com.coforge.main.dto.ApiResponseDto;
import com.coforge.main.dto.DepartmentDto;
import com.coforge.main.dto.UserDetailsEvent;

@Service
public class DepartmentProducer {

	private static final Logger log = LoggerFactory.getLogger(DepartmentProducer.class);

	private NewTopic topic;

	private KafkaTemplate<String, UserDetailsEvent> kafkaTemplate;

	public DepartmentProducer(NewTopic topic, KafkaTemplate<String, UserDetailsEvent> kafkaTemplate) {
		this.topic = topic;
		this.kafkaTemplate = kafkaTemplate;
	}

	public void sendDepartmentDetails(ApiResponseDto event) {
		log.info("ApiResponseDto event -> {}", event);

		// create message
		Message<ApiResponseDto> message = MessageBuilder
				.withPayload(event)
				.setHeader(KafkaHeaders.TOPIC, topic.name())
				.build();

		kafkaTemplate.send(message);
	}
}
