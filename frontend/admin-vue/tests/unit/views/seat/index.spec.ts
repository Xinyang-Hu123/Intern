import { shallowMount, createLocalVue } from '@vue/test-utils'
import Vue from 'vue'
import SeatManagement from '@/views/seat/index.vue'
import {
  addSeat,
  downloadQrCode,
  getAllSeats,
  getSeatList,
  getSeatStatistics
} from '@/api/seat'

jest.mock('@/api/seat', () => ({
  addSeat: jest.fn(),
  changeSeatStatus: jest.fn(),
  deleteSeat: jest.fn(),
  downloadQrCode: jest.fn(),
  editSeat: jest.fn(),
  getAllSeats: jest.fn(),
  getSeatList: jest.fn(),
  getSeatStatistics: jest.fn(),
  regenerateQrCode: jest.fn()
}))

const localVue = createLocalVue()
const flushPromises = () => new Promise(resolve => setTimeout(resolve, 0))
const stubs = {
  'el-button': true,
  'el-card': true,
  'el-dialog': true,
  'el-empty': true,
  'el-form': true,
  'el-form-item': true,
  'el-input': true,
  'el-input-number': true,
  'el-option': true,
  'el-pagination': true,
  'el-select': true,
  'el-table': true,
  'el-table-column': true,
  'el-tag': true
}

describe('SeatManagement', () => {
  const createObjectURL = jest.fn(() => 'blob:seat-a01')

  beforeEach(() => {
    ;(URL as any).createObjectURL = createObjectURL
    ;(getSeatList as jest.Mock).mockResolvedValue({ code: 1, data: { records: [], total: 0 } })
    ;(getAllSeats as jest.Mock).mockResolvedValue({ code: 1, data: [] })
    ;(getSeatStatistics as jest.Mock).mockResolvedValue({ code: 1, data: {} })
    ;(addSeat as jest.Mock).mockResolvedValue({
      data: {
        code: 1,
        data: { id: 12, seatCode: 'A01', seatName: 'A区 01号桌' }
      }
    })
    ;(downloadQrCode as jest.Mock).mockResolvedValue({ data: new Blob(['qr-code'], { type: 'image/png' }) })
  })

  afterEach(() => {
    jest.clearAllMocks()
  })

  it('shows a QR preview after adding a seat', async () => {
    const wrapper = shallowMount(SeatManagement, {
      localVue,
      stubs,
      mocks: {
        $message: { error: jest.fn(), success: jest.fn() }
      }
    })
    const vm: any = wrapper.vm
    vm.$refs.seatForm = {
      validate: (callback: (valid: boolean) => void) => callback(true)
    }
    vm.form = { seatCode: 'A01', seatName: 'A区 01号桌', areaName: 'A区', capacity: 4, sort: 0 }

    vm.submitForm()
    await flushPromises()
    await Vue.nextTick()

    expect(addSeat).toHaveBeenCalledWith(vm.form)
    expect(downloadQrCode).toHaveBeenCalledWith(12)
    expect(createObjectURL).toHaveBeenCalledWith(expect.any(Blob))
    expect(vm.qrPreviewVisible).toBe(true)
    expect(vm.qrPreviewUrl).toBe('blob:seat-a01')
    expect(vm.qrPreviewSeat).toEqual({ id: 12, seatCode: 'A01', seatName: 'A区 01号桌' })
  })
})
