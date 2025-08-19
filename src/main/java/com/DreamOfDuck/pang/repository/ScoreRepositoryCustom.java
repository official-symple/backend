package com.DreamOfDuck.pang.repository;

import com.DreamOfDuck.pang.entity.Score;

public interface ScoreRepositoryCustom {
    Long countByScoreGreaterThanEqual(Long curScore);
}
