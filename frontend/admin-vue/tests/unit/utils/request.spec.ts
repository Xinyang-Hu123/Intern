jest.mock('axios', () => ({
  __esModule: true,
  default: {
    create: jest.fn(() => {
      const mockCancel = jest.fn()
      return {
      interceptors: {
          request: { use: jest.fn() },
          response: { use: jest.fn() }
        },
        CancelToken: { source: jest.fn(() => ({ token: 'cancel-token', cancel: mockCancel })) }
      }
    }),
    CancelToken: { source: jest.fn(() => ({ token: 'cancel-token', cancel: jest.fn() })) }
  },
  CancelToken: { source: jest.fn() }
}))

jest.mock('element-ui', () => ({
  Message: { error: jest.fn() },
  MessageBox: {}
}))

jest.mock('@/store/modules/user', () => ({
  UserModule: { token: '' }
}))

jest.mock('@/router', () => ({}))

import axios from 'axios'
import '@/utils/request'

describe('request response interceptor', () => {
  it('allows opening the same QR preview again after its blob response completes', () => {
    const instance: any = (axios as any).create.mock.results[0].value
    const requestSuccess = instance.interceptors.request.use.mock.calls[0][0]
    const responseSuccess = instance.interceptors.response.use.mock.calls[0][0]
    const config = {
      url: '/qr/download/1',
      method: 'get',
      responseType: 'blob',
      headers: {}
    }

    const firstRequest = requestSuccess(Object.assign({}, config))
    responseSuccess({
      config: firstRequest,
      data: new Blob(['qr-code'], { type: 'image/png' })
    })
    requestSuccess(Object.assign({}, config))

    expect((axios as any).CancelToken.source).not.toHaveBeenCalled()
  })
})
