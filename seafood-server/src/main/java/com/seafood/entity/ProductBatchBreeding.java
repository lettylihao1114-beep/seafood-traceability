package com.seafood.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("product_batch_breeding")
public class ProductBatchBreeding {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String batchNo;
    private String productVariety;
    private String aquaticQuarantineCert;
    private String officialInspector;
    private Long enterpriseId;
    /** 0待发布(新建)/1已发布/2已下架 */
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
