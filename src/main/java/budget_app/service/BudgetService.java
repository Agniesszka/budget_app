package budget_app.service;

import budget_app.DAO.BudgetRepository;
import budget_app.DAO.TransactionRepository;
import budget_app.model.Budget;
import budget_app.model.Transaction;
import budget_app.model.Transaction.TransactionType;
import budget_app.model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BudgetService implements IBudgetService {

    private final BudgetRepository budgetRepository;
    private final TransactionRepository transactionRepository;

    public BudgetService(BudgetRepository budgetRepository, TransactionRepository transactionRepository) {
        this.budgetRepository = budgetRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public void addTransaction(Transaction transaction) {
        Budget budget = getBudget();

        if (transaction.getType() == TransactionType.INCOME) {
            budget.setTotalIncome(budget.getTotalIncome() + transaction.getAmount());
        } else {
            budget.setTotalExpense(budget.getTotalExpense() + transaction.getAmount());
        }

        transactionRepository.save(transaction);
        budgetRepository.save(budget);
    }

    @Transactional(readOnly = true)
    public double getBalance() {
        return getBudget().getBalance();
    }

    @Transactional(readOnly = true)
    public List<Transaction> getAllTransactionsByUser(User user) {
        return transactionRepository.findByUser(user); }


    @Transactional(readOnly = true)
    public List<Transaction> filterTransactions(TransactionType type,
                                                Transaction.TransactionCategory category,
                                                LocalDate startDate,
                                                LocalDate endDate,
                                                User user) {
        return transactionRepository.findByUser(user).stream()
                .filter(t -> type == null || t.getType() == type)
                .filter(t -> category == null || t.getCategory() == category)
                .filter(t -> startDate == null || !t.getDate().isBefore(startDate))
                .filter(t -> endDate == null || !t.getDate().isAfter(endDate))
                .collect(Collectors.toList());
    }

    public double calculateBalance(List<Transaction> transactions) {
        return transactions.stream()
                .mapToDouble(t -> t.getType() == TransactionType.INCOME ? t.getAmount() : -t.getAmount())
                .sum();
    }

    @Transactional(readOnly = true)
    public Map<String, Double> getMonthlyReport(User user) {
        List<Transaction> transactions = transactionRepository.findByUser(user);
        if (transactions.isEmpty()) {
            return Map.of();
        }

        return transactions.stream()
                .collect(Collectors.groupingBy(
                        t -> t.getDate().getMonth().toString() + " " + t.getDate().getYear(),
                        Collectors.summingDouble(t -> t.getType() == TransactionType.INCOME ? t.getAmount() : -t.getAmount())
                ));
    }

    @Transactional(readOnly = true)
    public Budget getBudget() {
        List<Budget> budgets = budgetRepository.findAll();
        if (budgets.isEmpty()) {
            Budget newBudget = new Budget();
            budgetRepository.save(newBudget);
            return newBudget;
        } else {
            return budgets.get(0);
        }
    }

    @Transactional
    public void deleteTransactionByIdAndUser(Long id, User user) {
        transactionRepository.findById(id).ifPresent(transaction -> {
            if (transaction.getUser().getId().equals(user.getId())) {
                Budget budget = getBudget();
                if (transaction.getType() == TransactionType.INCOME) {
                    budget.setTotalIncome(budget.getTotalIncome() - transaction.getAmount());
                } else {
                    budget.setTotalExpense(budget.getTotalExpense() - transaction.getAmount());
                }
                transactionRepository.delete(transaction);
                budgetRepository.save(budget);
            }
        });
    }
}