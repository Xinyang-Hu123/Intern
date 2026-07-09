import request from '@/utils/request'

const endpointMap: any = {
  marketing: '/activity/marketing',
  coupon: '/activity/coupon',
  user: '/system/user',
  role: '/system/role',
  menu: '/system/menu'
}

const getEndpoint = (resource: string) => endpointMap[resource]

export const pageManagementResource = (resource: string, params: any) => {
  return request({
    url: `${getEndpoint(resource)}/page`,
    method: 'get',
    params
  })
}

export const saveManagementResource = (resource: string, data: any) => {
  return request({
    url: getEndpoint(resource),
    method: data.id ? 'put' : 'post',
    data
  })
}

export const deleteManagementResource = (resource: string, id: number) => {
  return request({
    url: `${getEndpoint(resource)}/${id}`,
    method: 'delete'
  })
}

export const setManagementResourceStatus = (
  resource: string,
  id: number,
  status: number
) => {
  return request({
    url: `${getEndpoint(resource)}/status/${status}`,
    method: 'post',
    params: { id }
  })
}
