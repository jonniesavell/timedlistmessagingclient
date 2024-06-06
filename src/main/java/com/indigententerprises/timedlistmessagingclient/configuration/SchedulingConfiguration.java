package com.indigententerprises.timedlistmessagingclient.configuration;

import com.indigententerprises.timedlistmessagingclient.scheduling.AppraisalJob;

import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SchedulingConfiguration {

    @Bean
    public JobDetail appraisalJobDetail() {
        return JobBuilder.newJob(AppraisalJob.class)
                .withIdentity("appraisal-job")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger appraisalJobTrigger() {
        final SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder.simpleSchedule()
                .withIntervalInSeconds(10)
                .repeatForever();

        return TriggerBuilder.newTrigger()
                .forJob(appraisalJobDetail())
                .withIdentity("appraisal-trigger")
                .withSchedule(scheduleBuilder)
                .build();
    }
}
