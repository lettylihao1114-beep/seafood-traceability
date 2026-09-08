package com.seafood.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.seafood.entity.ProductBatchBreeding;
import com.seafood.entity.ProductBatchProcessing;
import com.seafood.entity.ProductBatchRetail;
import com.seafood.entity.ProductBatchWholesale;
import com.seafood.mapper.NodeEnterpriseMapper;
import com.seafood.mapper.ProductBatchBreedingMapper;
import com.seafood.mapper.ProductBatchProcessingMapper;
import com.seafood.mapper.ProductBatchRetailMapper;
import com.seafood.mapper.ProductBatchWholesaleMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class WarningService {

    private static final int STALL_DAYS = 30;

    private final NodeEnterpriseMapper nodeEnterpriseMapper;
    private final ProductBatchBreedingMapper breedingMapper;
    private final ProductBatchProcessingMapper processingMapper;
    private final ProductBatchWholesaleMapper wholesaleMapper;
    private final ProductBatchRetailMapper retailMapper;

    public WarningService(NodeEnterpriseMapper nodeEnterpriseMapper,
                          ProductBatchBreedingMapper breedingMapper,
                          ProductBatchProcessingMapper processingMapper,
                          ProductBatchWholesaleMapper wholesaleMapper,
                          ProductBatchRetailMapper retailMapper) {
        this.nodeEnterpriseMapper = nodeEnterpriseMapper;
        this.breedingMapper = breedingMapper;
        this.processingMapper = processingMapper;
        this.wholesaleMapper = wholesaleMapper;
        this.retailMapper = retailMapper;
    }

    /** 管理端大屏预警摘要 */
    public Map<String, Object> summary() {
        LocalDateTime before = LocalDateTime.now().minusDays(STALL_DAYS);
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("enterpriseCount", nodeEnterpriseMapper.selectCount(null));
        m.put("totalBatches", breedingMapper.selectCount(null) + processingMapper.selectCount(null)
                + wholesaleMapper.selectCount(null) + retailMapper.selectCount(null));
        m.put("pendingConfirm", processingMapper.selectCount(new LambdaQueryWrapper<ProductBatchProcessing>().eq(ProductBatchProcessing::getStatus, 1))
                + wholesaleMapper.selectCount(new LambdaQueryWrapper<ProductBatchWholesale>().eq(ProductBatchWholesale::getStatus, 1))
                + retailMapper.selectCount(new LambdaQueryWrapper<ProductBatchRetail>().eq(ProductBatchRetail::getStatus, 1)));
        m.put("activeBatches", breedingMapper.selectCount(new LambdaQueryWrapper<ProductBatchBreeding>().eq(ProductBatchBreeding::getStatus, 1))
                + processingMapper.selectCount(new LambdaQueryWrapper<ProductBatchProcessing>().eq(ProductBatchProcessing::getStatus, 2))
                + wholesaleMapper.selectCount(new LambdaQueryWrapper<ProductBatchWholesale>().eq(ProductBatchWholesale::getStatus, 2))
                + retailMapper.selectCount(new LambdaQueryWrapper<ProductBatchRetail>().eq(ProductBatchRetail::getStatus, 2)));
        m.put("stallBatches", breedingMapper.selectCount(new LambdaQueryWrapper<ProductBatchBreeding>()
                        .eq(ProductBatchBreeding::getStatus, 0).lt(ProductBatchBreeding::getCreatedAt, before))
                + processingMapper.selectCount(new LambdaQueryWrapper<ProductBatchProcessing>()
                        .eq(ProductBatchProcessing::getStatus, 0).lt(ProductBatchProcessing::getCreatedAt, before))
                + wholesaleMapper.selectCount(new LambdaQueryWrapper<ProductBatchWholesale>()
                        .eq(ProductBatchWholesale::getStatus, 0).lt(ProductBatchWholesale::getCreatedAt, before))
                + retailMapper.selectCount(new LambdaQueryWrapper<ProductBatchRetail>()
                        .eq(ProductBatchRetail::getStatus, 0).lt(ProductBatchRetail::getCreatedAt, before)));
        return m;
    }
}
