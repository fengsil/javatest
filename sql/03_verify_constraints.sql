-- ============================================================================
-- 实验二  约束核验脚本：逐项检查主键、非空、唯一、外键、CHECK 与 3NF 关联查询
-- 执行：mysql -uroot -p < 03_verify_constraints.sql
-- ============================================================================
USE `hospital systems`;

SELECT '===== 1. 数据表清单（引擎 / 字符集 / 注释） =====' AS section;
SELECT TABLE_NAME, ENGINE, TABLE_COLLATION, TABLE_COMMENT, TABLE_ROWS
FROM information_schema.TABLES
WHERE TABLE_SCHEMA = 'hospital systems' ORDER BY TABLE_NAME;

SELECT '===== 2. 主键、非空、唯一约束统计 =====' AS section;
SELECT TABLE_NAME,
       SUM(IF(COLUMN_KEY = 'PRI', 1, 0)) AS pk_count,
       SUM(IF(IS_NULLABLE = 'NO', 1, 0)) AS not_null_count,
       SUM(IF(COLUMN_KEY = 'UNI', 1, 0)) AS single_col_unique
FROM information_schema.COLUMNS
WHERE TABLE_SCHEMA = 'hospital systems'
GROUP BY TABLE_NAME ORDER BY TABLE_NAME;

SELECT '===== 3. 唯一索引明细 =====' AS section;
SELECT TABLE_NAME, INDEX_NAME, GROUP_CONCAT(COLUMN_NAME ORDER BY SEQ_IN_INDEX) AS columns_in_index
FROM information_schema.STATISTICS
WHERE TABLE_SCHEMA = 'hospital systems' AND NON_UNIQUE = 0 AND INDEX_NAME <> 'PRIMARY'
GROUP BY TABLE_NAME, INDEX_NAME ORDER BY TABLE_NAME, INDEX_NAME;

SELECT '===== 4. 外键明细 =====' AS section;
SELECT kcu.TABLE_NAME, kcu.CONSTRAINT_NAME, kcu.COLUMN_NAME,
       kcu.REFERENCED_TABLE_NAME, kcu.REFERENCED_COLUMN_NAME,
       rc.DELETE_RULE, rc.UPDATE_RULE
FROM information_schema.KEY_COLUMN_USAGE kcu
JOIN information_schema.REFERENTIAL_CONSTRAINTS rc
     ON rc.CONSTRAINT_SCHEMA = kcu.CONSTRAINT_SCHEMA
    AND rc.CONSTRAINT_NAME = kcu.CONSTRAINT_NAME
WHERE kcu.TABLE_SCHEMA = 'hospital systems' AND kcu.REFERENCED_TABLE_NAME IS NOT NULL
ORDER BY kcu.TABLE_NAME, kcu.CONSTRAINT_NAME;

SELECT '===== 5. CHECK 约束明细 =====' AS section;
SELECT tc.TABLE_NAME, cc.CONSTRAINT_NAME, cc.CHECK_CLAUSE
FROM information_schema.TABLE_CONSTRAINTS tc
JOIN information_schema.CHECK_CONSTRAINTS cc
     ON cc.CONSTRAINT_SCHEMA = tc.CONSTRAINT_SCHEMA
    AND cc.CONSTRAINT_NAME = tc.CONSTRAINT_NAME
WHERE tc.TABLE_SCHEMA = 'hospital systems' AND tc.CONSTRAINT_TYPE = 'CHECK'
ORDER BY tc.TABLE_NAME, cc.CONSTRAINT_NAME;

SELECT '===== 6. 3NF 关联还原：挂号信息不冗余存储，全部由外键 JOIN 得到 =====' AS section;
SELECT r.registration_no, r.visit_date, r.time_slot, r.status,
       p.name AS patient_name, p.phone AS patient_phone,
       d.name AS doctor_name, dep.dept_name, jt.title_name, jt.registration_fee
FROM registration r
JOIN patient p      ON r.patient_id = p.patient_id
JOIN doctor d       ON r.doctor_id  = d.doctor_id
JOIN department dep ON d.dept_id    = dep.dept_id
JOIN job_title jt   ON d.title_id   = jt.title_id
ORDER BY r.registration_id LIMIT 5;

SELECT '===== 7. 3NF 关联还原：处方头 + 明细 + 药品字典 =====' AS section;
SELECT pr.prescription_no, pr.diagnosis, dg.drug_name, dg.specification,
       it.dosage, it.frequency, it.days, it.quantity, dg.price
FROM prescription pr
JOIN prescription_item it ON it.prescription_id = pr.prescription_id
JOIN drug dg              ON dg.drug_id = it.drug_id
ORDER BY pr.prescription_id, it.item_id LIMIT 6;
