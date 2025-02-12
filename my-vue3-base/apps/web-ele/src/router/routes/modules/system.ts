import type { RouteRecordRaw } from 'vue-router';

import { BasicLayout } from '#/layouts';

const routes: RouteRecordRaw[] = [
  {
    component: BasicLayout,
    meta: {
      icon: 'ion:settings-outline',
      title: '系统管理',
    },
    name: 'System',
    path: '/system',
    redirect: '/system/user',
    children: [
      {
        name: 'SystemUser',
        component: () => import('#/views/system/user/index.vue'),
        path: '/system/user',
        meta: {
          icon: 'ion:people-outline',
          title: '用户管理',
        },
      },
    ],
  },
];

export default routes;
