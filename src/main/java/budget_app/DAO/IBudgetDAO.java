package budget_app.DAO;

import budget_app.model.Budget;

public interface IBudgetDAO {
    Budget getBudget();
    void saveOrUpdate(Budget budget);

}
