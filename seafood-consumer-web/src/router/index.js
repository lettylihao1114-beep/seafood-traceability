import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  { path: '/', name: 'Home', component: () => import('../views/Home.vue') },
  { path: '/trace/:code', name: 'Trace', component: () => import('../views/Trace.vue') }
]

export default createRouter({ history: createWebHistory(), routes })
