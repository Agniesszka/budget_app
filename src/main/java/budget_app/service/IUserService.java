package budget_app.service;

import budget_app.model.User;

import java.util.Optional;

public interface IUserService {
    public Optional<User> login(String username, String rawPassword);
    public void register(String username, String password);
}
