package com.ZorvynFinanceDataAssignemt.FinanceDataProcessing.Repository;

import com.ZorvynFinanceDataAssignemt.FinanceDataProcessing.Entity.TrackBalances;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TrackBalanceRepository extends JpaRepository<TrackBalances, Long> {

    // Make sure this starts with SELECT, not SCL!
    Optional<TrackBalances> findByUser_Id(Long id);
}