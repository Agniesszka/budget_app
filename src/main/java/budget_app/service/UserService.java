package budget_app.service;

import budget_app.DAO.UserRepository;
import budget_app.model.User;
import budget_app.util.HashUtil;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService implements IUserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<User> login(String username, String rawPassword) {
        String hashedPassword = HashUtil.hashPassword(rawPassword);
        return userRepository.findByUsernameAndPassword(username, hashedPassword);
    }

    @Override
    public void register(String username, String password) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Nazwa użytkownika jest już zajęta");
        }

        String hashedPassword = HashUtil.hashPassword(password);
        User user = new User(null, username, hashedPassword);
        userRepository.save(user);
    }
}