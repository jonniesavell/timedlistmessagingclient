package com.indigententerprises.timedlistmessagingclient.messagetransmission;

import com.indigententerprises.messagingartifacts.AppraisalRequest;

import jakarta.jms.JMSException;
import jakarta.jms.Message;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessagePostProcessor;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicReference;

@Service
public class RequestInitiator {

    private final JmsTemplate jmsTemplate;

    public RequestInitiator(final JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    public String sendMessage(final AppraisalRequest appraisalRequest) {

        final AtomicReference<Message> msg = new AtomicReference<>();

        jmsTemplate.convertAndSend(appraisalRequest, new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws JMSException {

                msg.set(message);

                return message;
            }
        });

        try {
            return msg.get().getJMSMessageID();
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }
}
