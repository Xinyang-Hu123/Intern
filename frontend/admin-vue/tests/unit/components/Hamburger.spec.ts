import { mount } from '@vue/test-utils'
import Hamburger from '@/components/Hamburger/index.vue'

describe('Hamburger.vue', () => {
  it('renders a self-contained local SVG icon', () => {
    const wrapper = mount(Hamburger)

    expect(wrapper.find('svg').exists()).toBe(true)
    expect(wrapper.find('svg-icon').exists()).toBe(false)
    expect(wrapper.find('use').exists()).toBe(false)
  })
})
