package tw.teddysoft.aiscrum.product.usecase.port.out;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.Version;
import tw.teddysoft.ezddd.usecase.port.inout.domainevent.DomainEventData;
import tw.teddysoft.ezddd.usecase.port.out.repository.impl.outbox.OutboxData;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "product")
public class ProductData implements OutboxData<String> {

    @Transient
    private List<DomainEventData> domainEventDatas;

    @Transient
    private String streamName;

    @Id
    @Column(name = "id")
    private String productId;

    @Column(name = "productName", nullable = false)
    private String productName;

    @Column(name = "state", nullable = false)
    private String state;

    @Column(name = "note")
    private String note;

    @Column(name = "extension")
    private String extension;

    @Column(name = "goalId")
    private String goalId;

    @Column(name = "goalTitle")
    private String goalTitle;

    @Column(name = "goalDescription")
    private String goalDescription;

    @Column(name = "goalState")
    private String goalState;

    @Column(name = "goalDefinedAt")
    private Instant goalDefinedAt;

    @Column(name = "goalRevisedAt")
    private Instant goalRevisedAt;

    @Column(name = "goalMetricsJson", columnDefinition = "TEXT")
    private String goalMetricsJson;

    @Column(name = "isDeleted", nullable = false)
    private boolean isDeleted;

    @Version
    @Column(columnDefinition = "bigint DEFAULT 0", nullable = false)
    private long version;

    public ProductData() {
        this(0L);
    }

    public ProductData(long version) {
        this.version = version;
        this.domainEventDatas = new ArrayList<>();
        this.isDeleted = false;
    }

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public String getExtension() { return extension; }
    public void setExtension(String extension) { this.extension = extension; }

    public String getGoalId() { return goalId; }
    public void setGoalId(String goalId) { this.goalId = goalId; }

    public String getGoalTitle() { return goalTitle; }
    public void setGoalTitle(String goalTitle) { this.goalTitle = goalTitle; }

    public String getGoalDescription() { return goalDescription; }
    public void setGoalDescription(String goalDescription) { this.goalDescription = goalDescription; }

    public String getGoalState() { return goalState; }
    public void setGoalState(String goalState) { this.goalState = goalState; }

    public Instant getGoalDefinedAt() { return goalDefinedAt; }
    public void setGoalDefinedAt(Instant goalDefinedAt) { this.goalDefinedAt = goalDefinedAt; }

    public Instant getGoalRevisedAt() { return goalRevisedAt; }
    public void setGoalRevisedAt(Instant goalRevisedAt) { this.goalRevisedAt = goalRevisedAt; }

    public String getGoalMetricsJson() { return goalMetricsJson; }
    public void setGoalMetricsJson(String goalMetricsJson) { this.goalMetricsJson = goalMetricsJson; }

    public boolean isDeleted() { return isDeleted; }
    public void setDeleted(boolean deleted) { isDeleted = deleted; }

    @Override
    @Transient
    public String getId() { return productId; }

    @Override
    @Transient
    public void setId(String id) { this.productId = id; }

    @Override
    public long getVersion() { return version; }

    @Override
    public void setVersion(long version) { this.version = version; }

    @Override
    @Transient
    public List<DomainEventData> getDomainEventDatas() { return this.domainEventDatas; }

    @Override
    @Transient
    public void setDomainEventDatas(List<DomainEventData> domainEventDatas) {
        this.domainEventDatas = domainEventDatas;
    }

    @Override
    @Transient
    public String getStreamName() { return streamName; }

    @Override
    @Transient
    public void setStreamName(String streamName) { this.streamName = streamName; }
}
