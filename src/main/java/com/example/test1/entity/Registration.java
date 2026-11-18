package com.example.test1.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 挂号记录实体（registration）
 * 只保存患者、医生两个外键，患者姓名、医生姓名、科室、挂号费均通过关联查询得到。
 */
@TableName("registration")
public class Registration implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 挂号记录主键
     */
    @TableId(value = "registration_id", type = IdType.AUTO)
    private Long registrationId;

    /**
     * 挂号单号
     */
    @TableField("registration_no")
    private String registrationNo;

    /**
     * 患者（外键 patient.patient_id）
     */
    @TableField("patient_id")
    private Long patientId;

    /**
     * 接诊医生（外键 doctor.doctor_id）
     */
    @TableField("doctor_id")
    private Long doctorId;

    /**
     * 就诊日期
     */
    @TableField("visit_date")
    private LocalDate visitDate;

    /**
     * 时段：AM / PM
     */
    @TableField("time_slot")
    private String timeSlot;

    /**
     * 状态：BOOKED / COMPLETED / CANCELLED
     */
    @TableField("status")
    private String status;

    /**
     * 主诉
     */
    @TableField("symptom")
    private String symptom;

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

    public Long getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(Long registrationId) {
        this.registrationId = registrationId;
    }

    public String getRegistrationNo() {
        return registrationNo;
    }

    public void setRegistrationNo(String registrationNo) {
        this.registrationNo = registrationNo;
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public Long getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Long doctorId) {
        this.doctorId = doctorId;
    }

    public LocalDate getVisitDate() {
        return visitDate;
    }

    public void setVisitDate(LocalDate visitDate) {
        this.visitDate = visitDate;
    }

    public String getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(String timeSlot) {
        this.timeSlot = timeSlot;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSymptom() {
        return symptom;
    }

    public void setSymptom(String symptom) {
        this.symptom = symptom;
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
        return "Registration{" +
            "registrationId = " + registrationId +
            ", registrationNo = " + registrationNo +
            ", patientId = " + patientId +
            ", doctorId = " + doctorId +
            ", visitDate = " + visitDate +
            ", timeSlot = " + timeSlot +
            ", status = " + status +
            ", symptom = " + symptom +
            ", createdAt = " + createdAt +
            ", updatedAt = " + updatedAt +
            "}";
    }
}
