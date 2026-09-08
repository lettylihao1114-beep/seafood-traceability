package com.seafood.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("product_batch_retail")
public class ProductBatchRetail {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String batchNo;
    private String productVariety;
    private String productType;
    private Long enterpriseId;
    private Long upstreamEnterpriseId;
    private String upstreamBatchNo;
    private String upstreamVariety;
    private String traceCode;
    /** 0新建/1待确认/2已确认/3已下架 */
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
