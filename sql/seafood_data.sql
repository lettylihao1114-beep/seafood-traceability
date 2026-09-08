-- ============================================================
-- 东软冷冻海产品溯源系统 - 种子数据
-- 所有企业/管理员初始密码均为 123456 (BCrypt，可被 Spring Security 校验)
-- ============================================================
USE seafood_trace;
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE product_batch_retail;
TRUNCATE TABLE product_batch_wholesale;
TRUNCATE TABLE product_batch_processing;
TRUNCATE TABLE product_batch_breeding;
TRUNCATE TABLE node_enterprise;
TRUNCATE TABLE city;
TRUNCATE TABLE province;
TRUNCATE TABLE admin;
SET FOREIGN_KEY_CHECKS = 1;

-- 密码 123456 的 BCrypt 哈希
SET @pwd = '$2b$10$NT9gN9AbhsjE7ba6MEd7Zu9zvTcxlIJ.u4FGFwxKUrHG7peQvJIKW';

-- ----------------------------
-- 管理员
-- ----------------------------
INSERT INTO admin (id, login_code, password) VALUES (1, 'admin', @pwd);

-- ----------------------------
-- 省
-- ----------------------------
INSERT INTO province (id, code, name, abbreviation) VALUES
  (1, 'LN', '辽宁', '辽'),
  (2, 'HA', '河南', '豫'),
  (3, 'SD', '山东', '鲁'),
  (4, 'GD', '广东', '粤');

-- ----------------------------
-- 市
-- ----------------------------
INSERT INTO city (id, code, name, province_id) VALUES
  (1, 'SHENYANG', '沈阳', 1),
  (2, 'DALIAN',  '大连', 1),
  (3, 'ZHENGZHOU','郑州', 2),
  (4, 'LUOYANG', '洛阳', 2),
  (5, 'JINAN',   '济南', 3),
  (6, 'QINGDAO', '青岛', 3),
  (7, 'GUANGZHOU','广州', 4),
  (8, 'ZHUHAI',  '珠海', 4);

-- ----------------------------
-- 节点企业（各类型、多省分布，便于统计图；注册时间散布近12个月便于趋势图）
-- id=1..4 为完整溯源链企业（辽宁沈阳）
-- ----------------------------
INSERT INTO node_enterprise (id, login_code, password, name, type, province_id, city_id, address, business_license_no, contact, phone, aquatic_epidemic_cert, environment_cert, food_circulation_cert, food_operation_cert, created_at) VALUES
 (1, 'breeding001', @pwd, '沈阳浑河水产养殖场',       'BREEDING',   1, 1, '辽宁省沈阳市皇姑区水产路8号',   '91110100MA0A1B2C3D', '王海', '13800000001', '水防证-0241', '环评-0512', NULL, NULL, DATE_SUB(NOW(), INTERVAL 11 MONTH)),
 (2, 'processing001', @pwd, '沈阳冷链海产加工厂',     'PROCESSING', 1, 1, '辽宁省沈阳市于洪区冷链加工巷12号', '91110100MA0E5F6G7H', '李冰', '13800000002', NULL, '环评-0613', NULL, NULL, DATE_SUB(NOW(), INTERVAL 10 MONTH)),
 (3, 'wholesale001', @pwd, '顺发海产批发中心',       'WHOLESALE',  1, 1, '辽宁省沈阳市大东区海产批发街36号', '91110100MA0I8J9K0L', '赵磊', '13800000003', NULL, NULL, '流通证-7788', '经营证-9901', DATE_SUB(NOW(), INTERVAL 9 MONTH)),
 (4, 'retail001', @pwd, '和平区海丰水产店',          'RETAIL',     1, 1, '辽宁省沈阳市和平区水产市场大厅B11', '91110100MA0M1N2O3P', '刘丰', '13800000004', NULL, NULL, NULL, '经营证-9902', DATE_SUB(NOW(), INTERVAL 8 MONTH)),
 (5, 'retail002', @pwd, '青泥洼海味店',              'RETAIL',     1, 2, '辽宁省大连市中山区青泥洼街21号', '91110100MA0Q4R5S6T', '陈海', '13800000005', NULL, NULL, NULL, '经营证-9903', DATE_SUB(NOW(), INTERVAL 7 MONTH)),
 (6, 'processing002', @pwd, '大连海珍水产加工厂',    'PROCESSING', 1, 2, '辽宁省大连市金州区海珍路77号',   '91110100MA0U7V8W9X', '孙海', '13800000006', NULL, '环评-0759', NULL, NULL, DATE_SUB(NOW(), INTERVAL 6 MONTH)),
 (7, 'breeding002', @pwd, '郑州黄河水产养殖园',      'BREEDING',   2, 3, '河南省郑州市惠济区养殖园1号',     '91410000MA0Y1A2B3C', '黄华', '13800000007', '水防证-0433', '环评-0945', NULL, NULL, DATE_SUB(NOW(), INTERVAL 5 MONTH)),
 (8, 'processing003', @pwd, '郑州金海冷冻加工',      'PROCESSING', 2, 3, '河南省郑州市金水区加工大道3号',   '91410000MA0Z4D5E6F', '郑海', '13800000008', NULL, '环评-0946', NULL, NULL, DATE_SUB(NOW(), INTERVAL 5 MONTH)),
 (9, 'wholesale002', @pwd, '豫通水产批发中心',       'WHOLESALE',  2, 4, '河南省洛阳市涧西区海产批发部9号', '91410000MA0G7H8I9J', '周通', '13800000009', NULL, NULL, '流通证-7789', '经营证-9904', DATE_SUB(NOW(), INTERVAL 4 MONTH)),
 (10,'retail003', @pwd, '永辉海鲜超市',              'RETAIL',     2, 3, '河南省郑州市二七区大学路55号',     '91410000MA0J1K2L3M', '钱海', '13800000010', NULL, NULL, NULL, '经营证-9905', DATE_SUB(NOW(), INTERVAL 4 MONTH)),
 (11,'retail004', @pwd, '洛水海鲜店',                'RETAIL',     2, 4, '河南省洛阳市老城区市场口',        '91410000MA0N4O5P6Q', '冯惠', '13800000011', NULL, NULL, NULL, '经营证-9906', DATE_SUB(NOW(), INTERVAL 3 MONTH)),
 (12,'breeding003', @pwd, '济南明湖水产养殖',        'BREEDING',   3, 5, '山东省济南市历城区养殖路88号',     '91370000MA0R7S8T9U', '张盛', '13800000012', '水防证-0455', '环评-1112', NULL, NULL, DATE_SUB(NOW(), INTERVAL 3 MONTH)),
 (13,'processing004', @pwd, '金蓝海产加工集团',      'PROCESSING', 3, 5, '山东省济南市天桥区加工园66号',     '91370000MA0V1W2X3Y', '谷海', '13800000013', NULL, '环评-1113', NULL, NULL, DATE_SUB(NOW(), INTERVAL 2 MONTH)),
 (14,'wholesale003', @pwd, '中储海产供应链',         'WHOLESALE',  3, 5, '山东省济南市历下区物流港19号',     '91370000MA0Z4A5B6C', '鲁储', '13800000014', NULL, NULL, '流通证-7790', '经营证-9907', DATE_SUB(NOW(), INTERVAL 2 MONTH)),
 (15,'breeding004', @pwd, '青岛胶州湾养殖场',        'BREEDING',   3, 6, '山东省青岛市莱西市养殖基地7号',     '91370000MA0D7E8F9G', '吴渔', '13800000015', '水防证-0456', '环评-1314', NULL, NULL, DATE_SUB(NOW(), INTERVAL 1 MONTH)),
 (16,'retail005', @pwd, '青岛海丰水产店',            'RETAIL',     3, 6, '山东省青岛市市南区农贸市场C12',     '91370000MA0H1I2J3K', '姜海', '13800000016', NULL, NULL, NULL, '经营证-9908', DATE_SUB(NOW(), INTERVAL 1 MONTH)),
 (17,'breeding005', @pwd, '广州珠江水产养殖',        'BREEDING',   4, 7, '广东省广州市白云区养殖场甲18号',    '91440000MA0L4M5N6O', '梁海', '13800000017', '水防证-0477', '环评-1516', NULL, NULL, DATE_SUB(NOW(), INTERVAL 0 MONTH)),
 (18,'processing005', @pwd, '广州广宏海产加工',      'PROCESSING', 4, 7, '广东省广州市番禺区加工街45号',      '91440000MA0P7Q8R9S', '曹宏', '13800000018', NULL, '环评-1517', NULL, NULL, DATE_SUB(NOW(), INTERVAL 0 MONTH)),
 (19,'retail006', @pwd, '家乐福海鲜广州店',          'RETAIL',     4, 7, '广东省广州市天河区商场路99号',      '91440000MA0T1U2V3W', '祁乐', '13800000019', NULL, NULL, NULL, '经营证-9909', DATE_SUB(NOW(), INTERVAL 0 MONTH)),
 (20,'wholesale004', @pwd, '珠海中食水产批发',       'WHOLESALE',  4, 8, '广东省珠海市香洲区冷链中心3号',      '91440000MA0X4Y5Z6A', '郑食', '13800000020', NULL, NULL, '流通证-7791', '经营证-9910', DATE_SUB(NOW(), INTERVAL 0 MONTH));

-- ----------------------------
-- 产品批号
-- ① 完整溯源链：养殖(沈阳浑河1)→加工(沈阳冷链2)→批发(顺发3)→零售(海丰4)，同一批号 345643633，全部已确认，零售带溯源码
--    对应需求规格/原型中示例数据（鲜活海鱼→冷冻海鱼→冷冻带鱼→冷冻带鱼段）
-- ----------------------------
INSERT INTO product_batch_breeding (id, batch_no, product_variety, aquatic_quarantine_cert, official_inspector, enterprise_id, status, created_at) VALUES
 (1, '345643633', '鲜活海鱼',   '水检证-3464435', '王海', 1, 1, DATE_SUB(NOW(), INTERVAL 6 MONTH)),
 (2, '1000001',   '鲜活大黄鱼', '水检证-1122334', '王海', 1, 0, DATE_SUB(NOW(), INTERVAL 1 MONTH));

INSERT INTO product_batch_processing (id, batch_no, product_variety, processing_inspection_cert, official_inspector, product_type, enterprise_id, upstream_enterprise_id, upstream_batch_no, upstream_variety, status, created_at) VALUES
 (1, '345643633', '冷冻海鱼',  '水检证-3464435', '李冰', '冷冻海产', 2, 1, '345643633', '鲜活海鱼',   2, DATE_SUB(NOW(), INTERVAL 5 MONTH)),
 (2, '5555666',   '带鱼段',    '水检证-9000112', '李冰', '冷冻海产', 2, 1, '345643633', '鲜活海鱼',   1, DATE_SUB(NOW(), INTERVAL 2 MONTH));

INSERT INTO product_batch_wholesale (id, batch_no, product_variety, product_type, enterprise_id, upstream_enterprise_id, upstream_batch_no, upstream_variety, status, created_at) VALUES
 (1, '345643633', '冷冻带鱼',   '冷冻海产', 3, 2, '345643633', '冷冻海鱼', 2, DATE_SUB(NOW(), INTERVAL 4 MONTH)),
 (2, '6666777',   '海产分割件', '冷冻海产', 3, 2, '345643633', '冷冻海鱼', 1, DATE_SUB(NOW(), INTERVAL 1 MONTH));

INSERT INTO product_batch_retail (id, batch_no, product_variety, product_type, enterprise_id, upstream_enterprise_id, upstream_batch_no, upstream_variety, trace_code, status, created_at) VALUES
 (1, '345643633', '冷冻带鱼段', '冷冻海产', 4, 3, '345643633', '冷冻带鱼', 'NFTS-20240901001', 2, DATE_SUB(NOW(), INTERVAL 3 MONTH)),
 (2, '7777888',   '鲅鱼段',     '冷冻海产', 4, 3, '345643633', '冷冻带鱼', NULL,                1, DATE_SUB(NOW(), INTERVAL 0 MONTH)),
 (3, '0000111',   '虾仁',       '冷冻海产', 4, 3, '345643633', '冷冻带鱼', NULL,                0, DATE_SUB(NOW(), INTERVAL 0 MONTH));
