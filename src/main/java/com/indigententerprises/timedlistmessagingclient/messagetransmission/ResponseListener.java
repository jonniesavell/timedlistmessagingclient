package com.indigententerprises.timedlistmessagingclient.messagetransmission;

import com.indigententerprises.messagingartifacts.AppraisalResponse;
import com.indigententerprises.timedlistmessagingclient.entities.Appraisal;
import com.indigententerprises.timedlistmessagingclient.repositories.AppraisalRepository;

import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;

import org.springframework.jms.support.converter.MessageConverter;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Optional;

public class ResponseListener implements MessageListener {

    private final AppraisalRepository appraisalRepository;
    private final MessageConverter messageConverter;

    public ResponseListener(
            final AppraisalRepository appraisalRepository,
            final MessageConverter messageConverter
    ) {
        this.appraisalRepository = appraisalRepository;
        this.messageConverter = messageConverter;
    }

    @Override
    public void onMessage(final Message message) {
        try {
            final AppraisalResponse appraisalResponse = (AppraisalResponse) messageConverter.fromMessage(message);
            final String messageCorrelationId = message.getJMSCorrelationID();
            final Optional<Appraisal> optional = appraisalRepository.findByCorrelationId(messageCorrelationId);

            if (optional.isPresent()) {
                final Appraisal appraisal = optional.get();
                appraisal.setAmount(appraisalResponse.getValue());

                final ZonedDateTime zonedDateTime = ZonedDateTime.parse(appraisalResponse.getDateString());
                appraisal.setDate(Date.from(zonedDateTime.toInstant()));
                appraisalRepository.save(appraisal);

                // TODO: remove print statements
                System.out.println("saved it");
            } else {
                System.out.println("missed it");
            }
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }
}
