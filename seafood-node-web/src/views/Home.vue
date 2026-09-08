<template>
  <div class="home">
    <el-card>
      <div class="greet">
        <el-avatar :size="46" icon="OfficeBuilding" />
        <div>
          <div class="name">{{ auth.name }}</div>
          <el-tag size="small" :type="tagType">{{ typeLabels[auth.type] || auth.type }}</el-tag>
        </div>
      </div>
    </el-card>

    <el-card class="menu-card">
      <div class="menu-title">主要功能</div>
      <div class="menu-grid">
        <div class="menu-item" @click="$router.push('/batch/create')">
          <el-icon><Plus /></el-icon>
          <span>新建产品批号</span>
        </div>
        <div class="menu-item" @click="$router.push('/batch/list')">
          <el-icon><List /></el-icon>
          <span>产品批号管理</span>
        </div>
        <div v-if="auth.type !== 'RETAIL'" class="menu-item" @click="$router.push('/confirm')">
          <el-icon><Check /></el-icon>
          <span>下游企业进场确认</span>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { auth, typeLabels } from '../store'

const tagType = computed(() => ({ BREEDING: 'success', PROCESSING: 'warning', WHOLESALE: '', RETAIL: 'info' }[auth.type] || ''))
</script>

<style scoped>
.home { display: grid; gap: 16px; }
.greet { display: flex; align-items: center; gap: 14px; }
.name { font-size: 18px; font-weight: 600; margin-bottom: 4px; }
.menu-title { font-size: 16px; font-weight: 600; margin-bottom: 14px; }
.menu-grid { display: grid; grid-template-columns: 1fr; gap: 12px; }
.menu-item { display: flex; align-items: center; gap: 12px; padding: 18px; border: 1px solid #e4e7ed;
  border-radius: 8px; cursor: pointer; transition: all 0.2s; }
.menu-item:hover { background: #f0f5ff; border-color: #4f7cff; }
.menu-item .el-icon { font-size: 26px; color: #4f7cff; }
</style>
