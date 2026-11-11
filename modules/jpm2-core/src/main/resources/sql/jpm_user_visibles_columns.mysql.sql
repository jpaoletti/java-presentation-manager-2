CREATE TABLE jpm_user_visibles_columns (
	id BIGINT auto_increment NOT NULL,
	username varchar(255) NOT NULL,
	entity varchar(100) NULL,
	`columns` TEXT NULL,
	CONSTRAINT jpm_user_visibles_columns_pk PRIMARY KEY (id)
)ENGINE=InnoDB;