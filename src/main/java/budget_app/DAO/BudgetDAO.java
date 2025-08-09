package budget_app.DAO;

import budget_app.model.Budget;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class BudgetDAO implements IBudgetDAO {

    private final SessionFactory sessionFactory;

    @Autowired
    public BudgetDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    @Transactional(readOnly = true)
    public Budget getBudget() {
        List<Budget> budgets = sessionFactory.getCurrentSession()
                .createQuery("from Budget", Budget.class)
                .list();
        if (budgets.isEmpty()) {
            return new Budget();
        }
        return budgets.get(0);
    }

    @Override
    @Transactional
    public void saveOrUpdate(Budget budget) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.save(budget);
            tx.commit();
        }
    }
}