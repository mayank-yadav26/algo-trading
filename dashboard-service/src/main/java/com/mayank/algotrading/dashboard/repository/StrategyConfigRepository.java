package com.mayank.algotrading.dashboard.repository;

import com.mayank.algotrading.common.model.StrategyConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StrategyConfigRepository extends JpaRepository<StrategyConfig, Long> {
    List<StrategyConfig> findByIsActiveTrue();
}
