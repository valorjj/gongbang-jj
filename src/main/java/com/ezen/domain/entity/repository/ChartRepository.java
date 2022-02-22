package com.ezen.domain.entity.repository;

import com.ezen.domain.entity.HistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChartRepository extends JpaRepository<HistoryEntity, Integer> {
}
