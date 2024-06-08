package com.indigententerprises.timedlistmessagingclient.callback;

import com.indigententerprises.services.Callback;

import com.indigententerprises.timedlistmessagingclient.entities.Appraisal;
import com.indigententerprises.timedlistmessagingclient.repositories.AppraisalRepository;

import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MessagingTimeoutCallback implements Callback<Appraisal> {

    final AppraisalRepository appraisalRepository;

    public MessagingTimeoutCallback(final AppraisalRepository appraisalRepository) {
        this.appraisalRepository = appraisalRepository;
    }

    @Override
    public void timedOut(final Appraisal appraisal) {

        final Optional<Appraisal> existingAppraisalOptional = appraisalRepository.findById(appraisal.getId());

        if (existingAppraisalOptional.isPresent()) {
            final Appraisal existingAppraisal = existingAppraisalOptional.get();
            if (existingAppraisal.getAmount() == null) {
                // response didn't arrive yet => invalidate it
                existingAppraisal.setValid(false);
                appraisalRepository.save(existingAppraisal);
            }
        }
    }
}
