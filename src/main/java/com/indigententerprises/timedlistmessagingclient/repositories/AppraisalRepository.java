package com.indigententerprises.timedlistmessagingclient.repositories;

import com.indigententerprises.timedlistmessagingclient.entities.Appraisal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AppraisalRepository extends JpaRepository<Appraisal, Long> {
    Optional<Appraisal> findByCorrelationId(String correlationId);
}
