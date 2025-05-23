package com.DreamOfDuck.talk.repository;

import com.DreamOfDuck.account.entity.Member;
import com.DreamOfDuck.talk.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {
    List<Session> findByHost(Member host);
}
