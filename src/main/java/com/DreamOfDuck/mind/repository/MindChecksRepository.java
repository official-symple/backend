package com.DreamOfDuck.mind.repository;

import com.DreamOfDuck.mind.entity.MindChecks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MindChecksRepository extends JpaRepository<MindChecks, Long> {
}
