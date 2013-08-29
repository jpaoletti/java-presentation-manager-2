CREATE TABLE IF NOT EXISTS `audit_records` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `datetime` datetime DEFAULT NULL,
  `entity` varchar(255) DEFAULT NULL,
  `item` varchar(255) DEFAULT NULL,
  `observations` varchar(255) DEFAULT NULL,
  `operation` varchar(255) DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB;