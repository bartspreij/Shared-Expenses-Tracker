package com.bartspreij.splitter.repository;

import com.bartspreij.splitter.model.Transaction;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface TransactionRepository extends CrudRepository<Transaction, Long> {

    void deleteByDateIsLessThan(LocalDate date);
    void deleteByDateIsLessThanEqual(LocalDate date);
}
