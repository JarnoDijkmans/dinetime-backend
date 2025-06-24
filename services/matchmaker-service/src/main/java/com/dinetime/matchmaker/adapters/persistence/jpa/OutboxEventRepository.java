package com.dinetime.matchmaker.adapters.persistence.jpa;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import com.dinetime.matchmaker.adapters.persistence.jpa.entity.OutboxEventEntity;

public interface OutboxEventRepository extends JpaRepository<OutboxEventEntity, Long> {
    List<OutboxEventEntity> findByPublishedFalse();
}
