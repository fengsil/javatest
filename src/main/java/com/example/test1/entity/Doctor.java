package com.example.test1.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 医生实体（doctor）
 */
@TableName("doctor")
public class Doctor implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 医生主键
     */
    @TableId(value = "doctor_id", type = IdType.AUTO)
    private Long doctorId;

    /**
     * 医生工号
     */
    @TableField("doctor_no")
    private String doctorNo;

    /**
     * 所属科室（外键 department.dept_id）
     */
    @TableField("dept_id")
    private Long deptId;

    /**
     * 职称（外键 job_title.title_id），挂号费由职称决定
     */
    @TableField("title_id")
    private Long titleId;

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
     * 状态：1 在诊 / 0 停诊
     */
    @TableField("status")
    private Byte status;

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

    public Long getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Long doctorId) {
        this.doctorId = doctorId;
    }

    public String getDoctorNo() {
        return doctorNo;
    }

    public void setDoctorNo(String doctorNo) {
        this.doctorNo = doctorNo;
    }

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    public Long getTitleId() {
        return titleId;
    }

    public void setTitleId(Long titleId) {
        this.titleId = titleId;
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

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
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
        return "Doctor{" +
            "doctorId = " + doctorId +
            ", doctorNo = " + doctorNo +
            ", deptId = " + deptId +
            ", titleId = " + titleId +
            ", name = " + name +
            ", gender = " + gender +
            ", phone = " + phone +
            ", idCard = " + idCard +
            ", status = " + status +
            ", createdAt = " + createdAt +
            ", updatedAt = " + updatedAt +
            "}";
    }
}
