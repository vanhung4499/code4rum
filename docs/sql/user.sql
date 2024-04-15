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
