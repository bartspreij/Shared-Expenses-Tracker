package splitter.database;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import splitter.sharedexpenses.Person;

@Repository
public interface PersonRepository extends CrudRepository<Person, Long> {

}
