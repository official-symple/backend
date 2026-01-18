package com.DreamOfDuck.pang.repository;

import com.DreamOfDuck.pang.entity.QScore;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

@Repository
public class ScoreRepositoryCustomImpl implements ScoreRepositoryCustom {
    private final JPAQueryFactory queryFactory;
    public ScoreRepositoryCustomImpl(EntityManager em) {this.queryFactory = new JPAQueryFactory(em);}

    @Override
    public Long countByScoreGreaterThanEqual(Long curScore) {
        QScore scores = QScore.score1;

        return queryFactory
                .select(scores.id.count())
                .from(scores)
                .where(
                    scores.score.goe(curScore)
                ).fetchOne();
    }
    @Override
    public Long countDistinctHostByScoreGreaterThan(Long curScore) {
        QScore scores = QScore.score1;

        return queryFactory
                .select(scores.host.countDistinct())
                .from(scores)
                .where(
                        scores.score.gt(curScore)
                )
                .fetchOne();
    }
}
