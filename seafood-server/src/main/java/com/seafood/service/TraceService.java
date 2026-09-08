package com.seafood.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.seafood.common.BizException;
import com.seafood.dto.TraceNodeVO;
import com.seafood.entity.*;
import com.seafood.mapper.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class TraceService {

    private final ProductBatchRetailMapper retailMapper;
    private final ProductBatchWholesaleMapper wholesaleMapper;
    private final ProductBatchProcessingMapper processingMapper;
    private final ProductBatchBreedingMapper breedingMapper;
    private final NodeEnterpriseMapper nodeEnterpriseMapper;
    private final ProvinceMapper provinceMapper;
    private final CityMapper cityMapper;

    public TraceService(ProductBatchRetailMapper retailMapper, ProductBatchWholesaleMapper wholesaleMapper,
                        ProductBatchProcessingMapper processingMapper, ProductBatchBreedingMapper breedingMapper,
                        NodeEnterpriseMapper nodeEnterpriseMapper, ProvinceMapper provinceMapper,
                        CityMapper cityMapper) {
        this.retailMapper = retailMapper;
        this.wholesaleMapper = wholesaleMapper;
        this.processingMapper = processingMapper;
        this.breedingMapper = breedingMapper;
        this.nodeEnterpriseMapper = nodeEnterpriseMapper;
        this.provinceMapper = provinceMapper;
        this.cityMapper = cityMapper;
    }

    /** 从溯源码出发，沿上游指针回溯，返回 养殖→加工→批发→零售 四级链 */
    public List<TraceNodeVO> trace(String traceCode) {
        ProductBatchRetail retail = retailMapper.selectOne(
                new LambdaQueryWrapper<ProductBatchRetail>().eq(ProductBatchRetail::getTraceCode, traceCode));
        if (retail == null) throw new BizException("溯源标识码不存在");

        List<TraceNodeVO> down2up = new ArrayList<>();
        down2up.add(node("RETAIL", retail.getEnterpriseId(), retail.getBatchNo(), retail.getProductVariety(),
                retail.getCreatedAt(), retail.getTraceCode()));

        if (retail.getUpstreamEnterpriseId() != null && retail.getUpstreamBatchNo() != null) {
            ProductBatchWholesale wholesale = wholesaleMapper.selectOne(new LambdaQueryWrapper<ProductBatchWholesale>()
                    .eq(ProductBatchWholesale::getEnterpriseId, retail.getUpstreamEnterpriseId())
                    .eq(ProductBatchWholesale::getBatchNo, retail.getUpstreamBatchNo()));
            if (wholesale != null) {
                down2up.add(node("WHOLESALE", wholesale.getEnterpriseId(), wholesale.getBatchNo(),
                        wholesale.getProductVariety(), wholesale.getCreatedAt(), null));
                ProductBatchProcessing processing = processingMapper.selectOne(new LambdaQueryWrapper<ProductBatchProcessing>()
                        .eq(ProductBatchProcessing::getEnterpriseId, wholesale.getUpstreamEnterpriseId())
                        .eq(ProductBatchProcessing::getBatchNo, wholesale.getUpstreamBatchNo()));
                if (processing != null) {
                    down2up.add(node("PROCESSING", processing.getEnterpriseId(), processing.getBatchNo(),
                            processing.getProductVariety(), processing.getCreatedAt(), null));
                    ProductBatchBreeding breeding = breedingMapper.selectOne(new LambdaQueryWrapper<ProductBatchBreeding>()
                            .eq(ProductBatchBreeding::getEnterpriseId, processing.getUpstreamEnterpriseId())
                            .eq(ProductBatchBreeding::getBatchNo, processing.getUpstreamBatchNo()));
                    if (breeding != null) {
                        down2up.add(node("BREEDING", breeding.getEnterpriseId(), breeding.getBatchNo(),
                                breeding.getProductVariety(), breeding.getCreatedAt(), null));
                    }
                }
            }
        }
        Collections.reverse(down2up);
        return down2up;
    }

    private TraceNodeVO node(String type, Long enterpriseId, String batchNo, String variety,
                             java.time.LocalDateTime createdAt, String traceCode) {
        TraceNodeVO v = new TraceNodeVO();
        v.setNodeType(type);
        v.setNodeTypeName(typeName(type));
        NodeEnterprise ent = enterpriseId == null ? null : nodeEnterpriseMapper.selectById(enterpriseId);
        v.setEnterpriseName(ent == null ? null : ent.getName());
        v.setBatchNo(batchNo);
        v.setProductVariety(variety);
        v.setRegion(regionName(ent));
        v.setBatchDate(createdAt == null ? null : createdAt.toLocalDate().toString());
        v.setTraceCode(traceCode);
        v.setCert(cert(type, enterpriseId, batchNo));
        return v;
    }

    private String cert(String type, Long enterpriseId, String batchNo) {
        return switch (type) {
            case "BREEDING" -> {
                ProductBatchBreeding b = breedingMapper.selectOne(new LambdaQueryWrapper<ProductBatchBreeding>()
                        .eq(ProductBatchBreeding::getEnterpriseId, enterpriseId)
                        .eq(ProductBatchBreeding::getBatchNo, batchNo));
                yield b == null ? null : b.getAquaticQuarantineCert();
            }
            case "PROCESSING" -> {
                ProductBatchProcessing s = processingMapper.selectOne(new LambdaQueryWrapper<ProductBatchProcessing>()
                        .eq(ProductBatchProcessing::getEnterpriseId, enterpriseId)
                        .eq(ProductBatchProcessing::getBatchNo, batchNo));
                yield s == null ? null : s.getProcessingInspectionCert();
            }
            default -> null;
        };
    }

    private String regionName(NodeEnterprise ent) {
        if (ent == null) return null;
        String province = ent.getProvinceId() == null ? null : safeName(provinceMapper.selectById(ent.getProvinceId()));
        String city = ent.getCityId() == null ? null : safeName(cityMapper.selectById(ent.getCityId()));
        if (province == null) return city;
        if (city == null) return province;
        return province + " " + city;
    }

    private String safeName(Object obj) {
        if (obj instanceof Province p) return p.getName();
        if (obj instanceof City c) return c.getName();
        return null;
    }

    private String typeName(String type) {
        return switch (type) {
            case "BREEDING" -> "水产养殖企业";
            case "PROCESSING" -> "冷冻加工企业";
            case "WHOLESALE" -> "批发商";
            case "RETAIL" -> "零售商";
            default -> type;
        };
    }
}
