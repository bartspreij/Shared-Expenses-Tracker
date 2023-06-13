package dev.goochem.splitter.repository;

import dev.goochem.splitter.entities.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
    List<Transaction> findAllByDateIsLessThanEqualOrderByDate(LocalDate date);
    void deleteByDateIsLessThan(LocalDate date);
    void deleteByDateIsLessThanEqual(LocalDate date);
}
