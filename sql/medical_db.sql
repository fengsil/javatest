CREATE DATABASE IF NOT EXISTS `medical_db` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `medical_db`;

DROP TABLE IF EXISTS `prescription`;
DROP TABLE IF EXISTS `registration`;
DROP TABLE IF EXISTS `patient`;
DROP TABLE IF EXISTS `doctor`;
DROP TABLE IF EXISTS `department`;

CREATE TABLE `department` (
  `dept_id` BIGINT NOT NULL AUTO_INCREMENT,
  `dept_name` VARCHAR(50) NOT NULL,
  `dept_location` VARCHAR(100) DEFAULT NULL,
  `dept_phone` VARCHAR(20) DEFAULT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`dept_id`),
  UNIQUE KEY `uk_department_name` (`dept_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `doctor` (
  `doctor_id` BIGINT NOT NULL AUTO_INCREMENT,
  `dept_id` BIGINT NOT NULL,
  `name` VARCHAR(50) NOT NULL,
  `gender` VARCHAR(10) NOT NULL DEFAULT 'unknown',
  `title` VARCHAR(50) DEFAULT NULL,
  `phone` VARCHAR(11) NOT NULL,
  `id_card` VARCHAR(18) NOT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`doctor_id`),
  UNIQUE KEY `uk_doctor_phone` (`phone`),
  UNIQUE KEY `uk_doctor_id_card` (`id_card`),
  KEY `idx_doctor_dept` (`dept_id`),
  CONSTRAINT `fk_doctor_department` FOREIGN KEY (`dept_id`) REFERENCES `department` (`dept_id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `patient` (
  `patient_id` BIGINT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(50) NOT NULL,
  `gender` VARCHAR(10) NOT NULL DEFAULT 'unknown',
  `birth_date` DATE DEFAULT NULL,
  `phone` VARCHAR(11) NOT NULL,
  `id_card` VARCHAR(18) NOT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`patient_id`),
  UNIQUE KEY `uk_patient_phone` (`phone`),
  UNIQUE KEY `uk_patient_id_card` (`id_card`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `registration` (
  `reg_id` BIGINT NOT NULL AUTO_INCREMENT,
  `patient_id` BIGINT NOT NULL,
  `doctor_id` BIGINT NOT NULL,
  `reg_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `status` TINYINT NOT NULL DEFAULT 0,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`reg_id`),
  KEY `idx_registration_patient` (`patient_id`),
  KEY `idx_registration_doctor` (`doctor_id`),
  CONSTRAINT `fk_registration_patient` FOREIGN KEY (`patient_id`) REFERENCES `patient` (`patient_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_registration_doctor` FOREIGN KEY (`doctor_id`) REFERENCES `doctor` (`doctor_id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `prescription` (
  `pres_id` BIGINT NOT NULL AUTO_INCREMENT,
  `reg_id` BIGINT NOT NULL,
  `diagnosis` VARCHAR(255) NOT NULL,
  `drug_info` TEXT,
  `pres_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`pres_id`),
  KEY `idx_prescription_reg` (`reg_id`),
  CONSTRAINT `fk_prescription_registration` FOREIGN KEY (`reg_id`) REFERENCES `registration` (`reg_id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
