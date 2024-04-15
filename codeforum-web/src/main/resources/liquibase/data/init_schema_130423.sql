SET NAMES utf8;
SET FOREIGN_KEY_CHECKS = 0;

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



-- ----------------------------
--  Table structure for `read_count`
-- ----------------------------
DROP TABLE IF EXISTS `read_count`;
CREATE TABLE `read_count` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Primary key ID',
  `document_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT 'Document ID (article/comment)',
  `document_type` tinyint(4) NOT NULL DEFAULT '1' COMMENT 'Document type: 1-article, 2-comment',
  `cnt` int(10) unsigned NOT NULL DEFAULT '0' COMMENT 'Access count',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Creation time',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Last update time',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_document_id_type` (`document_id`,`document_type`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COMMENT='Counter table';


-- ----------------------------
--  Table structure for `request_count`
-- ----------------------------
DROP TABLE IF EXISTS `request_count`;
CREATE TABLE `request_count` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Primary key ID',
  `host` varchar(32) NOT NULL DEFAULT '' COMMENT 'Machine IP',
  `cnt` int(10) unsigned NOT NULL DEFAULT '0' COMMENT 'Access count',
  `date` date NOT NULL COMMENT 'Current date',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Creation time',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Last update time',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_unique_id_date` (`date`,`host`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COMMENT='Request count table';


-- ----------------------------
--  Table structure for `config`
-- ----------------------------
DROP TABLE IF EXISTS `config`;
CREATE TABLE `config` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Primary Key ID',
  `type` tinyint(4) NOT NULL DEFAULT '0' COMMENT 'Configuration Type: 1-Homepage, 2-Sidebar, 3-Advertisement, 4-Announcement',
  `name` varchar(64) NOT NULL DEFAULT '' COMMENT 'Name',
  `banner_url` varchar(256) NOT NULL DEFAULT '' COMMENT 'Banner URL',
  `jump_url` varchar(256) NOT NULL DEFAULT '' COMMENT 'Jump URL',
  `content` varchar(256) NOT NULL DEFAULT '' COMMENT 'Content',
  `rank` tinyint(4) NOT NULL DEFAULT '0' COMMENT 'Ranking',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT 'Status: 0-Unpublished, 1-Published',
  `tags` varchar(64) NOT NULL DEFAULT '' COMMENT 'Configuration Associated Tags, separated by English commas: 1-Hot, 2-Official, 3-Recommended',
  `extra` varchar(1024) NOT NULL DEFAULT '{}' COMMENT 'Extra Information',
  `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT 'Is Deleted',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Creation Time',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Last Update Time',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COMMENT='Configuration Table';


-- ----------------------------
--  Table structure for `dict_common`
-- ----------------------------
DROP TABLE IF EXISTS `dict_common`;
CREATE TABLE `dict_common` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Primary key ID',
  `type_code` varchar(100) NOT NULL DEFAULT '' COMMENT 'Dictionary type, such as sex, status, etc.',
  `dict_code` varchar(100) NOT NULL DEFAULT '' COMMENT 'Code for the value of the dictionary type',
  `dict_desc` varchar(200) NOT NULL DEFAULT '' COMMENT 'Description of the value of the dictionary type',
  `sort_no` int(8) unsigned NOT NULL DEFAULT '0' COMMENT 'Sorting number',
  `remark` varchar(500) DEFAULT '' COMMENT 'Remark',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Creation time',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Last update time',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_type_code_dict_code` (`type_code`,`dict_code`)
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8mb4 COMMENT='Common data dictionary';

-- ----------------------------
--  Table structure for `global_conf`
-- ----------------------------
DROP TABLE IF EXISTS `global_conf`;
CREATE TABLE `global_conf`
(
    `id`          int unsigned NOT NULL AUTO_INCREMENT COMMENT 'Primary key ID',
    `key`         varchar(128) NOT NULL DEFAULT '' COMMENT 'Configuration key',
    `value`       varchar(512) NOT NULL DEFAULT '' COMMENT 'Configuration value',
    `comment`     varchar(128) NOT NULL DEFAULT '' COMMENT 'Comment',
    `deleted`     tinyint      NOT NULL DEFAULT '0' COMMENT 'Is deleted: 0-not deleted, 1-deleted',
    `create_time` timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Creation time',
    `update_time` timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Last update time',
    PRIMARY KEY (`id`),
    KEY           `idx_key` (`key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Global configuration table';


-- ----------------------------
--  Table structure for `notify_msg`
-- ----------------------------
DROP TABLE IF EXISTS `notify_msg`;
CREATE TABLE `notify_msg` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Primary key ID',
  `related_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT 'Related primary key',
  `notify_user_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT 'User ID to be notified',
  `operate_user_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT 'User ID triggering this notification',
  `msg` varchar(1024) NOT NULL DEFAULT '' COMMENT 'Message content',
  `type` tinyint(3) unsigned NOT NULL DEFAULT '0' COMMENT 'Type: 0-default, 1-comment, 2-reply, 3-like, 4-collect, 5-follow, 6-system',
  `state` tinyint(3) unsigned NOT NULL DEFAULT '0' COMMENT 'Read state: 0-unread, 1-read',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Creation time',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Last update time',
  PRIMARY KEY (`id`),
  KEY `key_notify_user_id_type_state` (`notify_user_id`,`type`,`state`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COMMENT='Notification message list';

-- ----------------------------
--  Table structure for `user`
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Primary key ID',
  `third_account_id` varchar(128) NOT NULL DEFAULT '' COMMENT 'Third-party user ID',
  `user_name` varchar(64) NOT NULL DEFAULT '' COMMENT 'Username',
  `password` varchar(128) NOT NULL DEFAULT '' COMMENT 'Password',
  `login_type` tinyint(4) NOT NULL DEFAULT '0' COMMENT 'Login type: 0-WeChat login, 1-account password login',
  `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT 'Is deleted',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Creation time',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Last update time',
  PRIMARY KEY (`id`),
  KEY `key_third_account_id` (`third_account_id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COMMENT='User login table';


-- ----------------------------
--  Table structure for `user_foot`
-- ----------------------------
DROP TABLE IF EXISTS `user_foot`;
CREATE TABLE `user_foot` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Primary key ID',
  `user_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT 'User ID',
  `document_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT 'Document ID (article/comment)',
  `document_type` tinyint(4) NOT NULL DEFAULT '1' COMMENT 'Document type: 1-article, 2-comment',
  `document_user_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT 'User ID who published the document',
  `collection_stat` tinyint(3) unsigned NOT NULL DEFAULT '0' COMMENT 'Collection status: 0-not collected, 1-collected, 2-canceled collection',
  `read_stat` tinyint(3) unsigned NOT NULL DEFAULT '0' COMMENT 'Read status: 0-unread, 1-read',
  `comment_stat` tinyint(3) unsigned NOT NULL DEFAULT '0' COMMENT 'Comment status: 0-not commented, 1-commented, 2-deleted comment',
  `praise_stat` tinyint(3) unsigned NOT NULL DEFAULT '0' COMMENT 'Praise status: 0-not praised, 1-praised, 2-canceled praise',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Creation time',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Last update time',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_user_document` (`user_id`,`document_id`,`document_type`),
  KEY `idx_document_id` (`document_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COMMENT='User footprints table';



-- ----------------------------
--  Table structure for `user_relation`
-- ----------------------------
DROP TABLE IF EXISTS `user_relation`;
CREATE TABLE `user_relation` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Primary key ID',
  `user_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT 'User ID',
  `follow_user_id` int(10) unsigned NOT NULL COMMENT 'User ID who is followed, i.e., follower User ID',
  `follow_state` tinyint(2) unsigned NOT NULL DEFAULT '0' COMMENT 'Follow state: 0-not followed, 1-followed, 2-unfollowed',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Creation time',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Last update time',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_follow` (`user_id`,`follow_user_id`),
  KEY `key_follow_user_id` (`follow_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='User relationship table';

SET FOREIGN_KEY_CHECKS = 1;
