package com.seafood.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("node_enterprise")
public class NodeEnterprise {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String loginCode;
    private String password;
    /** 展示用，非表字段 */
    @TableField(exist = false)
    private String provinceName;
    @TableField(exist = false)
    private String cityName;
    private String name;
    /** BREEDING / PROCESSING / WHOLESALE / RETAIL */
    private String type;
    private Long provinceId;
    private Long cityId;
    private String address;
    private String businessLicenseNo;
    private String contact;
    private String phone;
    private String aquaticEpidemicCert;
    private String environmentCert;
    private String foodCirculationCert;
    private String foodOperationCert;
    private Integer status;
    private LocalDateTime createdAt;
}
