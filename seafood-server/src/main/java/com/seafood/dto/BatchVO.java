package com.seafood.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BatchVO {
    private Long id;
    private Long enterpriseId;
    private String enterpriseName;
    private String batchNo;
    private String productVariety;
    private String productType;
    private String aquaticQuarantineCert;
    private String processingInspectionCert;
    private String officialInspector;
    private Long upstreamEnterpriseId;
    private String upstreamEnterpriseName;
    private String upstreamBatchNo;
    private String upstreamVariety;
    private String traceCode;
    private Integer status;
    private LocalDateTime createdAt;
}
