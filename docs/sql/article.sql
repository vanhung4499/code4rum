-- ----------------------------
--  Table structure for `article`
-- ----------------------------
DROP TABLE IF EXISTS `article`;
CREATE TABLE `article` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Primary Key ID',
  `user_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT 'User ID',
  `article_type` tinyint(4) NOT NULL DEFAULT '1' COMMENT 'Article type: 1-Blog, 2-Q&A',
  `title` varchar(120) NOT NULL DEFAULT '' COMMENT 'Article title',
  `short_title` varchar(120) NOT NULL DEFAULT '' COMMENT 'Short title',
  `picture` varchar(128) NOT NULL DEFAULT '' COMMENT 'Article cover picture',
  `summary` varchar(300) NOT NULL DEFAULT '' COMMENT 'Article summary',
  `category_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT 'Category ID',
  `source` tinyint(4) NOT NULL DEFAULT '1' COMMENT 'Source: 1-Reprint, 2-Original, 3-Translation',
  `source_url` varchar(128) NOT NULL DEFAULT '1' COMMENT 'Original article link',
  `official_stat` int(10) unsigned NOT NULL DEFAULT '0' COMMENT 'Official status: 0-Non-official, 1-Official',
  `topping_stat` int(10) unsigned NOT NULL DEFAULT '0' COMMENT 'Topping status: 0-Not topped, 1-Topped',
  `cream_stat` int(10) unsigned NOT NULL DEFAULT '0' COMMENT 'Cream status: 0-Not featured, 1-Featured',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT 'Status: 0-Unpublished, 1-Published',
  `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT 'Whether deleted',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Creation time',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Last update time',
  PRIMARY KEY (`id`),
  KEY `idx_category_id` (`category_id`),
  KEY `idx_title` (`title`),
  KEY `idx_short_title` (`short_title`)
) ENGINE=InnoDB AUTO_INCREMENT=108 DEFAULT CHARSET=utf8mb4 COMMENT='Article table';


-- ----------------------------
--  Table structure for `article_detail`
-- ----------------------------
DROP TABLE IF EXISTS `article_detail`;
CREATE TABLE `article_detail` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Primary Key ID',
  `article_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT 'Article ID',
  `version` int(10) unsigned NOT NULL DEFAULT '0' COMMENT 'Version number',
  `content` longtext COMMENT 'Article content',
  `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT 'Whether deleted',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Creation time',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Last update time',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_article_version` (`article_id`,`version`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COMMENT='Article detail table';

-- ----------------------------
--  Table structure for `tag`
-- ----------------------------
DROP TABLE IF EXISTS `tag`;
CREATE TABLE `tag` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Primary key ID',
  `tag_name` varchar(120) NOT NULL COMMENT 'Tag name',
  `tag_type` tinyint(4) NOT NULL DEFAULT '1' COMMENT 'Tag type: 1-system tag, 2-custom tag',
  `category_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT 'Category ID',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT 'Status: 0-unpublished, 1-published',
  `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT 'Is deleted',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Creation time',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Last update time',
  PRIMARY KEY (`id`),
  KEY `idx_category_id` (`category_id`)
) ENGINE=InnoDB AUTO_INCREMENT=138 DEFAULT CHARSET=utf8mb4 COMMENT='Tag management table';


-- ----------------------------
--  Table structure for `article_tag`
-- ----------------------------
DROP TABLE IF EXISTS `article_tag`;
CREATE TABLE `article_tag` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Primary Key ID',
  `article_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT 'Article ID',
  `tag_id` int(11) NOT NULL DEFAULT '0' COMMENT 'Tag ID',
  `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT 'Whether deleted',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Creation time',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Last update time',
  PRIMARY KEY (`id`),
  KEY `idx_tag_id` (`tag_id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COMMENT='Mapping of article tags';


-- ----------------------------
--  Table structure for `banner`
-- ----------------------------
DROP TABLE IF EXISTS `banner`;
CREATE TABLE `banner` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Primary Key ID',
  `banner_name` varchar(64) NOT NULL DEFAULT '' COMMENT 'Name',
  `banner_url` varchar(256) NOT NULL DEFAULT '' COMMENT 'Image URL',
  `banner_type` tinyint(4) NOT NULL DEFAULT '0' COMMENT 'Type: 1-Homepage, 2-Sidebar, 3-Advertisement',
  `rank` tinyint(4) NOT NULL DEFAULT '0' COMMENT 'Rank',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT 'Status: 0-Unpublished, 1-Published',
  `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT 'Whether deleted',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Creation time',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Last update time',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Banner table';


-- ----------------------------
--  Table structure for `category`
-- ----------------------------
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Primary Key ID',
  `category_name` varchar(64) NOT NULL DEFAULT '' COMMENT 'Category Name',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT 'Status: 0-Unpublished, 1-Published',
  `rank` tinyint(4) NOT NULL DEFAULT '0' COMMENT 'Rank',
  `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT 'Whether deleted',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Creation time',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Last update time',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COMMENT='Category Management Table';


-- ----------------------------
--  Table structure for `column_article`
-- ----------------------------
DROP TABLE IF EXISTS `column_article`;
CREATE TABLE `column_article` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Primary Key ID',
  `column_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT 'Column ID',
  `article_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT 'Article ID',
  `section` int(10) unsigned NOT NULL DEFAULT '0' COMMENT 'Section Order, Smaller is Prior',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Creation Time',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Last Update Time',
  PRIMARY KEY (`id`),
  KEY `column_id` (`column_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COMMENT='Column Article List';


-- ----------------------------
--  Table structure for `column_info`
-- ----------------------------
DROP TABLE IF EXISTS `column_info`;
CREATE TABLE `column_info` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Column ID',
  `column_name` varchar(64) NOT NULL DEFAULT '' COMMENT 'Column Name',
  `user_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT 'Author ID',
  `introduction` varchar(256) NOT NULL DEFAULT '' COMMENT 'Column Introduction',
  `cover` varchar(128) NOT NULL DEFAULT '' COMMENT 'Column Cover',
  `state` tinyint(3) unsigned NOT NULL DEFAULT '0' COMMENT 'Status: 0-Pending Review, 1-Serializing, 2-Completed',
  `publish_time` timestamp NOT NULL DEFAULT '1970-01-02 00:00:00' COMMENT 'Online Time',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Creation Time',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Last Update Time',
  `section` int(10) unsigned NOT NULL DEFAULT '0' COMMENT 'Order',
  `nums` int(10) unsigned NOT NULL DEFAULT '0' COMMENT 'Estimated Number of Articles to Update in the Column',
  `type` int(10) unsigned NOT NULL DEFAULT '0' COMMENT 'Column Type: 0-Free, 1-Login Required, 2-Limited Time Free',
  `free_start_time` timestamp NOT NULL DEFAULT '1970-01-02 00:00:00' COMMENT 'Limited Time Free Start Time',
  `free_end_time` timestamp NOT NULL DEFAULT '1970-01-02 00:00:00' COMMENT 'Limited Time Free End Time',
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COMMENT='Column';


-- ----------------------------
--  Table structure for `comment`
-- ----------------------------
DROP TABLE IF EXISTS `comment`;
CREATE TABLE `comment` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Primary Key ID',
  `article_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT 'Article ID',
  `user_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT 'User ID',
  `content` varchar(300) NOT NULL DEFAULT '' COMMENT 'Comment Content',
  `top_comment_id` int(11) NOT NULL DEFAULT '0' COMMENT 'Top-level Comment ID',
  `parent_comment_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT 'Parent Comment ID',
  `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT 'Is Deleted',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Creation Time',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Last Update Time',
  PRIMARY KEY (`id`),
  KEY `idx_article_id` (`article_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COMMENT='Comment Table';
