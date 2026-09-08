package com.seafood.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("operation_log")
public class OperationLog {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String userCode;
    private String role;
    /** BREEDING / PROCESSING / WHOLESALE / RETAIL / ADMIN / null */
    private String nodeType;
    private String action;
    private String target;
    private String detail;
    private LocalDateTime createdAt;
}
