/*
SQLyog Community v13.1.7 (64 bit)
MySQL - 8.0.24 : Database - emulate_backend
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`emulate_backend` /*!40100 DEFAULT CHARACTER SET utf8 */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `emulate_backend`;

/*Table structure for table `backend_dept` */

DROP TABLE IF EXISTS `backend_dept`;

CREATE TABLE `backend_dept` (
  `dept_id` bigint NOT NULL AUTO_INCREMENT,
  `parent_id` bigint DEFAULT NULL COMMENT '上级部门ID，一级部门为0',
  `name` varchar(50) DEFAULT NULL COMMENT '部门名称',
  `order_num` int DEFAULT NULL COMMENT '排序',
  `del_flag` tinyint DEFAULT '0' COMMENT '是否删除  -1：已删除  0：正常',
  PRIMARY KEY (`dept_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='部门管理';

/*Data for the table `backend_dept` */

/*Table structure for table `backend_log` */

DROP TABLE IF EXISTS `backend_log`;

CREATE TABLE `backend_log` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) DEFAULT NULL COMMENT '用户名',
  `operation` varchar(50) DEFAULT NULL COMMENT '用户操作',
  `method` varchar(200) DEFAULT NULL COMMENT '请求方法',
  `params` varchar(5000) DEFAULT NULL COMMENT '请求参数',
  `time` bigint NOT NULL COMMENT '执行时长(毫秒)',
  `ip` varchar(64) DEFAULT NULL COMMENT 'IP地址',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统日志';

/*Data for the table `backend_log` */

/*Table structure for table `backend_menu` */

DROP TABLE IF EXISTS `backend_menu`;

CREATE TABLE `backend_menu` (
  `menu_id` bigint NOT NULL AUTO_INCREMENT,
  `parent_id` bigint DEFAULT '0' COMMENT '父菜单ID，一级菜单为0',
  `name` varchar(50) DEFAULT NULL COMMENT '菜单名称',
  `url` varchar(200) DEFAULT NULL COMMENT '菜单URL',
  `perms` varchar(500) DEFAULT NULL COMMENT '授权(多个用逗号分隔，如：user:list,user:create)',
  `type` tinyint DEFAULT '0' COMMENT '类型   0：目录   1：菜单   2：按钮',
  `shortcut` tinyint DEFAULT '0' COMMENT '0否1是 快捷菜单',
  `target` varchar(100) DEFAULT '_self' COMMENT '跳转目标',
  `icon` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT 'fa' COMMENT '菜单图标',
  `order_num` int DEFAULT '0' COMMENT '排序',
  PRIMARY KEY (`menu_id`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='菜单管理';

/*Data for the table `backend_menu` */

insert  into `backend_menu`(`menu_id`,`parent_id`,`name`,`url`,`perms`,`type`,`shortcut`,`target`,`icon`,`order_num`) values 
(1,0,'系统配置',NULL,NULL,0,0,'_self','fa fa-address-book',1),
(2,1,'用户管理','page/user/list.html','user:list',1,0,'_self','fa',2),
(3,1,'角色管理','page/role/list.html','role:list',1,0,'_self','fa',3),
(4,1,'菜单管理','page/menu/list.html','menu:list',1,0,'_self','fa',4),
(5,0,'UI示例','',NULL,0,0,'_self','fa fa-address-book',2),
(6,5,'组合表单','page/form.html',NULL,1,0,'_self','fa',1),
(7,5,'分步表单','page/form-step.html',NULL,1,0,'_self','fa',0),
(8,5,'layer示例','page/layer.html',NULL,1,0,'_self','fa',0),
(9,5,'富文本示例','page/editor.html',NULL,1,0,'_self','fa',0),
(10,5,'表格示例','page/table.html',NULL,1,0,'_self','fa',0),
(11,5,'上传示例','page/upload.html',NULL,1,0,'_self','fa',0),
(12,5,'按钮示例','page/button.html',NULL,1,0,'_self','fa',0),
(16,3,'角色删除','','role:delete',2,0,'_self','fa',0),
(17,3,'角色保存','','role:save,menu:role:select',2,0,'_self','fa',0),
(18,4,'菜单保存','','menu:save,menu:select',2,0,'_self','fa',0),
(19,4,'菜单删除','','menu:delete',2,0,'_self','fa',0),
(20,2,'用户保存','','user:save,role:select',2,0,'_self','fa',0),
(21,2,'用户删除','','user:delete',2,0,'_self','fa',0);

/*Table structure for table `backend_role` */

DROP TABLE IF EXISTS `backend_role`;

CREATE TABLE `backend_role` (
  `role_id` bigint NOT NULL AUTO_INCREMENT,
  `role_name` varchar(100) DEFAULT NULL COMMENT '角色名称',
  `remark` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '备注',
  `dept_id` bigint DEFAULT '0' COMMENT '部门ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色';

/*Data for the table `backend_role` */

insert  into `backend_role`(`role_id`,`role_name`,`remark`,`dept_id`,`create_time`) values 
(1,'超级管理员','系统内置',0,'2021-09-24 23:31:01'),
(2,'内部研发员','内部研发',0,'2021-09-25 00:22:15'),
(3,'内部测试员','内部测试',0,'2021-09-25 00:22:53');

/*Table structure for table `backend_role_dept` */

DROP TABLE IF EXISTS `backend_role_dept`;

CREATE TABLE `backend_role_dept` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `role_id` bigint DEFAULT NULL COMMENT '角色ID',
  `dept_id` bigint DEFAULT NULL COMMENT '部门ID',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色与部门对应关系';

/*Data for the table `backend_role_dept` */

/*Table structure for table `backend_role_menu` */

DROP TABLE IF EXISTS `backend_role_menu`;

CREATE TABLE `backend_role_menu` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `role_id` bigint NOT NULL,
  `menu_id` bigint NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=55 DEFAULT CHARSET=utf8mb3;

/*Data for the table `backend_role_menu` */

insert  into `backend_role_menu`(`id`,`role_id`,`menu_id`) values 
(29,3,5),
(30,3,6),
(31,3,7),
(32,3,8),
(33,3,9),
(34,3,10),
(35,3,11),
(36,3,12),
(37,1,1),
(38,1,2),
(39,1,20),
(40,1,21),
(41,1,3),
(42,1,16),
(43,1,17),
(44,1,4),
(45,1,18),
(46,1,19),
(47,1,5),
(48,1,6),
(49,1,7),
(50,1,8),
(51,1,9),
(52,1,10),
(53,1,11),
(54,1,12);

/*Table structure for table `backend_user` */

DROP TABLE IF EXISTS `backend_user`;

CREATE TABLE `backend_user` (
  `user_id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(100) DEFAULT NULL COMMENT '密码',
  `salt` varchar(20) DEFAULT NULL COMMENT '盐',
  `mobile` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '手机号',
  `nickname` varchar(200) DEFAULT '' COMMENT '昵称',
  `status` tinyint DEFAULT '0' COMMENT '状态  0：禁用   1：正常',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统用户';

/*Data for the table `backend_user` */

insert  into `backend_user`(`user_id`,`username`,`password`,`salt`,`mobile`,`nickname`,`status`,`create_time`) values 
(1,'admin','uk2oBUj0KEW/e4zLfum1gg==','4Z9Bnjk9piw8hN0Y','1388888888','系统超管',1,'2021-09-22 21:37:43'),
(8,'test002','MqynbI9QXGBOemVLjCUuCA==','4Z9Bnjk9piw8hN0Y','13899999211','测试尼玛',0,'2021-09-27 04:50:14'),
(9,'123111','MqynbI9QXGBOemVLjCUuCA==','4Z9Bnjk9piw8hN0Y','13423311123','1232323',0,'2021-09-27 21:43:27'),
(10,'13423311123','MqynbI9QXGBOemVLjCUuCA==','4Z9Bnjk9piw8hN0Y','13423311123','13423311123',1,'2021-09-27 22:53:52');

/*Table structure for table `backend_user_role` */

DROP TABLE IF EXISTS `backend_user_role`;

CREATE TABLE `backend_user_role` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint DEFAULT NULL COMMENT '用户ID',
  `role_id` bigint DEFAULT NULL COMMENT '角色ID',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户与角色对应关系';

/*Data for the table `backend_user_role` */

insert  into `backend_user_role`(`id`,`user_id`,`role_id`) values 
(1,1,1),
(7,6,1),
(22,9,2),
(23,9,3),
(24,10,3),
(25,8,1);

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
