-- Add image_url column to rss_article table for storing extracted article images
ALTER TABLE rss_article ADD COLUMN image_url VARCHAR(1024) DEFAULT NULL COMMENT 'Article thumbnail/image URL' AFTER enclosure_type;
