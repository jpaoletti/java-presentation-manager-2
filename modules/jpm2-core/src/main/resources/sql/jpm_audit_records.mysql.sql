CREATE TABLE IF NOT EXISTS `jpm_audit_records` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `datetime` datetime DEFAULT NULL,
  `entity` varchar(255) DEFAULT NULL,
  `item` varchar(255) DEFAULT NULL,
  `item_owner` varchar(255) DEFAULT NULL,
  `observations` varchar(255) DEFAULT NULL,
  `operation` varchar(255) DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB;
CREATE INDEX `jpm_audit_records_entity_IDX` ON `jpm_audit_records` (`entity`);
CREATE INDEX `jpm_audit_records_username_IDX` ON `jpm_audit_records` (`username`);
CREATE INDEX `jpm_audit_item` ON `jpm_audit_records` (`entity`, `item`);
CREATE INDEX `jpm_audit_owner_general` ON `jpm_audit_records` (`entity`, `item_owner`, `id`);
