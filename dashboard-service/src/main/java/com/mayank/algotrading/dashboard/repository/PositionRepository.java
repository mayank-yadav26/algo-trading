package com.mayank.algotrading.dashboard.repository;

import com.mayank.algotrading.common.model.Position;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PositionRepository extends JpaRepository<Position, Long> {
    List<Position> findByStrategyIdAndIsOpenTrue(Long strategyId);
    List<Position> findByIsOpenTrue();
}
