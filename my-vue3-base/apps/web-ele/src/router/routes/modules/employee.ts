import type { RouteRecordRaw } from 'vue-router';

import { BasicLayout } from '#/layouts';
import { $t } from '#/locales';

const routes: RouteRecordRaw[] = [
  {
    component: BasicLayout,
    meta: {
      icon: 'mdi:account-group',
      title: $t('page.employeeInfoExt.title'),
    },
    name: 'EmployeeInfoExt',
    path: '/employeeInfoExt',
    redirect: '/employeeInfoExt/employee',
    children: [
      {
        name: 'Employee',
        path: '/employeeInfoExt/employee',
        component: () => import('#/views/employeeInfoExt/employee/index.vue'),
        meta: {
          icon: 'mdi:account-group',
          title: $t('page.employeeInfoExt.employee'),
        },
      },
    ],
  },
];

export default routes;
