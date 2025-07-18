package com.DreamOfDuck.talk.repository;

import com.DreamOfDuck.Admin.SearchRequest;
import com.DreamOfDuck.talk.entity.Session;

import java.util.List;

public interface SessionRepositoryCustom {
    List<Session> searchSessions(SearchRequest request);
}
