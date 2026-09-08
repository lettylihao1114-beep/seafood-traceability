import { reactive } from 'vue'
import { getProfile } from './api'

export const auth = reactive({
  name: localStorage.getItem('nodeName') || '',
  type: localStorage.getItem('nodeType') || ''
})

export const typeLabels = { BREEDING: '水产养殖企业', PROCESSING: '冷冻加工企业', WHOLESALE: '批发商', RETAIL: '零售商' }

export async function loadAuth() {
  const res = await getProfile()
  auth.name = res.data.name
  auth.type = res.data.type
  localStorage.setItem('nodeName', auth.name)
  localStorage.setItem('nodeType', auth.type)
  return res.data
}
