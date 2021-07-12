CREATE TABLE IF NOT EXISTS `jpm_user_searchs` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(150) NOT NULL,
  `username` varchar(255) DEFAULT NULL,
  `entity` varchar(255) DEFAULT NULL,
  `field` varchar(255) NOT NULL,
  `enabled` char(1) NOT NULL DEFAULT 'Y',
  `parameters` longtext NOT NULL,
  PRIMARY KEY (`id`),
  KEY `username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;