package splitter.database;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import splitter.sharedexpenses.Transaction;

@Repository
public interface TransactionRepository extends CrudRepository<Transaction, Long> {
}
