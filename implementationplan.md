# 外卖订单系统实现计划

## 项目概述
基于Java + Maven + Spring Boot + Jersey的多模块外卖订单后端系统，遵循严格的分层架构和依赖方向规则。

## 当前状态分析
项目已创建基本结构，包含：
1. **Spring Boot基础项目**（food-ordering-backend）- 使用Spring Initializr生成
   - Spring Boot 3.5.0
   - Java 17
   - 依赖：Spring Web, Jersey, Spring Data JPA, H2 Database, Lombok
   - 已成功编译通过
2. **多模块结构**：4个模块目录：data-module, dao-module, service-module, api-module
3. **架构规范文档**（AGENTS.md）
4. **示例文档**（deepseek-harnessengineering-java-example.md）
5. **实现计划**（implementationplan.md）

## 实现阶段

### 阶段1：基础模块结构完善
1. **data-module** - 实体类定义
   - OrderEntity（订单实体）
   - UserEntity（用户实体）
   - FoodItemEntity（菜品实体）
   - OrderStatus枚举
   - PaymentStatus枚举

2. **dao-module** - 数据访问层
   - mongodb, username: harness, password: harness, db : test_db
   - OrderDAO接口及实现
   - UserDAO接口及实现
   - FoodItemDAO接口及实现
   - 使用JdbcTemplate或JPA实现

3. **service-module** - 业务逻辑层
   - OrderService接口及实现
   - UserService接口及实现
   - FoodItemService接口及实现
   - 业务逻辑验证和转换

4. **api-module** - 控制器层
   - OrderResource（Jersey资源）
   - UserResource（Jersey资源）
   - FoodItemResource（Jersey资源）
   - DTO类定义（Request/Response）

### 阶段2：核心功能实现
1. **订单管理功能**
   - 创建订单
   - 查询订单详情
   - 更新订单状态
   - 取消订单
   - 订单列表查询

2. **用户管理功能**
   - 用户注册
   - 用户登录
   - 用户信息查询
   - 用户地址管理

3. **菜品管理功能**
   - 菜品列表查询
   - 菜品详情查询
   - 菜品分类查询

### 阶段3：高级功能
1. **支付集成**
   - 支付状态更新
   - 支付回调处理

2. **库存管理**
   - 菜品库存检查
   - 库存扣减

3. **订单状态流转**
   - 状态机实现
   - 状态变更通知

### 阶段4：测试与验证
1. **单元测试**
   - Service层测试
   - DAO层测试
   - API层测试

2. **集成测试**
   - API端点测试
   - 数据库集成测试

3. **架构验证**
   - 依赖方向检查
   - 接口规范检查
   - DTO规范检查

## 技术栈详细配置

### 依赖管理
1. **Spring Boot 3.2.4**
2. **Jersey 3.x**（JAX-RS实现）
3. **Spring JDBC** 或 **Spring Data JPA**
4. **H2 Database**（开发环境）
5. **MySQL**（生产环境）
6. **Lombok**（代码简化）
7. **Spring Boot Test**（测试）

### 模块依赖关系
严格按照AGENTS.md规定的依赖方向：
- api-module → service-module + data-module
- service-module → dao-module + data-module
- dao-module → data-module
- data-module → 无内部依赖

## 代码规范
1. **接口分离原则**：每层定义接口，实现类依赖接口
2. **DTO模式**：API层使用DTO，不与实体类直接交互
3. **异常处理**：统一的异常处理机制
4. **日志记录**：适当的日志级别和内容
5. **输入验证**：请求参数验证

## 部署配置
1. **application.yml**配置
2. **数据库连接池**配置
3. **Jersey资源扫描**配置
4. **跨域配置**（CORS）
5. **健康检查端点**

## 风险与缓解
1. **依赖方向违规**：通过脚本检查确保合规
2. **循环依赖**：模块化设计避免循环
3. **性能问题**：数据库查询优化，索引设计
4. **安全性**：输入验证，SQL注入防护

## 验收标准
1. 所有模块编译通过
2. 依赖方向检查通过
3. 核心API功能测试通过
4. 单元测试覆盖率>80%
5. 代码符合架构规范

## 下一步行动
1. 完善各模块的pom.xml依赖配置
2. 创建实体类（data-module）
3. 实现DAO接口和实现类
4. 实现Service接口和业务逻辑
5. 实现Jersey资源和DTO
6. 编写测试用例
7. 运行验证脚本检查架构合规性