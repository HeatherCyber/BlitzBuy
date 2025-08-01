-- 创建订单表
CREATE TABLE `order` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '订单ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `goods_id` BIGINT NOT NULL COMMENT '商品ID',
    `delivery_address` VARCHAR(500) NOT NULL COMMENT '收货地址',
    `goods_name` VARCHAR(200) NOT NULL COMMENT '商品名称',
    `goods_count` INT NOT NULL DEFAULT 1 COMMENT '商品数量',
    `goods_price` DECIMAL(10,2) NOT NULL COMMENT '商品价格',
    `order_channel` TINYINT NOT NULL DEFAULT 1 COMMENT '订单渠道：1-秒杀，2-普通购买',
    `order_status` TINYINT NOT NULL DEFAULT 0 COMMENT '订单状态：0-未支付，1-已支付，2-已发货，3-已收货，4-已退款，5-已完成',
    `create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `pay_time` TIMESTAMP NULL COMMENT '付款时间',
    PRIMARY KEY (`id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_goods_id` (`goods_id`),
    INDEX `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- 创建秒杀订单表
CREATE TABLE `flash_sale_order` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '秒杀订单ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `order_id` BIGINT NOT NULL COMMENT '订单ID',
    `goods_id` BIGINT NOT NULL COMMENT '商品ID',
    `create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_goods` (`user_id`, `goods_id`) COMMENT '用户商品唯一索引，防止重复秒杀',
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_order_id` (`order_id`),
    INDEX `idx_goods_id` (`goods_id`),
    FOREIGN KEY (`order_id`) REFERENCES `order`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='秒杀订单表'; 

-- 创建用户信息表
CREATE TABLE `user` (
id BIGINT(20) NOT NULL COMMENT '用户ID, 设为主键, 唯一手机号',
nickname VARCHAR(255) NOT NULL DEFAULT '',
password VARCHAR(32) NOT NULL DEFAULT '' COMMENT 'MD5(MD5(pass明文+固定salt)+salt)',
salt VARCHAR(10) NOT NULL DEFAULT '',
head VARCHAR(128) NOT NULL DEFAULT '' COMMENT '头像',
register_date DATETIME DEFAULT NULL COMMENT '注册时间',
last_login_date DATETIME DEFAULT NULL COMMENT '最后一次登录时间',
login_count INT(11) DEFAULT '0' COMMENT '登录次数',
PRIMARY KEY(`id`)
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4;

-- 创建商品信息表
CREATE TABLE `goods` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '商品ID',
  `name` varchar(100) NOT NULL COMMENT '商品名称',
	`image_url` VARCHAR(255) COMMENT '商品主图URL(OSS/CDN地址)',
  `price` decimal(10,2) NOT NULL COMMENT '原价',
  `description` text COMMENT '商品详情',
	`stock` int NOT NULL COMMENT '库存总量',
  `status` tinyint DEFAULT 1 COMMENT '状态(1:上架 0:下架)',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品基础信息表';