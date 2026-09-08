package com.seafood.service;

import com.seafood.entity.City;
import com.seafood.entity.NodeEnterprise;
import com.seafood.entity.ProductBatchBreeding;
import com.seafood.entity.ProductBatchProcessing;
import com.seafood.entity.ProductBatchRetail;
import com.seafood.entity.ProductBatchWholesale;
import com.seafood.entity.Province;
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

import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * 核心能力：溯源码 → 养殖→加工→批发→零售 四级回溯。
 * 纯 Mockito，不依赖数据库。
 */
@ExtendWith(MockitoExtension.class)
class TraceServiceTest {

    @Mock ProductBatchRetailMapper retailMapper;
    @Mock ProductBatchWholesaleMapper wholesaleMapper;
    @Mock ProductBatchProcessingMapper processingMapper;
    @Mock ProductBatchBreedingMapper breedingMapper;
    @Mock NodeEnterpriseMapper nodeEnterpriseMapper;
    @Mock ProvinceMapper provinceMapper;
    @Mock CityMapper cityMapper;

    @InjectMocks TraceService traceService;

    @Test
    void traceBacktracksFourLevels() {
        // 零售(4)→批发(3)→加工(2)→养殖(1)，批号统一 345643633
        ProductBatchRetail retail = new ProductBatchRetail();
        retail.setEnterpriseId(4L); retail.setTraceCode("NFTS-20240901001");
        retail.setUpstreamEnterpriseId(3L); retail.setUpstreamBatchNo("345643633");
        retail.setProductVariety("冷冻带鱼段"); retail.setCreatedAt(LocalDateTime.of(2026, 6, 8, 10, 0));

        ProductBatchWholesale wholesale = new ProductBatchWholesale();
        wholesale.setEnterpriseId(3L); wholesale.setUpstreamEnterpriseId(2L);
        wholesale.setUpstreamBatchNo("345643633"); wholesale.setProductVariety("冷冻带鱼");

        ProductBatchProcessing processing = new ProductBatchProcessing();
        processing.setEnterpriseId(2L); processing.setUpstreamEnterpriseId(1L);
        processing.setUpstreamBatchNo("345643633"); processing.setProductVariety("冷冻海鱼");

        ProductBatchBreeding breeding = new ProductBatchBreeding();
        breeding.setEnterpriseId(1L); breeding.setProductVariety("鲜活海鱼");

        when(retailMapper.selectOne(any())).thenReturn(retail);
        when(wholesaleMapper.selectOne(any())).thenReturn(wholesale);
        when(processingMapper.selectOne(any())).thenReturn(processing);
        when(breedingMapper.selectOne(any())).thenReturn(breeding);

        Map<Long, NodeEnterprise> ents = Map.of(
                1L, ent(1L, "沈阳浑河水产养殖场", "BREEDING"),
                2L, ent(2L, "沈阳冷链海产加工厂", "PROCESSING"),
                3L, ent(3L, "顺发海产批发中心", "WHOLESALE"),
                4L, ent(4L, "和平区海丰水产店", "RETAIL"));
        when(nodeEnterpriseMapper.selectById(any())).thenAnswer(inv -> ents.get(inv.getArgument(0)));

        when(provinceMapper.selectById(any())).thenAnswer(inv -> province("辽宁"));
        when(cityMapper.selectById(any())).thenAnswer(inv -> city("沈阳"));

        var list = traceService.trace("NFTS-20240901001");

        assertEquals(4, list.size(), "应回溯出四级节点");
        assertEquals("水产养殖企业", list.get(0).getNodeTypeName());
        assertEquals("冷冻加工企业", list.get(1).getNodeTypeName());
        assertEquals("批发商", list.get(2).getNodeTypeName());
        assertEquals("零售商", list.get(3).getNodeTypeName());
        assertEquals("NFTS-20240901001", list.get(3).getTraceCode());
        assertEquals("沈阳浑河水产养殖场", list.get(0).getEnterpriseName());
    }

    private NodeEnterprise ent(Long id, String name, String type) {
        NodeEnterprise e = new NodeEnterprise();
        e.setId(id); e.setName(name); e.setType(type); e.setProvinceId(1L); e.setCityId(1L);
        return e;
    }

    private Province province(String name) {
        Province p = new Province(); p.setId(1L); p.setName(name); return p;
    }

    private City city(String name) {
        City c = new City(); c.setId(1L); c.setName(name); return c;
    }
}
