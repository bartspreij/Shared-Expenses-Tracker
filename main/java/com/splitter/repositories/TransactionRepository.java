package com.splitter.repositories;

import com.splitter.entities.Transaction;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface TransactionRepository extends CrudRepository<Transaction, Long> {

    void deleteByDateIsLessThan(LocalDate date);
    void deleteByDateIsLessThanEqual(LocalDate date);
}
