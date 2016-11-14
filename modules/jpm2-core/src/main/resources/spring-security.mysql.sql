create table users(
      username varchar(50) not null primary key,
      password varchar(250) not null,
      enabled boolean not null
);

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
INSERT INTO `users` (`username`, `password`, `enabled`) VALUES ('admin', '$2a$12$zofXZl6UI.uTuqBSyKwvvOh2Qbx5vjGkgGv8MeH9/6TBPncRK2RHq', '1'); -- admin / test
INSERT INTO `group_members` (`username`, `group_id`) VALUES ('admin', '1');

INSERT INTO `authorities` (`authority`) VALUES ('SPECIAL');
