ALTER TABLE `tl_account`
	ADD COLUMN `usernum` INT NULL DEFAULT '0' COMMENT 'ץȡ�û�����' AFTER `status`,
	ADD COLUMN `groupnum` INT NULL DEFAULT '0' COMMENT '�����Ⱥ������' AFTER `usernum`;


ALTER TABLE `tl_group`
	CHANGE COLUMN `usernum` `usernum` INT NULL DEFAULT NULL AFTER `is_channel`,
	ADD COLUMN `out_` CHAR(1) NULL DEFAULT NULL COMMENT '0-��Ч��1-�ų����⣬��ץȡ�û���������' AFTER `status`;


ALTER TABLE `tl_user`
	ADD COLUMN `star` INT NULL DEFAULT NULL COMMENT '�Ǽ���ֵԽ������Խ��' AFTER `msg_time`,
	ADD COLUMN `update_date` DATETIME NULL DEFAULT NULL AFTER `star`;


ALTER TABLE `tl_users`
	ADD COLUMN `from_group` VARCHAR(64) NOT NULL AFTER `username`;
ALTER TABLE `tl_group`
	ADD COLUMN `offset_` INT(11) NULL DEFAULT NULL COMMENT '��ȡ���ݵ�����ƫ����' AFTER `usernum`,
	ADD COLUMN `update_num` INT(11) NULL DEFAULT NULL COMMENT '��Ϣ���´���' AFTER `offset_`;

ALTER TABLE `tl_user`
	ADD COLUMN `msg_num` INT(11) NULL DEFAULT NULL COMMENT '��Ϣ����' AFTER `star`;
