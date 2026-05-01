# AGENTS.md - 订单服务项目（Java + Spring Boot + Jersey）

> 本文件是 AI 开发助手的唯一入口。所有架构规范以本文件为准。

## 项目模块架构（严格分层）

```
data-module          # 最底层：实体、枚举、常量（无任何业务依赖）
    ↑
dao-module           # DAO接口 + 实现（依赖 data-module）
    ↑
service-module       # Service接口 + 实现（依赖 dao-module + data-module）
    ↑
api-module           # Jersey资源 + DTO（依赖 service-module + data-module）
```

## Maven依赖方向（硬性规则）

```xml
<!-- api-module/pom.xml -->
<dependency>
    <groupId>com.example</groupId>
    <artifactId>service-module</artifactId>
</dependency>
<dependency>
    <groupId>com.example</groupId>
    <artifactId>data-module</artifactId>
</dependency>

<!-- service-module/pom.xml -->
<dependency>
    <groupId>com.example</groupId>
    <artifactId>dao-module</artifactId>
</dependency>
<dependency>
    <groupId>com.example</groupId>
    <artifactId>data-module</artifactId>
</dependency>

<!-- dao-module/pom.xml -->
<dependency>
    <groupId>com.example</groupId>
    <artifactId>data-module</artifactId>
</dependency>

<!-- data-module/pom.xml -->
<!-- 无任何内部依赖，只有 JDK + 第三方库 -->
```

## 禁止的依赖方向

- ❌ api-module 不能依赖 dao-module（必须通过 service）
- ❌ service-module 不能依赖 api-module
- ❌ dao-module 不能依赖 service-module
- ❌ data-module 不能依赖任何其他 module

## 接口解耦规范

### Service层
```java
// service-module/interfaces/OrderService.java
public interface OrderService {
    OrderDTO getOrderById(Long id);
    OrderDTO createOrder(CreateOrderRequest request);
}

// service-module/impl/OrderServiceImpl.java
@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderDAO orderDAO;  // 依赖接口，不依赖实现

    @Override
    public OrderDTO getOrderById(Long id) {
        OrderEntity entity = orderDAO.findById(id);
        return convertToDTO(entity);
    }
}
```

### DAO层
```java
// dao-module/interfaces/OrderDAO.java
public interface OrderDAO {
    OrderEntity findById(Long id);
    OrderEntity save(OrderEntity entity);
}

// dao-module/impl/OrderDAOImpl.java
@Repository
public class OrderDAOImpl implements OrderDAO {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public OrderEntity findById(Long id) {
        // 实现
    }
}
```

## DTO规范

### Request DTO
```java
// api-module/dto/request/CreateOrderRequest.java
public class CreateOrderRequest {
    @NotNull(message = "userId不能为空")
    private Long userId;

    @NotBlank(message = "productCode不能为空")
    private String productCode;

    @Min(value = 1, message = "数量至少为1")
    private Integer quantity;

    // getters/setters
}
```

### Response DTO
```java
// api-module/dto/response/OrderResponse.java
public class OrderResponse {
    private Long orderId;
    private String orderNumber;
    private OrderStatus status;
    private BigDecimal totalAmount;
    private LocalDateTime createTime;

    // getters/setters
}
```

### 转换规则
- **Entity（data-module）↔ DTO（api-module）**：在 Service 层完成转换
- **禁止**：Controller 直接返回 Entity
- **禁止**：DTO 包含业务逻辑

## Jersey资源规范

```java
// api-module/resources/OrderResource.java
@Path("/api/orders")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Component
public class OrderResource {

    @Autowired
    private OrderService orderService;  // 依赖接口

    @GET
    @Path("/{id}")
    public Response getOrder(@PathParam("id") Long id) {
        OrderDTO dto = orderService.getOrderById(id);
        OrderResponse response = OrderResponse.fromDTO(dto);
        return Response.ok(response).build();
    }

    @POST
    public Response createOrder(CreateOrderRequest request) {
        OrderDTO dto = orderService.createOrder(request);
        OrderResponse response = OrderResponse.fromDTO(dto);
        return Response.status(Response.Status.CREATED)
                      .entity(response)
                      .build();
    }
}
```

## 命名约定

| 层级 | 接口命名 | 实现命名 | 包路径 |
|------|---------|---------|--------|
| Controller | - | `XxxResource` | `com.example.api.resources` |
| Service | `XxxService` | `XxxServiceImpl` | `com.example.service.interfaces/impl` |
| DAO | `XxxDAO` | `XxxDAOImpl` | `com.example.dao.interfaces/impl` |
| DTO (Request) | `XxxRequest` | - | `com.example.api.dto.request` |
| DTO (Response) | `XxxResponse` | - | `com.example.api.dto.response` |
| Entity | `XxxEntity` | - | `com.example.data.entity` |

## 验证流程（Agent 执行顺序）

**在创建或修改任何 Java 文件前，必须先运行：**

```bash
# 验证操作是否合法
python3 scripts/validate.py --action "create" --file "api-module/src/main/java/..."

# 或运行完整检查
mvn clean compile
python3 scripts/validate.py
```

## 常用命令

```bash
# 开发前必跑验证
python3 scripts/validate.py

# 单独检查依赖方向
bash scripts/lint-deps.sh

# 检查接口规范
bash scripts/lint-interface.sh

# 检查DTO规范
bash scripts/lint-dto.sh

# Maven构建
mvn clean compile
mvn test
```

## 已知坏模式（禁止生成）

### ❌ 错误示例1：Controller直接调用DAO
```java
// 错误：api-module直接依赖dao-module
@Path("/orders")
public class OrderResource {
    @Autowired
    private OrderDAO orderDAO;  // ❌ 禁止！
}
```

### ❌ 错误示例2：Service依赖具体实现类
```java
// 错误：依赖实现类而非接口
@Service
public class OrderServiceImpl {
    @Autowired
    private OrderDAOImpl orderDAO;  // ❌ 应该依赖 OrderDAO 接口
}
```

### ❌ 错误示例3：Controller返回Entity
```java
// 错误：直接返回Entity
@GET
public OrderEntity getOrder() {  // ❌ 应该返回 OrderResponse
    return orderService.getOrder();
}
```

### ❌ 错误示例4：Entity包含业务逻辑
```java
// 错误：Entity中有业务方法
@Entity
@Table(name = "orders")
public class OrderEntity {
    public BigDecimal calculateTotal() {  // ❌ 业务逻辑应该在Service
        // ...
    }
}
```

### ❌ 错误示例5：循环依赖
```xml
<!-- 错误：service-module依赖api-module -->
<dependency>
    <groupId>com.example</groupId>
    <artifactId>api-module</artifactId>  <!-- ❌ 禁止 -->
</dependency>
```

## 经验教训（持续更新）

- 2026-04-01: 发现 Agent 在 Service 层直接使用 OrderDAOImpl 类 → 已补充 lint-interface.sh 规则
- 2026-04-05: 发现 Controller 返回 Entity 暴露敏感字段 → 已强制使用 DTO
- 2026-04-10: 发现 data-module 引入了 Spring 依赖 → 已强制 data-module 保持纯净