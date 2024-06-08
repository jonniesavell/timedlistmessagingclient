package com.indigententerprises.timedlistmessagingclient.configuration;

import com.indigententerprises.components.TimedListComponent;
import com.indigententerprises.services.TimedListAdminService;
import com.indigententerprises.services.TimedListService;

import com.indigententerprises.timedlistmessagingclient.callback.MessagingTimeoutCallback;
import com.indigententerprises.timedlistmessagingclient.entities.Appraisal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TimedListConfiguration {

    private MessagingTimeoutCallback messagingTimeoutCallback;

    @Autowired
    public void setMessagingTimeoutCallback(final MessagingTimeoutCallback messagingTimeoutCallback) {
        this.messagingTimeoutCallback = messagingTimeoutCallback;
    }

    @Bean
    public TimedListComponent<Appraisal> timedListComponent() {
        final TimedListComponent<Appraisal> result = new TimedListComponent<>(messagingTimeoutCallback);
        result.init();
        return result;
    }

    /////////////////////
    // public services //
    /////////////////////
    @Bean
    public TimedListService<Appraisal> timedListService() {
        final TimedListComponent<Appraisal> component = timedListComponent();
        return component;
    }

    @Bean
    public TimedListAdminService<Appraisal> timedListAdminService() {
        final TimedListComponent<Appraisal> component = timedListComponent();
        return component;
    }
}
