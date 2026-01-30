-- 创建数据库
CREATE DATABASE IF NOT EXISTS `rss` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `rss`;

-- RSS源表
CREATE TABLE IF NOT EXISTS `rss_feed` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `feed_url` VARCHAR(500) NOT NULL COMMENT 'RSS源URL',
    `feed_name` VARCHAR(200) NOT NULL COMMENT 'RSS源名称',
    `feed_description` TEXT COMMENT 'RSS源描述',
    `feed_link` VARCHAR(500) COMMENT 'RSS源链接',
    `feed_category` VARCHAR(100) COMMENT 'RSS源分类',
    `feed_language` VARCHAR(50) COMMENT '语言',
    `feed_image_url` VARCHAR(500) COMMENT '图片URL',
    `last_fetch_time` DATETIME COMMENT '最后抓取时间',
    `fetch_interval` INT(11) NOT NULL DEFAULT 3600 COMMENT '抓取间隔(秒)',
    `status` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '状态(0:禁用 1:启用)',
    `fetch_error_count` INT(11) NOT NULL DEFAULT 0 COMMENT '抓取错误次数',
    `last_error_msg` TEXT COMMENT '最后错误信息',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除(0:否 1:是)',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_feed_url` (`feed_url`),
    KEY `idx_status` (`status`),
    KEY `idx_last_fetch_time` (`last_fetch_time`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='RSS源表';

-- 文章表
CREATE TABLE IF NOT EXISTS `rss_article` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `feed_id` BIGINT(20) NOT NULL COMMENT 'RSS源ID',
    `article_guid` VARCHAR(500) NOT NULL COMMENT '文章唯一标识',
    `title` VARCHAR(500) NOT NULL COMMENT '文章标题',
    `link` VARCHAR(500) COMMENT '文章链接',
    `description` TEXT COMMENT '文章描述',
    `author` VARCHAR(200) COMMENT '作者',
    `pub_date` DATETIME COMMENT '发布时间',
    `category` VARCHAR(200) COMMENT '分类',
    `enclosure_url` VARCHAR(500) COMMENT '附件URL',
    `image_url` VARCHAR(1024) DEFAULT NULL COMMENT 'Article thumbnail/image URL',
    `enclosure_type` VARCHAR(100) COMMENT '附件类型',
    `is_read` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否已读(0:未读 1:已读)',
    `is_favorite` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否收藏(0:未收藏 1:已收藏)',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除(0:否 1:是)',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_feed_article` (`feed_id`, `article_guid`),
    KEY `idx_feed_id` (`feed_id`),
    KEY `idx_pub_date` (`pub_date`),
    KEY `idx_is_read` (`is_read`),
    KEY `idx_is_favorite` (`is_favorite`),
    CONSTRAINT `fk_article_feed` FOREIGN KEY (`feed_id`) REFERENCES `rss_feed` (`id`) ON DELETE CASCADE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章表';

-- 文章原文内容表
CREATE TABLE IF NOT EXISTS `article_content` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `article_id` BIGINT(20) NOT NULL COMMENT '文章ID',
    `content` LONGTEXT COMMENT '原文内容(HTML)',
    `plain_text` LONGTEXT COMMENT '纯文本内容',
    `word_count` INT(11) DEFAULT 0 COMMENT '字数',
    `fetch_status` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '抓取状态(0:未抓取 1:成功 2:失败)',
    `fetch_time` DATETIME COMMENT '抓取时间',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_article_id` (`article_id`),
    CONSTRAINT `fk_content_article` FOREIGN KEY (`article_id`) REFERENCES `rss_article` (`id`) ON DELETE CASCADE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章原文内容表';

-- 文章笔记表
CREATE TABLE IF NOT EXISTS `article_note` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `article_id` BIGINT(20) NOT NULL COMMENT '文章ID',
    `note_content` TEXT COMMENT '笔记内容',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_article_id` (`article_id`),
    CONSTRAINT `fk_note_article` FOREIGN KEY (`article_id`) REFERENCES `rss_article` (`id`) ON DELETE CASCADE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章笔记表';

-- 文章划线标注表
CREATE TABLE IF NOT EXISTS `article_highlight` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `article_id` BIGINT(20) NOT NULL COMMENT '文章ID',
    `selected_text` TEXT NOT NULL COMMENT '选中的文本',
    `start_offset` INT(11) NOT NULL COMMENT '起始偏移量',
    `end_offset` INT(11) NOT NULL COMMENT '结束偏移量',
    `xpath` VARCHAR(500) COMMENT 'XPath路径',
    `color` VARCHAR(20) NOT NULL DEFAULT 'yellow' COMMENT '高亮颜色',
    `note` TEXT COMMENT '批注内容',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_article_id` (`article_id`),
    CONSTRAINT `fk_highlight_article` FOREIGN KEY (`article_id`) REFERENCES `rss_article` (`id`) ON DELETE CASCADE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章划线标注表';

-- 标签表
CREATE TABLE IF NOT EXISTS `tag` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `name` VARCHAR(50) NOT NULL COMMENT '标签名称',
    `color` VARCHAR(20) DEFAULT '#409eff' COMMENT '标签颜色',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_name` (`name`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='标签表';

-- 划线标签关联表
CREATE TABLE IF NOT EXISTS `highlight_tag` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `highlight_id` BIGINT(20) NOT NULL COMMENT '划线ID',
    `tag_id` BIGINT(20) NOT NULL COMMENT '标签ID',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_highlight_tag` (`highlight_id`, `tag_id`),
    KEY `idx_tag_id` (`tag_id`),
    CONSTRAINT `fk_ht_highlight` FOREIGN KEY (`highlight_id`) REFERENCES `article_highlight` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_ht_tag` FOREIGN KEY (`tag_id`) REFERENCES `tag` (`id`) ON DELETE CASCADE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='划线标签关联表';

-- 高亮复习记录表
CREATE TABLE IF NOT EXISTS `highlight_review` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `highlight_id` BIGINT(20) NOT NULL COMMENT '高亮ID',
    `action` VARCHAR(20) NOT NULL COMMENT '复习操作(keep/master/feedback/discard)',
    `review_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '复习时间',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_highlight_id` (`highlight_id`),
    KEY `idx_review_time` (`review_time`),
    CONSTRAINT `fk_review_highlight` FOREIGN KEY (`highlight_id`) REFERENCES `article_highlight` (`id`) ON DELETE CASCADE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='高亮复习记录表';

-- 复习配置表
CREATE TABLE IF NOT EXISTS `review_config` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `daily_count` INT(11) NOT NULL DEFAULT 10 COMMENT '每日复习数量',
    `recency` INT(11) NOT NULL DEFAULT 50 COMMENT '内容新旧程度(0-100, 0表示更旧, 100表示更新)',
    `include_new` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否包含新高亮',
    `include_mastered` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否包含已掌握的',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='复习配置表';

-- 文章复习频率表
CREATE TABLE IF NOT EXISTS `article_review_frequency` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `article_id` BIGINT(20) NOT NULL COMMENT '文章ID',
    `frequency` INT(11) NOT NULL DEFAULT 2 COMMENT '复习频率(0:never, 1:less, 2:normally, 3:more)',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_article_id` (`article_id`),
    CONSTRAINT `fk_freq_article` FOREIGN KEY (`article_id`) REFERENCES `rss_article` (`id`) ON DELETE CASCADE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章复习频率表';

-- 初始化默认复习配置
INSERT INTO `review_config` (`daily_count`, `recency`, `include_new`, `include_mastered`) VALUES (10, 50, 1, 0);

-- Web Article table for URL-based articles
CREATE TABLE IF NOT EXISTS `web_article` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
    `url` VARCHAR(2048) NOT NULL COMMENT 'Original URL',
    `title` VARCHAR(500) NOT NULL COMMENT 'Article title',
    `description` TEXT COMMENT 'Article description/summary',
    `content` LONGTEXT COMMENT 'Full article content (HTML)',
    `author` VARCHAR(255) COMMENT 'Article author',
    `domain` VARCHAR(255) COMMENT 'Source domain',
    `image_url` VARCHAR(2048) COMMENT 'Cover image URL',
    `word_count` INT DEFAULT 0 COMMENT 'Word count',
    `reading_time` INT DEFAULT 1 COMMENT 'Estimated reading time in minutes',
    `progress` INT DEFAULT 0 COMMENT 'Reading progress 0-100',
    `category` VARCHAR(20) DEFAULT 'inbox' COMMENT 'Category: inbox, later, archive',
    `language` VARCHAR(10) COMMENT 'Language code (en, zh, etc.)',
    `is_read` TINYINT DEFAULT 0 COMMENT '0: unread, 1: read',
    `is_favorite` TINYINT DEFAULT 0 COMMENT '0: not favorite, 1: favorite',
    `saved_time` DATETIME NOT NULL COMMENT 'Time when article was saved',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Create time',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update time',
    `is_deleted` TINYINT DEFAULT 0 COMMENT '0: not deleted, 1: deleted',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_url` (`url`(500)),
    KEY `idx_category` (`category`),
    KEY `idx_saved_time` (`saved_time`),
    KEY `idx_is_deleted` (`is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Web articles saved from URLs';
