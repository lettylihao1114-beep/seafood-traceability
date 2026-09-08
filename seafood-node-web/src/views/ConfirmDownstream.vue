<template>
  <div class="confirm">
    <h3 class="page-title">下游企业进场确认</h3>
    <el-card>
      <div class="filter">
        <el-input v-model="name" placeholder="下游企业名称（模糊查询）" clearable style="width: 260px" />
        <el-button type="primary" @click="load">查找</el-button>
      </div>

      <el-table :data="rows" stripe>
        <el-table-column prop="enterpriseName" label="下游企业名称" width="200" />
        <el-table-column prop="batchNo" label="进场批号" width="130" />
        <el-table-column prop="productVariety" label="进场品种" width="140" />
        <el-table-column prop="upstreamBatchNo" label="所属上游企业产品批号" width="170" />
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button type="success" size="small" @click="onConfirm(row)">确认</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listConfirmDownstream, confirmBatch } from '../api'

const name = ref('')
const rows = ref([])

async function load() {
  rows.value = (await listConfirmDownstream(name.value)).data
}

async function onConfirm(row) {
  await ElMessageBox.confirm(`确认「${row.enterpriseName}」的进场批号 ${row.batchNo} 吗？`, '进场确认', { type: 'warning' })
  await confirmBatch(row.id)
  ElMessage.success('已确认')
  load()
}

onMounted(load)
</script>

<style scoped>
.page-title { margin: 0 0 14px; }
.filter { display: flex; gap: 10px; margin-bottom: 14px; }
</style>
