package com.indigententerprises.timedlistmessagingclient;

import com.indigententerprises.messagingartifacts.AppraisalRequest;
//import com.indigententerprises.messagingartifacts.AppraisalResponse;
import com.indigententerprises.timedlistmessagingclient.entities.Appraisal;
import com.indigententerprises.timedlistmessagingclient.entities.Product;
import com.indigententerprises.timedlistmessagingclient.messagetransmission.RequestInitiator;
import com.indigententerprises.timedlistmessagingclient.repositories.AppraisalRepository;
import com.indigententerprises.timedlistmessagingclient.repositories.ProductRepository;

import jakarta.jms.Message;
import jakarta.jms.Queue;
import jakarta.jms.QueueConnection;
import jakarta.jms.QueueConnectionFactory;
import jakarta.jms.QueueSender;
import jakarta.jms.QueueSession;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.support.converter.MessageConverter;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@SpringBootApplication
public class TimedlistMessagingClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(TimedlistMessagingClientApplication.class, args);
	}

	@Bean
	protected CommandLineRunner test(
			final RequestInitiator requestInitiator,
			final ProductRepository productRepository,
			final AppraisalRepository appraisalRepository,
			final QueueConnectionFactory replierConnectionFactory,
			final Queue requesterQueue,
			final MessageConverter messageConverter
	) {
		return args -> {
			final Optional<Product> optional = productRepository.findByName("BOSS frame/fork");

			if (optional.isPresent()) {
				final Product product = optional.get();
				final AppraisalRequest appraisalRequest = new AppraisalRequest();
				appraisalRequest.setItemName(product.getName());
				final String messageId = requestInitiator.sendMessage(appraisalRequest);
				final Appraisal appraisal = new Appraisal();
				appraisal.setProduct(product);
				appraisal.setCorrelationId(messageId);
				appraisalRepository.save(appraisal);

				/*
				final AppraisalResponse appraisalResponse = new AppraisalResponse();
				final ZonedDateTime now = ZonedDateTime.now(ZoneId.of("UTC"));
				final String dateString = now.format(DateTimeFormatter.ISO_DATE_TIME);
				appraisalResponse.setDateString(dateString);
				appraisalResponse.setValue(100000L);
				appraisalResponse.setItemName(product.getName());

				final QueueConnection queueConnection = replierConnectionFactory.createQueueConnection();

				try {
					final QueueSession queueSession = queueConnection.createQueueSession(false, QueueSession.AUTO_ACKNOWLEDGE);

					try {
						final QueueSender queueSender = queueSession.createSender(requesterQueue);

						try {
							final Message message = messageConverter.toMessage(appraisalResponse, queueSession);
							message.setJMSCorrelationID(messageId);
							queueSender.send(message);
						} finally {
							queueSender.close();
						}
					} finally {
						queueSession.close();
					}
				} finally {
					queueConnection.close();
				}
				*/
			}
		};
	}
}
