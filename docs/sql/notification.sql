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
