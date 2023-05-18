package splitter.database;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import splitter.sharedexpenses.GroupOfPeople;

@Repository
public interface GroupRepository extends CrudRepository<GroupOfPeople, Long> {
}
