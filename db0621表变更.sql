ALTER TABLE `tl_account`
	ADD COLUMN `usernum` INT NULL DEFAULT '0' COMMENT '抓取用户数量' AFTER `status`,
	ADD COLUMN `groupnum` INT NULL DEFAULT '0' COMMENT '加入的群组数量' AFTER `usernum`;


ALTER TABLE `tl_group`
	CHANGE COLUMN `usernum` `usernum` INT NULL DEFAULT NULL AFTER `is_channel`,
	ADD COLUMN `out_` CHAR(1) NULL DEFAULT NULL COMMENT '0-有效，1-排除在外，不抓取用户，不加入' AFTER `status`;


ALTER TABLE `tl_user`
	ADD COLUMN `star` INT NULL DEFAULT NULL COMMENT '星级，值越大，评级越高' AFTER `msg_time`,
	ADD COLUMN `update_date` DATETIME NULL DEFAULT NULL AFTER `star`;


ALTER TABLE `tl_users`
	ADD COLUMN `from_group` VARCHAR(64) NOT NULL AFTER `username`;
ALTER TABLE `tl_group`
	ADD COLUMN `offset_` INT(11) NULL DEFAULT NULL COMMENT '抽取数据的索引偏移数' AFTER `usernum`,
	ADD COLUMN `update_num` INT(11) NULL DEFAULT NULL COMMENT '消息更新次数' AFTER `offset_`;

ALTER TABLE `tl_user`
	ADD COLUMN `msg_num` INT(11) NULL DEFAULT NULL COMMENT '消息数量' AFTER `star`;
