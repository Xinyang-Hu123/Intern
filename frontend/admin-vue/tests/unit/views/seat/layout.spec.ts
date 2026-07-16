import {
  clampPercent,
  createAutomaticLayout,
  mergeLayout,
  Seat,
  snapPercent,
  statusText
} from '@/views/seat/types'

const seats: Seat[] = [
  { id: 1, seatNumber: 'A01', status: 0 },
  { id: 2, seatNumber: 'A02', status: 1 },
  { id: 3, seatNumber: 'A03', status: 2 }
]

describe('seat layout helpers', () => {
  it('keeps coordinates inside the visible canvas', () => {
    expect(clampPercent(-20)).toBe(8)
    expect(clampPercent(110)).toBe(92)
    expect(clampPercent(47.123)).toBe(47.12)
  })

  it('snaps dragged coordinates to a stable grid', () => {
    expect(snapPercent(13.1)).toBe(14)
    expect(snapPercent(12.9)).toBe(12)
  })

  it('creates a deterministic position for every seat', () => {
    const first = createAutomaticLayout(seats)
    const second = createAutomaticLayout(seats)
    expect(first).toEqual(second)
    expect(Object.keys(first)).toHaveLength(3)
    Object.keys(first).forEach(key => {
      expect(first[key].x).toBeGreaterThanOrEqual(8)
      expect(first[key].x).toBeLessThanOrEqual(92)
    })
  })

  it('merges valid saved positions and clamps invalid values', () => {
    const result = mergeLayout(seats, { '1': { x: -4, y: 120 } })
    expect(result['1']).toEqual({ x: 8, y: 92 })
    expect(result['2']).toBeDefined()
  })

  it('uses the backend status vocabulary', () => {
    expect(statusText(0)).toBe('空闲')
    expect(statusText(3)).toBe('清洁中')
    expect(statusText(99)).toBe('未知')
  })
})
