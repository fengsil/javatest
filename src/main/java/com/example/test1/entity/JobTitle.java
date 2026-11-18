package com.example.test1.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 职称与挂号费字典实体（job_title）
 * 挂号费只依赖职称，不依赖医生，避免 doctor_id -> title -> fee 的传递依赖。
 */
@TableName("job_title")
public class JobTitle implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 职称主键
     */
    @TableId(value = "title_id", type = IdType.AUTO)
    private Long titleId;

    /**
     * 职称编码
     */
    @TableField("title_code")
    private String titleCode;

    /**
     * 职称名称
     */
    @TableField("title_name")
    private String titleName;

    /**
     * 该职称对应的挂号费（元）
     */
    @TableField("registration_fee")
    private BigDecimal registrationFee;

    public Long getTitleId() {
        return titleId;
    }

    public void setTitleId(Long titleId) {
        this.titleId = titleId;
    }

    public String getTitleCode() {
        return titleCode;
    }

    public void setTitleCode(String titleCode) {
        this.titleCode = titleCode;
    }

    public String getTitleName() {
        return titleName;
    }

    public void setTitleName(String titleName) {
        this.titleName = titleName;
    }

    public BigDecimal getRegistrationFee() {
        return registrationFee;
    }

    public void setRegistrationFee(BigDecimal registrationFee) {
        this.registrationFee = registrationFee;
    }

    @Override
    public String toString() {
        return "JobTitle{" +
            "titleId = " + titleId +
            ", titleCode = " + titleCode +
            ", titleName = " + titleName +
            ", registrationFee = " + registrationFee +
            "}";
    }
}
