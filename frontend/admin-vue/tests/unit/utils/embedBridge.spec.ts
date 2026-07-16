import { allowedEmbedOrigins, isAllowedEmbedOrigin } from '@/utils/embedBridge'

describe('seat embed bridge', () => {
  it('always allows the current application origin', () => {
    expect(allowedEmbedOrigins('', 'https://admin.example.com')).toEqual(['https://admin.example.com'])
  })

  it('parses a comma-separated parent allowlist', () => {
    expect(allowedEmbedOrigins(
      'https://portal.example.com, https://ops.example.com',
      'https://admin.example.com'
    )).toEqual([
      'https://portal.example.com',
      'https://ops.example.com',
      'https://admin.example.com'
    ])
  })

  it('rejects origins outside the allowlist', () => {
    expect(isAllowedEmbedOrigin(
      'https://unknown.example.com',
      'https://portal.example.com',
      'https://admin.example.com'
    )).toBe(false)
  })
})
