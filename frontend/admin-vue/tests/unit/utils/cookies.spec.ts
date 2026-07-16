import Cookies from 'js-cookie'
import { setToken, setUserInfo, setUsername } from '@/utils/cookies'

jest.mock('js-cookie', () => ({
  get: jest.fn(),
  set: jest.fn(),
  remove: jest.fn()
}))

describe('cookie boundary', () => {
  beforeEach(() => {
    jest.clearAllMocks()
  })

  it('uses fixed minimum-scope attributes for the authentication token', () => {
    ;(setToken as any)('token-value', { domain: 'evil.example', path: '/' })

    expect(Cookies.set).toHaveBeenCalledWith('token', 'token-value', {
      path: '/',
      sameSite: 'strict',
      secure: false
    })
  })

  it('serializes structured user data before writing it', () => {
    setUserInfo({ name: 'admin' })
    setUsername('admin')

    expect(Cookies.set).toHaveBeenCalledWith(
      'user_info',
      '{"name":"admin"}',
      expect.any(Object)
    )
    expect(Cookies.set).toHaveBeenCalledWith('username', 'admin', expect.any(Object))
  })
})
