<template>
  <div class="edit">
    <h3 class="page-title">更新产品批号</h3>
    <el-card>
      <el-form :model="form" label-width="130px">
        <el-form-item label="产品批号">
          <el-input v-model="form.batchNo" disabled />
        </el-form-item>
        <el-form-item label="产品品种">
          <el-input v-model="form.productVariety" />
        </el-form-item>

        <template v-if="auth.type === 'BREEDING'">
          <el-form-item label="水产品检疫合格证">
            <el-input v-model="form.aquaticQuarantineCert" />
          </el-form-item>
          <el-form-item label="官方检验员名称">
            <el-input v-model="form.officialInspector" />
          </el-form-item>
          <el-form-item label="是否发布">
            <el-switch v-model="form.publish" />
          </el-form-item>
        </template>

        <template v-if="auth.type === 'PROCESSING'">
          <el-form-item label="水产品检验检疫合格证">
            <el-input v-model="form.processingInspectionCert" />
          </el-form-item>
          <el-form-item label="官方检验员名称">
            <el-input v-model="form.officialInspector" />
          </el-form-item>
        </template>

        <template v-if="auth.type !== 'BREEDING'">
          <el-form-item label="产品类型">
            <el-select v-model="form.productType">
              <el-option label="冷冻海产" value="冷冻海产" />
              <el-option label="冰鲜海产" value="冰鲜海产" />
              <el-option label="速冻海鲜" value="速冻海鲜" />
            </el-select>
          </el-form-item>
          <el-divider content-position="left">进场信息（上游）</el-divider>
          <el-form-item label="上游企业名称">
            <el-input :model-value="form.upstreamEnterpriseName" disabled />
          </el-form-item>
          <el-form-item label="上游企业产品批号">
            <el-input v-model="form.upstreamBatchNo" disabled />
          </el-form-item>
          <el-form-item label="上游产品品种">
            <el-input v-model="form.upstreamVariety" disabled />
          </el-form-item>
          <el-form-item label="是否向上游发送确认请求">
            <el-switch v-model="form.sendConfirm" />
          </el-form-item>
        </template>
      </el-form>

      <div class="actions">
        <el-button type="primary" size="large" @click="submit">保存</el-button>
        <el-button size="large" @click="$router.back()">返回</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { auth } from '../store'
import { batchDetail, updateBatch } from '../api'

const route = useRoute()
const router = useRouter()
const form = reactive({})

async function load() {
  const res = await batchDetail(route.params.id)
  Object.assign(form, res.data, { publish: res.data.status === 1, sendConfirm: res.data.status === 1 })
}

async function submit() {
  await updateBatch(route.params.id, form)
  ElMessage.success('保存成功')
  router.push('/batch/list')
}

onMounted(load)
</script>

<style scoped>
.page-title { margin: 0 0 14px; }
.actions { display: flex; gap: 12px; margin-top: 8px; }
</style>
