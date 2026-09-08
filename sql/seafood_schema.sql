-- ============================================================
-- 冷冻海产品溯源系统 - 数据库结构
-- MySQL 8.0 / utf8mb4
-- ============================================================
CREATE DATABASE IF NOT EXISTS seafood_trace DEFAULT CHARSET utf8mb4 COLLATE utf8mb4_general_ci;
USE seafood_trace;

SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS product_batch_retail;
DROP TABLE IF EXISTS product_batch_wholesale;
DROP TABLE IF EXISTS product_batch_processing;
DROP TABLE IF EXISTS product_batch_breeding;
DROP TABLE IF EXISTS node_enterprise;
DROP TABLE IF EXISTS city;
DROP TABLE IF EXISTS province;
DROP TABLE IF EXISTS admin;
SET FOREIGN_KEY_CHECKS = 1;

-- ----------------------------
-- 管理员
-- ----------------------------
CREATE TABLE admin (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '编号',
  login_code VARCHAR(50) NOT NULL COMMENT '登录编码',
  password VARCHAR(100) NOT NULL COMMENT '登录密码(BCrypt)',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_admin_code (login_code)
) ENGINE=InnoDB COMMENT='管理员信息';

-- ----------------------------
-- 省
-- ----------------------------
CREATE TABLE province (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  code VARCHAR(10) NOT NULL,
  name VARCHAR(50) NOT NULL,
  abbreviation VARCHAR(20),
  UNIQUE KEY uk_province_code (code)
) ENGINE=InnoDB COMMENT='省信息';

-- ----------------------------
-- 市
-- ----------------------------
CREATE TABLE city (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  code VARCHAR(10) NOT NULL,
  name VARCHAR(50) NOT NULL,
  province_id BIGINT NOT NULL,
  UNIQUE KEY uk_city_code (code),
  KEY idx_city_province (province_id),
  CONSTRAINT fk_city_province FOREIGN KEY (province_id) REFERENCES province(id)
) ENGINE=InnoDB COMMENT='市信息';

-- ----------------------------
-- 节点企业（水产养殖/冷冻加工/批发/零售）
-- ----------------------------
CREATE TABLE node_enterprise (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  login_code VARCHAR(50) NOT NULL COMMENT '登录编码',
  password VARCHAR(100) NOT NULL COMMENT '登录密码(BCrypt)',
  name VARCHAR(100) NOT NULL COMMENT '企业名称',
  type VARCHAR(20) NOT NULL COMMENT 'BREEDING/PROCESSING/WHOLESALE/RETAIL',
  province_id BIGINT,
  city_id BIGINT,
  address VARCHAR(200) COMMENT '企业地址',
  business_license_no VARCHAR(50) COMMENT '营业执照编号',
  contact VARCHAR(50) COMMENT '联系人',
  phone VARCHAR(20) COMMENT '联系电话',
  aquatic_epidemic_cert VARCHAR(50) COMMENT '水产养殖防疫合格证',
  environment_cert VARCHAR(50) COMMENT '环境影响评价资质证书',
  food_circulation_cert VARCHAR(50) COMMENT '食品流通许可证',
  food_operation_cert VARCHAR(50) COMMENT '食品经营许可证',
  status TINYINT DEFAULT 1 COMMENT '1启用/0停用',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_ent_code (login_code),
  KEY idx_ent_type (type),
  KEY idx_ent_region (province_id, city_id),
  CONSTRAINT fk_ent_province FOREIGN KEY (province_id) REFERENCES province(id),
  CONSTRAINT fk_ent_city FOREIGN KEY (city_id) REFERENCES city(id)
) ENGINE=InnoDB COMMENT='节点企业信息';

-- ----------------------------
-- 水产养殖企业产品批号
-- ----------------------------
CREATE TABLE product_batch_breeding (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  batch_no VARCHAR(50) NOT NULL COMMENT '产品批号',
  product_variety VARCHAR(50) NOT NULL COMMENT '产品品种',
  aquatic_quarantine_cert VARCHAR(50) COMMENT '水产品检疫合格证',
  official_inspector VARCHAR(50) COMMENT '官方检验员名称',
  enterprise_id BIGINT NOT NULL COMMENT '所属水产养殖企业ID',
  status TINYINT DEFAULT 0 COMMENT '0待发布/1已发布/2已下架',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_bb_ent_batch (enterprise_id, batch_no),
  KEY idx_bb_status (status),
  CONSTRAINT fk_bb_ent FOREIGN KEY (enterprise_id) REFERENCES node_enterprise(id)
) ENGINE=InnoDB COMMENT='水产养殖企业产品批号';

-- ----------------------------
-- 冷冻加工企业产品批号
-- ----------------------------
CREATE TABLE product_batch_processing (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  batch_no VARCHAR(50) NOT NULL COMMENT '产品批号',
  product_variety VARCHAR(50) NOT NULL COMMENT '产品品种',
  processing_inspection_cert VARCHAR(50) COMMENT '水产品检验检疫合格证',
  official_inspector VARCHAR(50) COMMENT '官方检验员名称',
  product_type VARCHAR(50) COMMENT '产品类型',
  enterprise_id BIGINT NOT NULL COMMENT '所属冷冻加工企业ID',
  upstream_enterprise_id BIGINT COMMENT '上游企业ID',
  upstream_batch_no VARCHAR(50) COMMENT '上游企业产品批号',
  upstream_variety VARCHAR(50) COMMENT '上游产品品种',
  status TINYINT DEFAULT 0 COMMENT '0新建/1待确认/2已确认/3已下架',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_sb_ent_batch (enterprise_id, batch_no),
  KEY idx_sb_status (status),
  KEY idx_sb_upstream (upstream_enterprise_id, upstream_batch_no),
  CONSTRAINT fk_sb_ent FOREIGN KEY (enterprise_id) REFERENCES node_enterprise(id)
) ENGINE=InnoDB COMMENT='冷冻加工企业产品批号';

-- ----------------------------
-- 批发商产品批号
-- ----------------------------
CREATE TABLE product_batch_wholesale (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  batch_no VARCHAR(50) NOT NULL COMMENT '产品批号',
  product_variety VARCHAR(50) NOT NULL COMMENT '产品品种',
  product_type VARCHAR(50) COMMENT '产品类型',
  enterprise_id BIGINT NOT NULL COMMENT '所属批发商ID',
  upstream_enterprise_id BIGINT COMMENT '上游企业ID',
  upstream_batch_no VARCHAR(50) COMMENT '上游企业产品批号',
  upstream_variety VARCHAR(50) COMMENT '上游产品品种',
  status TINYINT DEFAULT 0 COMMENT '0新建/1待确认/2已确认/3已下架',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_wb_ent_batch (enterprise_id, batch_no),
  KEY idx_wb_status (status),
  KEY idx_wb_upstream (upstream_enterprise_id, upstream_batch_no),
  CONSTRAINT fk_wb_ent FOREIGN KEY (enterprise_id) REFERENCES node_enterprise(id)
) ENGINE=InnoDB COMMENT='批发商产品批号';

-- ----------------------------
-- 零售商产品批号
-- ----------------------------
CREATE TABLE product_batch_retail (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  batch_no VARCHAR(50) NOT NULL COMMENT '产品批号',
  product_variety VARCHAR(50) NOT NULL COMMENT '产品品种',
  product_type VARCHAR(50) COMMENT '产品类型',
  enterprise_id BIGINT NOT NULL COMMENT '所属零售商ID',
  upstream_enterprise_id BIGINT COMMENT '上游企业ID',
  upstream_batch_no VARCHAR(50) COMMENT '上游企业产品批号',
  upstream_variety VARCHAR(50) COMMENT '上游产品品种',
  trace_code VARCHAR(64) COMMENT '溯源标识码',
  status TINYINT DEFAULT 0 COMMENT '0新建/1待确认/2已确认/3已下架',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_rb_ent_batch (enterprise_id, batch_no),
  UNIQUE KEY uk_rb_trace (trace_code),
  KEY idx_rb_status (status),
  KEY idx_rb_upstream (upstream_enterprise_id, upstream_batch_no),
  CONSTRAINT fk_rb_ent FOREIGN KEY (enterprise_id) REFERENCES node_enterprise(id)
) ENGINE=InnoDB COMMENT='零售商产品批号';
