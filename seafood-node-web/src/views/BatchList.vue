<template>
  <div>
    <div class="bar">
      <h3 class="page-title">产品批号管理</h3>
      <el-button type="primary" plain @click="exportCsv">导出 CSV</el-button>
    </div>
    <el-card>
      <el-tabs v-model="activeStatus" @tab-change="load">
        <el-tab-pane v-for="s in statuses" :key="s[0]" :label="s[1]" :name="String(s[0])" />
      </el-tabs>

      <el-table :data="rows" stripe @row-click="goDetail">
        <el-table-column prop="batchNo" label="产品批号" width="130" />
        <el-table-column prop="productVariety" label="品种" width="140" />
        <el-table-column prop="productType" label="产品类型" width="110" />
        <el-table-column prop="upstreamBatchNo" label="进场批号" width="130" />
        <el-table-column prop="enterpriseName" label="上游企业" />
        <el-table-column label="状态" width="140">
          <template #default="{ row }">
            <el-tag :type="statusTag(row.status)">{{ statusLabel(row.status) }}</el-tag>
            <el-tag v-if="isStale(row)" type="danger" effect="dark" size="small" style="margin-left:6px">预警</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="230" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.status === 0" link type="primary" @click.stop="goEdit(row)">更新</el-button>
            <el-button v-if="row.status === 0" link type="danger" @click.stop="onDelete(row)">删除</el-button>
            <el-button v-if="auth.type === 'BREEDING' && row.status === 0" link type="success" @click.stop="onPublish(row)">发布</el-button>
            <el-button v-if="auth.type !== 'BREEDING' && row.status === 0" link type="success" @click.stop="onSend(row)">送确认</el-button>
            <el-button v-if="(auth.type === 'BREEDING' && row.status === 1) || (auth.type !== 'BREEDING' && row.status === 2)" link type="warning" @click.stop="onOffShelf(row)">下架</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { auth, typeLabels } from '../store'
import { listBatches, deleteBatch, publishBatch, sendConfirm, offShelf } from '../api'

const router = useRouter()
const activeStatus = ref('0')
const rows = ref([])

const isBreeding = computed(() => auth.type === 'BREEDING')
const statuses = computed(() => isBreeding.value ? [[0, '待发布'], [1, '已发布']] : [[0, '新建'], [1, '待确认'], [2, '已确认']])

async function load() {
  const res = await listBatches(activeStatus.value)
  rows.value = res.data
}

function statusLabel(s) {
  if (isBreeding.value) return { 0: '待发布', 1: '已发布', 2: '已下架' }[s]
  return { 0: '新建', 1: '待确认', 2: '已确认', 3: '已下架' }[s]
}
function statusTag(s) {
  if (s === 0) return 'info'
  if (isBreeding.value && s === 1) return 'success'
  if (!isBreeding.value && s === 2) return 'success'
  return 'warning'
}

function goDetail(row) { router.push(`/batch/detail/${row.id}`) }
function goEdit(row) { router.push(`/batch/edit/${row.id}`) }

async function onPublish(row) { await publishBatch(row.id); ElMessage.success('已发布'); load() }
async function onSend(row) { await sendConfirm(row.id); ElMessage.success('已发送确认请求'); load() }
async function onOffShelf(row) {
  await ElMessageBox.confirm('确认下架该产品批号吗？', '提示', { type: 'warning' })
  await offShelf(row.id); ElMessage.success('已下架'); load()
}
async function onDelete(row) {
  await ElMessageBox.confirm('确认删除该产品批号吗？', '提示', { type: 'warning' })
  await deleteBatch(row.id); ElMessage.success('已删除'); load()
}

function isStale(row) {
  if (!row.createdAt) return false
  const days = (Date.now() - new Date(row.createdAt).getTime()) / 86400000
  if (days < 30) return false
  // 停滞：养殖 待发布(0) / 其它 新建(0)；待确认超期：状态1
  return row.status === 0 || row.status === 1
}

function exportCsv() {
  const header = ['产品批号', '产品品种', '产品类型', '进场批号', '上游企业', '状态', '创建时间']
  const lines = rows.value.map((r) => [
    r.batchNo, r.productVariety, r.productType || '', r.upstreamBatchNo || '',
    r.enterpriseName || '', statusLabel(r.status), r.createdAt || ''
  ])
  const csv = [header, ...lines]
    .map((row) => row.map((v) => `"${String(v ?? '').replace(/"/g, '""')}"`).join(','))
    .join('\n')
  const blob = new Blob(['﻿' + csv], { type: 'text/csv;charset=utf-8;' })
  const a = document.createElement('a')
  a.href = URL.createObjectURL(blob)
  a.download = `批号台账_${new Date().toISOString().slice(0, 10)}.csv`
  a.click()
  URL.revokeObjectURL(a.href)
  ElMessage.success('已导出当前列表')
}

onMounted(load)
</script>

<style scoped>
.bar { display: flex; align-items: center; justify-content: space-between; margin: 0 0 14px; }
.page-title { margin: 0; }
</style>
