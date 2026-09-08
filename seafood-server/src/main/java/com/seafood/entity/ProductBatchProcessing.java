package com.seafood.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("product_batch_processing")
public class ProductBatchProcessing {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String batchNo;
    private String productVariety;
    private String processingInspectionCert;
    private String officialInspector;
    private String productType;
    private Long enterpriseId;
    private Long upstreamEnterpriseId;
    private String upstreamBatchNo;
    private String upstreamVariety;
    /** 0新建/1待确认/2已确认/3已下架 */
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
