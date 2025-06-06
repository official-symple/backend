package com.DreamOfDuck.record;

import com.DreamOfDuck.account.entity.Member;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface HealthRepository extends JpaRepository<Health, Long> {
    Optional<Health> findByDateAndHost(LocalDate date, Member host);
    List<Health> findByHost(Member host);
    @Query("SELECT h FROM Health h WHERE h.date BETWEEN :startDate AND :endDate AND h.host = :host")
    List<Health> findByDatePeriodAndHost(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, @Param("host") Member host);


}
