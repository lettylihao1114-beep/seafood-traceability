import axios from 'axios'
import { ElMessage } from 'element-plus'

const request = axios.create({ baseURL: '/api', timeout: 15000 })

request.interceptors.response.use(
  (res) => {
    if (res.data.code === 200) return res.data
    ElMessage.error(res.data.message || '查询失败')
    return Promise.reject(new Error(res.data.message))
  },
  (err) => {
    ElMessage.error((err.response && err.response.data && err.response.data.message) || '网络错误')
    return Promise.reject(err)
  }
)

export const trace = (code) => request.get('/open/trace', { params: { traceCode: code } })
