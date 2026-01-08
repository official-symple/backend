package com.DreamOfDuck.account.repository;

import com.DreamOfDuck.account.entity.Attendance;
import com.DreamOfDuck.account.entity.Language;
import com.DreamOfDuck.account.entity.Member;
import com.DreamOfDuck.mind.entity.MindChecks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
    List<Member> findByLocation(String location);
    List<Member> findByLanguage(Language language);
    Boolean existsByNickname(String nickname);
    @Query("SELECT DISTINCT m FROM Member m LEFT JOIN FETCH m.mindCheckTimes")
    List<Member> findAllWithMindCheckTimes();
    @Query("SELECT DISTINCT m.location FROM Member m WHERE m.location IS NOT NULL")
    List<String> findDistinctLocations();
    @Query("SELECT a FROM Member m JOIN m.attendedDates a WHERE m = :member ORDER BY a.date DESC")
    List<Attendance> findRecentAttendanceByMember(@Param("member") Member member);
    @Query("SELECT a FROM Member m JOIN m.attendedDates a WHERE m = :member AND a.date = :date")
    Optional<Attendance> findAttendanceByMemberAndDate(
            @Param("member") Member member,
            @Param("date") LocalDate date
    );

    @Query("SELECT DISTINCT m FROM Member m " +
            "LEFT JOIN m.mindCheckTimes t " + // LEFT JOIN으로 변경 (설정 없는 유저 포함)
            "WHERE (m.location = :zone OR (:zone = 'Asia/Seoul' AND m.location IS NULL)) " +
            "AND (" +
            "   (t.dayOfWeek = :dayOfWeek AND t.dayTime = :targetTime) " + // 1. 설정이 있고 시간이 맞는 경우
            "   OR " +
            "   (t IS NULL AND :targetTime = :defaultTime) " + // 2. 설정이 없고(NULL) 현재 시간이 기본 시간인 경우
            ")")
    List<Member> findMorningTargets(@Param("zone") String zone,
                                    @Param("dayOfWeek") DayOfWeek dayOfWeek,
                                    @Param("targetTime") LocalTime targetTime,
                                    @Param("defaultTime") LocalTime defaultTime);

    @Query("SELECT DISTINCT m FROM Member m " +
            "LEFT JOIN m.mindCheckTimes t " +
            "WHERE (m.location = :zone OR (:zone = 'Asia/Seoul' AND m.location IS NULL)) " +
            "AND (" +
            "   (t.dayOfWeek = :dayOfWeek AND t.nightTime = :targetTime) " +
            "   OR " +
            "   (t IS NULL AND :targetTime = :defaultTime) " +
            ")")
    List<Member> findNightTargets(@Param("zone") String zone,
                                  @Param("dayOfWeek") DayOfWeek dayOfWeek,
                                  @Param("targetTime") LocalTime targetTime,
                                  @Param("defaultTime") LocalTime defaultTime);


    @Query("SELECT DISTINCT m FROM Member m " +
            "LEFT JOIN m.mindCheckTimes t " +
            "WHERE (m.location = :zone OR (:zone = 'Asia/Seoul' AND m.location IS NULL)) " +
            "AND (" +
            "   (t.dayOfWeek = :dayOfWeek AND t.dayTime = :targetTime) " +
            "   OR " +
            "   (t IS NULL AND :targetTime = :defaultTime) " +
            ") " +
            "AND NOT EXISTS (SELECT mc FROM MindChecks mc WHERE mc.host = m AND mc.date = :today)")
    List<Member> findNotCheckedTargets(@Param("zone") String zone,
                                       @Param("dayOfWeek") DayOfWeek dayOfWeek,
                                       @Param("targetTime") LocalTime targetTime,
                                       @Param("defaultTime") LocalTime defaultTime,
                                       @Param("today") LocalDate today);
}
