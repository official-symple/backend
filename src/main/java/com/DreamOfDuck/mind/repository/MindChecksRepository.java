package com.DreamOfDuck.mind.repository;

import com.DreamOfDuck.account.entity.Member;
import com.DreamOfDuck.mind.entity.MindCheck;
import com.DreamOfDuck.mind.entity.MindChecks;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MindChecksRepository extends JpaRepository<MindChecks, Long> {
    @Query("SELECT mc FROM MindChecks mc " +
            "LEFT JOIN FETCH mc.dayMindCheck " +
            "LEFT JOIN FETCH mc.nightMindCheck " +
            "WHERE mc.host = :host AND mc.date BETWEEN :start AND :end " +
            "ORDER BY mc.date DESC")
    List<MindChecks> findByHostAndDateBetweenOrderByDateDesc(
            @Param("host") Member host,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end
    );

    @Query("SELECT mc FROM MindChecks mc " +
            "LEFT JOIN FETCH mc.dayMindCheck " +
            "LEFT JOIN FETCH mc.nightMindCheck " +
            "WHERE mc.host = :host AND mc.date = :date")
    List<MindChecks> findByHostAndDate(
            @Param("host") Member host,
            @Param("date") LocalDate date
    );
}
