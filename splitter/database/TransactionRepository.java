package splitter.database;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import splitter.sharedexpenses.Transaction;

import java.time.LocalDate;

@Repository
public interface TransactionRepository extends CrudRepository<Transaction, Long> {

    void deleteByDateIsLessThan(LocalDate date);
    void deleteByDateIsLessThanEqual(LocalDate date);
}
