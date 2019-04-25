CREATE TABLE `jpm_user_favorites` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(255) NULL,
  `title` VARCHAR(1000) NULL,
  `link` VARCHAR(3000) NULL,
  PRIMARY KEY (`id`));