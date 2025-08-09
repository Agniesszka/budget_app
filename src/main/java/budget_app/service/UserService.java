package budget_app.service;

import budget_app.model.User;

import java.util.Optional;
import budget_app.DAO.IUserDAO;
import org.springframework.beans.factory.annotation.Autowired;
import budget_app.util.HashUtil;
import org.springframework.stereotype.Service;

@Service
public class UserService implements IUserService {
    private final IUserDAO userDAO;

    @Autowired
    public UserService(IUserDAO userDAO) {
        this.userDAO = userDAO;
    }
    @Override
    public Optional<User> login(String username, String rawPassword) {
        String hashedPassword = HashUtil.hashPassword(rawPassword);
        return userDAO.findByUsernameAndPassword(username, hashedPassword);
    }
    @Override
    public void register(String username, String password) {
        if (userDAO.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Nazwa użytkownika jest już zajęta");
        }

        String hashedPassword = HashUtil.hashPassword(password);
        User user = new User(null, username, hashedPassword);
        userDAO.save(user);
    }
}
