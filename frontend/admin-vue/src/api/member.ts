import request from '@/utils/request'

export const getMemberPage = (params: any) => {
  return request({ url: '/member/page', method: 'get', params })
}

export const getMemberById = (id: string | number) => {
  return request({ url: `/member/${id}`, method: 'get' })
}

export const memberStatusByStatus = (params: any) => {
  return request({
    url: `/member/status/${params.status}`,
    method: 'post',
    params: { id: params.id }
  })
}

export const getMemberCommentPage = (params: any) => {
  return request({ url: '/member/comment/page', method: 'get', params })
}

export const memberCommentStatusByStatus = (params: any) => {
  return request({
    url: `/member/comment/status/${params.status}`,
    method: 'post',
    params: { id: params.id }
  })
}

export const deleteMemberComment = (id: string | number) => {
  return request({ url: '/member/comment', method: 'delete', params: { id } })
}

export const getMemberFavoritePage = (params: any) => {
  return request({ url: '/member/favorite/page', method: 'get', params })
}

export const deleteMemberFavorite = (id: string | number) => {
  return request({ url: '/member/favorite', method: 'delete', params: { id } })
}
