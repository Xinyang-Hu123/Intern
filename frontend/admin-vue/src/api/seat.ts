import request from '@/utils/request'
import axios from 'axios'

export const getSeatPage = (params: any) => request({
  url: '/seat/page',
  method: 'get',
  params
})

export const getSeatList = (params?: any) => request({
  url: '/seat/list',
  method: 'get',
  params
})

export const createSeat = (data: any) => request({
  url: '/seat',
  method: 'post',
  data
})

export const updateSeat = (data: any) => request({
  url: '/seat',
  method: 'put',
  data
})

export const deleteSeat = (id: number) => request({
  url: '/seat',
  method: 'delete',
  params: { id }
})

export const updateSeatStatus = (id: number, status: number) => request({
  url: `/seat/status/${status}`,
  method: 'put',
  params: { id }
})

// The public layout endpoint is intentionally outside the admin /api proxy.
export const getPublicSeatLayout = () => axios.get('/user/seat/layout', {
  timeout: 15000
})
