package tw.teddysoft.aiscrum.product.entity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
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

    public ProductGoal(ProductGoalId id, String title, String description) {
        this.id = Objects.requireNonNull(id, "ProductGoalId cannot be null");
        this.title = Objects.requireNonNull(title, "title cannot be null");
        this.description = description;
        this.metrics = new ArrayList<>();
        this.definedAt = Instant.now();
        this.revisedAt = this.definedAt;
        this.state = ProductGoalState.PLANNED;
    }

    ProductGoal(ProductGoalId id, String title, String description,
                List<GoalMetric> metrics, Instant definedAt, Instant revisedAt, ProductGoalState state) {
        this.id = Objects.requireNonNull(id, "ProductGoalId cannot be null");
        this.title = title;
        this.description = description;
        this.metrics = new ArrayList<>(Objects.requireNonNull(metrics, "metrics cannot be null"));
        this.definedAt = Objects.requireNonNull(definedAt, "definedAt cannot be null");
        this.revisedAt = revisedAt;
        this.state = Objects.requireNonNull(state, "state cannot be null");
    }

    public ProductGoalId getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public List<GoalMetric> getMetrics() { return Collections.unmodifiableList(metrics); }
    public Instant getDefinedAt() { return definedAt; }
    public Instant getRevisedAt() { return revisedAt; }
    public ProductGoalState getState() { return state; }

    void updateTitle(String newTitle) {
        this.title = Objects.requireNonNull(newTitle, "title cannot be null");
    }

    void updateDescription(String newDescription) {
        this.description = newDescription;
    }

    void addMetric(GoalMetric metric) {
        this.metrics.add(Objects.requireNonNull(metric, "metric cannot be null"));
    }

    void updateRevisedAt(Instant revisedAt) {
        this.revisedAt = Objects.requireNonNull(revisedAt, "revisedAt cannot be null");
    }

    void changeState(ProductGoalState newState) {
        this.state = Objects.requireNonNull(newState, "state cannot be null");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductGoal that = (ProductGoal) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
