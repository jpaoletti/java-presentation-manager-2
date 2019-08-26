CREATE TABLE IF NOT EXISTS `users` (
  `username` varchar(255) NOT NULL,
  `account_non_expired` char(1) DEFAULT 'Y',
  `account_non_locked` char(1) DEFAULT 'Y',
  `credentials_non_expired` char(1) DEFAULT 'Y',
  `enabled` char(1) NOT NULL,
  `mail` varchar(255) DEFAULT NULL,
  `name` varchar(2000) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `login_attemps` int(3) NOT NULL DEFAULT '0',
  PRIMARY KEY (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


create table authorities(
      authority varchar(50) not null primary key
);

create table groups (
  id bigint(20) NOT NULL primary key AUTO_INCREMENT,
  group_name varchar(50) not null
);

create table group_authorities (
  group_id bigint not null,
  authority varchar(50) not null,
  constraint fk_group_authorities_group foreign key(group_id) references groups(id));

ALTER TABLE `group_authorities` ADD INDEX (  `authority` );
ALTER TABLE  `group_authorities` ADD PRIMARY KEY (  `group_id` ,  `authority` ) ;

create table group_members (
  id bigint(20) NOT NULL primary key AUTO_INCREMENT,
  username varchar(50) not null,
  group_id bigint not null,
  constraint fk_group_members_group foreign key(group_id) references groups(id));

create table persistent_logins (
  username varchar(50) not null,
  series varchar(64) primary key,
  token varchar(64) not null,
  last_used timestamp not null
);

INSERT INTO `groups` (`id`, `group_name`) VALUES (NULL, 'Administrators'), (NULL, 'Users');
INSERT INTO `users` VALUES ('admin', 'Y', 'Y', 'Y', 'Y', '', 'Administrator', '$2a$12$zofXZl6UI.uTuqBSyKwvvOh2Qbx5vjGkgGv8MeH9/6TBPncRK2RHq', 0); -- admin / test
INSERT INTO `group_members` (`username`, `group_id`) VALUES ('admin', '1');

INSERT INTO group_authorities (group_id, authority) VALUES
(1, 'ROLE_USER'),
(1, 'ROLE_USER_FAVORITE'),
(1, 'ROLE_USER_RECENT'),
(1, 'jpm.auth.operation.jpm-entity-group.add'),
(1, 'jpm.auth.operation.jpm-entity-group.delete'),
(1, 'jpm.auth.operation.jpm-entity-group.edit'),
(1, 'jpm.auth.operation.jpm-entity-group.list'),
(1, 'jpm.auth.operation.jpm-entity-group.show'),
(1, 'jpm.auth.operation.jpm-entity-user.add'),
(1, 'jpm.auth.operation.jpm-entity-user.delete'),
(1, 'jpm.auth.operation.jpm-entity-user.edit'),
(1, 'jpm.auth.operation.jpm-entity-user.list'),
(1, 'jpm.auth.operation.jpm-entity-user.resetPassword'),
(1, 'jpm.auth.operation.jpm-entity-user.show');
