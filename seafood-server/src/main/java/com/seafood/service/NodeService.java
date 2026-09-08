package com.seafood.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.seafood.common.BizException;
import com.seafood.dto.BatchSaveRequest;
import com.seafood.dto.BatchVO;
import com.seafood.dto.UpstreamBatchVO;
import com.seafood.entity.*;
import com.seafood.mapper.*;
import com.seafood.security.SecurityUser;
import com.seafood.security.SecurityUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;

@Service
public class NodeService {

    public static final String BREEDING = "BREEDING";
    public static final String PROCESSING = "PROCESSING";
    public static final String WHOLESALE = "WHOLESALE";
    public static final String RETAIL = "RETAIL";

    private final NodeEnterpriseMapper nodeEnterpriseMapper;
    private final ProvinceMapper provinceMapper;
    private final CityMapper cityMapper;
    private final ProductBatchBreedingMapper breedingMapper;
    private final ProductBatchProcessingMapper processingMapper;
    private final ProductBatchWholesaleMapper wholesaleMapper;
    private final ProductBatchRetailMapper retailMapper;
    private final PasswordEncoder passwordEncoder;

    public NodeService(NodeEnterpriseMapper nodeEnterpriseMapper, ProvinceMapper provinceMapper,
                       CityMapper cityMapper,
                       ProductBatchBreedingMapper breedingMapper,
                       ProductBatchProcessingMapper processingMapper,
                       ProductBatchWholesaleMapper wholesaleMapper,
                       ProductBatchRetailMapper retailMapper,
                       PasswordEncoder passwordEncoder) {
        this.nodeEnterpriseMapper = nodeEnterpriseMapper;
        this.provinceMapper = provinceMapper;
        this.cityMapper = cityMapper;
        this.breedingMapper = breedingMapper;
        this.processingMapper = processingMapper;
        this.wholesaleMapper = wholesaleMapper;
        this.retailMapper = retailMapper;
        this.passwordEncoder = passwordEncoder;
    }

    // ==================== 我的 / 改密 ====================

    public NodeEnterprise profile() {
        SecurityUser user = SecurityUtils.current();
        NodeEnterprise ent = nodeEnterpriseMapper.selectById(user.getId());
        if (ent == null) throw new BizException("企业不存在");
        if (ent.getProvinceId() != null) {
            Province p = provinceMapper.selectById(ent.getProvinceId());
            if (p != null) ent.setProvinceName(p.getName());
        }
        if (ent.getCityId() != null) {
            City c = cityMapper.selectById(ent.getCityId());
            if (c != null) ent.setCityName(c.getName());
        }
        return ent;
    }

    @Transactional
    public void changePassword(String oldPassword, String newPassword) {
        SecurityUser user = SecurityUtils.current();
        NodeEnterprise ent = nodeEnterpriseMapper.selectById(user.getId());
        if (ent == null || !passwordEncoder.matches(oldPassword, ent.getPassword())) {
            throw new BizException("旧密码不正确");
        }
        NodeEnterprise update = new NodeEnterprise();
        update.setId(ent.getId());
        update.setPassword(passwordEncoder.encode(newPassword));
        nodeEnterpriseMapper.updateById(update);
    }

    // ==================== 批号列表（按类型+状态） ====================

    public List<BatchVO> listBatches(String status) {
        SecurityUser user = SecurityUtils.current();
        Integer code = Integer.valueOf(status);
        return switch (user.getType()) {
            case BREEDING -> list(BREEDING, user.getId(), code);
            case PROCESSING -> list(PROCESSING, user.getId(), code);
            case WHOLESALE -> list(WHOLESALE, user.getId(), code);
            case RETAIL -> list(RETAIL, user.getId(), code);
            default -> throw new BizException("未知企业类型");
        };
    }

    private List<BatchVO> list(String type, Long enterpriseId, Integer status) {
        return switch (type) {
            case BREEDING -> breedingMapper.selectList(new LambdaQueryWrapper<ProductBatchBreeding>()
                    .eq(ProductBatchBreeding::getEnterpriseId, enterpriseId).
                    eq(ProductBatchBreeding::getStatus, status).orderByDesc(ProductBatchBreeding::getCreatedAt))
                    .stream().map(this::toVO).toList();
            case PROCESSING -> processingMapper.selectList(new LambdaQueryWrapper<ProductBatchProcessing>()
                    .eq(ProductBatchProcessing::getEnterpriseId, enterpriseId)
                    .eq(ProductBatchProcessing::getStatus, status).orderByDesc(ProductBatchProcessing::getCreatedAt))
                    .stream().map(this::toVO).toList();
            case WHOLESALE -> wholesaleMapper.selectList(new LambdaQueryWrapper<ProductBatchWholesale>()
                    .eq(ProductBatchWholesale::getEnterpriseId, enterpriseId)
                    .eq(ProductBatchWholesale::getStatus, status).orderByDesc(ProductBatchWholesale::getCreatedAt))
                    .stream().map(this::toVO).toList();
            case RETAIL -> retailMapper.selectList(new LambdaQueryWrapper<ProductBatchRetail>()
                    .eq(ProductBatchRetail::getEnterpriseId, enterpriseId)
                    .eq(ProductBatchRetail::getStatus, status).orderByDesc(ProductBatchRetail::getCreatedAt))
                    .stream().map(this::toVO).toList();
            default -> throw new BizException("未知企业类型");
        };
    }

    public BatchVO detail(String type, Long id) {
        return switch (type) {
            case BREEDING -> toVO(require(breedingMapper.selectById(id), "批号不存在"));
            case PROCESSING -> toVO(require(processingMapper.selectById(id), "批号不存在"));
            case WHOLESALE -> toVO(require(wholesaleMapper.selectById(id), "批号不存在"));
            case RETAIL -> toVO(require(retailMapper.selectById(id), "批号不存在"));
            default -> throw new BizException("未知企业类型");
        };
    }

    // ==================== 新建 ====================

    @Transactional
    public void create(BatchSaveRequest req) {
        SecurityUser user = SecurityUtils.current();
        String type = user.getType();
        if (!StringUtils.hasText(req.getBatchNo())) throw new BizException("产品批号不能为空");
        if (!StringUtils.hasText(req.getProductVariety())) throw new BizException("产品品种不能为空");
        switch (type) {
            case BREEDING -> {
                if (!StringUtils.hasText(req.getAquaticQuarantineCert())) throw new BizException("水产品检疫合格证不能为空");
                ProductBatchBreeding e = new ProductBatchBreeding();
                e.setBatchNo(req.getBatchNo());
                e.setProductVariety(req.getProductVariety());
                e.setAquaticQuarantineCert(req.getAquaticQuarantineCert());
                e.setOfficialInspector(req.getOfficialInspector());
                e.setEnterpriseId(user.getId());
                e.setStatus(0);
                breedingMapper.insert(e);
            }
            case PROCESSING -> {
                ProductBatchProcessing e = new ProductBatchProcessing();
                e.setBatchNo(req.getBatchNo());
                e.setProductVariety(req.getProductVariety());
                e.setProductType(req.getProductType());
                e.setProcessingInspectionCert(req.getProcessingInspectionCert());
                e.setOfficialInspector(req.getOfficialInspector());
                e.setEnterpriseId(user.getId());
                applyUpstream(e, req, BREEDING);
                e.setStatus(0);
                processingMapper.insert(e);
            }
            case WHOLESALE -> {
                ProductBatchWholesale e = new ProductBatchWholesale();
                e.setBatchNo(req.getBatchNo());
                e.setProductVariety(req.getProductVariety());
                e.setProductType(req.getProductType());
                e.setEnterpriseId(user.getId());
                applyUpstream(e, req, PROCESSING);
                e.setStatus(0);
                wholesaleMapper.insert(e);
            }
            case RETAIL -> {
                ProductBatchRetail e = new ProductBatchRetail();
                e.setBatchNo(req.getBatchNo());
                e.setProductVariety(req.getProductVariety());
                e.setProductType(req.getProductType());
                e.setEnterpriseId(user.getId());
                applyUpstream(e, req, WHOLESALE);
                e.setStatus(0);
                retailMapper.insert(e);
            }
            default -> throw new BizException("未知企业类型");
        }
    }

    // ==================== 更新 ====================

    @Transactional
    public void update(String type, Long id, BatchSaveRequest req) {
        switch (type) {
            case BREEDING -> {
                ProductBatchBreeding e = require(breedingMapper.selectById(id), "批号不存在");
                e.setProductVariety(req.getProductVariety());
                e.setAquaticQuarantineCert(req.getAquaticQuarantineCert());
                e.setOfficialInspector(req.getOfficialInspector());
                if (Boolean.TRUE.equals(req.getPublish())) e.setStatus(1);
                breedingMapper.updateById(e);
            }
            case PROCESSING -> {
                ProductBatchProcessing e = require(processingMapper.selectById(id), "批号不存在");
                e.setProductVariety(req.getProductVariety());
                e.setProductType(req.getProductType());
                if (StringUtils.hasText(req.getProcessingInspectionCert())) e.setProcessingInspectionCert(req.getProcessingInspectionCert());
                if (req.getUpstreamEnterpriseId() != null) {
                    e.setUpstreamEnterpriseId(req.getUpstreamEnterpriseId());
                    e.setUpstreamBatchNo(req.getUpstreamBatchNo());
                    e.setUpstreamVariety(lookupUpstreamVariety(BREEDING, req.getUpstreamEnterpriseId(), req.getUpstreamBatchNo()));
                }
                if (Boolean.TRUE.equals(req.getSendConfirm())) e.setStatus(1);
                processingMapper.updateById(e);
            }
            case WHOLESALE -> {
                ProductBatchWholesale e = require(wholesaleMapper.selectById(id), "批号不存在");
                e.setProductVariety(req.getProductVariety());
                e.setProductType(req.getProductType());
                if (req.getUpstreamEnterpriseId() != null) {
                    e.setUpstreamEnterpriseId(req.getUpstreamEnterpriseId());
                    e.setUpstreamBatchNo(req.getUpstreamBatchNo());
                    e.setUpstreamVariety(lookupUpstreamVariety(PROCESSING, req.getUpstreamEnterpriseId(), req.getUpstreamBatchNo()));
                }
                if (Boolean.TRUE.equals(req.getSendConfirm())) e.setStatus(1);
                wholesaleMapper.updateById(e);
            }
            case RETAIL -> {
                ProductBatchRetail e = require(retailMapper.selectById(id), "批号不存在");
                e.setProductVariety(req.getProductVariety());
                e.setProductType(req.getProductType());
                if (req.getUpstreamEnterpriseId() != null) {
                    e.setUpstreamEnterpriseId(req.getUpstreamEnterpriseId());
                    e.setUpstreamBatchNo(req.getUpstreamBatchNo());
                    e.setUpstreamVariety(lookupUpstreamVariety(WHOLESALE, req.getUpstreamEnterpriseId(), req.getUpstreamBatchNo()));
                }
                if (Boolean.TRUE.equals(req.getSendConfirm())) e.setStatus(1);
                retailMapper.updateById(e);
            }
            default -> throw new BizException("未知企业类型");
        }
    }

    // ==================== 删除 / 发布 / 下架 / 发送确认 ====================

    @Transactional
    public void delete(String type, Long id) {
        switch (type) {
            case BREEDING -> {
                ProductBatchBreeding e = require(breedingMapper.selectById(id), "批号不存在");
                if (e.getStatus() != 0) throw new BizException("仅待发布状态的批号可删除");
                breedingMapper.deleteById(id);
            }
            case PROCESSING -> {
                ProductBatchProcessing e = require(processingMapper.selectById(id), "批号不存在");
                if (e.getStatus() != 0) throw new BizException("仅新建状态的批号可删除");
                processingMapper.deleteById(id);
            }
            case WHOLESALE -> {
                ProductBatchWholesale e = require(wholesaleMapper.selectById(id), "批号不存在");
                if (e.getStatus() != 0) throw new BizException("仅新建状态的批号可删除");
                wholesaleMapper.deleteById(id);
            }
            case RETAIL -> {
                ProductBatchRetail e = require(retailMapper.selectById(id), "批号不存在");
                if (e.getStatus() != 0) throw new BizException("仅新建状态的批号可删除");
                retailMapper.deleteById(id);
            }
            default -> throw new BizException("未知企业类型");
        }
    }

    @Transactional
    public void publish(String type, Long id) {
        if (!BREEDING.equals(type)) throw new BizException("仅水产养殖企业支持发布");
        ProductBatchBreeding e = require(breedingMapper.selectById(id), "批号不存在");
        if (e.getStatus() != 0) throw new BizException("仅待发布状态可发布");
        e.setStatus(1);
        breedingMapper.updateById(e);
    }

    @Transactional
    public void sendConfirm(String type, Long id) {
        if (BREEDING.equals(type)) throw new BizException("水产养殖企业无需发送确认请求");
        if (PROCESSING.equals(type)) {
            ProductBatchProcessing e = require(processingMapper.selectById(id), "批号不存在");
            if (e.getStatus() != 0) throw new BizException("仅新建状态可发送确认请求");
            e.setStatus(1);
            processingMapper.updateById(e);
        } else if (WHOLESALE.equals(type)) {
            ProductBatchWholesale e = require(wholesaleMapper.selectById(id), "批号不存在");
            if (e.getStatus() != 0) throw new BizException("仅新建状态可发送确认请求");
            e.setStatus(1);
            wholesaleMapper.updateById(e);
        } else {
            ProductBatchRetail e = require(retailMapper.selectById(id), "批号不存在");
            if (e.getStatus() != 0) throw new BizException("仅新建状态可发送确认请求");
            e.setStatus(1);
            retailMapper.updateById(e);
        }
    }

    @Transactional
    public void offShelf(String type, Long id) {
        if (BREEDING.equals(type)) {
            ProductBatchBreeding e = require(breedingMapper.selectById(id), "批号不存在");
            if (e.getStatus() == 2) throw new BizException("该批号已下架");
            e.setStatus(2);
            breedingMapper.updateById(e);
        } else if (PROCESSING.equals(type)) {
            ProductBatchProcessing e = require(processingMapper.selectById(id), "批号不存在");
            if (e.getStatus() == 3) throw new BizException("该批号已下架");
            e.setStatus(3);
            processingMapper.updateById(e);
        } else if (WHOLESALE.equals(type)) {
            ProductBatchWholesale e = require(wholesaleMapper.selectById(id), "批号不存在");
            if (e.getStatus() == 3) throw new BizException("该批号已下架");
            e.setStatus(3);
            wholesaleMapper.updateById(e);
        } else {
            ProductBatchRetail e = require(retailMapper.selectById(id), "批号不存在");
            if (e.getStatus() == 3) throw new BizException("该批号已下架");
            e.setStatus(3);
            retailMapper.updateById(e);
        }
    }

    // ==================== 下游企业进场确认 ====================

    /** 下游确认列表：query 下游类型的批号表，条件是上游企业==我 且 状态==待确认 */
    public List<BatchVO> confirmDownstream(String name) {
        SecurityUser user = SecurityUtils.current();
        return switch (user.getType()) {
            case BREEDING -> confirmList(PROCESSING, user.getId(), name);
            case PROCESSING -> confirmList(WHOLESALE, user.getId(), name);
            case WHOLESALE -> confirmList(RETAIL, user.getId(), name);
            case RETAIL -> throw new BizException("零售商无下游企业可确认");
            default -> throw new BizException("未知企业类型");
        };
    }

    private List<BatchVO> confirmList(String downstreamType, Long myId, String name) {
        final Long myIdF = myId;
        java.util.List<BatchVO> all;
        switch (downstreamType) {
            case PROCESSING -> all = processingMapper.selectList(new LambdaQueryWrapper<ProductBatchProcessing>()
                    .eq(ProductBatchProcessing::getUpstreamEnterpriseId, myIdF)
                    .eq(ProductBatchProcessing::getStatus, 1))
                    .stream().map(this::toVO).toList();
            case WHOLESALE -> all = wholesaleMapper.selectList(new LambdaQueryWrapper<ProductBatchWholesale>()
                    .eq(ProductBatchWholesale::getUpstreamEnterpriseId, myIdF)
                    .eq(ProductBatchWholesale::getStatus, 1))
                    .stream().map(this::toVO).toList();
            case RETAIL -> all = retailMapper.selectList(new LambdaQueryWrapper<ProductBatchRetail>()
                    .eq(ProductBatchRetail::getUpstreamEnterpriseId, myIdF)
                    .eq(ProductBatchRetail::getStatus, 1))
                    .stream().map(this::toVO).toList();
            default -> all = new java.util.ArrayList<>();
        }
        return all.stream().filter(v -> !StringUtils.hasText(name) || v.getEnterpriseName().contains(name)).toList();
    }

    /** 确认下游批号：状态→已确认；若为零售批号且未生成溯源码，则自动生成 */
    @Transactional
    public void confirmDownstreamBatch(String type, Long batchId) {
        SecurityUser user = SecurityUtils.current();
        switch (user.getType()) {
            case BREEDING -> {
                ProductBatchProcessing e = require(processingMapper.selectById(batchId), "批号不存在");
                if (e.getStatus() != 1) throw new BizException("该批号不是待确认状态");
                e.setStatus(2);
                processingMapper.updateById(e);
            }
            case PROCESSING -> {
                ProductBatchWholesale e = require(wholesaleMapper.selectById(batchId), "批号不存在");
                if (e.getStatus() != 1) throw new BizException("该批号不是待确认状态");
                e.setStatus(2);
                wholesaleMapper.updateById(e);
            }
            case WHOLESALE -> {
                ProductBatchRetail e = require(retailMapper.selectById(batchId), "批号不存在");
                if (e.getStatus() != 1) throw new BizException("该批号不是待确认状态");
                e.setStatus(2);
                if (!StringUtils.hasText(e.getTraceCode())) e.setTraceCode(generateTraceCode());
                retailMapper.updateById(e);
            }
            default -> throw new BizException("零售商无下游企业可确认");
        }
    }

    // ==================== 新建用：上游联动下拉 ====================

    public List<NodeEnterprise> upstreamEnterprises(Long provinceId, Long cityId) {
        String type = SecurityUtils.current().getType();
        String upstreamType = switch (type) {
            case PROCESSING -> BREEDING;
            case WHOLESALE -> PROCESSING;
            case RETAIL -> WHOLESALE;
            default -> throw new BizException("水产养殖企业无上游企业");
        };
        LambdaQueryWrapper<NodeEnterprise> w = new LambdaQueryWrapper<NodeEnterprise>()
                .eq(NodeEnterprise::getType, upstreamType)
                .eq(provinceId != null, NodeEnterprise::getProvinceId, provinceId)
                .eq(cityId != null, NodeEnterprise::getCityId, cityId)
                .orderByAsc(NodeEnterprise::getName);
        return nodeEnterpriseMapper.selectList(w);
    }

    public List<UpstreamBatchVO> upstreamBatches(Long enterpriseId, String status) {
        String type = SecurityUtils.current().getType();
        String upstreamType = switch (type) {
            case PROCESSING -> BREEDING;
            case WHOLESALE -> PROCESSING;
            case RETAIL -> WHOLESALE;
            default -> throw new BizException("水产养殖企业无上游企业");
        };
        if (!StringUtils.hasText(status)) status = "0";
        return switch (upstreamType) {
            case BREEDING -> breedingMapper.selectList(new LambdaQueryWrapper<ProductBatchBreeding>()
                    .eq(ProductBatchBreeding::getEnterpriseId, enterpriseId)
                    .ne(ProductBatchBreeding::getStatus, 2).orderByDesc(ProductBatchBreeding::getCreatedAt))
                    .stream().map(b -> new UpstreamBatchVO(b.getId(), b.getBatchNo(), b.getProductVariety(), b.getStatus())).toList();
            case PROCESSING -> processingMapper.selectList(new LambdaQueryWrapper<ProductBatchProcessing>()
                    .eq(ProductBatchProcessing::getEnterpriseId, enterpriseId)
                    .ne(ProductBatchProcessing::getStatus, 3).orderByDesc(ProductBatchProcessing::getCreatedAt))
                    .stream().map(b -> new UpstreamBatchVO(b.getId(), b.getBatchNo(), b.getProductVariety(), b.getStatus())).toList();
            case WHOLESALE -> wholesaleMapper.selectList(new LambdaQueryWrapper<ProductBatchWholesale>()
                    .eq(ProductBatchWholesale::getEnterpriseId, enterpriseId)
                    .ne(ProductBatchWholesale::getStatus, 3).orderByDesc(ProductBatchWholesale::getCreatedAt))
                    .stream().map(b -> new UpstreamBatchVO(b.getId(), b.getBatchNo(), b.getProductVariety(), b.getStatus())).toList();
            default -> List.of();
        };
    }

    // ==================== 工具 ====================

    private <T> T require(T obj, String msg) {
        if (obj == null) throw new BizException(msg);
        return obj;
    }

    private void applyUpstream(ProductBatchProcessing e, BatchSaveRequest req, String upstreamType) {
        if (req.getUpstreamEnterpriseId() == null || !StringUtils.hasText(req.getUpstreamBatchNo())) {
            throw new BizException("必须选择上游企业及其产品批号");
        }
        e.setUpstreamEnterpriseId(req.getUpstreamEnterpriseId());
        e.setUpstreamBatchNo(req.getUpstreamBatchNo());
        e.setUpstreamVariety(lookupUpstreamVariety(upstreamType, req.getUpstreamEnterpriseId(), req.getUpstreamBatchNo()));
    }

    private void applyUpstream(ProductBatchWholesale e, BatchSaveRequest req, String upstreamType) {
        if (req.getUpstreamEnterpriseId() == null || !StringUtils.hasText(req.getUpstreamBatchNo())) {
            throw new BizException("必须选择上游企业及其产品批号");
        }
        e.setUpstreamEnterpriseId(req.getUpstreamEnterpriseId());
        e.setUpstreamBatchNo(req.getUpstreamBatchNo());
        e.setUpstreamVariety(lookupUpstreamVariety(upstreamType, req.getUpstreamEnterpriseId(), req.getUpstreamBatchNo()));
    }

    private void applyUpstream(ProductBatchRetail e, BatchSaveRequest req, String upstreamType) {
        if (req.getUpstreamEnterpriseId() == null || !StringUtils.hasText(req.getUpstreamBatchNo())) {
            throw new BizException("必须选择上游企业及其产品批号");
        }
        e.setUpstreamEnterpriseId(req.getUpstreamEnterpriseId());
        e.setUpstreamBatchNo(req.getUpstreamBatchNo());
        e.setUpstreamVariety(lookupUpstreamVariety(upstreamType, req.getUpstreamEnterpriseId(), req.getUpstreamBatchNo()));
    }

    private String lookupUpstreamVariety(String upstreamType, Long enterpriseId, String batchNo) {
        return switch (upstreamType) {
            case BREEDING -> {
                ProductBatchBreeding b = breedingMapper.selectOne(new LambdaQueryWrapper<ProductBatchBreeding>()
                        .eq(ProductBatchBreeding::getEnterpriseId, enterpriseId)
                        .eq(ProductBatchBreeding::getBatchNo, batchNo));
                yield b == null ? null : b.getProductVariety();
            }
            case PROCESSING -> {
                ProductBatchProcessing b = processingMapper.selectOne(new LambdaQueryWrapper<ProductBatchProcessing>()
                        .eq(ProductBatchProcessing::getEnterpriseId, enterpriseId)
                        .eq(ProductBatchProcessing::getBatchNo, batchNo));
                yield b == null ? null : b.getProductVariety();
            }
            case WHOLESALE -> {
                ProductBatchWholesale b = wholesaleMapper.selectOne(new LambdaQueryWrapper<ProductBatchWholesale>()
                        .eq(ProductBatchWholesale::getEnterpriseId, enterpriseId)
                        .eq(ProductBatchWholesale::getBatchNo, batchNo));
                yield b == null ? null : b.getProductVariety();
            }
            default -> null;
        };
    }

    private String generateTraceCode() {
        return "NFTS-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
    }

    private String enterpriseName(Long id) {
        NodeEnterprise e = id == null ? null : nodeEnterpriseMapper.selectById(id);
        return e == null ? null : e.getName();
    }

    // ---- toVO ----
    private BatchVO toVO(ProductBatchBreeding e) {
        BatchVO v = base(e.getBatchNo(), e.getProductVariety(), e.getEnterpriseId(), e.getStatus(), e.getCreatedAt());
        v.setId(e.getId());
        v.setAquaticQuarantineCert(e.getAquaticQuarantineCert());
        v.setOfficialInspector(e.getOfficialInspector());
        return v;
    }

    private BatchVO toVO(ProductBatchProcessing e) {
        BatchVO v = base(e.getBatchNo(), e.getProductVariety(), e.getEnterpriseId(), e.getStatus(), e.getCreatedAt());
        v.setId(e.getId());
        v.setProductType(e.getProductType());
        v.setProcessingInspectionCert(e.getProcessingInspectionCert());
        v.setOfficialInspector(e.getOfficialInspector());
        fillUpstream(v, e.getUpstreamEnterpriseId(), e.getUpstreamBatchNo(), e.getUpstreamVariety());
        return v;
    }

    private BatchVO toVO(ProductBatchWholesale e) {
        BatchVO v = base(e.getBatchNo(), e.getProductVariety(), e.getEnterpriseId(), e.getStatus(), e.getCreatedAt());
        v.setId(e.getId());
        v.setProductType(e.getProductType());
        fillUpstream(v, e.getUpstreamEnterpriseId(), e.getUpstreamBatchNo(), e.getUpstreamVariety());
        return v;
    }

    private BatchVO toVO(ProductBatchRetail e) {
        BatchVO v = base(e.getBatchNo(), e.getProductVariety(), e.getEnterpriseId(), e.getStatus(), e.getCreatedAt());
        v.setId(e.getId());
        v.setProductType(e.getProductType());
        v.setTraceCode(e.getTraceCode());
        fillUpstream(v, e.getUpstreamEnterpriseId(), e.getUpstreamBatchNo(), e.getUpstreamVariety());
        return v;
    }

    private BatchVO base(String batchNo, String variety, Long entId, Integer status, java.time.LocalDateTime createdAt) {
        BatchVO v = new BatchVO();
        v.setEnterpriseId(entId);
        v.setBatchNo(batchNo);
        v.setProductVariety(variety);
        v.setStatus(status);
        v.setCreatedAt(createdAt);
        v.setEnterpriseName(enterpriseName(entId));
        return v;
    }

    private void fillUpstream(BatchVO v, Long upstreamEntId, String upstreamBatchNo, String upstreamVariety) {
        v.setUpstreamEnterpriseId(upstreamEntId);
        v.setUpstreamBatchNo(upstreamBatchNo);
        v.setUpstreamVariety(upstreamVariety);
        v.setUpstreamEnterpriseName(enterpriseName(upstreamEntId));
    }
}
