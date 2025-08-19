package com.DreamOfDuck.talk.repository;

import com.DreamOfDuck.admin.SearchRequest;
import com.DreamOfDuck.account.entity.Gender;
import com.DreamOfDuck.account.entity.QMember;
import com.DreamOfDuck.talk.entity.Cause;
import com.DreamOfDuck.talk.entity.QSession;
import com.DreamOfDuck.talk.entity.Session;
import com.DreamOfDuck.talk.entity.Talker;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.List;

@Repository
public class SessionRepositoryCustomImpl implements SessionRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    public SessionRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }
    @Override
    public List<Session> searchSessions(SearchRequest request) {
        QSession session = QSession.session;
        QMember member = QMember.member;

        LocalDate now = LocalDate.now();
        LocalDate maxBirthday = null;
        LocalDate minBirthday = null;
        if (request.getAge() != null) {
            int ageGroup = request.getAge();
            maxBirthday = now.minusYears(ageGroup);
            minBirthday = now.minusYears(ageGroup + 9).minusDays(1);
        }

        return queryFactory
                .selectFrom(session)
                .join(session.host, member)
                .where(
                        genderEq(request.getGender()),
                        nicknameLike(request.getNickname()),
                        blueScoreEq(request.getBlueScore()),
                        causeEq(request.getCause()),
                        duckTypeEq(request.getDuckType()),
                        birthdayBetween(minBirthday, maxBirthday),
                        QSession.session.lastEmotion.isNotNull(),
                        QSession.session.solutions.isNotEmpty(),
                        QSession.session.problem.isNotNull(),
                        QSession.session.mission.isNotNull(),
                        QSession.session.advice.isNotEmpty()
                )
                .fetch();
    }

    private BooleanExpression genderEq(String genderStr) {
        if (!StringUtils.hasText(genderStr)) return null;

        try {
            Gender genderEnum = Gender.valueOf(genderStr.toUpperCase()); // "female" → FEMALE
            return QMember.member.gender.eq(genderEnum);
        } catch (IllegalArgumentException e) {
            // 유효하지 않은 값이면 조건 무시
            return null;
        }
    }


    private BooleanExpression nicknameLike(String nickname) {
        if (!StringUtils.hasText(nickname)) return null;
        return QMember.member.nickname.eq(nickname);
    }

    private BooleanExpression blueScoreEq(String blueScore) {
        if (!StringUtils.hasText(blueScore)) return null;
        return QMember.member.totalStatus.eq(blueScore);
    }

    private BooleanExpression causeEq(String causeStr) {
        if (!StringUtils.hasText(causeStr)) return null;
        return QSession.session.cause.eq(Cause.fromText(causeStr));
    }

    private BooleanExpression duckTypeEq(String duckTypeStr) {
        if (!StringUtils.hasText(duckTypeStr)) return null;
        return QSession.session.duckType.eq(Talker.valueOf(duckTypeStr));
    }

    private BooleanExpression birthdayBetween(LocalDate minBirthday, LocalDate maxBirthday) {
        if (minBirthday == null || maxBirthday == null) return null;
        return QMember.member.birthday.between(minBirthday, maxBirthday);
    }
}
