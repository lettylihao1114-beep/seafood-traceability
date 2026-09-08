import request from './request'

export const login = (data) => request.post('/auth/login', data)
export const getProfile = () => request.get('/node/profile')
export const changePassword = (data) => request.post('/node/password', data)

export const listBatches = (status) => request.get('/node/batches', { params: { status } })
export const batchDetail = (id) => request.get(`/node/batches/${id}`)
export const createBatch = (data) => request.post('/node/batches', data)
export const updateBatch = (id, data) => request.put(`/node/batches/${id}`, data)
export const deleteBatch = (id) => request.delete(`/node/batches/${id}`)
export const publishBatch = (id) => request.post(`/node/batches/${id}/publish`)
export const sendConfirm = (id) => request.post(`/node/batches/${id}/send-confirm`)
export const offShelf = (id) => request.post(`/node/batches/${id}/off-shelf`)

export const listConfirmDownstream = (name) => request.get('/node/confirm-downstream', { params: { name } })
export const confirmBatch = (id) => request.post(`/node/confirm-downstream/${id}`)

export const listProvinces = () => request.get('/node/regions/provinces')
export const listCities = (provinceId) => request.get('/node/regions/cities', { params: { provinceId } })
export const listUpstreamEnterprises = (params) => request.get('/node/upstream/enterprises', { params })
export const listUpstreamBatches = (params) => request.get('/node/upstream/batches', { params })
