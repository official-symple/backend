package com.DreamOfDuck.record.repository;

import com.DreamOfDuck.account.entity.Member;
import com.DreamOfDuck.record.entity.Goal;
import com.DreamOfDuck.record.entity.Health;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GoalRepository extends JpaRepository<Goal, Long> {
    List<Goal> findByHost(Member host);
}
