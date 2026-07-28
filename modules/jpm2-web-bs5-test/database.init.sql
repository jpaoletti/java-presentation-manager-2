-- ============================================================================
-- jpm_test testbed initialization.
--
-- Full schema + seed for jpm2-web-bs5-test: the JPM core/security tables (with a
-- seeded 'admin' user, password "admin") plus every generic module migrated into
-- JPM2 (MailSender, Syslog, Batch, ThreadRunner, Customization, CacheAdmin,
-- Sysparam) and the group_authorities that let the Administrators group use them.
--
-- Table/column definitions match the JPA entities in jpm2-core; module DDL mirrors
-- the real apps. Run against the jpm_test database.
-- ============================================================================

CREATE DATABASE IF NOT EXISTS jpm_test;
USE jpm_test;

SET foreign_key_checks = 0;

-- ---------------------------------------------------------------------------
-- JPM core / security
-- ---------------------------------------------------------------------------
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
INSERT INTO `group_authorities` VALUES
 (1,'jpm.auth.operation.jpm-entity-group.add'),(1,'jpm.auth.operation.jpm-entity-group.delete'),(1,'jpm.auth.operation.jpm-entity-group.edit'),(1,'jpm.auth.operation.jpm-entity-group.list'),(1,'jpm.auth.operation.jpm-entity-group.show'),
 (1,'jpm.auth.operation.jpm-entity-test.add'),(1,'jpm.auth.operation.jpm-entity-test.delete'),(1,'jpm.auth.operation.jpm-entity-test.deleteSelected'),(1,'jpm.auth.operation.jpm-entity-test.edit'),(1,'jpm.auth.operation.jpm-entity-test.generalAudit'),(1,'jpm.auth.operation.jpm-entity-test.itemAudit'),(1,'jpm.auth.operation.jpm-entity-test.list'),(1,'jpm.auth.operation.jpm-entity-test.longTest'),(1,'jpm.auth.operation.jpm-entity-test.show'),
 (1,'jpm.auth.operation.jpm-entity-user.add'),(1,'jpm.auth.operation.jpm-entity-user.delete'),(1,'jpm.auth.operation.jpm-entity-user.edit'),(1,'jpm.auth.operation.jpm-entity-user.list'),(1,'jpm.auth.operation.jpm-entity-user.resetPassword'),(1,'jpm.auth.operation.jpm-entity-user.show'),
 (1,'jpm.auth.operation.jpm-entity-weaktest.add'),(1,'jpm.auth.operation.jpm-entity-weaktest.delete'),(1,'jpm.auth.operation.jpm-entity-weaktest.deleteSelected'),(1,'jpm.auth.operation.jpm-entity-weaktest.edit'),(1,'jpm.auth.operation.jpm-entity-weaktest.list'),(1,'jpm.auth.operation.jpm-entity-weaktest.show'),
 (1,'jpm.auth.operation.user.profile'),(1,'ROLE_USER'),(1,'ROLE_USER_FAVORITE'),(1,'ROLE_USER_RECENT');
-- Basic authorities for the Users group (id 2): ROLE_USER is required by the security config for any access.
INSERT INTO `group_authorities` VALUES (2,'ROLE_USER'),(2,'ROLE_USER_FAVORITE'),(2,'ROLE_USER_RECENT'),(2,'jpm.auth.operation.user.profile');

DROP TABLE IF EXISTS `group_members`;
CREATE TABLE `group_members` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `group_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_group_members_group` (`group_id`),
  CONSTRAINT `fk_group_members_group` FOREIGN KEY (`group_id`) REFERENCES `jpm_groups` (`id`)
) ENGINE=InnoDB;
INSERT INTO `group_members` VALUES (2,'admin',1),(3,'admin',2),(4,'jpaoletti',1),(5,'jpaoletti',2);

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
  `hierarchy_level` int NOT NULL DEFAULT 999,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB;
-- hierarchy_level: lower = higher privilege. Administrators=0 (top), Users=999 (default/min).
INSERT INTO `jpm_groups` VALUES (1,'Administrators',0),(2,'Users',999);

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

DROP TABLE IF EXISTS `jpm_user_visibles_columns`;
CREATE TABLE `jpm_user_visibles_columns` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(255) NOT NULL,
  `entity` varchar(255) DEFAULT NULL,
  `columns` text,
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
-- user: admin / password: admin
INSERT INTO `users` VALUES ('admin','Y','Y','Y','Y','','Administrator','$2a$12$zofXZl6UI.uTuqBSyKwvvOh2Qbx5vjGkgGv8MeH9/6TBPncRK2RHq',0);

-- ---------------------------------------------------------------------------
-- Migrated generic modules (table defs match the jpm2-core JPA entities)
-- ---------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `mail_senders` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(1000) DEFAULT NULL,
  `description` varchar(3000) DEFAULT NULL,
  `sender_type` tinyint NOT NULL DEFAULT '0',
  `enabled` char(1) NOT NULL DEFAULT 'Y',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS `mail_senders_parameteres` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) DEFAULT NULL,
  `value` text,
  `sender` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `mail_senders_parameteres_FK` (`sender`),
  CONSTRAINT `mail_senders_parameteres_FK` FOREIGN KEY (`sender`) REFERENCES `mail_senders` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS `syslog` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `event_date` date DEFAULT NULL,
  `event_datetime` datetime DEFAULT NULL,
  `severity` varchar(255) DEFAULT NULL,
  `permission` varchar(255) DEFAULT NULL,
  `message` varchar(255) DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `batchs` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `task` varchar(255) DEFAULT NULL,
  `enabled` char(1) DEFAULT 'N',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS `batchs_parameteres` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `value` varchar(255) DEFAULT NULL,
  `batch` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `batchs_parameteres_FK` (`batch`),
  CONSTRAINT `batchs_parameteres_FK` FOREIGN KEY (`batch`) REFERENCES `batchs` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS `threads_runners` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `description` varchar(255) DEFAULT NULL,
  `enabled` char(1) NOT NULL DEFAULT 'N',
  `debug` char(1) NOT NULL DEFAULT 'N',
  `name` varchar(255) DEFAULT NULL,
  `clazz` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS `threads_runners_parameters` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `value` varchar(255) DEFAULT NULL,
  `thread_runner` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `threads_runners_parameters_FK` (`thread_runner`),
  CONSTRAINT `threads_runners_parameters_FK` FOREIGN KEY (`thread_runner`) REFERENCES `threads_runners` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS `customization` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `login_style` varchar(5000) DEFAULT NULL,
  `login_page` varchar(255) DEFAULT NULL,
  `login_logo` longblob,
  `logo` longblob,
  `favicon` longblob,
  `messages` longtext,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS `cache_admins` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `description` varchar(255) DEFAULT NULL,
  `code` varchar(255) DEFAULT NULL,
  `cache_type` tinyint DEFAULT NULL,
  `active` char(1) DEFAULT 'Y',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS `cache_admins_parameters` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `value` text,
  `cache_admin` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `cache_admins_parameters_FK` (`cache_admin`),
  CONSTRAINT `cache_admins_parameters_FK` FOREIGN KEY (`cache_admin`) REFERENCES `cache_admins` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS `jpm_sysparam` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `param_key` varchar(255) NOT NULL,
  `param_type` varchar(30) DEFAULT 'STRING',
  `param_value` longtext,
  `param_group` varchar(255) DEFAULT 'general',
  `cached` char(1) DEFAULT 'Y',
  `read_role` varchar(255) DEFAULT NULL,
  `write_role` varchar(255) DEFAULT NULL,
  `updated_by` varchar(255) DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `jpm_sysparam_key_uq` (`param_key`)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS `jpm_sysparam_group` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `label` varchar(255) DEFAULT NULL,
  `icon` varchar(255) DEFAULT NULL,
  `style` varchar(255) DEFAULT NULL,
  `collapsed` char(1) DEFAULT 'Y',
  `sort_order` int DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `jpm_sysparam_group_name_uq` (`name`)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------------
-- Grant the migrated-module operations to the Administrators group (id=1)
-- ---------------------------------------------------------------------------
INSERT IGNORE INTO `group_authorities` (`group_id`,`authority`) VALUES
 (1,'jpm.auth.operation.sysparam.list'),(1,'jpm.auth.operation.sysparam.show'),(1,'jpm.auth.operation.sysparam.setValue'),(1,'jpm.auth.operation.sysparam.clearCache'),(1,'jpm.auth.operation.sysparam.clearAllCache'),(1,'jpm.auth.operation.sysparam.history'),(1,'jpm.auth.operation.sysparam.import'),(1,'jpm.auth.operation.sysparam.export'),(1,'jpm.auth.operation.sysparam.exportSelected'),(1,'jpm.auth.operation.sysparam.itemAudit'),(1,'jpm.auth.operation.sysparam.delete'),
 (1,'jpm.sysparam.auth.revealSecret'), (1,'jpm.auth.operation.sysparam.edit'),(1,'jpm.auth.operation.sysparam.sysparamHealth'),(1,'jpm.auth.operation.sysparam.sysparamTree'),
 (1,'jpm.auth.operation.sysparamGroup.list'),(1,'jpm.auth.operation.sysparamGroup.add'),(1,'jpm.auth.operation.sysparamGroup.show'),(1,'jpm.auth.operation.sysparamGroup.edit'),(1,'jpm.auth.operation.sysparamGroup.delete'),(1,'jpm.auth.operation.sysparamGroup.itemAudit'),(1,'jpm.auth.operation.sysparamGroup.generalAudit'),
 (1,'jpm.auth.operation.cacheAdmin.list'),(1,'jpm.auth.operation.cacheAdmin.add'),(1,'jpm.auth.operation.cacheAdmin.generalAudit'),(1,'jpm.auth.operation.cacheAdmin.show'),(1,'jpm.auth.operation.cacheAdmin.edit'),(1,'jpm.auth.operation.cacheAdmin.reloadCacheAdmin'),(1,'jpm.auth.operation.cacheAdmin.cacheInfo'),(1,'jpm.auth.operation.cacheAdmin.cacheEntry'),(1,'jpm.auth.operation.cacheAdmin.reloadCacheAdminSelected'),(1,'jpm.auth.operation.cacheAdmin.duplicate'),(1,'jpm.auth.operation.cacheAdmin.import'),(1,'jpm.auth.operation.cacheAdmin.export'),(1,'jpm.auth.operation.cacheAdmin.exportSelected'),(1,'jpm.auth.operation.cacheAdmin.itemAudit'),(1,'jpm.auth.operation.cacheAdmin.delete'),
 (1,'jpm.auth.operation.cacheAdminParameter.list'),(1,'jpm.auth.operation.cacheAdminParameter.add'),(1,'jpm.auth.operation.cacheAdminParameter.generalAudit'),(1,'jpm.auth.operation.cacheAdminParameter.show'),(1,'jpm.auth.operation.cacheAdminParameter.edit'),(1,'jpm.auth.operation.cacheAdminParameter.delete'),(1,'jpm.auth.operation.cacheAdminParameter.itemAudit'),
 (1,'jpm.auth.operation.mailSender.list'),(1,'jpm.auth.operation.mailSender.add'),(1,'jpm.auth.operation.mailSender.import'),(1,'jpm.auth.operation.mailSender.generalAudit'),(1,'jpm.auth.operation.mailSender.show'),(1,'jpm.auth.operation.mailSender.edit'),(1,'jpm.auth.operation.mailSender.export'),(1,'jpm.auth.operation.mailSender.exportSelected'),(1,'jpm.auth.operation.mailSender.duplicate'),(1,'jpm.auth.operation.mailSender.reloadMailSender'),(1,'jpm.auth.operation.mailSender.testMailSender'),(1,'jpm.auth.operation.mailSender.itemAudit'),(1,'jpm.auth.operation.mailSender.delete'),
 (1,'jpm.auth.operation.mailSenderParameter.list'),(1,'jpm.auth.operation.mailSenderParameter.add'),(1,'jpm.auth.operation.mailSenderParameter.generalAudit'),(1,'jpm.auth.operation.mailSenderParameter.show'),(1,'jpm.auth.operation.mailSenderParameter.edit'),(1,'jpm.auth.operation.mailSenderParameter.delete'),(1,'jpm.auth.operation.mailSenderParameter.itemAudit'),
 (1,'jpm.auth.operation.syslog.list'),(1,'jpm.auth.operation.syslog.show'),
 (1,'jpm.auth.operation.batch.list'),(1,'jpm.auth.operation.batch.add'),(1,'jpm.auth.operation.batch.show'),(1,'jpm.auth.operation.batch.edit'),(1,'jpm.auth.operation.batch.delete'),(1,'jpm.auth.operation.batch.itemAudit'),(1,'jpm.auth.operation.batch.generalAudit'),
 (1,'jpm.auth.operation.batchParameter.list'),(1,'jpm.auth.operation.batchParameter.add'),(1,'jpm.auth.operation.batchParameter.show'),(1,'jpm.auth.operation.batchParameter.edit'),(1,'jpm.auth.operation.batchParameter.delete'),(1,'jpm.auth.operation.batchParameter.itemAudit'),(1,'jpm.auth.operation.batchParameter.generalAudit'),
 (1,'jpm.auth.operation.threadRunner.list'),(1,'jpm.auth.operation.threadRunner.add'),(1,'jpm.auth.operation.threadRunner.show'),(1,'jpm.auth.operation.threadRunner.edit'),(1,'jpm.auth.operation.threadRunner.delete'),(1,'jpm.auth.operation.threadRunner.itemAudit'),(1,'jpm.auth.operation.threadRunner.generalAudit'),
 (1,'jpm.auth.operation.threadRunnerParameter.list'),(1,'jpm.auth.operation.threadRunnerParameter.add'),(1,'jpm.auth.operation.threadRunnerParameter.show'),(1,'jpm.auth.operation.threadRunnerParameter.edit'),(1,'jpm.auth.operation.threadRunnerParameter.delete'),(1,'jpm.auth.operation.threadRunnerParameter.itemAudit'),(1,'jpm.auth.operation.threadRunnerParameter.generalAudit'),
 (1,'jpm.auth.operation.customization.list'),(1,'jpm.auth.operation.customization.add'),(1,'jpm.auth.operation.customization.show'),(1,'jpm.auth.operation.customization.edit'),(1,'jpm.auth.operation.customization.delete'),(1,'jpm.auth.operation.customization.itemAudit'),(1,'jpm.auth.operation.customization.generalAudit');

SET foreign_key_checks = 1;
