package splitter.database;

import splitter.sharedexpenses.GroupOfPeople;
import splitter.sharedexpenses.Person;
import splitter.sharedexpenses.SplitterLogic;
import splitter.sharedexpenses.Transaction;


public class DB {
    private GroupRepository groupRepository;
    private PersonRepository personRepository;
    private TransactionRepository transactionRepository;
    private SplitterLogic splitterLogic;

    public DB(GroupRepository groupRepository, PersonRepository personRepository, TransactionRepository transactionRepository, SplitterLogic splitterLogic) {
        this.groupRepository = groupRepository;
        this.personRepository = personRepository;
        this.transactionRepository = transactionRepository;
        this.splitterLogic = splitterLogic;
    }

    public void loadDB() {
        loadGroups();
        loadTransactions();
    }

    public void loadGroups() {
        Iterable<GroupOfPeople> entities = groupRepository.findAll();

        for (GroupOfPeople group : entities) {
            splitterLogic.addGroup(group);
        }
    }

    public void loadTransactions() {
        Iterable<Transaction> entities = transactionRepository.findAll();

        for (Transaction t : entities) {
            splitterLogic.addTransaction(t);
        }
    }

    public void loadPeople() {
        Iterable<Person> entities = personRepository.findAll();

        for (Person p : entities) {

        }
    }

}
