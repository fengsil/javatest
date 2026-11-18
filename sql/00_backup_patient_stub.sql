-- ============================================================================
-- 实验二  原 patient 草稿表定义备份（执行 medical_db.sql 之前该表 0 行数据）
-- 如需恢复：先删除 patient 相关外键子表，再执行本文件中的 CREATE TABLE。
-- 备份时间：2026-11-12
-- ============================================================================
CREATE TABLE `patient` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
