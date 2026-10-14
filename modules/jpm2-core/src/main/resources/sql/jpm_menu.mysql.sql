-- Dynamic menu (entities/jpmMenu.xml)
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
  `user_condition` varchar(255) DEFAULT NULL,
  `sort_order` int DEFAULT NULL,
  `active` char(1) NOT NULL DEFAULT 'Y',
  PRIMARY KEY (`id`),
  KEY `jpm_menu_parent_FK` (`parent`),
  CONSTRAINT `jpm_menu_parent_FK` FOREIGN KEY (`parent`) REFERENCES `jpm_menu` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB;
