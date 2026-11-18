package com.example.test1.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 患者实体（patient）
 */
@TableName("patient")
public class Patient implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 患者主键
     */
    @TableId(value = "patient_id", type = IdType.AUTO)
    private Long patientId;

    /**
     * 就诊卡号
     */
    @TableField("patient_no")
    private String patientNo;

    /**
     * 姓名
     */
    @TableField("name")
    private String name;

    /**
     * 性别：M 男 / F 女
     */
    @TableField("gender")
    private String gender;

    /**
     * 手机号（11 位）
     */
    @TableField("phone")
    private String phone;

    /**
     * 身份证号（18 位）
     */
    @TableField("id_card")
    private String idCard;

    /**
     * 联系地址（单一原子值）
     */
    @TableField("address")
    private String address;

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

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public String getPatientNo() {
        return patientNo;
    }

    public void setPatientNo(String patientNo) {
        this.patientNo = patientNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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
        return "Patient{" +
            "patientId = " + patientId +
            ", patientNo = " + patientNo +
            ", name = " + name +
            ", gender = " + gender +
            ", phone = " + phone +
            ", idCard = " + idCard +
            ", address = " + address +
            ", createdAt = " + createdAt +
            ", updatedAt = " + updatedAt +
            "}";
    }
}
