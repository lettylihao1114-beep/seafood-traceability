<template>
  <div class="login-wrap">
    <el-card class="login-card">
      <h2 class="title">东软冷冻海产品溯源系统</h2>
      <p class="subtitle">系统管理端</p>
      <el-form :model="form" @keyup.enter="handleLogin">
        <el-form-item>
          <el-input v-model="form.loginCode" placeholder="登录编码" size="large" clearable />
        </el-form-item>
        <el-form-item>
          <el-input v-model="form.password" type="password" placeholder="登录密码" size="large" show-password />
        </el-form-item>
        <el-button type="primary" size="large" style="width: 100%" :loading="loading" @click="handleLogin">
          登 录
        </el-button>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login } from '../api'

const router = useRouter()
const loading = ref(false)
const form = reactive({ loginCode: '', password: '' })

async function handleLogin() {
  if (!form.loginCode || !form.password) return ElMessage.warning('请输入登录编码和密码')
  loading.value = true
  try {
    const res = await login(form)
    localStorage.setItem('token', res.data.token)
    localStorage.setItem('role', res.data.role)
    ElMessage.success('登录成功')
    router.push('/')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-wrap {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #2b3a55 0%, #1c2534 100%);
}
.login-card {
  width: 380px;
  padding: 10px 10px 20px;
}
.title {
  text-align: center;
  margin: 6px 0 4px;
}
.subtitle {
  text-align: center;
  color: #909399;
  margin: 0 0 24px;
}
</style>
