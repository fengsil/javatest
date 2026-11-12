-- ============================================================================
-- 实验二  医疗系统数据库设计与基础 CRUD —— 建表脚本
-- 目标库：hospital systems（MySQL 8.0.45 / InnoDB / utf8mb4_unicode_ci）
-- 设计原则：严格第三范式（3NF），全部业务字段原子化，非主属性只依赖于候选键，
--           且不存在传递依赖；无派生字段、无重复组、无冗余快照字段。
-- 执行方式：mysql -uroot -p < 01_schema.sql
-- 注意：脚本按外键逆序 DROP 后重建，仅用于实验库初始化；原 patient 草稿表的
--       定义已备份在 00_backup_patient_stub.sql，可随时恢复。
-- ============================================================================

CREATE DATABASE IF NOT EXISTS `hospital systems`
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;
USE `hospital systems`;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS prescription_item;
DROP TABLE IF EXISTS prescription;
DROP TABLE IF EXISTS registration;
DROP TABLE IF EXISTS patient;
DROP TABLE IF EXISTS doctor;
DROP TABLE IF EXISTS job_title;
DROP TABLE IF EXISTS department;
DROP TABLE IF EXISTS drug;

SET FOREIGN_KEY_CHECKS = 1;

-- ---------------------------------------------------------------------------
-- 1. 科室表 department（核心实体 1）
--    dept_id 为主键；dept_code / dept_name 唯一；所有业务字段非空。
--    不保存"医生人数""处方数量"等可统计派生的字段（避免冗余）。
-- ---------------------------------------------------------------------------
CREATE TABLE department (
    dept_id     BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '科室主键',
    dept_code   VARCHAR(20)     NOT NULL                COMMENT '科室编码，如 D001',
    dept_name   VARCHAR(64)     NOT NULL                COMMENT '科室名称',
    location    VARCHAR(128)    NOT NULL                COMMENT '门诊位置（楼栋-楼层-诊区）',
    tel         VARCHAR(20)     NOT NULL                COMMENT '科室联系电话',
    description VARCHAR(255)    DEFAULT NULL            COMMENT '科室简介（可空，不参与键依赖）',
    created_at  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP                    COMMENT '创建时间',
    updated_at  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (dept_id),
    CONSTRAINT uk_department_code UNIQUE (dept_code),
    CONSTRAINT uk_department_name UNIQUE (dept_name)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '科室';

-- ---------------------------------------------------------------------------
-- 2. 职称字典表 job_title（消除传递依赖的支撑实体）
--    挂号费 registration_fee 由"职称"决定，而不是由"医生"决定。
--    若把挂号费放在 doctor 表，则存在 doctor_id -> title -> fee 的传递依赖，
--    违反 3NF；因此独立成字典表，doctor 仅存 title_id 外键。
-- ---------------------------------------------------------------------------
CREATE TABLE job_title (
    title_id          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '职称主键',
    title_code        VARCHAR(20)     NOT NULL                COMMENT '职称编码',
    title_name        VARCHAR(32)     NOT NULL                COMMENT '职称名称',
    registration_fee  DECIMAL(8, 2)   NOT NULL DEFAULT 0.00   COMMENT '该职称对应的挂号费（元）',
    PRIMARY KEY (title_id),
    CONSTRAINT uk_job_title_code UNIQUE (title_code),
    CONSTRAINT uk_job_title_name UNIQUE (title_name),
    CONSTRAINT ck_job_title_fee  CHECK (registration_fee >= 0)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '职称与挂号费字典';

-- ---------------------------------------------------------------------------
-- 3. 药品字典表 drug（支撑实体）
--    药品名称/规格/单位/单价只在此表保存一次，处方明细只存 drug_id。
-- ---------------------------------------------------------------------------
CREATE TABLE drug (
    drug_id       BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '药品主键',
    drug_code     VARCHAR(32)     NOT NULL                COMMENT '药品编码（国药准字/院内编码）',
    drug_name     VARCHAR(64)     NOT NULL                COMMENT '药品通用名',
    specification VARCHAR(64)     NOT NULL                COMMENT '规格',
    unit          VARCHAR(16)     NOT NULL                COMMENT '计价单位',
    price         DECIMAL(10, 2)  NOT NULL                COMMENT '单价（元）',
    PRIMARY KEY (drug_id),
    CONSTRAINT uk_drug_code UNIQUE (drug_code),
    CONSTRAINT ck_drug_price CHECK (price >= 0)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '药品字典';

-- ---------------------------------------------------------------------------
-- 4. 医生表 doctor（核心实体 2）
--    dept_id / title_id 均为外键；工号、手机号、身份证号唯一且非空。
--    不保存所属科室名称、职称名称、挂号费、年龄/出生日期（可由身份证推导）。
-- ---------------------------------------------------------------------------
CREATE TABLE doctor (
    doctor_id  BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '医生主键',
    doctor_no  VARCHAR(24)     NOT NULL                COMMENT '医生工号',
    dept_id    BIGINT UNSIGNED NOT NULL                COMMENT '所属科室（外键）',
    title_id   BIGINT UNSIGNED NOT NULL                COMMENT '职称（外键）',
    name       VARCHAR(32)     NOT NULL                COMMENT '姓名',
    gender     CHAR(1)         NOT NULL                COMMENT '性别：M 男 / F 女',
    phone      CHAR(11)        NOT NULL                COMMENT '手机号（11 位）',
    id_card    CHAR(18)        NOT NULL                COMMENT '身份证号（18 位）',
    status     TINYINT         NOT NULL DEFAULT 1      COMMENT '状态：1 在诊 / 0 停诊',
    created_at DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP                    COMMENT '创建时间',
    updated_at DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (doctor_id),
    CONSTRAINT uk_doctor_no     UNIQUE (doctor_no),
    CONSTRAINT uk_doctor_phone  UNIQUE (phone),
    CONSTRAINT uk_doctor_idcard UNIQUE (id_card),
    KEY idx_doctor_dept  (dept_id),
    KEY idx_doctor_title (title_id),
    KEY idx_doctor_name  (name),
    CONSTRAINT fk_doctor_dept  FOREIGN KEY (dept_id)  REFERENCES department (dept_id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_doctor_title FOREIGN KEY (title_id) REFERENCES job_title  (title_id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT ck_doctor_gender  CHECK (gender IN ('M', 'F')),
    CONSTRAINT ck_doctor_status  CHECK (status IN (0, 1)),
    CONSTRAINT ck_doctor_phone   CHECK (phone REGEXP '^1[3-9][0-9]{9}$'),
    CONSTRAINT ck_doctor_idcard  CHECK (id_card REGEXP '^[0-9]{17}[0-9Xx]$')
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '医生';

-- ---------------------------------------------------------------------------
-- 5. 患者表 patient（核心实体 3）
--    就诊卡号、手机号、身份证号唯一；性别/联系方式原子化，无冗余联系人组。
-- ---------------------------------------------------------------------------
CREATE TABLE patient (
    patient_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '患者主键',
    patient_no VARCHAR(24)     NOT NULL                COMMENT '就诊卡号',
    name       VARCHAR(32)     NOT NULL                COMMENT '姓名',
    gender     CHAR(1)         NOT NULL                COMMENT '性别：M 男 / F 女',
    phone      CHAR(11)        NOT NULL                COMMENT '手机号（11 位）',
    id_card    CHAR(18)        NOT NULL                COMMENT '身份证号（18 位）',
    address    VARCHAR(200)    DEFAULT NULL            COMMENT '联系地址（单一原子值）',
    created_at DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP                    COMMENT '创建时间',
    updated_at DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (patient_id),
    CONSTRAINT uk_patient_no     UNIQUE (patient_no),
    CONSTRAINT uk_patient_phone  UNIQUE (phone),
    CONSTRAINT uk_patient_idcard UNIQUE (id_card),
    KEY idx_patient_name (name),
    CONSTRAINT ck_patient_gender  CHECK (gender IN ('M', 'F')),
    CONSTRAINT ck_patient_phone   CHECK (phone REGEXP '^1[3-9][0-9]{9}$'),
    CONSTRAINT ck_patient_idcard  CHECK (id_card REGEXP '^[0-9]{17}[0-9Xx]$')
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '患者';

-- ---------------------------------------------------------------------------
-- 6. 挂号记录表 registration（核心实体 4）
--    只存 patient_id 与 doctor_id 两个外键，不存患者姓名、医生姓名、科室名称、
--    挂号费：这些都能通过外键关联得到，写入即构成冗余/传递依赖。
--    (doctor_id, visit_date, time_slot) 唯一，保证同一医生同一时段不重复放号。
-- ---------------------------------------------------------------------------
CREATE TABLE registration (
    registration_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '挂号记录主键',
    registration_no VARCHAR(32)     NOT NULL               COMMENT '挂号单号',
    patient_id      BIGINT UNSIGNED NOT NULL               COMMENT '患者（外键）',
    doctor_id       BIGINT UNSIGNED NOT NULL               COMMENT '接诊医生（外键）',
    visit_date      DATE            NOT NULL               COMMENT '就诊日期',
    time_slot       VARCHAR(16)     NOT NULL               COMMENT '时段：AM / PM',
    status          VARCHAR(16)     NOT NULL DEFAULT 'BOOKED' COMMENT '状态：BOOKED / COMPLETED / CANCELLED',
    symptom         VARCHAR(255)    DEFAULT NULL           COMMENT '主诉',
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP                    COMMENT '创建时间',
    updated_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (registration_id),
    CONSTRAINT uk_registration_no    UNIQUE (registration_no),
    CONSTRAINT uk_doctor_visit_slot  UNIQUE (doctor_id, visit_date, time_slot),
    KEY idx_registration_patient (patient_id),
    KEY idx_registration_date    (visit_date),
    CONSTRAINT fk_registration_patient FOREIGN KEY (patient_id) REFERENCES patient (patient_id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_registration_doctor  FOREIGN KEY (doctor_id)  REFERENCES doctor  (doctor_id)  ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT ck_registration_slot   CHECK (time_slot IN ('AM', 'PM')),
    CONSTRAINT ck_registration_status CHECK (status IN ('BOOKED', 'COMPLETED', 'CANCELLED'))
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '挂号记录';

-- ---------------------------------------------------------------------------
-- 7. 电子处方表 prescription（核心实体 5）
--    一次挂号对应一张处方（registration_id 唯一，1:1）。
--    不存患者、医生、科室：prescription_id -> registration_id -> doctor_id -> dept_id，
--    若冗余保存即产生传递依赖，违反 3NF。
-- ---------------------------------------------------------------------------
CREATE TABLE prescription (
    prescription_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '处方主键',
    prescription_no VARCHAR(32)     NOT NULL               COMMENT '处方编号',
    registration_id BIGINT UNSIGNED NOT NULL               COMMENT '挂号记录（外键，1:1）',
    diagnosis       VARCHAR(255)    NOT NULL               COMMENT '临床诊断',
    advice          VARCHAR(500)    DEFAULT NULL           COMMENT '医嘱',
    issued_at       DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '开具时间',
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (prescription_id),
    CONSTRAINT uk_prescription_no          UNIQUE (prescription_no),
    CONSTRAINT uk_prescription_registration UNIQUE (registration_id),
    CONSTRAINT fk_prescription_registration FOREIGN KEY (registration_id)
        REFERENCES registration (registration_id) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '电子处方（处方头）';

-- ---------------------------------------------------------------------------
-- 8. 处方明细表 prescription_item（消除重复组，满足 1NF/2NF）
--    一张处方含多种药品；若把药品字段做成处方表的重复列，会违反 1NF。
--    明细以 item_id 为主键，并设 (prescription_id, drug_id) 唯一约束。
-- ---------------------------------------------------------------------------
CREATE TABLE prescription_item (
    item_id         BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '明细主键',
    prescription_id BIGINT UNSIGNED NOT NULL               COMMENT '处方（外键）',
    drug_id         BIGINT UNSIGNED NOT NULL               COMMENT '药品（外键）',
    dosage          VARCHAR(32)     NOT NULL               COMMENT '单次用量，如 1 片',
    frequency       VARCHAR(32)     NOT NULL               COMMENT '用药频次，如 每日三次',
    days            TINYINT UNSIGNED NOT NULL              COMMENT '用药天数',
    quantity        INT UNSIGNED    NOT NULL               COMMENT '开药数量',
    PRIMARY KEY (item_id),
    CONSTRAINT uk_item_prescription_drug UNIQUE (prescription_id, drug_id),
    KEY idx_item_drug (drug_id),
    CONSTRAINT fk_item_prescription FOREIGN KEY (prescription_id)
        REFERENCES prescription (prescription_id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_item_drug FOREIGN KEY (drug_id)
        REFERENCES drug (drug_id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT ck_item_days     CHECK (days > 0),
    CONSTRAINT ck_item_quantity CHECK (quantity > 0)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '电子处方明细';
