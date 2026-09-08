import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '../router'

const request = axios.create({ baseURL: '/api', timeout: 15000 })

request.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

request.interceptors.response.use(
  (res) => {
    if (res.data.code === 200) return res.data
    ElMessage.error(res.data.message || '请求失败')
    return Promise.reject(new Error(res.data.message))
  },
  (err) => {
    if (err.response && err.response.status === 401) {
      localStorage.clear()
      router.push('/login')
    }
    ElMessage.error((err.response && err.response.data && err.response.data.message) || '网络错误')
    return Promise.reject(err)
  }
)

export default request
