<template>
  <div class="home">
    <div class="hero">
      <h1>冷冻海产品溯源系统</h1>
      <p class="slogan">来源可查 · 去向可追 · 责任可究</p>

      <div class="query-box">
        <el-input v-model="code" size="large" placeholder="请输入产品包装上的溯源标识码" @keyup.enter="search" />
        <el-button type="primary" size="large" class="btn" @click="search">明源</el-button>
      </div>

      <div class="scan-row" @click="tip">
        <el-icon :size="30"><FullScreen /></el-icon>
        <span>扫描二维码</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = useRouter()
const code = ref('')

function search() {
  if (!code.value.trim()) return ElMessage.warning('请输入溯源标识码')
  router.push(`/trace/${code.value.trim()}`)
}

function tip() {
  ElMessage.info('扫码功能将在后续版本开放，请输入溯源标识码查询')
}
</script>

<style scoped>
.home { min-height: 100vh; background: linear-gradient(160deg, #1d7a5f 0%, #134b3b 100%); }
.hero { padding: 90px 20px 60px; text-align: center; color: #fff; }
.hero h1 { font-size: 34px; margin: 0 0 8px; }
.slogan { color: rgba(255, 255, 255, 0.85); margin: 0 0 34px; }
.query-box { display: flex; justify-content: center; gap: 12px; max-width: 520px; margin: 0 auto; }
.query-box .el-input { max-width: 360px; }
.btn { background: #ffd500; color: #333; border: none; font-weight: 600; }
.scan-row { display: inline-flex; align-items: center; gap: 8px; margin-top: 28px; color: rgba(255, 255, 255, 0.9); cursor: pointer; }
</style>
