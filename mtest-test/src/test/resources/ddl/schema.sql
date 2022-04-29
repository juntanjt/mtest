drop table if exists `USER_TABLE`;

CREATE TABLE `USER_TABLE` (
  `id` INT NOT NULL,
  `name` VARCHAR(64) NOT NULL,
  `sex` VARCHAR(16) ,
  `age` VARCHAR(16) ,
  `post_code` VARCHAR(64) ,
  `country` VARCHAR(64) ,
  `province` VARCHAR(64) ,
  `abbreviation` VARCHAR(64) ,
  `telephone` VARCHAR(64) ,
  PRIMARY KEY (`id`)
) DEFAULT CHARSET=utf8;