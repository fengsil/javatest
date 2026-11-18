package com.example.test1.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 药品字典实体（drug）
 */
@TableName("drug")
public class Drug implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 药品主键
     */
    @TableId(value = "drug_id", type = IdType.AUTO)
    private Long drugId;

    /**
     * 药品编码（国药准字/院内编码）
     */
    @TableField("drug_code")
    private String drugCode;

    /**
     * 药品通用名
     */
    @TableField("drug_name")
    private String drugName;

    /**
     * 规格
     */
    @TableField("specification")
    private String specification;

    /**
     * 计价单位
     */
    @TableField("unit")
    private String unit;

    /**
     * 单价（元）
     */
    @TableField("price")
    private BigDecimal price;

    public Long getDrugId() {
        return drugId;
    }

    public void setDrugId(Long drugId) {
        this.drugId = drugId;
    }

    public String getDrugCode() {
        return drugCode;
    }

    public void setDrugCode(String drugCode) {
        this.drugCode = drugCode;
    }

    public String getDrugName() {
        return drugName;
    }

    public void setDrugName(String drugName) {
        this.drugName = drugName;
    }

    public String getSpecification() {
        return specification;
    }

    public void setSpecification(String specification) {
        this.specification = specification;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Drug{" +
            "drugId = " + drugId +
            ", drugCode = " + drugCode +
            ", drugName = " + drugName +
            ", specification = " + specification +
            ", unit = " + unit +
            ", price = " + price +
            "}";
    }
}
