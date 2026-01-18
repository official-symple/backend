package com.DreamOfDuck.pang.repository;

import com.DreamOfDuck.account.entity.Member;
import com.DreamOfDuck.pang.entity.Score;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScoreRepository extends JpaRepository<Score, Long> {
    List<Score> findAllByOrderByScoreDesc();
    Optional<Score> findTopByHostOrderByScoreDesc(Member host);


}
