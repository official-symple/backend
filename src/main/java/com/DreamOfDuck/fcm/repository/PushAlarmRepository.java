package com.DreamOfDuck.fcm.repository;

import com.DreamOfDuck.fcm.entity.PushAlarm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PushAlarmRepository extends JpaRepository<PushAlarm, Long> {
}
