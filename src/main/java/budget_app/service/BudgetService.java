package budget_app.service;

import budget_app.DAO.IBudgetDAO;
import budget_app.DAO.ITransactionDAO;
import budget_app.model.Budget;
import budget_app.model.Transaction;
import budget_app.model.Transaction.TransactionType;
import budget_app.model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
@Service
public class BudgetService implements IBudgetService {

    private final IBudgetDAO budgetDAO;
    private final ITransactionDAO transactionDAO;

    public BudgetService(IBudgetDAO budgetDAO, ITransactionDAO transactionDAO) {
        this.budgetDAO = budgetDAO;
        this.transactionDAO = transactionDAO;
    }

    @Override
    public void addTransaction(Transaction transaction) {
        Budget budget = budgetDAO.getBudget();

        if (transaction.getType() == TransactionType.INCOME) {
            budget.setTotalIncome(budget.getTotalIncome() + transaction.getAmount());
        } else {
            budget.setTotalExpense(budget.getTotalExpense() + transaction.getAmount());
        }

        transactionDAO.save(transaction);
        budgetDAO.saveOrUpdate(budget);
    }

    @Override
    @Transactional(readOnly = true)
    public double getBalance() {
        Budget budget = getBudget();
        return budget.getBalance();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Transaction> getAllTransactionsByUser(User user) {
        return transactionDAO.findByUser(user);
    }
    @Override
    public List<Transaction> filterTransactions(String type, String category, User user) {
        List<Transaction> all = transactionDAO.findByUser(user);

        return all.stream()
                .filter(t -> (type == null || type.isEmpty() || t.getType().name().equalsIgnoreCase(type)))
                .filter(t -> (category == null || category.isEmpty() || t.getCategory().equalsIgnoreCase(category)))
                .collect(Collectors.toList());
    }

    @Override
    public double calculateBalance(List<Transaction> transactions) {
        return transactions.stream()
                .mapToDouble(t -> {
                    switch (t.getType()) {
                        case INCOME:
                            return t.getAmount();
                        case EXPENSE:
                            return -t.getAmount();
                        default:
                            return 0.0;
                    }
                })
                .sum();
    }

    @Override
    public Map<String, Double> getMonthlyReport(User user) {
        List<Transaction> transactions = transactionDAO.findByUser(user);
        if (transactions.isEmpty()) {
            return Map.of();
        }

        return transactions.stream()
                .collect(Collectors.groupingBy(
                        t -> t.getDate().getMonth().toString() + " " + t.getDate().getYear(),
                        Collectors.summingDouble(t -> {
                            switch (t.getType()) {
                                case INCOME:
                                    return t.getAmount();
                                case EXPENSE:
                                    return -t.getAmount();
                                default:
                                    return 0.0;
                            }
                        })
                ));
    }
    @Override
    @Transactional(readOnly = true)
    public Budget getBudget() {
        return budgetDAO.getBudget();
    }
    @Override
    @Transactional
    public void deleteTransactionByIdAndUser(Long id, User user) {
        Optional<Transaction> optionalTransaction = transactionDAO.findById(id);
        if (optionalTransaction.isPresent()) {
            Transaction transaction = optionalTransaction.get();
            if (transaction.getUser().getId().equals(user.getId())) {
                Budget budget = budgetDAO.getBudget();
                if (transaction.getType() == TransactionType.INCOME) {
                    budget.setTotalIncome(budget.getTotalIncome() - transaction.getAmount());
                } else {
                    budget.setTotalExpense(budget.getTotalExpense() - transaction.getAmount());
                }
                transactionDAO.delete(transaction);
                budgetDAO.saveOrUpdate(budget);
            }
        }
    }

}
