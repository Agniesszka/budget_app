package budget_app.DAO;
import budget_app.model.Transaction;
import budget_app.model.User;
import java.util.List;
import java.util.Optional;

public interface ITransactionDAO {
    void save(Transaction transaction);
   List<Transaction> findByUser(User user);
    Optional<Transaction> findById(Long id);
    void delete(Transaction transaction);
    List<Transaction> findAll();

}
