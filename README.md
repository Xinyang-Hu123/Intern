# 团队协同开发规范

> 技术栈需求：Java 17 + Spring Boot 3.x （暂定）

---

## 一、团队成员与分工(预配置，以实际项目为准)

| 成员  | 方向 | 负责模块（示例，按实际填写） |
|-----|---|---|
| 成员A | Java 后端 | 订单模块 |
| 成员B | Java 后端 | 用户模块、认证鉴权 |
| 成员C | 全栈 | 商户模块（前后端） |
| 成员D | 全栈 | 配送模块（前后端） |
| 成员E | Java 后端 / 全栈 | 支付模块 |

> 每人负责的模块写进这张表，避免撞车。新功能开始前先建 Issue 认领，没认领的模块不要动。

---

## 二、第一次使用前必做（只需一次）

### 1. 配置换行符

**Windows 执行：**
```bash
git config --global core.autocrlf true
```

**macOS 执行：**
```bash
git config --global core.autocrlf input
```

### 2. Clone 仓库

```bash
git clone https://github.com/Xinyang-Hu123/Intern.git
cd Intern-Delivery
git checkout dev
```

### 3. 验证本地环境

```bash
# macOS / Git Bash
./mvnw clean verify

# Windows CMD
mvnw clean verify
```

看到 `BUILD SUCCESS` 说明环境正常，可以开始开发。

---

## 三、分支结构

```
main          ← 只在阶段性发布时从 dev 合入，打版本 tag
│
dev           ← 团队集成主干，所有 feature 合并到这里
│
├── feature/xinyang-order-api       ← 个人功能分支
├── feature/memberB-user-auth
├── feature/memberC-merchant-page
└── ...
```

| 分支 | 用途 | 能否直接 push |
|---|---|---|
| `main` | 阶段性发布版本 | ❌ 禁止，只接受来自 dev 的 PR |
| `dev` | 团队集成主干 | ❌ 禁止，只接受 feature 的 PR |
| `feature/名字-功能` | 个人开发分支 | ✅ 自由 push |

**分支命名规则：** `feature/你的名字-功能模块`，例如：
- `feature/xinyang-order-api`
- `feature/memberB-user-auth`
- `feature/memberC-merchant-frontend`

---

## 四、完整开发流程

### Step 1：建 Issue 认领任务

> **写代码前必须先建 Issue，禁止直接开发。**

1. GitHub 仓库页面 → **Issues** → **New issue**
2. Title 写清楚要做什么：`实现用户查看历史订单列表`
3. 右侧 **Assignees** 勾选自己
4. 右侧 **Labels** 选类型：`feature` / `bug` / `docs`
5. 提交后记下编号，如 `#12`

### Step 2：签出自己的 feature 分支

```bash
# 确保 dev 是最新的
git checkout dev
git pull origin dev

# 新建自己的分支
git checkout -b feature/xinyang-order-api
```

### Step 3：开发 + 提交

```bash
git add .
git commit -m "feat(order): #12 实现历史订单查询接口"
```

**Commit 格式：** `类型(模块): #Issue编号 描述`

| 类型 | 场景 |
|---|---|
| `feat` | 新功能 |
| `fix` | 修 bug |
| `docs` | 文档、注释 |
| `test` | 测试代码 |
| `refactor` | 重构 |
| `style` | 格式调整，不影响逻辑 |

**禁止** 写 `update`、`修改`、`fix bug`、`test` 等无意义信息。

### Step 4：push 前本地预检

```bash
# macOS / Git Bash
./mvnw clean verify

# Windows CMD
mvnw clean verify
```

**必须看到 `BUILD SUCCESS` 才能 push，红了就本地修好再推。**

### Step 5：推送到远端

```bash
git push origin feature/xinyang-order-api
```

### Step 6：发 Pull Request

1. 打开 GitHub，页面顶部会出现黄色提示条，点击 **Compare & pull request**
2. 确认：**base 选 `dev`**，不是 `main`
3. Title 写功能名
4. Description 里写：
   ```
   ## 做了什么
   实现了用户查看历史订单列表的接口，支持分页和按时间筛选。
   
   ## 关联 Issue
   Closes #12
   ```
5. 右侧 **Reviewers** 选 **至少 1 名** 其他成员
6. 点击 **Create pull request**

### Step 7：CI 自动检查

PR 发出后，GitHub Actions 会自动在云端跑流水线，PR 页面底部可以看到进度。

- ✅ **绿勾** → CI 通过，进入人工 Review 阶段
- ❌ **红叉** → 编译或测试挂了，回本地修好重新 `git push`，CI 自动重跑

**红叉期间 PR 不能合并。**

### Step 8：Code Review

被选为 Reviewer 的人打开 PR → **Files changed**：

- 检查 SQL 里有没有大写字母
- 检查 Controller 里有没有写业务逻辑
- 检查有没有硬编码的路径或密码
- 对有问题的行点 **+** 号留 Comment
- 没问题就点右上角 **Review changes** → **Approve** → **Submit review**

**PR 提交人需要回复所有 Comment，修改后重新 push，Reviewer 确认后再 Approve。**

### Step 9：合并

CI ✅ + 至少 1 个 Approve → PR 提交人点合并。

**统一选 Squash and merge**，把开发过程中的零碎 commit 压成一条干净记录合入 `dev`。

合并后点 **Delete branch** 删掉已合并的 feature 分支。


---

## 五、发版流程

当 `dev` 上积累了若干个功能，准备交作业或演示时：

```bash
# 1. 确保 dev 是最新的
git checkout dev
git pull origin dev

# 2. 发 PR：dev → main（在 GitHub 网页操作）
#    Title 写：Release v1.0
#    同样需要 CI 通过 + 1 个 Approve

# 3. 合并后在本地打 tag
git checkout main
git pull origin main
git tag -a v1.0 -m "第一阶段功能完成"
git push origin v1.0
```

tag 会出现在 GitHub 仓库的 **Releases** 页面，方便回溯每个版本。

---

## 六、项目目录结构

```
Intern-Delivery/
├── .github/
│   └── workflows/
│       └── ci.yml                   # CI 配置，不要随意改动
├── src/
│   ├── main/java/com/example/intern/
│   │   ├── controller/              # 入参校验 + 路由分发，禁止写业务逻辑
│   │   ├── service/                 # 核心业务逻辑
│   │   ├── mapper/                  # MyBatis-Plus 数据库操作
│   │   ├── model/                   # Entity / DTO / VO
│   │   │   ├── entity/
│   │   │   ├── dto/
│   │   │   └── vo/
│   │   └── InternApplication.java
│   └── test/java/com/example/intern/ # 测试代码，包路径与业务代码 1:1 对应
├── .gitignore
└── pom.xml
```

**`.gitignore` 必须包含以下内容（Spring Initializr 默认已有）：**
```
.idea/
target/
*.class
*.log
```

**必须提交的文件：** `pom.xml`、`.gitignore`、`.github/workflows/ci.yml`、`mvnw`、`mvnw.cmd`

---

## 八、编码规范

### 数据库命名

表名、字段名全部小写加下划线，禁止大写字母。CI 跑在 Linux 上，大写表名会直接报错。

```sql
-- ✅ 正确
SELECT * FROM order_detail WHERE user_id = 1;

-- ❌ 错误
SELECT * FROM OrderDetail WHERE UserId = 1;
```

### 三种数据对象

禁止把数据库 Entity 直接返回给前端。

| 类型 | 职责 | 流转范围 |
|---|---|---|
| `Entity` | 和数据库表一一对应 | Mapper ↔ Service |
| `DTO` | 接收前端传来的请求数据 | Controller → Service |
| `VO` | 返回给前端的响应数据 | Service → Controller → 前端 |

### Controller 只做分发

```java
@PostMapping
public Result<OrderVO> placeOrder(@Validated @RequestBody OrderCreateDTO dto) {
    // Controller 里只调 Service，不写任何业务判断
    return Result.success(orderService.createOrder(dto));
}
```

### Service 写业务逻辑，多表写操作加事务

```java
@Override
@Transactional(rollbackFor = Exception.class)
public OrderVO createOrder(OrderCreateDTO dto) {
    OrderEntity entity = new OrderEntity();
    entity.setStatus("UNPAID");
    orderMapper.insert(entity);
    return new OrderVO(entity.getId(), "UNPAID");
}
```

---

## 九、测试规范

测试文件放在 `src/test/java/`，包路径与业务类完全对应。

**单元测试**（必写，不启动 Spring 容器，跑得快）：

```java
@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    @DisplayName("正常下单应返回 UNPAID 状态")
    void shouldCreateOrderSuccess() {
        OrderCreateDTO dto = new OrderCreateDTO(2026001L);
        OrderVO vo = orderService.createOrder(dto);
        assertThat(vo).isNotNull();
        assertThat(vo.getStatus()).isEqualTo("UNPAID");
    }
}
```

**集成测试**（按需写，启动完整 Spring 容器）：

```java
@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturn200WhenPlaceOrder() throws Exception {
        mockMvc.perform(post("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"userId\": 2026001}"))
               .andExpect(status().isOk());
    }
}
```

---

## 十、常用 Git 命令速查

### 查看当前状态

```bash
git status          # 看改了哪些文件
git branch          # 看当前在哪个分支（* 号标记当前）
git log --oneline   # 看提交历史
```

### 撤销操作

```bash
# 撤销最后一次 commit（代码保留，只撤销提交记录）
git reset --soft HEAD~1

# 撤销 add（把文件从暂存区移出来）
git restore --staged 文件名
```

### 同步最新代码

```bash
git checkout dev
git pull origin dev

# 把 dev 最新代码合入自己的 feature 分支（避免最后 PR 冲突太多）
git checkout feature/xinyang-order-api
git merge dev
```

### 临时切换分支

```bash
# 先把当前改动藏起来
git stash

# 去做其他事
git checkout dev

# 回来后恢复
git checkout feature/xinyang-order-api
git stash pop
```

### 解决合并冲突

```bash
# 冲突时 git 会标记文件，打开冲突文件找到这段：
# <<<<<<< HEAD
# 你的代码
# =======
# 别人的代码
# >>>>>>> dev

# 手动保留正确内容，删掉标记符号，然后：
git add .
git commit -m "fix: 解决与 dev 的合并冲突"
```

### 遇到问题不知道怎么办

**不要乱操作，先截图，发群里问。** 特别是涉及 `reset --hard`、`force push` 的操作，单独操作可能覆盖别人的代码。

---

## 十一、新成员入组检查清单

- [ ] 在分工表里填上自己的名字和负责模块
- [ ] 执行换行符配置命令
- [ ] Clone 仓库，切到 `dev` 分支
- [ ] 本地跑 `./mvnw clean verify` 看到 `BUILD SUCCESS`
- [ ] 在 GitHub 上建一个测试 Issue（Title 写"测试-你的名字"）
- [ ] 走完一次完整流程：建分支 → commit → push → 发 PR → 请人 Review → 合并
- [ ] 确认自己在仓库的 Settings → Collaborators 里有权限

