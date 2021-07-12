CREATE TABLE `jpm_report_user_saves` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `report_id` VARCHAR(500) NOT NULL,
  `name` VARCHAR(500) NOT NULL,
  `username` VARCHAR(500) NOT NULL,
  `content` LONGTEXT NOT NULL,
  PRIMARY KEY (`id`));