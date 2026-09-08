package com.seafood.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seafood.common.BizException;
import com.seafood.common.MapConvert;
import com.seafood.common.PageResult;
import com.seafood.dto.StatItem;
import com.seafood.entity.City;
import com.seafood.entity.NodeEnterprise;
import com.seafood.entity.Province;
import com.seafood.mapper.CityMapper;
import com.seafood.mapper.NodeEnterpriseMapper;
import com.seafood.mapper.ProvinceMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class AdminService {

    private static final Map<String, String> TYPE_LABELS;
    static {
        TYPE_LABELS = new HashMap<>();
        TYPE_LABELS.put("BREEDING", "水产养殖企业");
        TYPE_LABELS.put("PROCESSING", "冷冻加工企业");
        TYPE_LABELS.put("WHOLESALE", "批发商");
        TYPE_LABELS.put("RETAIL", "零售商");
    }

    private final NodeEnterpriseMapper nodeEnterpriseMapper;
    private final ProvinceMapper provinceMapper;
    private final CityMapper cityMapper;
    private final PasswordEncoder passwordEncoder;

    public AdminService(NodeEnterpriseMapper nodeEnterpriseMapper, ProvinceMapper provinceMapper,
                        CityMapper cityMapper, PasswordEncoder passwordEncoder) {
        this.nodeEnterpriseMapper = nodeEnterpriseMapper;
        this.provinceMapper = provinceMapper;
        this.cityMapper = cityMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public PageResult<NodeEnterprise> pageEnterprise(long page, long size, String name, String type,
                                                     Long provinceId, Long cityId) {
        LambdaQueryWrapper<NodeEnterprise> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(name), NodeEnterprise::getName, name)
               .eq(StringUtils.hasText(type), NodeEnterprise::getType, type)
               .eq(provinceId != null, NodeEnterprise::getProvinceId, provinceId)
               .eq(cityId != null, NodeEnterprise::getCityId, cityId)
               .orderByDesc(NodeEnterprise::getId);
        Page<NodeEnterprise> result = nodeEnterpriseMapper.selectPage(new Page<>(page, size), wrapper);
        enrichNames(result.getRecords());
        return new PageResult<>(result.getTotal(), result.getRecords());
    }

    public NodeEnterprise detail(Long id) {
        NodeEnterprise ent = nodeEnterpriseMapper.selectById(id);
        if (ent == null) {
            throw new BizException("企业不存在");
        }
        enrichNames(List.of(ent));
        return ent;
    }

    @Transactional
    public void create(NodeEnterprise entity) {
        if (nodeEnterpriseMapper.selectCount(
                new LambdaQueryWrapper<NodeEnterprise>().eq(NodeEnterprise::getLoginCode, entity.getLoginCode())) > 0) {
            throw new BizException("登录编码已存在");
        }
        if (!TYPE_LABELS.containsKey(entity.getType())) {
            throw new BizException("企业类型不合法");
        }
        String rawPwd = StringUtils.hasText(entity.getPassword()) ? entity.getPassword() : "123456";
        entity.setPassword(passwordEncoder.encode(rawPwd));
        entity.setStatus(1);
        nodeEnterpriseMapper.insert(entity);
    }

    @Transactional
    public void update(NodeEnterprise entity) {
        NodeEnterprise exist = nodeEnterpriseMapper.selectById(entity.getId());
        if (exist == null) {
            throw new BizException("企业不存在");
        }
        entity.setPassword(null); // 密码更新由流通节点端自己完成
        nodeEnterpriseMapper.updateById(entity);
    }

    @Transactional
    public void delete(Long id) {
        nodeEnterpriseMapper.deleteById(id);
    }

    /** 近12个月注册数量趋势（含无人注册月份补0） */
    public List<StatItem> appearTrend() {
        List<StatItem> raw = MapConvert.toStat(nodeEnterpriseMapper.countByMonth());
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM");
        LocalDate now = LocalDate.now();
        List<StatItem> result = new ArrayList<>();
        Map<String, Long> map = raw.stream().collect(Collectors.toMap(StatItem::getName, StatItem::getValue));
        for (int i = 11; i >= 0; i--) {
            String ym = now.minusMonths(i).format(fmt);
            result.add(new StatItem(ym, map.getOrDefault(ym, 0L)));
        }
        return result;
    }

    public List<StatItem> provinceDistribution() {
        return MapConvert.toStat(nodeEnterpriseMapper.countByProvince());
    }

    public List<StatItem> typeDistribution() {
        return MapConvert.toStat(nodeEnterpriseMapper.countByType()).stream()
                .map(s -> new StatItem(TYPE_LABELS.getOrDefault(s.getName(), s.getName()), s.getValue()))
                .collect(Collectors.toList());
    }

    public List<StatItem> provinceBar() {
        return MapConvert.toStat(nodeEnterpriseMapper.countByProvince());
    }

    public List<Province> listProvinces() {
        return provinceMapper.selectList(null);
    }

    public List<City> listCities(Long provinceId) {
        return cityMapper.selectList(new LambdaQueryWrapper<City>().eq(City::getProvinceId, provinceId));
    }

    private void enrichNames(List<NodeEnterprise> list) {
        if (list.isEmpty()) {
            return;
        }
        List<Long> provinceIds = list.stream().map(NodeEnterprise::getProvinceId).filter(java.util.Objects::nonNull).distinct().toList();
        List<Long> cityIds = list.stream().map(NodeEnterprise::getCityId).filter(java.util.Objects::nonNull).distinct().toList();
        Map<Long, Province> provinces = provinceIds.isEmpty() ? Map.of() :
                provinceMapper.selectBatchIds(provinceIds).stream().collect(Collectors.toMap(Province::getId, Function.identity()));
        Map<Long, City> cities = cityIds.isEmpty() ? Map.of() :
                cityMapper.selectBatchIds(cityIds).stream().collect(Collectors.toMap(City::getId, Function.identity()));
        for (NodeEnterprise ent : list) {
            Province p = provinces.get(ent.getProvinceId());
            City c = cities.get(ent.getCityId());
            if (p != null) ent.setProvinceName(p.getName());
            if (c != null) ent.setCityName(c.getName());
        }
    }
}
