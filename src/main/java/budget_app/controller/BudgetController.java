package budget_app.controller;

import jakarta.servlet.http.HttpSession;
import budget_app.model.Transaction;
import budget_app.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import budget_app.service.IBudgetService;

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

        List<String> categories = List.of("Jedzenie", "Transport", "Rozrywka", "Inne");
        model.addAttribute("categories", categories);
        model.addAttribute("transaction", new Transaction());

        List<Transaction> transactions = budgetService.getAllTransactionsByUser(user);
        model.addAttribute("transactions", transactions);
        model.addAttribute("balance", budgetService.getBalance());

        return "budget";
    }

    @PostMapping("/add")
    public String addTransaction(@ModelAttribute("transaction") Transaction transaction, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        transaction.setUser(user);
        budgetService.addTransaction(transaction);
        return "redirect:/budget";
    }

    @GetMapping("/filter")
    public String filterTransactions(@RequestParam(required = false) String type,
                                     @RequestParam(required = false) String category,
                                     Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        List<Transaction> filtered = budgetService.filterTransactions(type, category, user);
        model.addAttribute("transactions", filtered);
        model.addAttribute("balance", budgetService.calculateBalance(filtered));
        model.addAttribute("transaction", new Transaction());
        model.addAttribute("categories", List.of("Jedzenie", "Transport", "Rozrywka", "Inne"));

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
        Map<String, Double> report = budgetService.getMonthlyReport(user);
        model.addAttribute("report", report);
        return "report";
    }


}
