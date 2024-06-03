package com.indigententerprises.timedlistmessagingclient.configuration;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import com.indigententerprises.timedlistmessagingclient.messagetransmission.ResponseListener;
import com.indigententerprises.timedlistmessagingclient.repositories.AppraisalRepository;
import jakarta.jms.Queue;
import jakarta.jms.QueueConnectionFactory;

import javax.naming.InitialContext;
import javax.naming.Context;
import javax.naming.NamingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageType;

@Configuration
public class MessagingConfiguration {

    @Autowired
    private AppraisalRepository appraisalRepository;

    @Bean
    public InitialContext initialContext() throws NamingException {
        return new InitialContext();
    }

    @Bean
    public QueueConnectionFactory requesterConnectionFactory() throws NamingException {
        final Context context = initialContext();
        return (QueueConnectionFactory) context.lookup(
                "cn=requesterConnectionFactory,ou=JMSConnectionFactories,ou=Resources,dc=deliciouscottage,dc=com"
        );
    }

    @Bean
    public QueueConnectionFactory replierConnectionFactory() throws NamingException {
        final Context context = initialContext();
        return (QueueConnectionFactory) context.lookup(
                "cn=replierConnectionFactory,ou=JMSConnectionFactories,ou=Resources,dc=deliciouscottage,dc=com"
        );
    }

    @Bean
    public Queue requesterQueue() throws NamingException {
        final Context context = initialContext();
        return (Queue) context.lookup(
                "cn=requesterQueue,ou=JMSDestinations,ou=Resources,dc=deliciouscottage,dc=com"
        );
    }

    @Bean
    public Queue replierQueue() throws NamingException {
        final Context context = initialContext();
        return (Queue) context.lookup(
                "cn=replierQueue,ou=JMSDestinations,ou=Resources,dc=deliciouscottage,dc=com"
        );
    }

    @Bean
    public ResponseListener messageListener() {
        return new ResponseListener(appraisalRepository, messageConverter());
    }

    @Bean
    public DefaultMessageListenerContainer messageListenerContainer() throws NamingException {
        final DefaultMessageListenerContainer result = new DefaultMessageListenerContainer();
        result.setDestination(requesterQueue());
        result.setConnectionFactory(requesterConnectionFactory());
        result.setMessageConverter(messageConverter());
        result.setMessageListener(messageListener());
        return result;
    }

    @Bean
    public MessageConverter messageConverter() {
        final XmlMapper xmlMapper = new XmlMapper();
        final MappingJackson2MessageConverter result = new MappingJackson2MessageConverter();
        result.setTargetType(MessageType.TEXT);
        result.setTypeIdPropertyName("_type");
        result.setObjectMapper(xmlMapper);
        return result;
    }

    @Bean
    public JmsTemplate jmsTemplate() throws NamingException {
        JmsTemplate result = new JmsTemplate(replierConnectionFactory());
        result.setMessageConverter(messageConverter());
        result.setDefaultDestination(replierQueue());
        return result;
    }
}
