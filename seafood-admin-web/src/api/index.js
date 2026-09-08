import request from './request'

export const login = (data) => request.post('/auth/login', data)

export const pageEnterprises = (params) => request.get('/admin/node-enterprises', { params })
export const getEnterprise = (id) => request.get(`/admin/node-enterprises/${id}`)
export const createEnterprise = (data) => request.post('/admin/node-enterprises', data)
export const updateEnterprise = (id, data) => request.put(`/admin/node-enterprises/${id}`, data)
export const deleteEnterprise = (id) => request.delete(`/admin/node-enterprises/${id}`)

export const listProvinces = () => request.get('/admin/regions/provinces')
export const listCities = (provinceId) => request.get('/admin/regions/cities', { params: { provinceId } })

export const trend = () => request.get('/admin/stats/appear-trend')
export const provincePie = () => request.get('/admin/stats/province-distribution')
export const typePie = () => request.get('/admin/stats/type-distribution')
export const provinceBar = () => request.get('/admin/stats/province-bar')
