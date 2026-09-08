package com.seafood.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class BatchSaveRequest {
    // 通用
    private String batchNo;
    private String productVariety;
    private String productType;
    // 养殖
    private String aquaticQuarantineCert;
    // 加工
    private String processingInspectionCert;
    // 官方检验员（养殖/加工两环节复用）
    private String officialInspector;
    // 上游进场信息（加工/批发/零售）
    private Long upstreamEnterpriseId;
    private String upstreamBatchNo;
    private String upstreamVariety;
    // 更新时：养殖"是否发布" / 其它"是否向上游发送确认请求"
    private Boolean publish;
    private Boolean sendConfirm;
}
