package com.indigententerprises.timedlistmessagingclient.scheduling;

import com.indigententerprises.messagingartifacts.AppraisalRequest;

import com.indigententerprises.services.ElementAlreadyFoundException;
import com.indigententerprises.services.TimedListService;

import com.indigententerprises.timedlistmessagingclient.entities.Appraisal;
import com.indigententerprises.timedlistmessagingclient.entities.Product;
import com.indigententerprises.timedlistmessagingclient.messagetransmission.RequestInitiator;
import com.indigententerprises.timedlistmessagingclient.repositories.AppraisalRepository;
import com.indigententerprises.timedlistmessagingclient.repositories.ProductRepository;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
public class AppraisalJob implements Job {

    private ProductRepository productRepository;
    private AppraisalRepository appraisalRepository;
    private RequestInitiator requestInitiator;
    private TimedListService<Appraisal> timedListService;

    @Autowired
    public void setProductRepository(final ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Autowired
    public void setAppraisalRepository(final AppraisalRepository appraisalRepository) {
        this.appraisalRepository = appraisalRepository;
    }

    @Autowired
    public void setRequestInitiator(final RequestInitiator requestInitiator) {
        this.requestInitiator = requestInitiator;
    }

    @Autowired
    public void setTimedListService(final TimedListService<Appraisal> timedListService) {
        this.timedListService = timedListService;
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        final List<Product> products = productRepository.findAll();
        final Random random = new Random();
        int nextRandom = random.nextInt();

        if (nextRandom < 0) {
            nextRandom = (-1) * nextRandom;
        }

        final int index = nextRandom % products.size();
        final Product randomlyChosenProduct = products.get(index);
        final AppraisalRequest appraisalRequest = new AppraisalRequest();
        appraisalRequest.setItemName(randomlyChosenProduct.getName());
        final Appraisal appraisal = new Appraisal();
        appraisal.setProduct(randomlyChosenProduct);
        appraisal.setValid(true);
        final String messageId = requestInitiator.sendMessage(appraisalRequest);

        // TODO: there is a subtle race condition here. we seek to use a JMS provided identifier
        // TODO:   as a correlation id. unfortunately, we cannot obtain this identifier until
        // TODO:   after the message is sent. only then can we persist the appraisal record.
        // TODO: were the response to arrive before appraisal record were persisted, then the
        // TODO:   listener would not see the record and it would be therefore unable to
        // TODO:   update it.
        // TODO: nothing prevents me from using my own correlation identifier and setting a
        // TODO:   message property to its value.
        appraisal.setCorrelationId(messageId);
        appraisalRepository.save(appraisal);

        try {
            timedListService.add(appraisal, 50L);
        } catch (ElementAlreadyFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
