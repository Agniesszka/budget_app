package budget_app.DAO;
import budget_app.model.User;
import java.util.Optional;

public interface IUserDAO {
    Optional<User> findByUsernameAndPassword(String username, String password);
    Optional<User> findByUsername(String username);
    void save(User user);
}
