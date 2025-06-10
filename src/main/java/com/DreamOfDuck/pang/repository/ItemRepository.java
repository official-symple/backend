package com.DreamOfDuck.pang.repository;

import com.DreamOfDuck.account.entity.Member;
import com.DreamOfDuck.pang.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    Optional<Item> findByHost(Member host);
}
