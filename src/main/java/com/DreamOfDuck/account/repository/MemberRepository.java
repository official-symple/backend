package com.DreamOfDuck.account.repository;

import com.DreamOfDuck.account.entity.Language;
import com.DreamOfDuck.account.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.scheduling.support.SimpleTriggerContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);
    List<Member> findByLanguage(Language language);
    Boolean existsByNickname(String nickname);
    @Query("SELECT DISTINCT m FROM Member m LEFT JOIN FETCH m.mindCheckTimes")
    List<Member> findAllWithMindCheckTimes();

}
