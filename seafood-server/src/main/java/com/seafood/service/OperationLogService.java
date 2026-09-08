package com.seafood.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seafood.common.PageResult;
import com.seafood.entity.OperationLog;
import com.seafood.mapper.OperationLogMapper;
import com.seafood.security.SecurityUser;
import com.seafood.security.SecurityUtils;
import org.springframework.stereotype.Service;

@Service
public class OperationLogService {

    private final OperationLogMapper operationLogMapper;

    public OperationLogService(OperationLogMapper operationLogMapper) {
        this.operationLogMapper = operationLogMapper;
    }

    /** 记录一条当前用户的操作日志；用户上下文不可用时用 Null 兜底 */
    public void log(String action, String target, String detail) {
        OperationLog log = new OperationLog();
        try {
            SecurityUser u = SecurityUtils.current();
            log.setUserCode(u.getUsername());
            log.setRole(u.getRole());
            log.setNodeType(u.getType());
        } catch (Exception e) {
            log.setUserCode(System.getProperty("user.name", "-"));
            log.setRole("SYSTEM");
        }
        log.setAction(action);
        log.setTarget(target);
        log.setDetail(detail);
        operationLogMapper.insert(log);
    }

    /** 显式操作人的记录（用于登录等认证前场景，此时 SecurityContext 尚为空） */
    public void log(String userCode, String role, String nodeType, String action, String target, String detail) {
        OperationLog log = new OperationLog();
        log.setUserCode(userCode);
        log.setRole(role);
        log.setNodeType(nodeType);
        log.setAction(action);
        log.setTarget(target);
        log.setDetail(detail);
        operationLogMapper.insert(log);
    }

    public PageResult<OperationLog> page(long page, long size) {
        Page<OperationLog> result = operationLogMapper.selectPage(new Page<>(page, size),
                new LambdaQueryWrapper<OperationLog>().orderByDesc(OperationLog::getId));
        return new PageResult<>(result.getTotal(), result.getRecords());
    }
}
