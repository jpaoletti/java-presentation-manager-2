-- @@ 1
INSERT IGNORE INTO group_authorities (group_id, authority) VALUES (1, 'jpm.auth.operation.ddlMigration.list'), (1, 'jpm.auth.operation.ddlMigration.show');
-- @@ 2
CREATE TABLE IF NOT EXISTS `jpm_menu` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `parent` bigint DEFAULT NULL,
  `type` varchar(20) NOT NULL,
  `label` varchar(255) DEFAULT NULL,
  `icon` varchar(100) DEFAULT NULL,
  `entity` varchar(100) DEFAULT NULL,
  `operation` varchar(100) DEFAULT NULL,
  `url` varchar(500) DEFAULT NULL,
  `authorities` varchar(1000) DEFAULT NULL,
  `sort_order` int DEFAULT NULL,
  `active` char(1) NOT NULL DEFAULT 'Y',
  PRIMARY KEY (`id`),
  KEY `jpm_menu_parent_FK` (`parent`),
  CONSTRAINT `jpm_menu_parent_FK` FOREIGN KEY (`parent`) REFERENCES `jpm_menu` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB;
-- @@ 3
INSERT IGNORE INTO group_authorities (group_id, authority) VALUES (1, 'jpm.auth.operation.jpmMenu.list'), (1, 'jpm.auth.operation.jpmMenu.add'), (1, 'jpm.auth.operation.jpmMenu.show'), (1, 'jpm.auth.operation.jpmMenu.edit'), (1, 'jpm.auth.operation.jpmMenu.delete'), (1, 'jpm.auth.operation.jpmMenu.itemAudit'), (1, 'jpm.auth.operation.jpmMenu.generalAudit'), (1, 'jpm.auth.operation.jpmMenu.menuPreview'), (1, 'jpm.auth.operation.jpmMenu.menuGenerate');
-- @@ 4
ALTER TABLE jpm_menu ADD COLUMN user_condition varchar(255) DEFAULT NULL AFTER authorities;
