<template>
  <div class="detail">
    <h3 class="page-title">产品批号信息</h3>
    <el-card>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="产品批号">{{ detail.batchNo }}</el-descriptions-item>
        <el-descriptions-item label="产品品种">{{ detail.productVariety }}</el-descriptions-item>
        <el-descriptions-item label="产品类型">{{ detail.productType || '-' }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="statusTag">{{ statusLabel }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item v-if="detail.aquaticQuarantineCert" label="水产品检疫合格证">{{ detail.aquaticQuarantineCert }}</el-descriptions-item>
        <el-descriptions-item v-if="detail.processingInspectionCert" label="水产品检验合格证">{{ detail.processingInspectionCert }}</el-descriptions-item>
        <el-descriptions-item v-if="detail.officialInspector" label="官方检验员">{{ detail.officialInspector }}</el-descriptions-item>
        <el-descriptions-item label="上游企业">{{ detail.upstreamEnterpriseName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="上游批号">{{ detail.upstreamBatchNo || '-' }}</el-descriptions-item>
        <el-descriptions-item label="上游品种">{{ detail.upstreamVariety || '-' }}</el-descriptions-item>
        <el-descriptions-item v-if="detail.traceCode" label="溯源标识码">
          <el-tag class="trace" type="success">{{ detail.traceCode }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ detail.createdAt }}</el-descriptions-item>
      </el-descriptions>
      <el-button class="back" @click="$router.back()">返回</el-button>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { auth } from '../store'
import { batchDetail } from '../api'

const route = useRoute()
const detail = ref({})
const isBreeding = computed(() => auth.type === 'BREEDING')
const statusLabel = computed(() => {
  const s = detail.value.status
  if (isBreeding.value) return { 0: '待发布', 1: '已发布', 2: '已下架' }[s]
  return { 0: '新建', 1: '待确认', 2: '已确认', 3: '已下架' }[s]
})
const statusTag = computed(() => {
  const s = detail.value.status
  if (s === 0) return 'info'
  if (s === 1) return 'warning'
  if (s === 2) return 'success'
  return 'info'
})

onMounted(async () => { detail.value = (await batchDetail(route.params.id)).data })
</script>

<style scoped>
.page-title { margin: 0 0 14px; }
.trace { font-weight: 600; }
.back { margin-top: 20px; }
</style>
