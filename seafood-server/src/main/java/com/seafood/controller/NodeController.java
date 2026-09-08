package com.seafood.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.seafood.common.Result;
import com.seafood.dto.BatchSaveRequest;
import com.seafood.dto.BatchVO;
import com.seafood.dto.ChangePasswordRequest;
import com.seafood.dto.UpstreamBatchVO;
import com.seafood.entity.City;
import com.seafood.entity.NodeEnterprise;
import com.seafood.entity.Province;
import com.seafood.mapper.CityMapper;
import com.seafood.mapper.ProvinceMapper;
import com.seafood.security.SecurityUtils;
import com.seafood.service.NodeService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/node")
public class NodeController {

    private final NodeService nodeService;
    private final ProvinceMapper provinceMapper;
    private final CityMapper cityMapper;

    public NodeController(NodeService nodeService, ProvinceMapper provinceMapper, CityMapper cityMapper) {
        this.nodeService = nodeService;
        this.provinceMapper = provinceMapper;
        this.cityMapper = cityMapper;
    }

    private String myType() {
        return SecurityUtils.current().getType();
    }

    // ---------- 我的 / 改密 ----------
    @GetMapping("/profile")
    public Result<NodeEnterprise> profile() {
        return Result.ok(nodeService.profile());
    }

    @PostMapping("/password")
    public Result<Void> changePassword(@Valid @RequestBody ChangePasswordRequest req) {
        nodeService.changePassword(req.getOldPassword(), req.getNewPassword());
        return Result.ok();
    }

    // ---------- 批号 ----------
    @GetMapping("/batches")
    public Result<List<BatchVO>> list(@RequestParam String status) {
        return Result.ok(nodeService.listBatches(status));
    }

    @GetMapping("/batches/{id}")
    public Result<BatchVO> detail(@PathVariable Long id) {
        return Result.ok(nodeService.detail(myType(), id));
    }

    @PostMapping("/batches")
    public Result<Void> create(@RequestBody BatchSaveRequest req) {
        nodeService.create(req);
        return Result.ok();
    }

    @PutMapping("/batches/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody BatchSaveRequest req) {
        nodeService.update(myType(), id, req);
        return Result.ok();
    }

    @DeleteMapping("/batches/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        nodeService.delete(myType(), id);
        return Result.ok();
    }

    @PostMapping("/batches/{id}/publish")
    public Result<Void> publish(@PathVariable Long id) {
        nodeService.publish(myType(), id);
        return Result.ok();
    }

    @PostMapping("/batches/{id}/send-confirm")
    public Result<Void> sendConfirm(@PathVariable Long id) {
        nodeService.sendConfirm(myType(), id);
        return Result.ok();
    }

    @PostMapping("/batches/{id}/off-shelf")
    public Result<Void> offShelf(@PathVariable Long id) {
        nodeService.offShelf(myType(), id);
        return Result.ok();
    }

    // ---------- 下游确认 ----------
    @GetMapping("/confirm-downstream")
    public Result<List<BatchVO>> confirmDownstream(@RequestParam(required = false) String name) {
        return Result.ok(nodeService.confirmDownstream(name));
    }

    @PostMapping("/confirm-downstream/{batchId}")
    public Result<Void> confirmBatch(@PathVariable Long batchId) {
        nodeService.confirmDownstreamBatch(myType(), batchId);
        return Result.ok();
    }

    // ---------- 省市（新建用） ----------
    @GetMapping("/regions/provinces")
    public Result<List<Province>> provinces() {
        return Result.ok(provinceMapper.selectList(null));
    }

    @GetMapping("/regions/cities")
    public Result<List<City>> cities(@RequestParam Long provinceId) {
        return Result.ok(cityMapper.selectList(new LambdaQueryWrapper<City>().eq(City::getProvinceId, provinceId)));
    }

    // ---------- 上游联动（新建/更新用） ----------
    @GetMapping("/upstream/enterprises")
    public Result<List<NodeEnterprise>> upstreamEnterprises(
            @RequestParam(required = false) Long provinceId,
            @RequestParam(required = false) Long cityId) {
        return Result.ok(nodeService.upstreamEnterprises(provinceId, cityId));
    }

    @GetMapping("/upstream/batches")
    public Result<List<UpstreamBatchVO>> upstreamBatches(
            @RequestParam Long enterpriseId,
            @RequestParam(required = false) String status) {
        return Result.ok(nodeService.upstreamBatches(enterpriseId, status));
    }
}
