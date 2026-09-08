<template>
  <div class="password">
    <h3 class="page-title">更新密码</h3>
    <el-card>
      <el-form :model="form" label-width="120px" style="max-width: 440px">
        <el-form-item label="旧密码">
          <el-input v-model="form.oldPassword" type="password" show-password />
        </el-form-item>
        <el-form-item label="新密码">
          <el-input v-model="form.newPassword" type="password" show-password />
        </el-form-item>
        <el-form-item label="再次输入新密码">
          <el-input v-model="confirm" type="password" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submit">更新密码</el-button>
          <el-button type="danger" plain @click="logout">退出登录</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { changePassword } from '../api'

const form = reactive({ oldPassword: '', newPassword: '' })
const confirm = ref('')

async function submit() {
  if (!form.oldPassword || !form.newPassword) return ElMessage.warning('请填写完整')
  if (form.newPassword !== confirm.value) return ElMessage.warning('两次输入的新密码不一致')
  await changePassword(form)
  ElMessage.success('密码已更新')
  logout()
}

function logout() {
  localStorage.clear()
  window.location.href = '/login'
}
</script>

<style scoped>
.page-title { margin: 0 0 14px; }
</style>
