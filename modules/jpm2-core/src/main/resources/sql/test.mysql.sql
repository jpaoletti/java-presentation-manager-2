CREATE TABLE `test` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `string_field` varchar(520) DEFAULT NULL,
  `int_field` int(11) DEFAULT NULL,
  `date_field` date DEFAULT NULL,
  `bool_field` char(1) DEFAULT NULL,
  `decimal_field` decimal(10,2) DEFAULT NULL,
  `test` bigint(20) DEFAULT NULL,
  `bigstring` text,
  `testEnum` int(11) DEFAULT NULL,
  `file` mediumblob,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


CREATE TABLE `test_weak` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `text` VARCHAR(1000) NULL,
  `test` BIGINT NULL,
  PRIMARY KEY (`id`));