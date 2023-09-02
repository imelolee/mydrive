/*
 Navicat Premium Data Transfer

 Source Server         : 192.168.83.129
 Source Server Type    : MySQL
 Source Server Version : 80034 (8.0.34-0ubuntu0.20.04.1)
 Source Host           : 192.168.83.129:3306
 Source Schema         : MyDrive

 Target Server Type    : MySQL
 Target Server Version : 80034 (8.0.34-0ubuntu0.20.04.1)
 File Encoding         : 65001

 Date: 02/09/2023 13:00:35
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for file_info
-- ----------------------------
DROP TABLE IF EXISTS `file_info`;
CREATE TABLE `file_info`  (
  `file_id` varchar(10) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT 'File ID',
  `user_id` varchar(10) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT 'User ID',
  `file_category` tinyint(1) NULL DEFAULT NULL COMMENT 'File Category：1-Video, 2-Audio, 3-Image, 4-Documnet, 5-Others',
  `file_type` tinyint(1) NULL DEFAULT NULL,
  `status` tinyint(1) NULL DEFAULT NULL COMMENT '0-Transfer, 1-Transfer Failed, 2-Transfer Successes',
  `recovery_time` datetime NULL DEFAULT NULL COMMENT 'Time move to recycle',
  `del_flag` tinyint(1) NULL DEFAULT NULL COMMENT 'Del Flag：0-Delete, 1-Recycle, 2-Normal',
  `file_pid` varchar(10) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT 'File Parent ID',
  `file_size` bigint NULL DEFAULT NULL COMMENT 'File Size',
  `file_name` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT 'File Name',
  `file_cover` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT 'File Cover',
  `file_path` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT 'File Path',
  `create_time` datetime NULL DEFAULT NULL COMMENT 'File Create Time',
  `last_update_time` datetime NULL DEFAULT NULL COMMENT 'File Last Update Time',
  `folder_type` tinyint(1) NULL DEFAULT NULL COMMENT 'Folder Type: 0-File, 1-Folder',
  `file_md5` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT 'File MD5 Value',
  PRIMARY KEY (`file_id`) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  INDEX `idx_del_flag`(`del_flag` ASC) USING BTREE,
  INDEX `idx_file_name`(`file_name` ASC) USING BTREE,
  INDEX `idx_file_pid`(`file_pid` ASC) USING BTREE,
  INDEX `idx_md5`(`file_md5` ASC) USING BTREE,
  INDEX `idx_recovery_time`(`recovery_time` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `user_id`(`user_id` ASC, `file_id` ASC) USING BTREE,
  CONSTRAINT `file_info_user_info_user_id_fk` FOREIGN KEY (`user_id`) REFERENCES `user_info` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = 'File Info Table' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for file_share
-- ----------------------------
DROP TABLE IF EXISTS `file_share`;
CREATE TABLE `file_share`  (
  `share_id` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT 'Share ID',
  `file_id` varchar(10) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT 'File ID',
  `user_id` varchar(10) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT 'User ID',
  `valid_type` tinyint NOT NULL COMMENT 'Valid Type: 0-1day, 1-7days, 2-30days, 3-no expired',
  `expire_time` datetime NULL DEFAULT NULL COMMENT 'Expired Time',
  `share_time` datetime NOT NULL COMMENT 'Share Time',
  `code` varchar(5) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT 'Code',
  `show_count` int NULL DEFAULT 0 COMMENT 'View Count',
  PRIMARY KEY (`share_id`) USING BTREE,
  INDEX `file_share_file_info_file_id__fk`(`user_id` ASC, `file_id` ASC) USING BTREE,
  CONSTRAINT `file_share_file_info_file_id__fk` FOREIGN KEY (`user_id`, `file_id`) REFERENCES `file_info` (`user_id`, `file_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = 'Share Info Table' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for user_info
-- ----------------------------
DROP TABLE IF EXISTS `user_info`;
CREATE TABLE `user_info`  (
  `user_id` varchar(10) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT 'User ID',
  `nick_name` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT 'NickName',
  `email` varchar(150) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT 'Email',
  `qq_open_id` varchar(35) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT 'Open ID',
  `qq_avatar` varchar(150) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT 'Avatar',
  `password` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT 'Password',
  `join_time` datetime NULL DEFAULT NULL COMMENT 'Join Time',
  `last_login_time` datetime NULL DEFAULT NULL COMMENT 'Last Login Time',
  `status` tinyint(1) NULL DEFAULT NULL COMMENT 'Account Status: 0-Disabled, 1-Enabled',
  `use_space` bigint NULL DEFAULT NULL COMMENT 'Use Space',
  `total_space` bigint NULL DEFAULT NULL COMMENT 'Total Space',
  PRIMARY KEY (`user_id`) USING BTREE,
  UNIQUE INDEX `key_email`(`email` ASC) USING BTREE,
  UNIQUE INDEX `key_qq_open_id`(`qq_open_id` ASC) USING BTREE,
  UNIQUE INDEX `key_nick_name`(`nick_name` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = 'User Info Table' ROW_FORMAT = DYNAMIC;

SET FOREIGN_KEY_CHECKS = 1;
