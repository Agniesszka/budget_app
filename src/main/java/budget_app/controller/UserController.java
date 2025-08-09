package budget_app.controller;

import budget_app.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import budget_app.service.UserService;

import jakarta.servlet.http.HttpSession;
import java.util.Optional;

@Controller
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {

        Optional<User> user = userService.login(username, password);
        if (user.isPresent()) {
            session.setAttribute("user", user.get());
            return "redirect:/budget";
        }

        model.addAttribute("error", "Nieprawidłowe dane logowania");
        return "login";
    }

    @GetMapping("/register")
    public String showRegisterForm() {
        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String username,
                           @RequestParam String password,
                           Model model) {
        try {
            userService.register(username, password);
            model.addAttribute("message", "Rejestracja zakończona sukcesem, możesz się zalogować");
            return "login";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
    @GetMapping("/")
    public String rootRedirect(HttpSession session) {
        if (session.getAttribute("user") != null) {
            return "redirect:/budget";
        }
        return "redirect:/login";
    }

}
