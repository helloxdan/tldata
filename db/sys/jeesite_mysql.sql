SET SESSION FOREIGN_KEY_CHECKS=0;

/* Drop Tables */

DROP TABLE IF EXISTS sys_tenant;
DROP TABLE IF EXISTS sys_role_office;
DROP TABLE IF EXISTS sys_user_role;
DROP TABLE IF EXISTS sys_user;
DROP TABLE IF EXISTS sys_office;
DROP TABLE IF EXISTS sys_area;
DROP TABLE IF EXISTS sys_dict;
DROP TABLE IF EXISTS sys_log;
DROP TABLE IF EXISTS sys_mdict;
DROP TABLE IF EXISTS sys_role_menu;
DROP TABLE IF EXISTS sys_menu;
DROP TABLE IF EXISTS sys_role;
DROP TABLE IF EXISTS sys_msg_user;
DROP TABLE IF EXISTS sys_msg;
DROP TABLE IF EXISTS sys_app_version;

 
/* Create Tables */

CREATE TABLE sys_tenant
(
	id varchar(64) NOT NULL COMMENT '编号', 
	name varchar(100) NOT NULL COMMENT '名称',
	code varchar(32) NOT NULL COMMENT '自定义编码',
	theme varchar(100) COMMENT '样式风格',
	logo varchar(100) COMMENT 'logo url',
	url varchar(100) COMMENT 'url',
	address varchar(255) COMMENT '联系地址',
	master varchar(100) COMMENT '负责人',
	phone varchar(200) COMMENT '电话',
	hot_tel varchar(32) COMMENT '热线电话',
	fax varchar(200) COMMENT '传真',
	email varchar(200) COMMENT '邮箱',
	description varchar(100) COMMENT '描述',
	sn          VARCHAR(32) COMMENT '编号',
	level_      VARCHAR(32) COMMENT '等级',
	type        VARCHAR(32) COMMENT '性质',
	class_      VARCHAR(32) COMMENT '分类',
	useable varchar(64) COMMENT '是否启用',
	`admin_account` VARCHAR(30) NULL DEFAULT NULL COMMENT '管理员账号',
	`status` CHAR(1) NULL DEFAULT NULL COMMENT '状态',
	`end_date` DATETIME NULL DEFAULT NULL COMMENT '到期时间',
	remarks varchar(255) COMMENT '备注信息',  
	create_by varchar(64) NOT NULL COMMENT '创建者',
	create_date datetime NOT NULL COMMENT '创建时间',
	update_by varchar(64) NOT NULL COMMENT '更新者',
	update_date datetime NOT NULL COMMENT '更新时间',
	del_flag char(1) DEFAULT '0' NOT NULL COMMENT '删除标记',
	PRIMARY KEY (id),
	UNIQUE INDEX UN_SYS_TENANT_CODE (code)
) COMMENT = '租户表';

CREATE TABLE sys_area
(
	id varchar(64) NOT NULL COMMENT '编号',
	parent_id varchar(64) NOT NULL COMMENT '父级编号',
	parent_ids varchar(2000) NOT NULL COMMENT '所有父级编号',
	name varchar(100) NOT NULL COMMENT '名称',
	sort decimal(10,0) NOT NULL COMMENT '排序',
	code varchar(100) COMMENT '区域编码',
	type char(1) COMMENT '区域类型',
	tenant_id varchar(64) NOT NULL COMMENT '所属租户',
	create_by varchar(64) NOT NULL COMMENT '创建者',
	create_date datetime NOT NULL COMMENT '创建时间',
	update_by varchar(64) NOT NULL COMMENT '更新者',
	update_date datetime NOT NULL COMMENT '更新时间',
	remarks varchar(255) COMMENT '备注信息',
	del_flag char(1) DEFAULT '0' NOT NULL COMMENT '删除标记',
	PRIMARY KEY (id)
) COMMENT = '区域表';


CREATE TABLE sys_dict
(
	id varchar(64) NOT NULL COMMENT '编号',
	value varchar(100) NOT NULL COMMENT '数据值',
	label varchar(100) NOT NULL COMMENT '标签名',
	type varchar(100) NOT NULL COMMENT '类型',
	description varchar(100) NOT NULL COMMENT '描述',
	sort decimal(10,0) NOT NULL COMMENT '排序（升序）',
	parent_id varchar(64) DEFAULT '0' COMMENT '父级编号',
	tenant_id varchar(64) NOT NULL COMMENT '所属租户',
	create_by varchar(64) NOT NULL COMMENT '创建者',
	create_date datetime NOT NULL COMMENT '创建时间',
	update_by varchar(64) NOT NULL COMMENT '更新者',
	update_date datetime NOT NULL COMMENT '更新时间',
	remarks varchar(255) COMMENT '备注信息',
	del_flag char(1) DEFAULT '0' NOT NULL COMMENT '删除标记',
	PRIMARY KEY (id)
) COMMENT = '字典表';


CREATE TABLE sys_log
(
	id varchar(64) NOT NULL COMMENT '编号',
	type char(1) DEFAULT '1' COMMENT '日志类型',
	title varchar(255) DEFAULT '' COMMENT '日志标题',
	create_by varchar(64) COMMENT '创建者',
	create_date datetime COMMENT '创建时间',
	tenant_id varchar(64) NOT NULL COMMENT '所属租户',
	remote_addr varchar(255) COMMENT '操作IP地址',
	user_agent varchar(255) COMMENT '用户代理',
	request_uri varchar(255) COMMENT '请求URI',
	method varchar(5) COMMENT '操作方式',
	params text COMMENT '操作提交的数据',
	exception text COMMENT '异常信息',
	PRIMARY KEY (id)
) COMMENT = '日志表';


CREATE TABLE sys_mdict
(
	id varchar(64) NOT NULL COMMENT '编号',
	parent_id varchar(64) NOT NULL COMMENT '父级编号',
	parent_ids varchar(2000) NOT NULL COMMENT '所有父级编号',
	name varchar(100) NOT NULL COMMENT '名称',
	sort decimal(10,0) NOT NULL COMMENT '排序',
	description varchar(100) COMMENT '描述',
	tenant_id varchar(64) NOT NULL COMMENT '所属租户',
	create_by varchar(64) NOT NULL COMMENT '创建者',
	create_date datetime NOT NULL COMMENT '创建时间',
	update_by varchar(64) NOT NULL COMMENT '更新者',
	update_date datetime NOT NULL COMMENT '更新时间',
	remarks varchar(255) COMMENT '备注信息',
	del_flag char(1) DEFAULT '0' NOT NULL COMMENT '删除标记',
	PRIMARY KEY (id)
) COMMENT = '多级字典表';


CREATE TABLE sys_menu
(
	id varchar(64) NOT NULL COMMENT '编号',
	parent_id varchar(64) NOT NULL COMMENT '父级编号',
	parent_ids varchar(2000) NOT NULL COMMENT '所有父级编号',
	name varchar(100) NOT NULL COMMENT '名称',
	sort decimal(10,0) NOT NULL COMMENT '排序',
	href varchar(2000) COMMENT '链接',
	target varchar(20) COMMENT '目标',
	icon varchar(100) COMMENT '图标',
	is_show char(1) NOT NULL COMMENT '是否在菜单中显示',
	permission varchar(200) COMMENT '权限标识',
	type varchar(32) NOT NULL COMMENT '授权类型,normal-通用，author-需要授权',
	tenant_id varchar(64) NOT NULL COMMENT '所属租户',
	remarks varchar(255) COMMENT '备注信息',
	create_by varchar(64) NOT NULL COMMENT '创建者',
	create_date datetime NOT NULL COMMENT '创建时间',
	update_by varchar(64) NOT NULL COMMENT '更新者',
	update_date datetime NOT NULL COMMENT '更新时间',
	del_flag char(1) DEFAULT '0' NOT NULL COMMENT '删除标记',
	PRIMARY KEY (id)
) COMMENT = '菜单表';


CREATE TABLE sys_office
(
	id varchar(64) NOT NULL COMMENT '编号',
	parent_id varchar(64) NOT NULL COMMENT '父级编号',
	parent_ids varchar(2000) NOT NULL COMMENT '所有父级编号',
	name varchar(100) NOT NULL COMMENT '名称',
	sort decimal(10,0) NOT NULL COMMENT '排序',
	area_id varchar(64) NOT NULL COMMENT '归属区域',
	code varchar(100) COMMENT '区域编码',
	type char(1) NOT NULL COMMENT '机构类型',
	grade char(1) NOT NULL COMMENT '机构等级',
	address varchar(255) COMMENT '联系地址',
	zip_code varchar(100) COMMENT '邮政编码',
	master varchar(100) COMMENT '负责人',
	phone varchar(200) COMMENT '电话',
	fax varchar(200) COMMENT '传真',
	email varchar(200) COMMENT '邮箱',
	USEABLE varchar(64) COMMENT '是否启用',
	PRIMARY_PERSON varchar(64) COMMENT '主负责人',
	DEPUTY_PERSON varchar(64) COMMENT '副负责人',
	tenant_id varchar(64) NOT NULL COMMENT '所属租户',
	create_by varchar(64) NOT NULL COMMENT '创建者',
	create_date datetime NOT NULL COMMENT '创建时间',
	update_by varchar(64) NOT NULL COMMENT '更新者',
	update_date datetime NOT NULL COMMENT '更新时间',
	remarks varchar(255) COMMENT '备注信息',
	del_flag char(1) DEFAULT '0' NOT NULL COMMENT '删除标记',
	PRIMARY KEY (id)
) COMMENT = '机构表';


CREATE TABLE sys_role
(
	id varchar(64) NOT NULL COMMENT '编号',
	office_id varchar(64) COMMENT '归属机构',
	name varchar(100) NOT NULL COMMENT '角色名称',
	enname varchar(255) COMMENT '英文名称',
	role_type varchar(255) COMMENT '角色类型',
	data_scope char(1) COMMENT '数据范围',
	is_sys varchar(64) COMMENT '是否系统数据',
	useable varchar(64) COMMENT '是否可用',
	tenant_id varchar(64) NOT NULL COMMENT '所属租户',
	create_by varchar(64) NOT NULL COMMENT '创建者',
	create_date datetime NOT NULL COMMENT '创建时间',
	update_by varchar(64) NOT NULL COMMENT '更新者',
	update_date datetime NOT NULL COMMENT '更新时间',
	remarks varchar(255) COMMENT '备注信息',
	del_flag char(1) DEFAULT '0' NOT NULL COMMENT '删除标记',
	PRIMARY KEY (id)
) COMMENT = '角色表';


CREATE TABLE sys_role_menu
(
	role_id varchar(64) NOT NULL COMMENT '角色编号',
	menu_id varchar(64) NOT NULL COMMENT '菜单编号',
	tenant_id varchar(64) NOT NULL COMMENT '所属租户',
	PRIMARY KEY (role_id, menu_id)
) COMMENT = '角色-菜单';


CREATE TABLE sys_role_office
(
	role_id varchar(64) NOT NULL COMMENT '角色编号',
	office_id varchar(64) NOT NULL COMMENT '机构编号',
	tenant_id varchar(64) NOT NULL COMMENT '所属租户',
	PRIMARY KEY (role_id, office_id)
) COMMENT = '角色-机构';


CREATE TABLE sys_user
(
	id varchar(64) NOT NULL COMMENT '编号',
	company_id varchar(64) NOT NULL COMMENT '归属公司',
	office_id varchar(64) NOT NULL COMMENT '归属部门',
	login_name varchar(100) NOT NULL COMMENT '登录名',
	password varchar(100) NOT NULL COMMENT '密码',
	no varchar(100) COMMENT '工号',
	name varchar(100) NOT NULL COMMENT '姓名',
	nickname varchar(100)  COMMENT '昵称',
	gender char(1) COMMENT '性别，M-男，F-女',
	birthday  datetime  COMMENT '生日',
	email varchar(200) COMMENT '邮箱',
	phone varchar(200) COMMENT '电话',
	mobile varchar(200) COMMENT '手机',
	address      VARCHAR(100),
	user_type char(1) COMMENT '用户类型',
	level_      VARCHAR(30) COMMENT '级别',
	post_      VARCHAR(30) COMMENT '职位',
	post_state      VARCHAR(30)  COMMENT '岗位状态',
	photo varchar(1000) COMMENT '用户头像',
	login_ip varchar(100) COMMENT '最后登陆IP',
	login_date datetime COMMENT '最后登陆时间',
	login_flag varchar(64) COMMENT '是否可登录',
	tenant_id varchar(64) NOT NULL COMMENT '所属租户',
	create_by varchar(64) NOT NULL COMMENT '创建者',
	create_date datetime NOT NULL COMMENT '创建时间',
	update_by varchar(64) NOT NULL COMMENT '更新者',
	update_date datetime NOT NULL COMMENT '更新时间',
	remarks varchar(255) COMMENT '备注信息',
	del_flag char(1) DEFAULT '0' NOT NULL COMMENT '删除标记',
	PRIMARY KEY (id)
) COMMENT = '用户表';


CREATE TABLE sys_user_role
(
	user_id varchar(64) NOT NULL COMMENT '用户编号',
	role_id varchar(64) NOT NULL COMMENT '角色编号',
	tenant_id varchar(64) NOT NULL COMMENT '所属租户',
	PRIMARY KEY (user_id, role_id)
) COMMENT = '用户-角色';

-- Create table
create table SYS_MSG
(
  id          VARCHAR(64) not null,
  type        VARCHAR(64) COMMENT'公告、通知、提醒',
  title       VARCHAR(32),
  content     VARCHAR(1000),
  from_       VARCHAR(32),
  user_type   VARCHAR(64) COMMENT'发送的用户类型',
  user_desc   VARCHAR(100) COMMENT'接收用户描述',
  user_data   VARCHAR(4000) COMMENT '接收用户json数据，用于延时发送',
  ticket      VARCHAR(64) COMMENT '发送短信票据',
  status      CHAR(1) default '0' COMMENT '是否关闭',  
   send_Time   datetime(6),
  send_state CHAR(1),
  remark      VARCHAR(200) COMMENT '备注',
  tenant_id   VARCHAR(64),
  create_by   VARCHAR(64),
  create_date datetime(6),
  update_by   VARCHAR(64),
  update_date datetime(6),
  del_flag    CHAR(1),
  PRIMARY KEY (id)
)COMMENT = '系统消息';
 
 
-- Create table
create table SYS_MSG_USER
(
  id        VARCHAR(64),
  msg_id    VARCHAR(64),
  user_id   VARCHAR(64),
  read_flag CHAR(1) default '0' COMMENT '0-未读，1-已读',
  read_date datetime(6),
  tenant_id VARCHAR(64),
  PRIMARY KEY (id)
)COMMENT = '系统消息用户关系，记录用户阅读记录'; 
 
CREATE TABLE `sys_msg_tpl` (
	`id` VARCHAR(64) NOT NULL,
	`type` VARCHAR(64) NULL DEFAULT NULL COMMENT '0-站内消息，1-短信',
	`title` VARCHAR(100) NULL DEFAULT NULL COMMENT '模板标题',
	`content` VARCHAR(1000) NULL DEFAULT NULL,
	`status` CHAR(1) NULL DEFAULT '0' COMMENT '是否关闭',
	`tenant_id` VARCHAR(64) NULL DEFAULT NULL,
	`create_by` VARCHAR(64) NULL DEFAULT NULL,
	`create_date` DATETIME(6) NULL DEFAULT NULL,
	`update_by` VARCHAR(64) NULL DEFAULT NULL,
	`update_date` DATETIME(6) NULL DEFAULT NULL,
	`del_flag` CHAR(1) NULL DEFAULT NULL,
	PRIMARY KEY (`id`),
	INDEX `idx_sys_msg_tpl_type` (`type`),
	INDEX `idx_sys_msg_tpl_del_flag` (`del_flag`)
)
COMMENT='系统消息模板'
COLLATE='utf8_general_ci'
ENGINE=InnoDB
;

create table sys_app_version
(
   id                   varchar(64) not null,
   apptype           	varchar(16) not null comment 'App类型(IOS,Android)',
   version           	varchar(32) comment '版本号',
   code           		varchar(32) comment 'App编码',
   name          		varchar(32) comment 'App名称',
   url                  varchar(100) comment 'App下载地址',
    description                  varchar(200) comment '发布说明',
   state	            varchar(16) not null comment '状态',
   `tenant_id` VARCHAR(64) NULL DEFAULT NULL,
   create_by            varchar(64) not null comment '创建人',
   create_date          DATETIME not null default current_timestamp comment '创建时间',
   update_by            varchar(64) not null comment '修改人',
   update_date          DATETIME not null default current_timestamp comment '修改时间',
   del_flag             varchar(10) not null default '0' comment '删除标记',
   primary key (id),
   index apptype (apptype),
   index create_by (create_date)
);

alter table sys_app_version comment 'App版本管理 ';

-- Create table
create table SYS_FEEDBACK
(
  id            VARCHAR(64) not null,
  type          CHAR(1) not null comment '问题类型',
  title         VARCHAR(100) comment '',
  content       VARCHAR(500) comment '问题内容',
  report_by     VARCHAR(64) not null comment '报告人',
  report_date   DATE not null comment '报告时间',
  remarks       VARCHAR(100) comment '备注',
  reply_by      VARCHAR(64) comment '回复人',
  reply_content VARCHAR(500) comment '回复内容',
  attachment    VARCHAR(100) comment '附件',
  state         VARCHAR(10) comment '状态',
  tenant_id     VARCHAR(64) not null,
  create_by     VARCHAR(64) not null,
  create_date   DATETIME default CURRENT_TIMESTAMP not null,
  update_by     VARCHAR(64) not null,
  update_date   DATETIME default CURRENT_TIMESTAMP not null,
  del_flag      CHAR(1) default '0' not null,
  	PRIMARY KEY (`id`)
) 
COMMENT='系统问题反馈 ';  

/* Create Indexes */

CREATE INDEX sys_area_parent_id ON sys_area (parent_id ASC);
/*CREATE INDEX sys_area_parent_ids ON sys_area (parent_ids ASC);*/
CREATE INDEX sys_area_del_flag ON sys_area (del_flag ASC);
CREATE INDEX sys_dict_value ON sys_dict (value ASC);
CREATE INDEX sys_dict_label ON sys_dict (label ASC);
CREATE INDEX sys_dict_del_flag ON sys_dict (del_flag ASC);
CREATE INDEX sys_log_create_by ON sys_log (create_by ASC);
CREATE INDEX sys_log_request_uri ON sys_log (request_uri ASC);
CREATE INDEX sys_log_type ON sys_log (type ASC);
CREATE INDEX sys_log_create_date ON sys_log (create_date ASC);
CREATE INDEX sys_mdict_parent_id ON sys_mdict (parent_id ASC);
/*CREATE INDEX sys_mdict_parent_ids ON sys_mdict (parent_ids ASC);*/
CREATE INDEX sys_mdict_del_flag ON sys_mdict (del_flag ASC);
CREATE INDEX sys_menu_parent_id ON sys_menu (parent_id ASC);
/*CREATE INDEX sys_menu_parent_ids ON sys_menu (parent_ids ASC);*/
CREATE INDEX sys_menu_del_flag ON sys_menu (del_flag ASC);
CREATE INDEX sys_office_parent_id ON sys_office (parent_id ASC);
/*CREATE INDEX sys_office_parent_ids ON sys_office (parent_ids ASC);*/
CREATE INDEX sys_office_del_flag ON sys_office (del_flag ASC);
CREATE INDEX sys_office_type ON sys_office (type ASC);
CREATE INDEX sys_role_del_flag ON sys_role (del_flag ASC);
CREATE INDEX sys_role_enname ON sys_role (enname ASC);
CREATE INDEX sys_user_office_id ON sys_user (office_id ASC);
CREATE INDEX sys_user_login_name ON sys_user (login_name ASC);
CREATE INDEX sys_user_company_id ON sys_user (company_id ASC);
CREATE INDEX sys_user_update_date ON sys_user (update_date ASC);
CREATE INDEX sys_user_del_flag ON sys_user (del_flag ASC);
CREATE INDEX idx_sys_msg_type ON sys_msg (type);
CREATE INDEX idx_sys_msg_del_flag ON sys_msg (del_flag);
CREATE INDEX idx_sys_msg_user_msg ON sys_msg_user (msg_id);
CREATE INDEX idx_sys_msg_user_userid ON sys_msg_user (user_id); 
create index IDX_SYS_FEEDBACK_U on SYS_FEEDBACK (REPORT_BY) ; 

