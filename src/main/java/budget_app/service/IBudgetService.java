package budget_app.service;

import budget_app.model.Budget;
import budget_app.model.Transaction;
import budget_app.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface IBudgetService {
    public void addTransaction(Transaction transaction);
    public double getBalance();
    public List<Transaction> getAllTransactionsByUser(User user);
    public List<Transaction> filterTransactions(Transaction.TransactionType type,
                                                Transaction.TransactionCategory category, LocalDate startDate,
                                                LocalDate endDate, User user);
    public double calculateBalance(List<Transaction> transactions);
    public Map<String, Double> getMonthlyReport(User user);
    public Budget getBudget();
    public void deleteTransactionByIdAndUser(Long id, User user);


}
