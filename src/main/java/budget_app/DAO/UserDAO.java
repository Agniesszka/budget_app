package budget_app.DAO;

import budget_app.model.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public class UserDAO implements IUserDAO{
    private final SessionFactory sessionFactory;

    @Autowired
    public UserDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }


    private Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByUsernameAndPassword(String username, String password) {
        User user = sessionFactory.getCurrentSession()
                .createQuery("FROM User WHERE username = :username AND password = :password", User.class)
                .setParameter("username", username)
                .setParameter("password", password)
                .uniqueResult();
        return Optional.ofNullable(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {
        User user = sessionFactory.getCurrentSession()
                .createQuery("FROM User WHERE username = :username", User.class)
                .setParameter("username", username)
                .uniqueResult();
        return Optional.ofNullable(user);
    }

    @Override
    public void save(User user) {
        sessionFactory.getCurrentSession().save(user);
    }
}
