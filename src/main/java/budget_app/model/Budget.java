package budget_app.model;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "budgets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Budget {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private double totalIncome = 0.0;
    @Column(nullable = false)
    private double totalExpense = 0.0;

    public double getBalance() {
        return totalIncome - totalExpense;
    }
}

