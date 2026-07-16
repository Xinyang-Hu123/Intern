const ABSOLUTE_OR_EXECUTABLE_URL = /^(?:[a-z][a-z\d+.-]*:|\/\/)/i

export const isAllowedApiUrl = (url: unknown): url is string => {
  if (typeof url !== 'string' || url.length === 0 || url.trim() !== url) {
    return false
  }

  return !ABSOLUTE_OR_EXECUTABLE_URL.test(url.replace(/\\/g, '/'))
}
