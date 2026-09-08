package com.seafood.controller;

import com.seafood.common.PageResult;
import com.seafood.common.Result;
import com.seafood.dto.StatItem;
import com.seafood.entity.City;
import com.seafood.entity.NodeEnterprise;
import com.seafood.entity.Province;
import com.seafood.service.AdminService;
import com.seafood.service.WarningService;
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
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;
    private final WarningService warningService;

    public AdminController(AdminService adminService, WarningService warningService) {
        this.adminService = adminService;
        this.warningService = warningService;
    }

    // ---------- 节点企业 CRUD ----------
    @GetMapping("/node-enterprises")
    public Result<PageResult<NodeEnterprise>> page(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Long provinceId,
            @RequestParam(required = false) Long cityId) {
        return Result.ok(adminService.pageEnterprise(page, size, name, type, provinceId, cityId));
    }

    @PostMapping("/node-enterprises")
    public Result<Void> create(@RequestBody NodeEnterprise entity) {
        adminService.create(entity);
        return Result.ok();
    }

    @GetMapping("/node-enterprises/{id}")
    public Result<NodeEnterprise> detail(@PathVariable Long id) {
        return Result.ok(adminService.detail(id));
    }

    @PutMapping("/node-enterprises/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody NodeEnterprise entity) {
        entity.setId(id);
        adminService.update(entity);
        return Result.ok();
    }

    @DeleteMapping("/node-enterprises/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        adminService.delete(id);
        return Result.ok();
    }

    // ---------- 省市 ----------
    @GetMapping("/regions/provinces")
    public Result<List<Province>> provinces() {
        return Result.ok(adminService.listProvinces());
    }

    @GetMapping("/regions/cities")
    public Result<List<City>> cities(@RequestParam Long provinceId) {
        return Result.ok(adminService.listCities(provinceId));
    }

    // ---------- 数据统计（可视化大屏） ----------
    @GetMapping("/stats/appear-trend")
    public Result<List<StatItem>> appearTrend() {
        return Result.ok(adminService.appearTrend());
    }

    @GetMapping("/stats/province-distribution")
    public Result<List<StatItem>> provinceDistribution() {
        return Result.ok(adminService.provinceDistribution());
    }

    @GetMapping("/stats/type-distribution")
    public Result<List<StatItem>> typeDistribution() {
        return Result.ok(adminService.typeDistribution());
    }

    @GetMapping("/stats/province-bar")
    public Result<List<StatItem>> provinceBar() {
        return Result.ok(adminService.provinceBar());
    }

    // ---------- 预警 ----------
    @GetMapping("/stats/warnings")
    public Result<java.util.Map<String, Object>> warnings() {
        return Result.ok(warningService.summary());
    }
}
