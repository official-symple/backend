package com.DreamOfDuck.talk.repository;

import com.DreamOfDuck.account.entity.Member;
import com.DreamOfDuck.talk.entity.Interview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InterviewRepository extends JpaRepository<Interview, Long> {
    Optional<Interview> findByHost(Member host);
}
