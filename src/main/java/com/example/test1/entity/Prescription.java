package com.example.test1.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 电子处方实体（prescription，处方头）
 * 不保存患者、医生、科室信息，均由 registration_id 外键关联得到。
 */
@TableName("prescription")
public class Prescription implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 处方主键
     */
    @TableId(value = "prescription_id", type = IdType.AUTO)
    private Long prescriptionId;

    /**
     * 处方编号
     */
    @TableField("prescription_no")
    private String prescriptionNo;

    /**
     * 挂号记录（外键，1:1）
     */
    @TableField("registration_id")
    private Long registrationId;

    /**
     * 临床诊断
     */
    @TableField("diagnosis")
    private String diagnosis;

    /**
     * 医嘱
     */
    @TableField("advice")
    private String advice;

    /**
     * 开具时间
     */
    @TableField("issued_at")
    private LocalDateTime issuedAt;

    /**
     * 创建时间（数据库维护）
     */
    @TableField("created_at")
    private LocalDateTime createdAt;

    /**
     * 更新时间（数据库维护）
     */
    @TableField("updated_at")
    private LocalDateTime updatedAt;

    public Long getPrescriptionId() {
        return prescriptionId;
    }

    public void setPrescriptionId(Long prescriptionId) {
        this.prescriptionId = prescriptionId;
    }

    public String getPrescriptionNo() {
        return prescriptionNo;
    }

    public void setPrescriptionNo(String prescriptionNo) {
        this.prescriptionNo = prescriptionNo;
    }

    public Long getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(Long registrationId) {
        this.registrationId = registrationId;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getAdvice() {
        return advice;
    }

    public void setAdvice(String advice) {
        this.advice = advice;
    }

    public LocalDateTime getIssuedAt() {
        return issuedAt;
    }

    public void setIssuedAt(LocalDateTime issuedAt) {
        this.issuedAt = issuedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "Prescription{" +
            "prescriptionId = " + prescriptionId +
            ", prescriptionNo = " + prescriptionNo +
            ", registrationId = " + registrationId +
            ", diagnosis = " + diagnosis +
            ", advice = " + advice +
            ", issuedAt = " + issuedAt +
            ", createdAt = " + createdAt +
            ", updatedAt = " + updatedAt +
            "}";
    }
}
