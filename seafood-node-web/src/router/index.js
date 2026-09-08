import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  { path: '/login', name: 'Login', component: () => import('../views/Login.vue') },
  {
    path: '/',
    component: () => import('../layout/NodeLayout.vue'),
    meta: { requiresAuth: true },
    children: [
      { path: '', name: 'Home', component: () => import('../views/Home.vue'), meta: { nav: '/', label: '首页' } },
      { path: 'batch/create', name: 'BatchCreate', component: () => import('../views/BatchCreate.vue'), meta: { nav: '/', label: '首页' } },
      { path: 'batch/list', name: 'BatchList', component: () => import('../views/BatchList.vue'), meta: { nav: '/', label: '首页' } },
      { path: 'batch/edit/:id', name: 'BatchEdit', component: () => import('../views/BatchEdit.vue'), meta: { nav: '/', label: '首页' } },
      { path: 'batch/detail/:id', name: 'BatchDetail', component: () => import('../views/BatchDetail.vue'), meta: { nav: '/', label: '首页' } },
      { path: 'confirm', name: 'ConfirmDownstream', component: () => import('../views/ConfirmDownstream.vue'), meta: { nav: '/', label: '首页' } },
      { path: 'profile', name: 'Profile', component: () => import('../views/Profile.vue'), meta: { nav: '/profile', label: '我的' } },
      { path: 'password', name: 'ChangePassword', component: () => import('../views/ChangePassword.vue'), meta: { nav: '/password', label: '更新密码' } }
    ]
  }
]

const router = createRouter({ history: createWebHistory(), routes })

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  if (to.meta.requiresAuth && !token) next('/login')
  else if (to.path === '/login' && token) next('/')
  else next()
})

export default router
