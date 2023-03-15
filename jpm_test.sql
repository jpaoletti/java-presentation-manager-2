CREATE DATABASE jpm_test;

USE jpm_test;

SET foreign_key_checks = 0;

DROP TABLE IF EXISTS `authorities`;

CREATE TABLE `authorities` (
  `authority` varchar(50) NOT NULL,
  PRIMARY KEY (`authority`)
) ENGINE=InnoDB;

INSERT INTO `authorities` VALUES ('ROLE_USER'),('ROLE_USER_FAVORITE'),('ROLE_USER_RECENT');

DROP TABLE IF EXISTS `group_authorities`;

CREATE TABLE `group_authorities` (
  `group_id` bigint NOT NULL,
  `authority` varchar(500) NOT NULL,
  PRIMARY KEY (`group_id`,`authority`),
  KEY `authority` (`authority`),
  CONSTRAINT `fk_group_authorities_group` FOREIGN KEY (`group_id`) REFERENCES `jpm_groups` (`id`)
) ENGINE=InnoDB;

INSERT INTO `group_authorities` VALUES (1,'jpm.auth.operation.jpm-entity-group.add'),(1,'jpm.auth.operation.jpm-entity-group.delete'),(1,'jpm.auth.operation.jpm-entity-group.edit'),(1,'jpm.auth.operation.jpm-entity-group.list'),(1,'jpm.auth.operation.jpm-entity-group.show'),(1,'jpm.auth.operation.jpm-entity-test.add'),(1,'jpm.auth.operation.jpm-entity-test.delete'),(1,'jpm.auth.operation.jpm-entity-test.deleteSelected'),(1,'jpm.auth.operation.jpm-entity-test.edit'),(1,'jpm.auth.operation.jpm-entity-test.generalAudit'),(1,'jpm.auth.operation.jpm-entity-test.itemAudit'),(1,'jpm.auth.operation.jpm-entity-test.list'),(1,'jpm.auth.operation.jpm-entity-test.longTest'),(1,'jpm.auth.operation.jpm-entity-test.show'),(1,'jpm.auth.operation.jpm-entity-user.add'),(1,'jpm.auth.operation.jpm-entity-user.delete'),(1,'jpm.auth.operation.jpm-entity-user.edit'),(1,'jpm.auth.operation.jpm-entity-user.list'),(1,'jpm.auth.operation.jpm-entity-user.resetPassword'),(1,'jpm.auth.operation.jpm-entity-user.show'),(1,'jpm.auth.operation.jpm-entity-weaktest.add'),(1,'jpm.auth.operation.jpm-entity-weaktest.delete'),(1,'jpm.auth.operation.jpm-entity-weaktest.deleteSelected'),(1,'jpm.auth.operation.jpm-entity-weaktest.edit'),(1,'jpm.auth.operation.jpm-entity-weaktest.list'),(1,'jpm.auth.operation.jpm-entity-weaktest.show'),(1,'jpm.auth.operation.user.profile'),(1,'ROLE_USER'),(1,'ROLE_USER_FAVORITE'),(1,'ROLE_USER_RECENT');

DROP TABLE IF EXISTS `group_members`;

CREATE TABLE `group_members` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `group_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_group_members_group` (`group_id`),
  CONSTRAINT `fk_group_members_group` FOREIGN KEY (`group_id`) REFERENCES `jpm_groups` (`id`)
) ENGINE=InnoDB;

INSERT INTO `group_members` VALUES (2,'admin',1),(3,'admin',2);

DROP TABLE IF EXISTS `jpm_api_messages`;
CREATE TABLE `jpm_api_messages` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `code` varchar(100) NOT NULL,
  `message` varchar(1000) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB;

DROP TABLE IF EXISTS `jpm_audit_records`;

CREATE TABLE `jpm_audit_records` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `datetime` datetime DEFAULT NULL,
  `entity` varchar(255) DEFAULT NULL,
  `item` varchar(255) DEFAULT NULL,
  `observations` varchar(255) DEFAULT NULL,
  `operation` varchar(255) DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB;

DROP TABLE IF EXISTS `jpm_groups`;

CREATE TABLE `jpm_groups` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `group_name` varchar(50) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB;

INSERT INTO `jpm_groups` VALUES (1,'Administrators'),(2,'Users');

DROP TABLE IF EXISTS `jpm_report_user_saves`;

CREATE TABLE `jpm_report_user_saves` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `report_id` varchar(500) NOT NULL,
  `name` varchar(500) NOT NULL,
  `username` varchar(500) NOT NULL,
  `content` longtext NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB;

DROP TABLE IF EXISTS `jpm_user_favorites`;

CREATE TABLE `jpm_user_favorites` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(255) DEFAULT NULL,
  `title` varchar(1000) DEFAULT NULL,
  `link` varchar(3000) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB;

DROP TABLE IF EXISTS `persistent_logins`;

CREATE TABLE `persistent_logins` (
  `username` varchar(50) NOT NULL,
  `series` varchar(64) NOT NULL,
  `token` varchar(64) NOT NULL,
  `last_used` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`series`)
) ENGINE=InnoDB;

DROP TABLE IF EXISTS `reports`;

CREATE TABLE `reports` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `code` varchar(200) NOT NULL,
  `name` varchar(1000) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB;

DROP TABLE IF EXISTS `test`;

CREATE TABLE `test` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `string_field` varchar(520) DEFAULT NULL,
  `int_field` int DEFAULT NULL,
  `date_field` date DEFAULT NULL,
  `bool_field` char(1) DEFAULT NULL,
  `decimal_field` decimal(10,2) DEFAULT NULL,
  `test` bigint DEFAULT NULL,
  `bigstring` text,
  `testEnum` int DEFAULT NULL,
  `file` mediumblob,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB;

INSERT INTO `test` VALUES (1,'Uno',1,'2019-08-01','Y',2.00,NULL,'',0,NULL),(2,'Dos',1,'2019-08-01','Y',1.00,1,'',1,NULL),(4,'Test',12,'2019-08-08','Y',125.00,NULL,'',1,NULL),(7,'asdfasf',NULL,'2021-07-08','Y',NULL,1,'',0,NULL);

DROP TABLE IF EXISTS `test_weak`;

CREATE TABLE `test_weak` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `text` varchar(1000) DEFAULT NULL,
  `test` bigint DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB;

INSERT INTO `test_weak` VALUES (1,'Test',5),(2,'test',4),(3,'a',4),(4,'s',7),(5,'asdasd',7),(6,'test',7);

DROP TABLE IF EXISTS `users`;

CREATE TABLE `users` (
  `username` varchar(255) NOT NULL,
  `account_non_expired` char(1) DEFAULT 'Y',
  `account_non_locked` char(1) DEFAULT 'Y',
  `credentials_non_expired` char(1) DEFAULT 'Y',
  `enabled` char(1) NOT NULL,
  `mail` varchar(255) DEFAULT NULL,
  `name` varchar(2000) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `login_attemps` int NOT NULL DEFAULT '0',
  PRIMARY KEY (`username`)
) ENGINE=InnoDB;

INSERT INTO `users` VALUES ('admin','Y','Y','Y','Y','','Administrator','$2a$12$zofXZl6UI.uTuqBSyKwvvOh2Qbx5vjGkgGv8MeH9/6TBPncRK2RHq',0);

SET foreign_key_checks = 1;

