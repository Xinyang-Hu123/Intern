import request from '@/utils/request'

/**
 * 优惠券管理
 */

export const getCouponPage = (params: any) => {
  return request({
    url: '/coupon/page',
    method: 'get',
    params
  })
}

export const addCoupon = (params: any) => {
  return request({
    url: '/coupon',
    method: 'post',
    data: { ...params }
  })
}

export const editCoupon = (params: any) => {
  return request({
    url: '/coupon',
    method: 'put',
    data: { ...params }
  })
}

export const deleteCoupon = (id: string | number) => {
  return request({
    url: '/coupon',
    method: 'delete',
    params: { id }
  })
}

export const enableOrDisableCoupon = (params: any) => {
  return request({
    url: `/coupon/status/${params.status}`,
    method: 'post',
    params: { id: params.id }
  })
}
