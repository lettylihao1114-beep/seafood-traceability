package com.seafood.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpstreamBatchVO {
    private Long id;
    private String batchNo;
    private String productVariety;
    private Integer status;
}
