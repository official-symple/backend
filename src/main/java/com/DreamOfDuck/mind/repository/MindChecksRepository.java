package com.DreamOfDuck.mind.repository;

import com.DreamOfDuck.account.entity.Member;
import com.DreamOfDuck.mind.entity.MindCheck;
import com.DreamOfDuck.mind.entity.MindChecks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MindChecksRepository extends JpaRepository<MindChecks, Long> {
    List<MindChecks> findByHostAndDateBetweenOrderByDateDesc( Member host, LocalDate start, LocalDate end );
    List<MindChecks> findByHostAndDate(Member host, LocalDate date);


}
