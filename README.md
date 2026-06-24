# 軟體架構期末報告

## 將 Read-only Entity Pattern 實作在 AI Coding 專案中

本次報告以 AI Coding 專案中的 `Product` Aggregate 為例，將 paper 中介紹的 **Read-only Entities pattern** 補充到 AI 生成 DDD Aggregate 與 Entity 的 skill 文件中。

- Aggregate：`Product`
- Child Entity：`ProductGoal`
- Demo Use Case：建立 Product 後，驗證 `ProductGoal` 對外暴露時是否能被直接修改

## Read-only Entity Pattern 簡介

Read-only Entity Pattern 用來解決 Aggregate 對外暴露內部 Entity 時可能破壞封裝的問題。

在 DDD 中，Aggregate Root 應該是修改 Aggregate 內部狀態的唯一入口。外部 client 不應該直接持有並修改 Aggregate 內部的 Entity。可是某些情境下，Aggregate 仍需要回傳內部 Entity 供外部讀取，例如：

```java
ProductGoal productGoal = product.getGoal();
```

如果 `getGoal()` 直接回傳內部真正的 mutable `ProductGoal`，外部就可以繞過 `Product` 直接修改它：

```java
productGoal.changeTitle("changed title");
```

這會破壞 Aggregate Root 維護 invariant 的責任。

Paper 中提出的解法是：Aggregate 若要回傳內部 Entity，應該回傳該 Entity 的 read-only 版本。這個 read-only Entity 保留原本的 domain type，但所有修改方法都會丟出例外，讓誤用可以立即被發現。

## 尚未實作 Read-only Entity Pattern

在尚未套用 pattern 前，`Product.getGoal()` 可能直接回傳內部的 `ProductGoal`：

[src](https://github.com/28cyc/readonly-entity/blob/before-readonly/src/main/java/tw/teddysoft/aiscrum/product/entity/Product.java)

```java
public ProductGoal getGoal() { return goal; }
```

外部可以繞過 `Product` Aggregate Root 直接修改 `ProductGoal`。

[Test](https://github.com/28cyc/readonly-entity/blob/before-readonly/src/test/java/tw/teddysoft/aiscrum/product/entity/ProductContractTest.java)

```java
ProductGoal productGoal = product.getGoal();

assertThat(productGoal.title()).isEqualTo("original title");
productGoal.changeTitle("changed title");
assertThat(productGoal.title()).isEqualTo("changed title");
```

## 實作 Read-only Entity Pattern 後

套用 pattern 後，`Product.getGoal()` 不再回傳內部真正的 `ProductGoal`，而是回傳 `ReadOnlyProductGoal`：

[src](https://github.com/28cyc/readonly-entity/blob/after-readonly/src/main/java/tw/teddysoft/aiscrum/product/entity/Product.java)

```java
public ProductGoal getGoal() {
    return goal == null ? null : new ReadOnlyProductGoal(goal);
}
```

外部無法直接修改 `ProductGoal`。

[Test](https://github.com/28cyc/readonly-entity/blob/after-readonly/src/test/java/tw/teddysoft/aiscrum/product/entity/ProductContractTest.java)

```java
ProductGoal productGoal = product.getGoal();

assertThat(productGoal).isInstanceOf(ReadOnlyProductGoal.class);
assertThat(productGoal.title()).isEqualTo("original title");

assertThatThrownBy(() -> productGoal.changeTitle("new title"))
        .isInstanceOf(UnsupportedOperationException.class)
        .hasMessage("ProductGoal is read-only");
```

這樣外部仍然可以讀取 `ProductGoal` 的資料，但不能直接修改它。

## 對 `aggregate.md` 的修改

### 1. 新增 Rule 13: Read-only Entity Exposure

在 `aggregate.md` 中新增強制規則，要求 Aggregate 若包含 child Entity，不可以直接對外回傳內部 mutable Entity。

規則包含：

- 單一 child Entity getter 必須回傳 `ReadOnly{Entity}`
- child Entity collection 必須回傳 unmodifiable collection
- collection 內每個 Entity 也必須轉成 `ReadOnly{Entity}`
- Entity mutation method 必須維持 package-private
- Entity mutation 只能由 Aggregate 的 `when(event)` 呼叫

### 2. 補上 verification checkpoint

讓 AI 生成 Aggregate 後檢查：

- public getter 是否直接洩漏 child Entity
- 是否出現 `return this.goal`
- 是否直接回傳 mutable collection
- child Entity getter 是否有包成 `ReadOnly{Entity}`

### 3. 新增 Step 9.6: Generate Read-only Entity Getters

在 generation template 中新增步驟，要求 AI 產生 Aggregate getter 時，必須套用 read-only entity pattern。

範例：

```java
public ProductGoal getGoal() {
    return goal == null ? null : new ReadOnlyProductGoal(goal);
}
```

collection 範例：

```java
public List<Task> getTasks() {
    return tasks.values().stream()
            .map(ReadOnlyTask::new)
            .collect(Collectors.toUnmodifiableList());
}
```

## 對 `entity.md` 的修改

### 1. Output 表格新增 `ReadOnly{ChildEntity}.java`

只要是 mutable child Entity，就必須同步產生對應的 read-only Entity class。

### 2. 新增 Rule 11: Generate Read-only Entity for Mutable Child Entity

規則要求：

- 每個 mutable child Entity 都要有 `ReadOnly{Entity}`
- `ReadOnly{Entity}` 與原 Entity 放在同一個 package
- `ReadOnly{Entity}` extends 原 Entity，保留 domain type
- query method 可以正常使用
- mutation method 必須 override 並丟出 `UnsupportedOperationException`
- collection getter 要回傳不可修改集合

### 3. 修改原本 child Entity collection 範例

原本範例直接回傳內部 Entity：

```java
return tasks.get(taskId);
```

修改後改成：

```java
Task task = tasks.get(taskId);
return task == null ? null : new ReadOnlyTask(task);
```

原本 collection 回傳：

```java
return List.copyOf(tasks.values());
```

修改後改成：

```java
return tasks.values().stream()
        .map(ReadOnlyTask::new)
        .collect(Collectors.toUnmodifiableList());
```

### 4. 補上 verification checkpoint

若 mutable class Entity 沒有產生 `ReadOnly{Entity}.java`，就視為生成未完成。

## 結論

本次修改不是只把 Read-only Entity Pattern 加入文件說明，而是將它補進 AI code generation skill 的規則、模板與驗證流程中。

修改後的生成流程會變成：

```text
entity.md
  產生 ChildEntity
  產生 ReadOnlyChildEntity

aggregate.md
  Aggregate 內部保存 mutable ChildEntity
  Aggregate 對外 getter 回傳 ReadOnlyChildEntity
  Aggregate command 與 when(event) 才能修改真正的 ChildEntity
```

這樣可以讓 AI 之後在生成 Aggregate 與 Child Entity 時，更穩定地遵守 Read-only Entity Pattern，避免直接暴露 Aggregate 內部 mutable Entity，進而保護 Aggregate 的封裝與一致性。
