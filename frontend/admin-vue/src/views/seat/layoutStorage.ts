import { SeatPositionMap } from './types'

const STORAGE_KEY = 'seat-layout-v1'

export const loadStoredLayout = (): SeatPositionMap => {
  try {
    const value = window.localStorage.getItem(STORAGE_KEY)
    return value ? JSON.parse(value) : {}
  } catch (_) {
    return {}
  }
}

export const saveStoredLayout = (positions: SeatPositionMap) => {
  window.localStorage.setItem(STORAGE_KEY, JSON.stringify(positions))
}

export const clearStoredLayout = () => {
  window.localStorage.removeItem(STORAGE_KEY)
}
