import request from '@/utils/request'

/**
 * 座位管理
 */

// 座位分页查询
export const getSeatList = (params: any) => {
  return request({
    url: '/seat/page',
    method: 'get',
    params
  })
}

// 新增座位
export const addSeat = (data: any) => {
  return request({
    url: '/seat',
    method: 'post',
    data
  })
}

// 编辑座位
export const editSeat = (data: any) => {
  return request({
    url: '/seat',
    method: 'put',
    data
  })
}

// 删除座位（停用）
export const deleteSeat = (params: any) => {
  return request({
    url: '/seat',
    method: 'delete',
    params
  })
}

// 修改座位状态（启用/停用）
export const changeSeatStatus = (data: any) => {
  return request({
    url: '/seat/status',
    method: 'put',
    data
  })
}

// 根据ID查询座位
export const getSeatById = (id: string | number) => {
  return request({
    url: "/seat/" + id,
    method: 'get'
  })
}

// 查询所有座位（用于布局展示）
export const getAllSeats = () => {
  return request({
    url: '/seat/list',
    method: 'get'
  })
}

// 座位统计信息
export const getSeatStatistics = () => {
  return request({
    url: '/seat/statistics',
    method: 'get'
  })
}

// 重新生成二维码
export const regenerateQrCode = (id: string | number) => {
  return request({
    url: "/seat/regenerate-qr/" + id,
    method: 'post'
  })
}

// 下载二维码
export const downloadQrCode = (id: string | number) => {
  return request({
    url: '/qr/download/' + id,
    method: 'get',
    responseType: 'blob'
  })
}
