# RSS Reader Backend

一个基于 Spring Boot 的 RSS 阅读器后端服务。

## 技术栈

- Java 8
- Maven
- Spring Boot 2.6.13
- MyBatis
- MySQL
- Druid
- Apache Abdera (RSS/Atom 解析)

## 功能特性

- Feed管理：添加、删除、更新RSS源
- 文章管理：获取文章、标记已读/未读、收藏
- 定时抓取：定时从RSS源抓取最新文章（可配置开关）
- 自动去重：基于 guid + feedId 的文章去重机制
- 并发抓取：支持多线程并发抓取RSS源
- 错误处理：自动重试机制和错误日志记录

## 快速开始

### 1. 环境要求

- JDK 1.8+
- Maven 3.x
- MySQL 5.7+

### 2. 数据库初始化

```bash
# 连接MySQL
mysql -u root -p

# 执行初始化脚本
source sql/init.sql
```

### 3. 配置修改

编辑 `src/main/resources/application-dev.yml`，修改数据库连接信息：

```yaml
spring:
  datasource:
    druid:
      url: jdbc:mysql://localhost:3306/rss?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
      username: root
      password: your_password
```

### 4. 构建运行

```bash
# 编译打包
mvn clean package

# 运行
java -jar target/rss-reader-1.0.0-SNAPSHOT.jar

# 或者使用 Maven 直接运行
mvn spring-boot:run
```

应用将在 `http://localhost:8080` 启动。

## API 接口

### Feed 管理

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/feeds | 添加RSS源 |
| DELETE | /api/feeds/{id} | 删除RSS源 |
| PUT | /api/feeds/{id} | 更新RSS源 |
| GET | /api/feeds | 获取RSS源列表(分页) |
| GET | /api/feeds/{id} | 获取RSS源详情 |
| POST | /api/feeds/{id}/fetch | 手动抓取指定源 |
| POST | /api/feeds/fetch-all | 手动抓取所有源 |

### 文章管理

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/articles | 获取文章列表(分页、筛选) |
| GET | /api/articles/{id} | 获取文章详情 |
| PUT | /api/articles/{id}/read | 标记已读/未读 |
| POST | /api/articles/batch-read | 批量标记已读 |
| PUT | /api/articles/{id}/favorite | 收藏/取消收藏 |
| GET | /api/articles/unread-count | 获取未读数量 |

## 使用示例

### 添加RSS源

```bash
curl -X POST http://localhost:8080/api/feeds \
  -H "Content-Type: application/json" \
  -d '{
    "feedUrl": "https://www.zhihu.com/rss",
    "feedName": "知乎热榜"
  }'
```

### 查询文章列表

```bash
# 查询所有文章
curl "http://localhost:8080/api/articles?page=1&size=20"

# 查询指定源的未读文章
curl "http://localhost:8080/api/articles?feedId=1&isRead=0&page=1&size=20"

# 搜索文章
curl "http://localhost:8080/api/articles?keyword=技术&page=1&size=20"
```

### 标记已读

```bash
curl -X PUT "http://localhost:8080/api/articles/1/read?isRead=1"
```

### 收藏文章

```bash
curl -X PUT http://localhost:8080/api/articles/1/favorite
```

## 配置说明

在 `application.yml` 中可以配置以下参数：

```yaml
rss:
  fetch:
    enabled: true              # 是否启用定时抓取
    cron: 0 */30 * * * ?      # 抓取频率（每30分钟）
    thread-pool-size: 5        # 并发线程数
    max-retries: 3             # 最大重试次数
    timeout: 30000             # 超时时间（毫秒）
```

## 监控

访问 Druid 监控页面：http://localhost:8080/druid

默认账号密码：admin / admin

## 项目结构

```
rss-reader/
├── sql/                       # 数据库脚本
├── src/main/
│   ├── java/com/rssreader/
│   │   ├── config/           # 配置类
│   │   ├── entity/           # 实体类
│   │   ├── dto/              # 数据传输对象
│   │   ├── vo/               # 视图对象
│   │   ├── mapper/           # MyBatis Mapper
│   │   ├── service/          # 服务层
│   │   ├── controller/       # 控制器
│   │   ├── task/             # 定时任务
│   │   ├── util/             # 工具类
│   │   ├── rss/              # RSS解析模型
│   │   ├── common/           # 公共类
│   │   └── exception/        # 异常处理
│   └── resources/
│       ├── mapper/           # MyBatis XML
│       └── application.yml   # 配置文件
└── pom.xml
```

## 许可证

MIT License
