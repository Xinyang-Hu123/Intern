import { isAllowedApiUrl } from '@/utils/urlSecurity'

describe('API URL boundary', () => {
  it.each([
    '/employee/login',
    'order/page?page=1',
    '?page=1'
  ])('allows relative API URL %s', (url) => {
    expect(isAllowedApiUrl(url)).toBe(true)
  })

  it.each([
    'https://evil.example/collect',
    '//evil.example/collect',
    'javascript:alert(1)',
    'data:text/plain,secret'
  ])('rejects absolute or executable URL %s', (url) => {
    expect(isAllowedApiUrl(url)).toBe(false)
  })
})
