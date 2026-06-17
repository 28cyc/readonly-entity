package tw.teddysoft.aiscrum.product.entity;

import java.time.Instant;

public class ReadOnlyProductGoal extends ProductGoal {

    public ReadOnlyProductGoal(ProductGoal real) {
        super(real.getId(), real.getTitle(), real.getDescription(),
              real.getMetrics(), real.getDefinedAt(), real.getRevisedAt(), real.getState());
    }

    @Override
    void updateTitle(String newTitle) {
        throw new UnsupportedOperationException("ProductGoal is read-only");
    }

    @Override
    void updateDescription(String newDescription) {
        throw new UnsupportedOperationException("ProductGoal is read-only");
    }

    @Override
    void addMetric(GoalMetric metric) {
        throw new UnsupportedOperationException("ProductGoal is read-only");
    }

    @Override
    void updateRevisedAt(Instant revisedAt) {
        throw new UnsupportedOperationException("ProductGoal is read-only");
    }

    @Override
    void changeState(ProductGoalState newState) {
        throw new UnsupportedOperationException("ProductGoal is read-only");
    }
}
