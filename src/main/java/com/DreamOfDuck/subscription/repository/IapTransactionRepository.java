package com.DreamOfDuck.subscription.repository;

import com.DreamOfDuck.subscription.entity.IapTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IapTransactionRepository extends JpaRepository<IapTransaction, Long> {
}


