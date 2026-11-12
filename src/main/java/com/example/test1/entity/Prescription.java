package com.example.test1.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author student
 * @since 
 */
@TableName("prescription")
public class Prescription implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "pres_id", type = IdType.AUTO)
    private Long presId;

    @TableField("reg_id")
    private Long regId;

    @TableField("diagnosis")
    private String diagnosis;

    @TableField("drug_info")
    private String drugInfo;

    @TableField("pres_time")
    private LocalDateTime presTime;

    @TableField("created_at")
    private LocalDateTime createdAt;

    public Long getPresId() {
        return presId;
    }

    public void setPresId(Long presId) {
        this.presId = presId;
    }

    public Long getRegId() {
        return regId;
    }

    public void setRegId(Long regId) {
        this.regId = regId;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getDrugInfo() {
        return drugInfo;
    }

    public void setDrugInfo(String drugInfo) {
        this.drugInfo = drugInfo;
    }

    public LocalDateTime getPresTime() {
        return presTime;
    }

    public void setPresTime(LocalDateTime presTime) {
        this.presTime = presTime;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Prescription{" +
            "presId = " + presId +
            ", regId = " + regId +
            ", diagnosis = " + diagnosis +
            ", drugInfo = " + drugInfo +
            ", presTime = " + presTime +
            ", createdAt = " + createdAt +
            "}";
    }
}
