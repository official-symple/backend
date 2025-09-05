package com.DreamOfDuck.mind.repository;

import com.DreamOfDuck.mind.entity.MindCheckTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MindCheckTimeRepository extends JpaRepository<MindCheckTime, Long> {
}
