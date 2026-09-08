package com.seafood.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.seafood.entity.NodeEnterprise;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

public interface NodeEnterpriseMapper extends BaseMapper<NodeEnterprise> {

    @Select("SELECT p.name AS name, COUNT(*) AS value FROM node_enterprise e " +
            "JOIN province p ON e.province_id = p.id GROUP BY p.name ORDER BY value DESC")
    List<Map<String, Object>> countByProvince();

    @Select("SELECT e.type AS name, COUNT(*) AS value FROM node_enterprise e GROUP BY e.type")
    List<Map<String, Object>> countByType();

    @Select("SELECT DATE_FORMAT(created_at, '%Y-%m') AS name, COUNT(*) AS value " +
            "FROM node_enterprise WHERE created_at >= DATE_SUB(NOW(), INTERVAL 12 MONTH) " +
            "GROUP BY DATE_FORMAT(created_at, '%Y-%m') ORDER BY name")
    List<Map<String, Object>> countByMonth();
}
