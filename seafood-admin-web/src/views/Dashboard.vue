<template>
  <div class="dashboard">
    <div class="header">
      <h1>冷冻海产品溯源系统 · 系统管理端</h1>
      <span class="admin">
        <el-icon><UserFilled /></el-icon> 管理员
        <el-button link type="danger" @click="logout">退出登录</el-button>
      </span>
    </div>

    <!-- 预警·概览 -->
    <el-row :gutter="16" class="stats">
      <el-col :span="4"><div class="stat"><div class="num">{{ warn.enterpriseCount }}</div><div class="lab">节点企业</div></div></el-col>
      <el-col :span="5"><div class="stat"><div class="num">{{ warn.totalBatches }}</div><div class="lab">批号总数</div></div></el-col>
      <el-col :span="5"><div class="stat warn"><div class="num">{{ warn.pendingConfirm }}</div><div class="lab">待确认积压</div></div></el-col>
      <el-col :span="5"><div class="stat"><div class="num">{{ warn.activeBatches }}</div><div class="lab">已确认/已发布</div></div></el-col>
      <el-col :span="5"><div class="stat danger"><div class="num">{{ warn.stallBatches }}</div><div class="lab">停滞批号</div></div></el-col>
    </el-row>

    <!-- 统计大屏 -->
    <el-row :gutter="16" class="charts">
      <el-col :span="12"><div class="chart-box"><div ref="trendRef" class="chart"></div></div></el-col>
      <el-col :span="12"><div class="chart-box"><div ref="provincePieRef" class="chart"></div></div></el-col>
      <el-col :span="12"><div class="chart-box"><div ref="typePieRef" class="chart"></div></div></el-col>
      <el-col :span="12"><div class="chart-box"><div ref="provinceBarRef" class="chart"></div></div></el-col>
      <el-col :span="24"><div class="chart-box"><div ref="geoRef" class="chart chart-tall"></div></div></el-col>
    </el-row>

    <!-- 企业筛选 -->
    <el-card class="filter-card">
      <el-form inline :model="query">
        <el-form-item label="名称">
          <el-input v-model="query.name" placeholder="企业名称模糊查询" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="query.type" placeholder="全部" clearable style="width: 140px">
            <el-option v-for="(v, k) in typeLabels" :key="k" :label="v" :value="k" />
          </el-select>
        </el-form-item>
        <el-form-item label="所属省">
          <el-select v-model="query.provinceId" placeholder="全部" clearable style="width: 130px" @change="onProvinceChange">
            <el-option v-for="p in provinces" :key="p.id" :label="p.name" :value="p.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="所属市">
          <el-select v-model="query.cityId" placeholder="全部" clearable style="width: 130px">
            <el-option v-for="c in queryCities" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="load(1)">查询</el-button>
          <el-button @click="resetQuery">清空</el-button>
          <el-button type="success" @click="openDialog('create')">新建</el-button>
          <el-button type="warning" plain @click="exportEnterprises">导出 CSV</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 企业列表 -->
    <el-card>
      <el-table :data="rows" border stripe>
        <el-table-column prop="id" label="编号" width="70" />
        <el-table-column prop="name" label="企业名称" min-width="160" />
        <el-table-column label="企业类型" width="110">
          <template #default="{ row }">
            <el-tag :type="tagType(row.type)">{{ typeLabels[row.type] || row.type }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="provinceName" label="所属省" width="90" />
        <el-table-column prop="cityName" label="所属市" width="90" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="openDialog('detail', row)">详情</el-button>
            <el-button link type="primary" @click="openDialog('edit', row)">编辑</el-button>
            <el-button link type="danger" @click="onDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        class="pager"
        background
        layout="prev, pager, next, total"
        :total="total"
        :page-size="query.size"
        :current-page="query.page"
        @current-change="load"
      />
    </el-card>

    <!-- 操作日志 -->
    <el-card class="log-card">
      <template #header>
        <div class="log-head">
          <span>操作日志（审计）</span>
          <el-button size="small" @click="loadLogs">刷新</el-button>
        </div>
      </template>
      <el-table :data="logs" border stripe size="small" empty-text="暂无日志">
        <el-table-column prop="createdAt" label="时间" width="170" />
        <el-table-column prop="userCode" label="操作人" width="120" />
        <el-table-column prop="role" label="角色" width="130" />
        <el-table-column prop="action" label="动作" width="140" />
        <el-table-column prop="target" label="对象" width="150" />
        <el-table-column prop="detail" label="详情" min-width="200" />
      </el-table>
      <el-pagination
        class="pager"
        background
        layout="prev, pager, next, total"
        :total="logTotal"
        :page-size="logQuery.size"
        :current-page="logQuery.page"
        @current-change="loadLogs"
      />
    </el-card>

    <!-- 新建/编辑/详情 Dialog -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="560px">
      <el-form :model="form" label-width="110px">
        <el-form-item label="企业名称"><el-input v-model="form.name" :disabled="mode === 'detail'" /></el-form-item>
        <el-form-item label="企业类型">
          <el-select v-model="form.type" :disabled="mode !== 'create'" style="width: 100%">
            <el-option v-for="(v, k) in typeLabels" :key="k" :label="v" :value="k" />
          </el-select>
        </el-form-item>
        <el-form-item label="登录编码">
          <el-input v-model="form.loginCode" :disabled="mode !== 'create'" placeholder="企业端登录账号" />
        </el-form-item>
        <el-form-item label="登录密码">
          <el-input v-model="form.password" :disabled="mode !== 'create'" placeholder="默认 123456" show-password />
        </el-form-item>
        <el-form-item label="所属省">
          <el-select v-model="form.provinceId" :disabled="mode === 'detail'" style="width: 100%" @change="onProvinceChangeForm">
            <el-option v-for="p in provinces" :key="p.id" :label="p.name" :value="p.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="所属市">
          <el-select v-model="form.cityId" :disabled="mode === 'detail'" style="width: 100%">
            <el-option v-for="c in formCities" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="企业地址"><el-input v-model="form.address" :disabled="mode === 'detail'" /></el-form-item>
        <el-form-item label="营业执照"><el-input v-model="form.businessLicenseNo" :disabled="mode === 'detail'" /></el-form-item>
        <el-form-item label="联系人"><el-input v-model="form.contact" :disabled="mode === 'detail'" /></el-form-item>
        <el-form-item label="联系电话"><el-input v-model="form.phone" :disabled="mode === 'detail'" /></el-form-item>
        <template v-if="form.type === 'BREEDING' || form.type === 'PROCESSING'">
          <el-form-item label="环境影响证书"><el-input v-model="form.environmentCert" :disabled="mode === 'detail'" /></el-form-item>
        </template>
        <template v-if="form.type === 'BREEDING'">
          <el-form-item label="水产养殖防疫证"><el-input v-model="form.aquaticEpidemicCert" :disabled="mode === 'detail'" /></el-form-item>
        </template>
        <template v-if="form.type === 'WHOLESALE' || form.type === 'RETAIL'">
          <el-form-item label="食品经营证"><el-input v-model="form.foodOperationCert" :disabled="mode === 'detail'" /></el-form-item>
        </template>
        <template v-if="form.type === 'WHOLESALE'">
          <el-form-item label="食品流通证"><el-input v-model="form.foodCirculationCert" :disabled="mode === 'detail'" /></el-form-item>
        </template>
      </el-form>
      <template #footer>
        <el-button v-if="mode === 'detail'" @click="dialogVisible = false">关闭</el-button>
        <template v-else>
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="save">保存</el-button>
        </template>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onBeforeUnmount, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import * as echarts from 'echarts'
import chinaJson from '../assets/china.json'
import {
  pageEnterprises, listProvinces, listCities, createEnterprise, updateEnterprise,
  deleteEnterprise, getEnterprise, trend, provincePie, typePie, provinceBar,
  warningStats, pageLogs
} from '../api'

const typeLabels = { BREEDING: '水产养殖企业', PROCESSING: '冷冻加工企业', WHOLESALE: '批发商', RETAIL: '零售商' }
const tagType = (type) => ({ BREEDING: 'success', PROCESSING: 'warning', WHOLESALE: '', RETAIL: 'info' }[type] || '')

const provinces = ref([])
const query = reactive({ page: 1, size: 10, name: '', type: '', provinceId: null, cityId: null })
const queryCities = ref([])
const rows = ref([])
const total = ref(0)

const dialogVisible = ref(false)
const mode = ref('create')
const dialogTitle = ref('')
const form = reactive({})
const formCities = ref([])

const charts = []
const trendRef = ref()
const provincePieRef = ref()
const typePieRef = ref()
const provinceBarRef = ref()
const geoRef = ref()

echarts.registerMap('china', chinaJson)

// 短省名 → geoJSON 全名（地图映射用）
function fullProvinceName(n) {
  const special = { 北京: '北京市', 天津: '天津市', 上海: '上海市', 重庆: '重庆市', 内蒙古: '内蒙古自治区', 广西: '广西壮族自治区', 西藏: '西藏自治区', 新疆: '新疆维吾尔自治区', 宁夏: '宁夏回族自治区', 香港: '香港特别行政区', 澳门: '澳门特别行政区' }
  if (special[n]) return special[n]
  if (n.endsWith('市') || n.endsWith('区') || n.endsWith('省') || n.endsWith('自治区')) return n
  return n + '省'
}

const warn = reactive({ enterpriseCount: 0, totalBatches: 0, pendingConfirm: 0, activeBatches: 0, stallBatches: 0 })
const logs = ref([])
const logTotal = ref(0)
const logQuery = reactive({ page: 1, size: 10 })

function typeLabel(v) { return typeLabels[v] || v }
function isEmpty(e) { return e == null || e === '' }

async function load(page) {
  query.page = page || query.page
  const res = await pageEnterprises(query)
  rows.value = res.data.records
  total.value = res.data.total
}

async function resetQuery() {
  query.name = ''
  query.type = ''
  query.provinceId = null
  query.cityId = null
  queryCities.value = []
  load(1)
}

async function onProvinceChange(v) {
  query.cityId = null
  queryCities.value = v ? (await listCities(v)).data : []
}

async function onProvinceChangeForm(v) {
  form.cityId = null
  formCities.value = v ? (await listCities(v)).data : []
}

async function loadProvinces() {
  provinces.value = (await listProvinces()).data
}

async function openDialog(m, row = {}) {
  mode.value = m
  dialogTitle.value = { create: '新建节点企业', edit: '编辑节点企业', detail: '节点企业详情' }[m]
  dialogVisible.value = true
  if (m === 'create') {
    Object.assign(form, {
      id: null, name: '', type: 'BREEDING', loginCode: '', password: '',
      provinceId: null, cityId: null, address: '', businessLicenseNo: '', contact: '', phone: '',
      aquaticEpidemicCert: '', environmentCert: '', foodCirculationCert: '', foodOperationCert: ''
    })
    formCities.value = []
  } else {
    Object.assign(form, row)
    formCities.value = row.provinceId ? (await listCities(row.provinceId)).data : []
  }
}

async function save() {
  if (!form.name) return ElMessage.warning('请输入企业名称')
  if (mode.value === 'create') {
    if (!form.loginCode) return ElMessage.warning('请输入登录编码')
    await createEnterprise(form)
    ElMessage.success('创建成功')
  } else {
    await updateEnterprise(form.id, form)
    ElMessage.success('保存成功')
  }
  dialogVisible.value = false
  load()
}

async function onDelete(row) {
  await ElMessageBox.confirm(`确定删除企业「${row.name}」吗？`, '提示', { type: 'warning' })
  await deleteEnterprise(row.id)
  ElMessage.success('删除成功')
  load()
}

async function loadWarnings() {
  const res = await warningStats()
  Object.assign(warn, res.data)
}

async function loadLogs(page) {
  logQuery.page = page || logQuery.page
  const res = await pageLogs(logQuery)
  logs.value = res.data.records
  logTotal.value = res.data.total
}

function exportEnterprises() {
  const header = ['编号', '企业名称', '企业类型', '所属省', '所属市', '地址', '联系人', '联系电话']
  const lines = rows.value.map((r) => [
    r.id, r.name, typeLabels[r.type] || r.type, r.provinceName || '', r.cityName || '',
    r.address || '', r.contact || '', r.phone || ''
  ])
  const csv = [header, ...lines]
    .map((row) => row.map((v) => `"${String(v ?? '').replace(/"/g, '""')}"`).join(','))
    .join('\n')
  const blob = new Blob(['﻿' + csv], { type: 'text/csv;charset=utf-8;' })
  const a = document.createElement('a')
  a.href = URL.createObjectURL(blob)
  a.download = `节点企业_${new Date().toISOString().slice(0, 10)}.csv`
  a.click()
  URL.revokeObjectURL(a.href)
  ElMessage.success('已导出当前页企业')
}

// ---- ECharts ----
function renderCharts() {
  const d = (el) => charts.push(echarts.init(el))
  trendRef.value && d(trendRef.value)
  provincePieRef.value && d(provincePieRef.value)
  typePieRef.value && d(typePieRef.value)
  provinceBarRef.value && d(provinceBarRef.value)
  geoRef.value && d(geoRef.value)
  charts.forEach((c) => { c.clear(); c.resize() })

  trend().then((res) => setChart(0, {
    title: { text: '近12个月企业注册数量趋势', left: 'center', textStyle: { fontSize: 15 } },
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: res.data.map((i) => i.name) },
    yAxis: { type: 'value' },
    series: [{ type: 'line', smooth: true, areaStyle: {}, data: res.data.map((i) => i.value), color: '#4f7cff' }]
  }))
  provincePie().then((res) => setChart(1, {
    title: { text: '省分组注册数量分布', left: 'center', textStyle: { fontSize: 15 } },
    tooltip: { trigger: 'item' },
    series: [{ type: 'pie', radius: '60%', data: res.data.map((i) => ({ name: i.name, value: i.value })) }]
  }))
  typePie().then((res) => setChart(2, {
    title: { text: '类型分组注册数量分布', left: 'center', textStyle: { fontSize: 15 } },
    tooltip: { trigger: 'item' },
    series: [{ type: 'pie', radius: '60%', data: res.data.map((i) => ({ name: i.name, value: i.value })) }]
  }))
  provinceBar().then((res) => setChart(3, {
    title: { text: '省分组注册数量统计', left: 'center', textStyle: { fontSize: 15 } },
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: res.data.map((i) => i.name) },
    yAxis: { type: 'value' },
    series: [{ type: 'bar', barWidth: '45%', data: res.data.map((i) => i.value), color: '#3fb27f' }]
  }))
  provinceBar().then((res) => setChart(4, {
    title: { text: '节点企业各省分布地图', left: 'center', textStyle: { fontSize: 15 } },
    tooltip: { trigger: 'item', formatter: (p) => `${p.name}<br/>企业数量：${p.value ?? 0}` },
    visualMap: { min: 0, max: Math.max(1, ...res.data.map((i) => i.value)), calculable: true, orient: 'horizontal', left: 'center', bottom: 0, text: ['多', '少'] },
    series: [{
      type: 'map', map: 'china', roam: false,
      itemStyle: { areaColor: '#eaf2f8', borderColor: '#b7c8d6' },
      emphasis: { itemStyle: { areaColor: '#9bd0f4' }, label: { show: true } },
      data: res.data.map((i) => ({ name: fullProvinceName(i.name), value: i.value }))
    }]
  }))
}

function setChart(idx, option) {
  charts[idx] && charts[idx].setOption(option)
}

function onResize() { charts.forEach((c) => c.resize()) }

function logout() {
  localStorage.clear()
  window.location.href = '/login'
}

onMounted(async () => {
  await loadProvinces()
  await load(1)
  await loadWarnings()
  await loadLogs(1)
  nextTick(() => {
    renderCharts()
    window.addEventListener('resize', onResize)
  })
})
onBeforeUnmount(() => window.removeEventListener('resize', onResize))
</script>

<style scoped>
.dashboard { padding: 16px 24px 40px; background: #f2f4f8; min-height: 100vh; }
.header { display: flex; align-items: center; justify-content: space-between; margin-bottom: 16px; }
.header h1 { font-size: 22px; margin: 0; }
.admin { color: #606266; }
.stats { margin-bottom: 16px; }
.stat { background: #fff; border-radius: 8px; padding: 16px; text-align: center; }
.stat .num { font-size: 26px; font-weight: 700; color: #303133; }
.stat .lab { color: #909399; font-size: 13px; margin-top: 4px; }
.stat.warn .num { color: #e6a23c; }
.stat.danger .num { color: #f56c6c; }
.charts { margin-bottom: 16px; }
.chart-box { background: #fff; border-radius: 8px; padding: 8px; margin-bottom: 16px; }
.chart { height: 280px; }
.chart-tall { height: 400px; }
.filter-card { margin-bottom: 16px; }
.pager { margin-top: 14px; justify-content: flex-end; }
.log-card { margin-top: 16px; }
.log-head { display: flex; align-items: center; justify-content: space-between; }
</style>
