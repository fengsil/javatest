package com.example.test1.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

/**
 * 电子处方明细实体（prescription_item）
 * 一张处方多条药品明细，消除处方表中的重复组，满足 1NF。
 */
@TableName("prescription_item")
public class PrescriptionItem implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 明细主键
     */
    @TableId(value = "item_id", type = IdType.AUTO)
    private Long itemId;

    /**
     * 处方（外键）
     */
    @TableField("prescription_id")
    private Long prescriptionId;

    /**
     * 药品（外键）
     */
    @TableField("drug_id")
    private Long drugId;

    /**
     * 单次用量，如 1 片
     */
    @TableField("dosage")
    private String dosage;

    /**
     * 用药频次，如 每日三次
     */
    @TableField("frequency")
    private String frequency;

    /**
     * 用药天数
     */
    @TableField("days")
    private Integer days;

    /**
     * 开药数量
     */
    @TableField("quantity")
    private Integer quantity;

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public Long getPrescriptionId() {
        return prescriptionId;
    }

    public void setPrescriptionId(Long prescriptionId) {
        this.prescriptionId = prescriptionId;
    }

    public Long getDrugId() {
        return drugId;
    }

    public void setDrugId(Long drugId) {
        this.drugId = drugId;
    }

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public Integer getDays() {
        return days;
    }

    public void setDays(Integer days) {
        this.days = days;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "PrescriptionItem{" +
            "itemId = " + itemId +
            ", prescriptionId = " + prescriptionId +
            ", drugId = " + drugId +
            ", dosage = " + dosage +
            ", frequency = " + frequency +
            ", days = " + days +
            ", quantity = " + quantity +
            "}";
    }
}
