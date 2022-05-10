drop table if exists `order_table`;

CREATE TABLE `order_table` (
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