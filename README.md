# RSS Reader - Backend

基于 Spring Boot 的 RSS 阅读器后端服务。

## 技术栈

- **Java 8+**
- **Spring Boot 2.6.13**
- **MyBatis** - ORM 框架
- **MySQL 5.7+** - 数据库
- **Druid** - 数据库连接池
- **Apache Abdera** - RSS/Atom 解析
- **Jsoup** - HTML 内容解析
- **Maven** - 构建工具

## 功能特性

### Feed 管理
- 添加、删除、更新 RSS 订阅源
- 定时抓取最新文章（可配置）
- 并发抓取支持
- 错误重试机制

### 文章管理
- 文章列表分页查询
- 已读/未读状态管理
- 收藏功能
- 全文搜索
- **图片自动提取**（从 media:content、media:thumbnail、enclosure、content 中提取）

### Web 文章
- 通过 URL 保存网页文章
- 自动提取标题、作者、内容
- 分类管理（inbox/later/archive）
- 阅读进度追踪

### 高亮与笔记
- 文本高亮（多种颜色）
- 标签管理
- 文档笔记
- 间隔重复复习系统

## 项目结构

```
rss-reader/
├── rss-ui/                    # 前端项目（独立目录）
├── sql/                       # 数据库脚本
├── src/main/
│   ├── java/com/rssreader/
│   │   ├── config/           # 配置类
│   │   ├── controller/       # REST 控制器
│   │   │   ├── FeedController.java
│   │   │   ├── ArticleController.java
│   │   │   ├── HighlightController.java
│   │   │   ├── NoteController.java
│   │   │   ├── ReviewController.java
│   │   │   └── WebArticleController.java
│   │   ├── dto/              # 数据传输对象
│   │   ├── entity/           # 数据库实体
│   │   │   ├── Feed.java
│   │   │   ├── Article.java
│   │   │   ├── ArticleContent.java
│   │   │   ├── ArticleHighlight.java
│   │   │   ├── ArticleNote.java
│   │   │   ├── WebArticle.java
│   │   │   └── Tag.java
│   │   ├── mapper/           # MyBatis Mapper 接口
│   │   ├── rss/              # RSS 解析模型
│   │   ├── service/          # 业务逻辑层
│   │   ├── task/             # 定时任务
│   │   ├── util/             # 工具类
│   │   │   ├── RssParser.java
│   │   │   ├── ContentExtractor.java
│   │   │   └── HttpUtils.java
│   │   └── vo/               # 视图对象
│   └── resources/
│       ├── mapper/           # MyBatis XML 映射文件
│       ├── db/migration/     # 数据库迁移脚本
│       └── application.yml   # 配置文件
└── pom.xml
```

## 快速开始

### 环境要求

- JDK 1.8+
- Maven 3.x
- MySQL 5.7+

### 数据库初始化

```bash
# 连接 MySQL
mysql -u root -p

# 创建数据库
CREATE DATABASE rss CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# 执行初始化脚本
source sql/init.sql
```

### 配置修改

编辑 `src/main/resources/application-dev.yml`：

```yaml
spring:
  datasource:
    druid:
      url: jdbc:mysql://localhost:3306/rss?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
      username: root
      password: your_password
```

### 构建运行

```bash
# 编译打包
mvn clean package

# 运行
java -jar target/rss-reader-1.0.0-SNAPSHOT.jar

# 或使用 Maven 直接运行
mvn spring-boot:run
```

应用将在 `http://localhost:8080` 启动。

## API 接口

### Feed 管理

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/feeds` | 获取所有订阅源 |
| POST | `/api/feeds` | 添加订阅源 |
| PUT | `/api/feeds/{id}` | 更新订阅源 |
| DELETE | `/api/feeds/{id}` | 删除订阅源 |
| POST | `/api/feeds/{id}/fetch` | 手动抓取指定源 |
| POST | `/api/feeds/fetch-all` | 抓取所有源 |

### 文章管理

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/articles` | 获取文章列表（分页） |
| GET | `/api/articles/{id}` | 获取文章详情 |
| PUT | `/api/articles/{id}/read` | 标记已读 |
| PUT | `/api/articles/{id}/unread` | 标记未读 |
| PUT | `/api/articles/{id}/favorite` | 切换收藏状态 |
| POST | `/api/articles/batch-read` | 批量标记已读 |
| GET | `/api/articles/unread-count` | 获取未读数量 |

### Web 文章

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/web-articles` | 通过 URL 保存文章 |
| GET | `/api/web-articles` | 获取文章列表 |
| GET | `/api/web-articles/{id}` | 获取文章详情 |
| PUT | `/api/web-articles/{id}/category` | 更新分类 |
| PUT | `/api/web-articles/{id}/read` | 标记已读 |
| PUT | `/api/web-articles/{id}/favorite` | 切换收藏 |
| PUT | `/api/web-articles/{id}/progress` | 更新阅读进度 |
| DELETE | `/api/web-articles/{id}` | 删除文章 |
| GET | `/api/web-articles/counts` | 获取分类统计 |

### 高亮管理

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/highlights` | 获取文章高亮 |
| POST | `/api/highlights` | 创建高亮 |
| PUT | `/api/highlights/{id}` | 更新高亮 |
| DELETE | `/api/highlights/{id}` | 删除高亮 |
| GET | `/api/tags` | 获取所有标签 |

### 笔记管理

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/notes/{articleId}` | 获取文章笔记 |
| POST | `/api/notes` | 创建/更新笔记 |

### 每日复习

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/review/today` | 获取今日复习项 |
| POST | `/api/review/{highlightId}` | 提交复习结果 |
| GET | `/api/review/config` | 获取复习配置 |
| PUT | `/api/review/config` | 更新复习配置 |

## 使用示例

### 添加 RSS 源

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

# 查询未读文章
curl "http://localhost:8080/api/articles?isRead=0&page=1&size=20"

# 搜索文章
curl "http://localhost:8080/api/articles?keyword=技术&page=1&size=20"
```

### 保存网页文章

```bash
curl -X POST http://localhost:8080/api/web-articles \
  -H "Content-Type: application/json" \
  -d '{
    "url": "https://example.com/article",
    "category": "inbox"
  }'
```

## 配置说明

```yaml
rss:
  fetch:
    enabled: true              # 是否启用定时抓取
    cron: 0 */30 * * * ?      # 抓取频率（每30分钟）
    thread-pool-size: 5        # 并发线程数
    max-retries: 3             # 最大重试次数
    timeout: 30000             # 超时时间（毫秒）
```

## 数据库表

| 表名 | 说明 |
|------|------|
| `rss_feed` | RSS 订阅源 |
| `rss_article` | RSS 文章 |
| `article_content` | 文章全文内容 |
| `article_highlight` | 文本高亮 |
| `article_note` | 文档笔记 |
| `tag` | 标签 |
| `highlight_tag` | 高亮-标签关联 |
| `web_article` | 网页文章 |
| `highlight_review` | 复习记录 |
| `review_config` | 复习配置 |

### 文章图片提取

RSS 解析时自动提取图片，优先级：
1. `media:content` 标签
2. `media:thumbnail` 标签
3. `enclosure`（图片类型）
4. `description/content` 中的第一张 `<img>`

## 监控

Druid 监控页面：http://localhost:8080/druid

默认账号密码：admin / admin

## 许可证

MIT License
