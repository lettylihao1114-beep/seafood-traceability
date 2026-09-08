package com.seafood.service;

import com.seafood.entity.ProductBatchBreeding;
import com.seafood.entity.ProductBatchProcessing;
import com.seafood.mapper.CityMapper;
import com.seafood.mapper.NodeEnterpriseMapper;
import com.seafood.mapper.ProductBatchBreedingMapper;
import com.seafood.mapper.ProductBatchProcessingMapper;
import com.seafood.mapper.ProductBatchRetailMapper;
import com.seafood.mapper.ProductBatchWholesaleMapper;
import com.seafood.mapper.ProvinceMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * 批号状态机核心流转（纯 Mockito，publish/offShelf 不依赖登录上下文）。
 */
@ExtendWith(MockitoExtension.class)
class NodeServiceTest {

    @Mock NodeEnterpriseMapper nodeEnterpriseMapper;
    @Mock ProvinceMapper provinceMapper;
    @Mock CityMapper cityMapper;
    @Mock ProductBatchBreedingMapper breedingMapper;
    @Mock ProductBatchProcessingMapper processingMapper;
    @Mock ProductBatchWholesaleMapper wholesaleMapper;
    @Mock ProductBatchRetailMapper retailMapper;
    @Mock PasswordEncoder passwordEncoder;
    @Mock OperationLogService operationLogService;

    @InjectMocks NodeService nodeService;

    @Test
    void breedingPublishFlipsStatus() {
        ProductBatchBreeding b = new ProductBatchBreeding();
        b.setId(2L); b.setBatchNo("1000001"); b.setStatus(0);
        when(breedingMapper.selectById(any())).thenReturn(b);

        nodeService.publish("BREEDING", 2L);

        assertEquals(1, b.getStatus(), "养殖批号 待发布→已发布");
    }

    @Test
    void publishTwiceThrows() {
        ProductBatchBreeding b = new ProductBatchBreeding();
        b.setId(2L); b.setStatus(1);
        when(breedingMapper.selectById(any())).thenReturn(b);

        // 已发布状态再次发布 → 抛业务异常
        try {
            nodeService.publish("BREEDING", 2L);
        } catch (com.seafood.common.BizException e) {
            assertEquals("仅待发布状态可发布", e.getMessage());
            return;
        }
        org.junit.jupiter.api.Assertions.fail("应抛出异常");
    }

    @Test
    void processingOffShelfFlipsStatus() {
        ProductBatchProcessing p = new ProductBatchProcessing();
        p.setId(3L); p.setBatchNo("5555666"); p.setStatus(0);
        when(processingMapper.selectById(any())).thenReturn(p);

        nodeService.offShelf("PROCESSING", 3L);

        assertEquals(3, p.getStatus(), "加工批号 任意状态→已下架(3)");
    }
}
