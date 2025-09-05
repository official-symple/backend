package com.DreamOfDuck.mind.repository;

import com.DreamOfDuck.mind.entity.MindCheck;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MindCheckRepository extends JpaRepository<MindCheck, Long> {
}
