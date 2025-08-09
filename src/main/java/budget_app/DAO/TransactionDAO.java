package budget_app.DAO;

import budget_app.model.Transaction;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import budget_app.model.User;
@Transactional
@Repository
public class TransactionDAO implements ITransactionDAO {

    private final SessionFactory sessionFactory;

    @Autowired
    public TransactionDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void save(Transaction transaction) {
        sessionFactory.getCurrentSession().save(transaction);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Transaction> findByUser(User user) {
        return sessionFactory.getCurrentSession()
                .createQuery("FROM Transaction t WHERE t.user.id = :userId", Transaction.class)
                .setParameter("userId", user.getId())
                .list();
    }
    @Override
    @Transactional(readOnly = true)
    public Optional<Transaction> findById(Long id) {
        return Optional.ofNullable(sessionFactory.getCurrentSession().get(Transaction.class, id));
    }
    @Override
    public void delete(Transaction transaction) {
        sessionFactory.getCurrentSession().delete(transaction);
    }
    @Override
    @Transactional(readOnly = true)
    public List<Transaction> findAll() {
        return sessionFactory.getCurrentSession()
                .createQuery("from Transaction", Transaction.class)
                .list();
    }

}
