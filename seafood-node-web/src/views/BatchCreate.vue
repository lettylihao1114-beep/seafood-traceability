<template>
  <div class="create">
    <h3 class="page-title">新建产品批号</h3>
    <el-card>
      <el-form :model="form" label-width="130px">
        <el-form-item label="产品批号">
          <el-input v-model="form.batchNo" placeholder="请输入本企业产品批号" />
        </el-form-item>
        <el-form-item label="产品品种">
          <el-input v-model="form.productVariety" placeholder="如：鲜活海鱼 / 冷冻海鱼 / 冷冻带鱼段" />
        </el-form-item>

        <!-- 养殖：检疫信息 -->
        <template v-if="auth.type === 'BREEDING'">
          <el-form-item label="水产品检疫合格证">
            <el-input v-model="form.aquaticQuarantineCert" />
          </el-form-item>
          <el-form-item label="官方检验员名称">
            <el-input v-model="form.officialInspector" />
          </el-form-item>
        </template>

        <!-- 加工：水产品检验信息 -->
        <template v-if="auth.type === 'PROCESSING'">
          <el-form-item label="水产品检验检疫合格证">
            <el-input v-model="form.processingInspectionCert" />
          </el-form-item>
          <el-form-item label="官方检验员名称">
            <el-input v-model="form.officialInspector" />
          </el-form-item>
        </template>

        <!-- 其它：产品类型 + 上游进场信息 -->
        <template v-if="auth.type !== 'BREEDING'">
          <el-form-item label="产品类型">
            <el-select v-model="form.productType" placeholder="请选择">
              <el-option label="冷冻海产" value="冷冻海产" />
              <el-option label="冰鲜海产" value="冰鲜海产" />
              <el-option label="速冻海鲜" value="速冻海鲜" />
            </el-select>
          </el-form-item>
          <el-divider content-position="left">本批号进场信息（上游）</el-divider>
          <el-form-item label="上游企业所在省">
            <el-select v-model="upstream.provinceId" @change="onProvince">
              <el-option v-for="p in provinces" :key="p.id" :label="p.name" :value="p.id" />
            </el-select>
          </el-form-item>
          <el-form-item label="上游企业所在市">
            <el-select v-model="upstream.cityId" @change="onCity">
              <el-option v-for="c in cities" :key="c.id" :label="c.name" :value="c.id" />
            </el-select>
          </el-form-item>
          <el-form-item label="上游企业名称">
            <el-select v-model="form.upstreamEnterpriseId" @change="onEnterprise">
              <el-option v-for="e in enterprises" :key="e.id" :label="e.name" :value="e.id" />
            </el-select>
          </el-form-item>
          <el-form-item label="上游企业产品批号">
            <el-select v-model="form.upstreamBatchNo" @change="onBatch">
              <el-option v-for="b in batches" :key="b.id" :label="`${b.batchNo}(${b.productVariety})`" :value="b.batchNo" />
            </el-select>
          </el-form-item>
          <el-form-item label="上游产品品种">
            <el-input v-model="form.upstreamVariety" disabled />
          </el-form-item>
        </template>
      </el-form>

      <div class="actions">
        <el-button type="primary" size="large" @click="submit">新建</el-button>
        <el-button size="large" @click="$router.push('/batch/list')">取消</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { auth } from '../store'
import { listProvinces, listCities, listUpstreamEnterprises, listUpstreamBatches, createBatch } from '../api'

const router = useRouter()
const form = reactive({
  batchNo: '', productVariety: '', productType: '',
  aquaticQuarantineCert: '', processingInspectionCert: '', officialInspector: '',
  upstreamEnterpriseId: null, upstreamBatchNo: '', upstreamVariety: ''
})
const upstream = reactive({ provinceId: null, cityId: null })
const provinces = ref([])
const cities = ref([])
const enterprises = ref([])
const batches = ref([])

async function loadProvinces() { provinces.value = (await listProvinces()).data }
async function onProvince() {
  upstream.cityId = null
  form.upstreamEnterpriseId = null
  form.upstreamBatchNo = ''
  cities.value = (await listCities(upstream.provinceId)).data
  enterprises.value = []
  batches.value = []
}
async function onCity() {
  form.upstreamEnterpriseId = null
  form.upstreamBatchNo = ''
  enterprises.value = (await listUpstreamEnterprises({ provinceId: upstream.provinceId, cityId: upstream.cityId })).data
  batches.value = []
}
async function onEnterprise() {
  form.upstreamBatchNo = ''
  batches.value = (await listUpstreamBatches({ enterpriseId: form.upstreamEnterpriseId })).data
}
async function onBatch() {
  const b = batches.value.find((x) => x.batchNo === form.upstreamBatchNo)
  form.upstreamVariety = b ? b.productVariety : ''
}

async function submit() {
  if (!form.batchNo) return ElMessage.warning('请输入产品批号')
  if (!form.productVariety) return ElMessage.warning('请输入产品品种')
  if (auth.type !== 'BREEDING' && (!form.upstreamEnterpriseId || !form.upstreamBatchNo)) {
    return ElMessage.warning('请选择上游企业及其产品批号')
  }
  await createBatch(form)
  ElMessage.success('创建成功')
  router.push('/batch/list')
}

loadProvinces()
</script>

<style scoped>
.page-title { margin: 0 0 14px; }
.actions { display: flex; gap: 12px; margin-top: 8px; }
</style>
