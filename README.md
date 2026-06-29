# 团队协同开发规范

> 技术栈：Java 17（团队统一版本，CI 使用 JDK 17）+ Spring Boot 2.7.3 + MyBatis + MySQL 8.0 + Redis + Vue 2 + 微信小程序

---

## 一、团队成员与分工（6人实训团队）

| 角色 | 人数 | 主要职责 | GitHub 责任 |
|---|---:|---|---|
| 产品经理 / 项目经理（PM） | 1 | 项目第一负责人，负责需求范围、原型确认、任务拆分、进度跟踪、阶段提交物、答辩材料和最终验收 | 维护 Milestone、Issue、项目看板和 Release；验收需求是否完成 |
| 系统分析师 / 技术负责人 | 1 | 负责技术方案、接口约定、数据库设计、核心流程设计、疑难问题攻坚、概要设计和详细设计文档 | 审查接口、数据模型、关键 PR 和架构变更 |
| 后端工程师 A | 1 | 负责管理端基础业务模块，例如员工、分类、菜品、套餐、文件上传等 | 维护后端功能 Issue，补充接口说明和单元测试 |
| 后端工程师 B | 1 | 负责订单、用户端、购物车、支付、WebSocket 通知、智能体集成等 | 维护跨端接口 Issue，保障接口联调和异常处理 |
| 前端 / 小程序工程师 | 1 | 负责 Vue 管理端页面、小程序页面、前后端联调、交互问题修复 | 维护前端 Issue，提交页面截图或联调说明 |
| 测试工程师 / 配置管理员 | 1 | 负责测试用例、功能验收、缺陷跟踪、代码规范检查、CI 状态、分支和版本管理 | 管理 bug Issue、检查 PR 模板、确认 CI 和测试结果 |

> 每个成员的真实姓名、GitHub 账号和负责模块由 PM 在项目看板中维护。新功能开始前必须先建 Issue 认领，没认领的模块不要直接开发。

### 每日协作节奏

- 每天开始前由 PM 确认当天目标、风险和阻塞项。
- 每天下班前所有成员更新自己负责的 Issue：完成内容、未完成原因、明日计划。
- 系统分析师负责当天技术风险同步；测试/配置管理员负责同步缺陷、CI 和分支状态。
- 涉及需求范围、接口字段、数据库表结构、验收口径变化时，必须先在 Issue 中确认，再改代码。

---

## 二、4周实训里程碑与交付物

本项目按课程实训周期推进：4 周，共 20 天，每周 5 天。团队协作以 GitHub Milestone 管理。

| 阶段 | 时间 | 目标 | 必交内容 | 负责人 |
|---|---|---|---|---|
| 项目启动 | 第 1 天 | 完成组队、角色分工、仓库权限、开发环境、项目范围确认 | 团队名称、团队宣言、团队 Logo、团队合影、成员分工表、项目看板 | PM |
| 需求与设计 | 第 2-5 天 | 完成需求分析、原型、数据库设计、系统设计 | 需求分析文档、原型、数据库设计、接口草案、用例图/类图/时序图/流程图 | PM、系统分析师 |
| 环境与核心功能 | 第 6-10 天 | 完成 Git/Maven 环境、核心功能和中期检查材料 | 可运行代码、核心接口、核心页面、中期检查材料 | 系统分析师、开发、测试 |
| 业务功能开发 | 第 11-18 天 | 完成主要业务模块、前后端联调、智能体相关能力接入 | 功能代码、测试用例、缺陷清单、接口说明更新 | 全体成员 |
| 整合与走查 | 第 19 天 | 完成代码走查、模块整合、综合测试、提交物冻结 | 源码、测试记录、部署说明、最终缺陷清单 | 测试/配置管理员 |
| 答辩评审 | 第 20 天 | 完成项目验收和团队答辩 | 答辩 PPT、概要设计文档、详细设计文档、项目源代码、演示环境 | PM、全体成员 |

### 考核对齐

| 考核项 | 权重 | 团队执行要求 |
|---|---:|---|
| 日常出勤和平时表现 | 20% | 每天更新 Issue 和看板，会议结论记录在 GitHub |
| 中期文档检查 | 20% | 第 10 天前提交原型、需求分析、详细设计和核心代码 |
| 团队项目提交评审 | 30% | 页面简洁美观，功能完整，主要流程无阻断 bug |
| 组员答辩 | 30% | 每个成员能说明自己的任务、代码、测试和问题处理过程 |

---

## 三、第一次使用前必做（只需一次）

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
cd Intern
git checkout dev
```

### 3. 验证本地环境

```bash
# 确认 Java 版本为 17
java -version

# 后端
cd sky-take-out
mvn clean verify
```

看到 `BUILD SUCCESS` 说明环境正常，可以开始开发。

---

## 四、分支结构

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

## 五、完整开发流程

### Step 1：PM 建 Milestone 和 Issue

> **写代码前必须先建 Issue，禁止直接开发。**

1. PM 按 4 周实训节奏创建 Milestone：`第1周-需求设计`、`第2周-核心功能`、`第3周-业务开发`、`第4周-整合答辩`。
2. PM 或模块负责人创建 Issue，Title 写清楚要做什么：`实现用户查看历史订单列表`。
3. Issue 必须写明：业务目标、验收标准、负责人、预计完成时间、影响模块。
4. 右侧 **Assignees** 勾选负责人，**Milestone** 选择所属阶段，**Labels** 选择类型。
5. 常用 Labels：`requirement` / `design` / `frontend` / `backend` / `miniprogram` / `test` / `bug` / `docs` / `ai-agent`。
6. 提交后在 commit 和 PR 中关联编号，如 `#12`。

### Step 2：需求和接口先确认

涉及下列内容时，必须先由 PM 和系统分析师在 Issue 中确认：

- 新增或修改页面流程、按钮、字段、校验规则。
- 新增或修改后端接口、DTO、VO、数据库表结构。
- 修改订单、支付、登录、权限、WebSocket、智能体集成等跨模块流程。
- 删除功能或改变原有验收口径。

### Step 3：签出自己的 feature 分支

```bash
# 确保 dev 是最新的
git checkout dev
git pull origin dev

# 新建自己的分支
git checkout -b feature/xinyang-order-api
```

### Step 4：开发 + 提交

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

### Step 5：push 前本地预检

```bash
# 在 sky-take-out/ 目录下执行
cd sky-take-out
mvn clean verify
```

**必须看到 `BUILD SUCCESS` 才能 push，红了就本地修好再推。**

前端或小程序改动还需要补充对应检查：

```bash
# 在 project-rjwm-admin-vue-ts/ 目录下执行
npm run lint
npm run test:unit
```

如果前端老依赖导致本地检查无法运行，PR 里必须写明原因，并提供页面截图或联调记录。

### Step 6：推送到远端

```bash
git push origin feature/xinyang-order-api
```

### Step 7：发 Pull Request

1. 打开 GitHub，页面顶部会出现黄色提示条，点击 **Compare & pull request**
2. 确认：**base 选 `dev`**，不是 `main`
3. Title 写功能名
4. Description 里写：
   ```
   ## 做了什么
   实现了用户查看历史订单列表的接口，支持分页和按时间筛选。
   
   ## 关联 Issue
   Closes #12

   ## 自测结果
   - [ ] mvn clean verify 通过
   - [ ] 前后端联调通过
   - [ ] 已补充或更新测试用例
   - [ ] 已更新相关文档/截图
   ```
5. 右侧 **Reviewers** 至少选择 1 名技术 Reviewer；跨模块 PR 需要 PM 或测试/配置管理员参与验收
6. 点击 **Create pull request**

### Step 8：CI 自动检查

PR 发出后，GitHub Actions 会自动在云端跑流水线，PR 页面底部可以看到进度。

- ✅ **绿勾** → CI 通过，进入人工 Review 阶段
- ❌ **红叉** → 编译或测试挂了，回本地修好重新 `git push`，CI 自动重跑

**红叉期间 PR 不能合并。**

### Step 9：Code Review

被选为 Reviewer 的人打开 PR → **Files changed**：

- 检查 SQL 里有没有大写字母
- 检查 Controller 里有没有写业务逻辑
- 检查有没有硬编码的路径或密码
- 检查 Issue 验收标准是否全部满足
- 检查是否影响中期/最终提交物
- 对有问题的行点 **+** 号留 Comment
- 没问题就点右上角 **Review changes** → **Approve** → **Submit review**

**PR 提交人需要回复所有 Comment，修改后重新 push，Reviewer 确认后再 Approve。**

### Step 10：合并

CI ✅ + 至少 1 个 Approve + Issue 验收项完成 → PR 提交人点合并。

**统一选 Squash and merge**，把开发过程中的零碎 commit 压成一条干净记录合入 `dev`。

合并后点 **Delete branch** 删掉已合并的 feature 分支。


---

## 六、发版流程

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

## 七、项目目录结构

```
Intern/
├── .github/
│   └── workflows/
│       └── ci.yml                       # CI 配置，不要随意改动
├── sky-take-out/                        # 后端 Java 项目（Maven 多模块）
│   ├── pom.xml                          # 父 POM，版本管理
│   ├── sky-common/                      # 公共模块：工具类、异常、常量
│   ├── sky-pojo/                        # 数据模块：Entity / DTO / VO
│   └── sky-server/                      # 主模块：启动类 + 所有业务代码
│       └── src/main/java/com/sky/
│           ├── controller/
│           │   ├── admin/               # 管理端接口
│           │   └── user/                # 用户端接口
│           ├── service/                 # 业务逻辑
│           ├── mapper/                  # MyBatis 数据库操作
│           ├── task/                    # 定时任务
│           ├── websocket/               # WebSocket 实时推送
│           └── config/                  # Spring 配置类
├── project-rjwm-admin-vue-ts/           # 管理端前端（Vue 2 + TypeScript）
│   └── src/
│       ├── views/                       # 页面组件
│       ├── api/                         # 接口封装
│       └── router.ts                    # 路由配置
├── mp-weixin/                           # 微信小程序
│   └── pages/
├── nginx-1.20.2/                        # Nginx 服务器（含配置和前端静态文件）
│   ├── conf/nginx.conf                  # Nginx 配置
│   └── html/                            # 前端打包产物
├── .gitignore
└── README.md
```

**`.gitignore` 已包含：** `.idea/`、`target/`、`*.iml`、`*.class`、`.DS_Store`

**必须提交的文件：** 不要提交 `node_modules/`、`target/`、`.idea/`，其他均可提交。

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

## 十一、项目部署指南

> 项目由 4 部分组成：Java 后端 + Vue 管理端 + 微信小程序 + Nginx 反代。

### 环境要求

| 组件 | 版本 | 说明 |
|------|------|------|
| JDK | 11+ | 后端使用 Spring Boot 2.7.3 |
| MySQL | 8.0 | 数据库名 `sky_take_out` |
| Redis | 6+ | 缓存 |
| Nginx | 1.20+ | 反向代理 + 静态文件 |
| Maven | 3.6+ | 后端构建 |
| Node.js | 14+ | 前端构建 |

### 1. 修改配置文件

部署前必须修改以下文件的配置项（当前均为占位符）：

**后端 `sky-take-out/sky-server/src/main/resources/application-dev.yml`：**

```yaml
sky:
  datasource:
    host: 你的MySQL地址
    port: 3306
    database: sky_take_out
    username: 你的用户名
    password: 你的密码
  redis:
    host: 你的Redis地址
    port: 6379
    password: 你的Redis密码
    database: 10
  alioss:
    endpoint: 你的OSS区域
    access-key-id: 你的AccessKey
    access-key-secret: 你的Secret
    bucket-name: 你的Bucket名
  wechat:
    appid: 你的小程序AppID
    secret: 你的小程序Secret
    # ... 其余微信支付配置按需填写
```

**后端 `sky-take-out/sky-server/src/main/resources/application.yml`：**

```yaml
sky:
  jwt:
    admin-secret-key: 你的管理端JWT密钥    # 不要用默认的 itcast
    user-secret-key: 你的用户端JWT密钥      # 不要用默认的 itheima
  shop:
    address: 你的店铺真实地址
  baidu:
    ak: 你的百度地图AK
```

**管理端前端 `project-rjwm-admin-vue-ts/.env.production`：**

```env
VUE_APP_URL = 'http://你的服务器IP:8080/admin'
VUE_APP_SOCKET_URL = 'ws://你的服务器IP:8080/ws/'
```

### 2. 后端打包部署

```bash
# 在 sky-take-out/ 目录下
mvn clean package -DskipTests

# 将 jar 包上传至服务器
scp sky-server/target/sky-server-1.0-SNAPSHOT.jar user@服务器IP:/opt/app/

# 服务器上启动（使用 dev 配置）
nohup java -jar sky-server-1.0-SNAPSHOT.jar --spring.profiles.active=dev > app.log 2>&1 &
```

### 3. 管理端前端部署

```bash
# 在 project-rjwm-admin-vue-ts/ 目录下
npm install
npm run build

# 将 dist/ 目录上传到服务器的 Nginx html 目录
scp -r dist/* user@服务器IP:/path/to/nginx/html/sky/
```

### 4. Nginx 配置

修改 `nginx-1.20.2/conf/nginx.conf`：

```nginx
upstream webservers {
    server 127.0.0.1:8080 weight=90;    # 改后端地址和端口
}

server {
    listen       80;
    server_name  localhost;               # 改真实域名

    location / {
        root   html/sky;                  # 前端静态文件路径
        index  index.html;
    }

    location /api/ {
        proxy_pass http://localhost:8080/admin/;   # 改后端地址
    }

    location /user/ {
        proxy_pass http://webservers/user/;
    }

    location /ws/ {
        proxy_pass http://webservers/ws/;          # WebSocket 代理
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
    }
}
```

启动 Nginx：

```bash
cd nginx-1.20.2
./nginx -c conf/nginx.conf
```

### 5. 微信小程序部署

1. 微信开发者工具打开 `mp-weixin/` 目录
2. 修改 `project.config.json` 中的 `appid` 为你的小程序 AppID
3. 在工具中点击「上传」即可发布

### 6. 验证部署

- 访问 `http://服务器IP` → 应看到管理端登录页
- 管理端登录后 → 菜品/订单管理正常
- 小程序端 → 浏览菜品、下单
- WebSocket 提醒 → 管理端收到新订单语音通知

### 7. 常见问题

| 问题 | 原因 | 解决 |
|------|------|------|
| 后端启动报数据库连接失败 | application-dev.yml 未改 | 修改 MySQL 连接信息 |
| 前端页面空白 | VUE_APP_URL 指向错误 | 修改 `.env.production` |
| 小程序请求 404 | vendor.js 中 baseUrl 未改 | 用 Uni-app 重建项目 |
| WebSocket 连接失败 | Nginx 未配置 Upgrade 头 | 参考上方 ws 配置 |
| 图片上传失败 | OSS 凭证未配置 | 修改 alioss 配置 |

---

## 十二、成果物提交标准

课程实训的过程材料和最终材料都要进入 GitHub 管理，避免只在群文件里流转。

| 类型 | 内容 | 存放建议 | 负责人 |
|---|---|---|---|
| 项目启动材料 | 团队名称、团队宣言、团队 Logo、团队合影、成员分工 | `docs/team/` 或 GitHub Wiki | PM |
| 需求材料 | 需求分析、业务流程、用户故事、验收标准 | `docs/requirements/` 或 GitHub Issues | PM、系统分析师 |
| 设计材料 | 原型、概要设计、详细设计、数据库设计、接口约定、UML 图 | `docs/design/` | 系统分析师 |
| 开发材料 | 项目源代码、接口说明、配置说明、部署说明 | 仓库源码目录和 `docs/deploy/` | 开发成员 |
| 测试材料 | 测试用例、缺陷清单、回归记录、测试结论 | `docs/test/` 和 GitHub Issues | 测试/配置管理员 |
| 答辩材料 | 答辩 PPT、演示脚本、成员任务说明、最终总结 | `docs/final/` | PM、全体成员 |

### 文档提交要求

- 文档类改动同样走 PR，不直接 push 到 `dev`。
- 阶段提交物必须和对应 Milestone 绑定。
- 中期检查前，PM 负责确认需求、设计、核心代码、演示环境是否齐备。
- 最终答辩前，测试/配置管理员负责冻结版本，PM 负责确认 PPT、演示路径和成员答辩分工。

---

## 十三、PM 日常检查清单

- [ ] 每周创建并维护 GitHub Milestone
- [ ] 每个功能都有明确 Issue、负责人、截止时间和验收标准
- [ ] 每天检查看板中 `In Progress`、`Blocked`、`Review` 状态
- [ ] 第 10 天前确认中期提交物：原型、需求分析、详细设计、核心代码
- [ ] 第 19 天确认代码走查、联调、综合测试和最终缺陷清单
- [ ] 第 20 天确认答辩 PPT、演示环境、成员答辩内容和最终源码版本

---

## 十四、新成员入组检查清单

- [ ] 在分工表里填上自己的名字和负责模块
- [ ] 执行换行符配置命令
- [ ] Clone 仓库，切到 `dev` 分支
- [ ] 本地 Java 使用 JDK 17
- [ ] 本地 `cd sky-take-out && mvn clean verify` 看到 `BUILD SUCCESS`
- [ ] 在 GitHub 上建一个测试 Issue（Title 写"测试-你的名字"）
- [ ] 走完一次完整流程：建分支 → commit → push → 发 PR → 请人 Review → 合并
- [ ] 确认自己在仓库的 Settings → Collaborators 里有权限

