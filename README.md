# 冷冻海产品溯源系统（冷冻海产品 / Seafood Traceability）

> 信息管理系统综合实践项目 —— 前后端分离的全栈食品溯源系统。
> 三端：**系统管理端**、**流通节点端**、**消费者端**。技术栈：Vue3 + ElementPlus + ECharts + Axios / SpringBoot + MyBatis-Plus + Spring Security + JWT / MySQL 8.0。

## 目录结构

```
冷冻海产品溯源系统/
├── sql/
│   ├── seafood_schema.sql    # 建库建表（9 张表）
│   └── seafood_data.sql      # 种子数据（省市/20家节点企业/完整溯源链/管理端）
├── seafood-server/           # 后端 SpringBoot（context-path=/api，端口 8080）
├── seafood-admin-web/        # 管理端 （端口 3001）
├── seafood-node-web/         # 流通节点端（端口 3002）
└── seafood-consumer-web/     # 消费者端（端口 3003）
```

## 环境要求

- JDK 17、Maven 3.9（本机 `E:/Maven/apache-maven-3.9.9`）
- Node 18+ / npm
- MySQL 8.0（本机服务名 `MySQL`，bin 在 `D:\Program Files\MySQL\MySQL Server 8.0\bin`）

## 一、初始化数据库

```bash
# 建库
mysql -uroot -p -e "CREATE DATABASE IF NOT EXISTS seafood_trace DEFAULT CHARSET utf8mb4;"
# 建表（务必带 utf8mb4，否则中文会报 data too long）
mysql -uroot -p --default-character-set=utf8mb4 seafood_trace < sql/seafood_schema.sql
# 种子数据
mysql -uroot -p --default-character-set=utf8mb4 seafood_trace < sql/seafood_data.sql
```

> 注：`seafood_schema.sql` 内已含 `CREATE DATABASE seafood_trace` 与 `USE seafood_trace`，直接整文件导入即可，上面的建库命令可选。

## 二、启动后端

```bash
cd seafood-server
# 用 Maven 打包（改用你的本地仓库路径 / Maven 路径）
E:/Maven/apache-maven-3.9.9/bin/mvn -Dmaven.repo.local=E:/Maven/repository -DskipTests package
# 启动（DB 账号密码用环境变量传入）
DB_USER=root DB_PASSWORD=你的密码 java -jar target/seafood-server-1.0.0.jar
# 后端：http://localhost:8080/api
```

## 三、启动三个前端

```bash
cd seafood-admin-web    && npm install && npm run dev   # http://localhost:3001
cd seafood-node-web     && npm install && npm run dev   # http://localhost:3002
cd seafood-consumer-web && npm install && npm run dev   # http://localhost:3003
```

## 四、登录账号（初始密码均为 `123456`）

| 端 | 账号 | 角色 | 说明 |
|---|---|---|---|
| 管理端 | `admin` | 管理员 | 节点企业管理 + 可视化大屏（趋势/省饼/类型饼/省柱） |
| 流通节点端 | `breeding001` | 水产养殖企业 | 沈阳浑河水产养殖场（辽宁沈阳） |
| 流通节点端 | `processing001` | 冷冻加工企业 | 沈阳冷链海产加工厂（辽宁沈阳） |
| 流通节点端 | `wholesale001` | 批发商 | 顺发海产批发中心（辽宁沈阳） |
| 流通节点端 | `retail001` | 零售商 | 和平区海丰水产店（辽宁沈阳） |
| 消费者端 | 免登录 | — | 输入溯源码 `NFTS-20240901001` 即可查全链路 |

> 其余企业账号：`水产养殖 breeding002..005`、`冷冻加工 processing002..005`、`批发 wholesale002..004`、`零售 retail002..006`，同样密码 `123456`。

## 五、验证过的核心链路

- 管理端：登录 / 企业分页+组合筛选 / 省市联级 / 新增·详情·编辑·删除 / 四类统计图数据。
- 流通节点端：按角色渲染批号表单；养殖(待发布→已发布)、加工/批发/零售(新建→送确认→待确认→已确认)状态机；下架；**下游进场确认**（养殖确认加工、加工确认批发、批发确认零售）。
- **溯源码自动生成**：零售商批号被上游确认后，自动生成溯源标识码。
- 消费者端：输溯源码 → 返回 养殖→加工→批发→零售 四级完整溯源链。

## 备注

- 脚手架用 **Vite**（而非 Vite 文档里的 Vue-cli），仅影响构建工具，UI 与功能一致；如答辩要求 Vue-cli 可切换。
- 技术选型细节见 `需求分析/`（本仓库根外的范例目录）。
