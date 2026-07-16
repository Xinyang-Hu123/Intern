import { mount, createLocalVue } from '@vue/test-utils';
import VueRouter from 'vue-router';
import ElementUI from 'element-ui';
import Breadcrumb from '@/components/Breadcrumb/index.vue';

const localVue = createLocalVue();
localVue.use(VueRouter);
localVue.use(ElementUI);

const routes = [
    {
        'path': '/',
        'children': [{
            'path': 'dashboard'
        }]
    },
    {
        'path': '/menu',
        'children': [{
            'path': 'menu1',
            'meta': { 'title': 'menu1' },
            'children': [{
                'path': 'menu1-1',
                'meta': { 'title': 'menu1-1' }
            },
            {
                'path': 'menu1-2',
                'redirect': 'noredirect',
                'meta': { 'title': 'menu1-2' },
                'children': [{
                    'path': 'menu1-2-1',
                    'meta': { 'title': 'menu1-2-1' }
                },
                {
                    'path': 'menu1-2-2'
                }]
            }]
        }]
    }];

describe('Breadcrumb.vue', () => {
    const createWrapper = () => {
        const router = new VueRouter({ mode: 'abstract', routes });
        const wrapper = mount(Breadcrumb, { localVue, router });
        return { router, wrapper };
    };

    it('dashboard', async () => {
        const { router, wrapper } = createWrapper();
        await router.push('/dashboard');
        await localVue.nextTick();
        const len = wrapper.findAll('.el-breadcrumb__inner').length;
        expect(len).toBe(0);
    });

    it('normal route', async () => {
        const { router, wrapper } = createWrapper();
        await router.push('/menu/menu1');
        await localVue.nextTick();
        const len = wrapper.findAll('.el-breadcrumb__inner').length;
        expect(len).toBe(1);
    });

    it('nested route', async () => {
        const { router, wrapper } = createWrapper();
        await router.push('/menu/menu1/menu1-2/menu1-2-1');
        await localVue.nextTick();
        const len = wrapper.findAll('.el-breadcrumb__inner').length;
        expect(len).toBe(3);
    });

    it('no meta.title', async () => {
        const { router, wrapper } = createWrapper();
        await router.push('/menu/menu1/menu1-2/menu1-2-2');
        await localVue.nextTick();
        const len = wrapper.findAll('.el-breadcrumb__inner').length;
        expect(len).toBe(2);
    });

    it('click link', async () => {
        const { router, wrapper } = createWrapper();
        await router.push('/menu/menu1/menu1-2/menu1-2-2');
        await localVue.nextTick();
        const breadcrumbArray = wrapper.findAll('.el-breadcrumb__inner');
        const first = breadcrumbArray.at(0);
        const href = first.find('a').text();
        expect(href).toBe('menu1');
    });

    it('noredirect', async () => {
        const { router, wrapper } = createWrapper();
        await router.push('/menu/menu1/menu1-2/menu1-2-1');
        await localVue.nextTick();
        const breadcrumbArray = wrapper.findAll('.el-breadcrumb__inner');
        const redirectBreadcrumb = breadcrumbArray.at(1);
        expect(redirectBreadcrumb.find('a').exists()).toBe(false);
    });

    it('last breadcrumb', async () => {
        const { router, wrapper } = createWrapper();
        await router.push('/menu/menu1/menu1-2/menu1-2-1');
        await localVue.nextTick();
        const breadcrumbArray = wrapper.findAll('.el-breadcrumb__inner');
        const redirectBreadcrumb = breadcrumbArray.at(2);
        expect(redirectBreadcrumb.find('a').exists()).toBe(false);
    });
});
