export interface SeatEmbedMessage {
  type: string
  payload?: any
  source: 'sky-take-out-seat-embed'
}

const parseOrigins = (value?: string): string[] =>
  (value || '').split(',').map(item => item.trim()).filter(Boolean)

export const allowedEmbedOrigins = (configured?: string, currentOrigin?: string) => {
  const origins = parseOrigins(configured)
  if (currentOrigin && origins.indexOf(currentOrigin) === -1) origins.push(currentOrigin)
  return origins
}

export const isAllowedEmbedOrigin = (
  origin: string,
  configured?: string,
  currentOrigin = window.location.origin
) => allowedEmbedOrigins(configured, currentOrigin).indexOf(origin) !== -1

const parentOrigin = () => {
  try {
    return document.referrer ? new URL(document.referrer).origin : window.location.origin
  } catch (_) {
    return window.location.origin
  }
}

export const postSeatEmbedMessage = (type: string, payload?: any) => {
  if (window.parent === window) return
  const targetOrigin = parentOrigin()
  if (!isAllowedEmbedOrigin(targetOrigin, process.env.VUE_APP_EMBED_ORIGINS)) return
  const message: SeatEmbedMessage = { type, payload, source: 'sky-take-out-seat-embed' }
  window.parent.postMessage(message, targetOrigin)
}
