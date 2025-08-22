package budget_app.controller;

import jakarta.servlet.http.HttpSession;
import budget_app.model.Transaction;
import budget_app.model.User;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import budget_app.service.IBudgetService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("/budget")
public class BudgetController {
    private final IBudgetService budgetService;

    public BudgetController(IBudgetService budgetService){
        this.budgetService = budgetService;
        }

    @GetMapping
    public String showBudget(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        System.out.println("User from session: " + user.getUsername());


        model.addAttribute("transactionTypes", Transaction.TransactionType.values());
        model.addAttribute("categories", Transaction.TransactionCategory.values());
        model.addAttribute("transaction", new Transaction());

        List<Transaction> transactions = budgetService.getAllTransactionsByUser(user);
        model.addAttribute("transactions", transactions);
        model.addAttribute("balance", budgetService.calculateBalance(transactions));

        return "budget";
    }

    @PostMapping("/add")
    public String addTransaction(@ModelAttribute("transaction") Transaction transaction, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        transaction.setUser(user);

        if (transaction.getType() == null || transaction.getCategory() == null) {
            return "redirect:/budget?error";
        }

        budgetService.addTransaction(transaction);
        return "redirect:/budget";
    }

    @GetMapping("/filter")
    public String filterTransactions(
            @RequestParam(required = false) Transaction.TransactionType type,
            @RequestParam(required = false) Transaction.TransactionCategory category,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Model model,
            HttpSession session) {

        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }


        List<Transaction> filtered = budgetService.filterTransactions(type, category, startDate, endDate, user);

        model.addAttribute("transactionTypes", Transaction.TransactionType.values());
        model.addAttribute("categories", Transaction.TransactionCategory.values());
        model.addAttribute("transactions", filtered);
        model.addAttribute("balance", budgetService.calculateBalance(filtered));
        model.addAttribute("transaction", new Transaction());

        return "budget";
    }

    @PostMapping("/delete/{id}")
    public String deleteTransaction(@PathVariable Long id, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        budgetService.deleteTransactionByIdAndUser(id, user);
        return "redirect:/budget";
    }

    @GetMapping("/report")
    public String report(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        Map<String, Double> report = budgetService.getMonthlyReport(user);

        List<String> labels = new ArrayList<>(report.keySet());
        List<Double> values = new ArrayList<>(report.values());

        model.addAttribute("labels", labels);
        model.addAttribute("values", values);

        return "report";
    }


}
