package tw.teddysoft.aiscrum.product.entity;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

public class ProductGoal {

    private final ProductGoalId id;
    private String title;
    private String description;
    private final List<GoalMetric> metrics;
    private final Instant definedAt;
    private Instant revisedAt;
    private ProductGoalState state;

    public ProductGoal(ProductGoalId id, String title, String description,
                       List<GoalMetric> metrics, Instant definedAt,
                       Instant revisedAt, ProductGoalState state) {
        this.id = Objects.requireNonNull(id, "ProductGoal id cannot be null");
        this.title = title;
        this.description = description;
        this.metrics = metrics;
        this.definedAt = definedAt;
        this.revisedAt = revisedAt;
        this.state = state;
    }

    public ProductGoalId id() { return id; }
    public String title() { return title; }
    public String description() { return description; }
    public List<GoalMetric> metrics() { return metrics; }
    public Instant definedAt() { return definedAt; }
    public Instant revisedAt() { return revisedAt; }
    public ProductGoalState state() { return state; }

    void changeTitle(String title) { this.title = title; }
    void changeDescription(String description) { this.description = description; }
    void changeRevisedAt(Instant revisedAt) { this.revisedAt = revisedAt; }
    void changeState(ProductGoalState state) { this.state = state; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductGoal other)) return false;
        return Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
