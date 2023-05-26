package com.splitter.service;

import com.splitter.entities.Transaction;
import com.splitter.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;

@Service
public class TransactionService {
    private final TransactionRepository repository;

    @Autowired
    public TransactionService(TransactionRepository repository) {
        this.repository = repository;
    }

    public List<Transaction> getTransactionsBeforeAGivenDate(LocalDate date) {
        return repository.findAllByDateIsLessThanEqualOrderByDate(date);
    }

    @Transactional
    public void addTransaction(Transaction transaction) {
        repository.save(transaction);
    }

    @Transactional
    public void deleteByDateIsLessThan(LocalDate date) {
        repository.deleteByDateIsLessThan(date);
    }

    @Transactional
    public void deleteByDateIsLessThanEqual(LocalDate date) {
        repository.deleteByDateIsLessThanEqual(date);
    }
}