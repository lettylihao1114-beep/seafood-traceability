<template>
  <div class="trace">
    <div class="head">
      <el-button link @click="$router.push('/')"><el-icon><ArrowLeft /></el-icon> 返回</el-button>
      <h3>溯源信息</h3>
      <div class="code-box">
        <span class="code">溯源码：{{ route.params.code }}</span>
        <el-button size="small" @click="copyCode">复制</el-button>
      </div>
    </div>

    <el-skeleton v-if="loading" :rows="6" animated />
    <el-empty v-else-if="list.length === 0" description="未查询到溯源信息" />

    <template v-else>
      <el-steps v-if="list.length > 1" :active="list.length" finish-status="success" align-center class="chain">
        <el-step v-for="(node, i) in list" :key="i" :title="node.nodeTypeName" :description="node.enterpriseName" />
      </el-steps>
      <div v-for="(node, i) in list" :key="i" class="node">
        <div class="node-head">
          <span class="step">{{ i + 1 }}</span>
          <span class="type-name">{{ node.nodeTypeName }}</span>
          <span class="chain" v-if="i < list.length - 1"><el-icon><Right /></el-icon></span>
        </div>
        <div class="node-body">
          <p class="name">{{ node.enterpriseName }}</p>
          <el-descriptions :column="2" border size="small">
            <el-descriptions-item label="产品品种">{{ node.productVariety }}</el-descriptions-item>
            <el-descriptions-item label="产品批号">{{ node.batchNo }}</el-descriptions-item>
            <el-descriptions-item v-if="node.cert" label="检验检疫合格证">{{ node.cert }}</el-descriptions-item>
            <el-descriptions-item label="产地">{{ node.region }}</el-descriptions-item>
            <el-descriptions-item label="批号日期">{{ node.batchDate }}</el-descriptions-item>
            <el-descriptions-item v-if="node.traceCode" label="溯源标识码">{{ node.traceCode }}</el-descriptions-item>
          </el-descriptions>
        </div>
      </div>
    </template>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { trace } from '../api'

const route = useRoute()
const list = ref([])
const loading = ref(true)

onMounted(async () => {
  try {
    list.value = (await trace(route.params.code)).data
  } finally {
    loading.value = false
  }
})

async function copyCode() {
  const code = route.params.code
  try {
    await navigator.clipboard.writeText(code)
    ElMessage.success('已复制溯源码')
  } catch (e) {
    // 降级：非安全上下文（http）clipboard 可能被禁，用旧 API
    const ta = document.createElement('textarea')
    ta.value = code
    document.body.appendChild(ta)
    ta.select()
    document.execCommand('copy')
    document.body.removeChild(ta)
    ElMessage.success('已复制溯源码')
  }
}
</script>

<style scoped>
.trace { max-width: 780px; margin: 0 auto; padding: 20px 16px; }
.head { display: flex; align-items: center; gap: 14px; margin-bottom: 18px; }
.head h3 { margin: 0; }
.code-box { display: flex; align-items: center; gap: 8px; margin-left: auto; }
.code { color: #909399; font-size: 13px; }
.chain { margin: 6px auto 22px; padding: 16px 20px; background: #fff; border: 1px solid #e4e7ed; border-radius: 10px; }
.node { background: #fff; border: 1px solid #e4e7ed; border-radius: 10px; margin-bottom: 16px; overflow: hidden; }
.node-head { display: flex; align-items: center; gap: 10px; padding: 12px 16px; background: #f5f6f8; }
.step { width: 26px; height: 26px; border-radius: 50%; background: #1d7a5f; color: #fff; display: inline-flex; align-items: center; justify-content: center; font-weight: 600; }
.type-name { font-weight: 600; }
.chain { margin-left: auto; color: #c0c4cc; }
.node-body { padding: 14px 16px; }
.name { font-size: 17px; font-weight: 600; margin: 0 0 12px; }
</style>
