package com.seafood.dto;

import lombok.Data;

@Data
public class TraceNodeVO {
    /** BREEDING / PROCESSING / WHOLESALE / RETAIL */
    private String nodeType;
    private String nodeTypeName;
    private String enterpriseName;
    private String batchNo;
    private String productVariety;
    private String cert;
    private String region;
    private String batchDate;
    private String traceCode;
}
