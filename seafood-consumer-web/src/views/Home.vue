<template>
  <div class="home">
    <div class="hero">
      <h1>冷冻海产品溯源系统</h1>
      <p class="slogan">来源可查 · 去向可追 · 责任可究</p>

      <div class="query-box">
        <el-input v-model="code" size="large" placeholder="请输入产品包装上的溯源标识码" @keyup.enter="search" clearable />
        <el-button type="primary" size="large" class="btn" @click="search">溯源</el-button>
      </div>

      <div class="recent" v-if="recent.length">
        <span class="recent-label">最近查询</span>
        <el-tag v-for="(c, i) in recent" :key="i" class="recent-tag" effect="plain" @click="go(c)">
          {{ c }}
        </el-tag>
        <span class="clear" @click="clear">清空</span>
      </div>

      <p class="hint">提示：溯源码通常印在冷冻海产品外包装的溯源标签上</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = useRouter()
const code = ref('')
const recent = ref([])
const KEY = 'nfets_trace_recent'

onMounted(() => {
  recent.value = JSON.parse(localStorage.getItem(KEY) || '[]')
})

function go(c) {
  code.value = c
  router.push(`/trace/${c}`)
}

function search() {
  const c = code.value.trim()
  if (!c) return ElMessage.warning('请输入溯源标识码')
  const list = [c, ...recent.value.filter((x) => x !== c)].slice(0, 5)
  localStorage.setItem(KEY, JSON.stringify(list))
  recent.value = list
  router.push(`/trace/${c}`)
}

function clear() {
  localStorage.removeItem(KEY)
  recent.value = []
}
</script>

<style scoped>
.home { min-height: 100vh; background: linear-gradient(160deg, #1d7a5f 0%, #134b3b 100%); }
.hero { padding: 90px 20px 50px; text-align: center; color: #fff; }
.hero h1 { font-size: 34px; margin: 0 0 8px; }
.slogan { color: rgba(255, 255, 255, 0.85); margin: 0 0 34px; }
.query-box { display: flex; justify-content: center; gap: 12px; max-width: 520px; margin: 0 auto; }
.query-box .el-input { max-width: 360px; }
.btn { background: #ffd500; color: #333; border: none; font-weight: 600; }
.recent { margin-top: 26px; }
.recent-label { color: rgba(255, 255, 255, 0.85); font-size: 13px; margin-right: 10px; }
.recent-tag { margin: 0 6px; cursor: pointer; border-color: rgba(255, 255, 255, 0.4); color: #fff; background: transparent; }
.clear { margin-left: 10px; color: rgba(255, 255, 255, 0.6); font-size: 12px; cursor: pointer; }
.hint { margin-top: 30px; color: rgba(255, 255, 255, 0.6); font-size: 12px; }
</style>
