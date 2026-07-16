export interface Seat {
  id: number
  seatNumber: string
  name?: string
  capacity?: number
  area?: string
  qrCode?: string
  status: number
  statusText?: string
  remark?: string
  createTime?: string
  updateTime?: string
}

export interface SeatPosition {
  x: number
  y: number
}

export interface SeatPositionMap {
  [seatId: string]: SeatPosition
}

export interface SeatStatusOption {
  value: number
  label: string
  icon: string
}

export const SEAT_STATUSES: SeatStatusOption[] = [
  { value: 0, label: '空闲', icon: 'el-icon-circle-check' },
  { value: 1, label: '使用中', icon: 'el-icon-dish' },
  { value: 2, label: '已预订', icon: 'el-icon-date' },
  { value: 3, label: '清洁中', icon: 'el-icon-brush' }
]

export const statusText = (status: number) => {
  const option = SEAT_STATUSES.find(item => item.value === status)
  return option ? option.label : '未知'
}

export const statusIcon = (status: number) => {
  const option = SEAT_STATUSES.find(item => item.value === status)
  return option ? option.icon : 'el-icon-warning-outline'
}

export const clampPercent = (value: number, edge = 8) =>
  Math.max(edge, Math.min(100 - edge, Number(value.toFixed(2))))

export const snapPercent = (value: number, step = 2) =>
  Number((Math.round(value / step) * step).toFixed(2))

export const createAutomaticLayout = (seats: Seat[]): SeatPositionMap => {
  const positions: SeatPositionMap = {}
  if (!seats.length) return positions

  const columns = Math.max(1, Math.ceil(Math.sqrt(seats.length * 1.6)))
  const rows = Math.max(1, Math.ceil(seats.length / columns))

  seats.forEach((seat, index) => {
    const column = index % columns
    const row = Math.floor(index / columns)
    positions[String(seat.id)] = {
      x: clampPercent(((column + 0.5) / columns) * 100),
      y: clampPercent(((row + 0.5) / rows) * 100)
    }
  })
  return positions
}

export const mergeLayout = (seats: Seat[], saved: SeatPositionMap): SeatPositionMap => {
  const automatic = createAutomaticLayout(seats)
  seats.forEach(seat => {
    const current = saved[String(seat.id)]
    if (current) {
      automatic[String(seat.id)] = {
        x: clampPercent(current.x),
        y: clampPercent(current.y)
      }
    }
  })
  return automatic
}
