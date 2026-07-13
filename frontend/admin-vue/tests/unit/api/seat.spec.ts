import request from '@/utils/request'
import { addSeat, downloadQrCode } from '@/api/seat'

jest.mock('@/utils/request', () => jest.fn())

describe('seat api', () => {
  afterEach(() => {
    jest.clearAllMocks()
  })

  it('posts new seats through the admin proxy without a duplicated admin prefix', () => {
    const seat = { seatCode: 'A01', seatName: 'A区 01号桌' }

    addSeat(seat)

    expect(request).toHaveBeenCalledWith({
      url: '/seat',
      method: 'post',
      data: seat
    })
  })

  it('downloads QR codes through the admin proxy without a duplicated admin prefix', () => {
    downloadQrCode(12)

    expect(request).toHaveBeenCalledWith({
      url: '/qr/download/12',
      method: 'get',
      responseType: 'blob'
    })
  })
})
