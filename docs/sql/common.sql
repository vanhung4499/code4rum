
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
--  Table structure for `DATABASECHANGELOG`
-- ----------------------------
DROP TABLE IF EXISTS `DATABASECHANGELOG`;
CREATE TABLE `DATABASECHANGELOG` (
  `ID` varchar(255) NOT NULL,
  `AUTHOR` varchar(255) NOT NULL,
  `FILENAME` varchar(255) NOT NULL,
  `DATEEXECUTED` datetime NOT NULL,
  `ORDEREXECUTED` int(11) NOT NULL,
  `EXECTYPE` varchar(10) NOT NULL,
  `MD5SUM` varchar(35) DEFAULT NULL,
  `DESCRIPTION` varchar(255) DEFAULT NULL,
  `COMMENTS` varchar(255) DEFAULT NULL,
  `TAG` varchar(255) DEFAULT NULL,
  `LIQUIBASE` varchar(20) DEFAULT NULL,
  `CONTEXTS` varchar(255) DEFAULT NULL,
  `LABELS` varchar(255) DEFAULT NULL,
  `DEPLOYMENT_ID` varchar(10) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- ----------------------------
--  Table structure for `DATABASECHANGELOGLOCK`
-- ----------------------------
DROP TABLE IF EXISTS `DATABASECHANGELOGLOCK`;
CREATE TABLE `DATABASECHANGELOGLOCK` (
  `ID` int(11) NOT NULL,
  `LOCKED` bit(1) NOT NULL,
  `LOCKGRANTED` datetime DEFAULT NULL,
  `LOCKEDBY` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


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
