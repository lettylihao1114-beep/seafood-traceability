<template>
  <div class="layout">
    <header class="topbar">
      <div class="title">冷冻海产品溯源系统</div>
      <div class="company" v-if="auth.name">
        {{ auth.name }}
        <el-tag size="small" class="type-tag">{{ typeLabels[auth.type] || auth.type }}</el-tag>
      </div>
    </header>
    <main class="content">
      <router-view />
    </main>
    <nav class="bottom-nav">
      <router-link v-for="item in navs" :key="item.path" :to="item.path" class="nav-item"
                   :class="{ active: $route.meta.nav === item.path }">
        <el-icon><component :is="item.icon" /></el-icon>
        <span>{{ item.label }}</span>
      </router-link>
    </nav>
  </div>
</template>

<script setup>
import { onMounted, computed } from 'vue'
import { auth, typeLabels, loadAuth } from '../store'

const navs = [
  { path: '/', label: '首页', icon: 'HomeFilled' },
  { path: '/profile', label: '我的', icon: 'User' },
  { path: '/password', label: '更新密码', icon: 'Key' }
]

onMounted(() => {
  if (!auth.name) loadAuth().catch(() => {})
})
</script>

<style scoped>
.layout { display: flex; flex-direction: column; min-height: 100vh; background: #f5f6f8; }
.topbar { position: sticky; top: 0; z-index: 10; display: flex; align-items: center; justify-content: space-between;
  background: #2b3a55; color: #fff; padding: 12px 20px; }
.topbar .title { font-size: 18px; font-weight: 600; }
.company { display: flex; align-items: center; gap: 8px; font-size: 14px; }
.type-tag { background: rgba(255, 255, 255, 0.2); border: none; color: #ffd500; }
.content { flex: 1; padding: 16px 16px 80px; }
.bottom-nav { position: fixed; bottom: 0; left: 0; right: 0; display: flex; background: #fff;
  border-top: 1px solid #e4e7ed; z-index: 10; }
.nav-item { flex: 1; display: flex; flex-direction: column; align-items: center; gap: 2px;
  padding: 10px 0; color: #909399; text-decoration: none; font-size: 12px; }
.nav-item.active { color: #4f7cff; }
</style>
