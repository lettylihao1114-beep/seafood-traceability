<template>
  <div class="profile">
    <h3 class="page-title">当前登录企业信息</h3>
    <el-card>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="企业名称">{{ info.name }}</el-descriptions-item>
        <el-descriptions-item label="企业类型">
          <el-tag>{{ typeLabels[info.type] || info.type }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="所属省市">{{ info.provinceName }} {{ info.cityName }}</el-descriptions-item>
        <el-descriptions-item label="企业地址">{{ info.address || '-' }}</el-descriptions-item>
        <el-descriptions-item label="营业执照编号">{{ info.businessLicenseNo || '-' }}</el-descriptions-item>
        <el-descriptions-item label="联系人">{{ info.contact || '-' }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ info.phone || '-' }}</el-descriptions-item>
        <el-descriptions-item label="登录编码">{{ info.loginCode }}</el-descriptions-item>
        <el-descriptions-item v-if="info.aquaticEpidemicCert" label="水产养殖防疫证">{{ info.aquaticEpidemicCert }}</el-descriptions-item>
        <el-descriptions-item v-if="info.environmentCert" label="环境影响证书">{{ info.environmentCert }}</el-descriptions-item>
        <el-descriptions-item v-if="info.foodCirculationCert" label="食品流通许可">{{ info.foodCirculationCert }}</el-descriptions-item>
        <el-descriptions-item v-if="info.foodOperationCert" label="食品经营许可">{{ info.foodOperationCert }}</el-descriptions-item>
      </el-descriptions>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getProfile } from '../api'
import { typeLabels } from '../store'

const info = ref({})
onMounted(async () => { info.value = (await getProfile()).data })
</script>

<style scoped>
.page-title { margin: 0 0 14px; }
</style>
