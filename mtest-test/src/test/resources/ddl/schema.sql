drop table if exists `TABLE_ORDER`;

CREATE TABLE `TABLE_ORDER` (
  `id` INTEGER NOT NULL auto_increment,
  `user_id` INTEGER NOT NULL,
  `item_id` INTEGER NOT NULL,
  `item_count` INTEGER NOT NULL,
  `amount` DOUBLE NOT NULL,
  `order_name` VARCHAR(64) ,
  `address` VARCHAR(1024) ,
  `telephone` VARCHAR(64) ,
  `create_time` TIMESTAMP NOT NULL,
  PRIMARY KEY (`id`)
) DEFAULT CHARSET=utf8;