-- 执行 01_schema.sql 之前，实验库中原有 patient 草稿表的定义备份（该表 0 行数据）。
-- 如需恢复，先删除新建的 patient 相关外键表，再执行本文件中的 CREATE TABLE。
-- 备份时间：2026-11-12
CREATE TABLE `patient` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
