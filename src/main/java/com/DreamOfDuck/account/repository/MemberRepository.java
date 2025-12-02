package com.DreamOfDuck.account.repository;

import com.DreamOfDuck.account.entity.Language;
import com.DreamOfDuck.account.entity.Member;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.scheduling.support.SimpleTriggerContext;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);
    List<Member> findByLanguage(Language language);
    Boolean existsByNickname(String nickname);
    @Query("SELECT DISTINCT m FROM Member m LEFT JOIN FETCH m.mindCheckTimes")
    List<Member> findAllWithMindCheckTimes();
    @Query("SELECT DISTINCT m.location FROM Member m WHERE m.location IS NOT NULL")
    List<String> findDistinctLocations();

    // 2. [아침 알림] 해당 지역 + 해당 요일 + 해당 시간에 알림 설정한 유저 조회
    @Query("SELECT m FROM Member m " +
            "JOIN m.mindCheckTimes t " +
            "WHERE (m.location = :zone OR (:zone = 'Asia/Seoul' AND m.location IS NULL)) " +
            "AND t.dayOfWeek = :dayOfWeek " +
            "AND t.dayTime = :targetTime ")
    List<Member> findMorningTargets(@Param("zone") String zone,
                                    @Param("dayOfWeek") DayOfWeek dayOfWeek,
                                    @Param("targetTime") LocalTime targetTime);

    // 3. [밤 알림]
    @Query("SELECT m FROM Member m " +
            "JOIN m.mindCheckTimes t " +
            "WHERE (m.location = :zone OR (:zone = 'Asia/Seoul' AND m.location IS NULL)) " +
            "AND t.dayOfWeek = :dayOfWeek " +
            "AND t.nightTime = :targetTime ")
    List<Member> findNightTargets(@Param("zone") String zone,
                                  @Param("dayOfWeek") DayOfWeek dayOfWeek,
                                  @Param("targetTime") LocalTime targetTime);

    // 4. [마감 임박/미완료 알림]
    // 조건: 알림 시간 일치 + 오늘 아직 마음 체크를 안 한 사람 (SubQuery 사용)
    @Query("SELECT m FROM Member m " +
            "JOIN m.mindCheckTimes t " +
            "WHERE (m.location = :zone OR (:zone = 'Asia/Seoul' AND m.location IS NULL)) " +
            "AND t.dayOfWeek = :dayOfWeek " +
            "AND t.dayTime = :targetTime " +
            "AND NOT EXISTS (SELECT mc FROM MindChecks mc WHERE mc.host = m AND mc.date = :today)")
    List<Member> findNotCheckedTargets(@Param("zone") String zone,
                                       @Param("dayOfWeek") DayOfWeek dayOfWeek,
                                       @Param("targetTime") LocalTime targetTime,
                                       @Param("today") LocalDate today);
}
