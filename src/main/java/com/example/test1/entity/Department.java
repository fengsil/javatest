package com.example.test1.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 科室实体（department）
 */
@TableName("department")
public class Department implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 科室主键
     */
    @TableId(value = "dept_id", type = IdType.AUTO)
    private Long deptId;

    /**
     * 科室编码，如 D001
     */
    @TableField("dept_code")
    private String deptCode;

    /**
     * 科室名称
     */
    @TableField("dept_name")
    private String deptName;

    /**
     * 门诊位置（楼栋-楼层-诊区）
     */
    @TableField("location")
    private String location;

    /**
     * 科室联系电话
     */
    @TableField("tel")
    private String tel;

    /**
     * 科室简介
     */
    @TableField("description")
    private String description;

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

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    public String getDeptCode() {
        return deptCode;
    }

    public void setDeptCode(String deptCode) {
        this.deptCode = deptCode;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
        return "Department{" +
            "deptId = " + deptId +
            ", deptCode = " + deptCode +
            ", deptName = " + deptName +
            ", location = " + location +
            ", tel = " + tel +
            ", description = " + description +
            ", createdAt = " + createdAt +
            ", updatedAt = " + updatedAt +
            "}";
    }
}
