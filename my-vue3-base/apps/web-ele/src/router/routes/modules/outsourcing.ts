import type { RouteRecordRaw } from 'vue-router';

import { BasicLayout } from '#/layouts';
import { $t } from '#/locales';

const routes: RouteRecordRaw[] = [
  {
    component: BasicLayout,
    meta: {
      icon: 'mdi:medical-bag',
      title: $t('page.outsourcing.title'),
    },
    name: 'Outsourcing',
    path: '/outsourcing',
    redirect: '/outsourcing/informations',
    children: [
      {
        name: 'Information',
        component: () => import('#/views/outsourcing/informations/index.vue'),
        path: '/outsourcing/informations',
        meta: {
          icon: 'mdi:medical-bag',
          title: $t('page.outsourcing.information'),
        },
      },
      {
        name: 'Account',
        component: () => import('#/views/outsourcing/account/index.vue'),
        path: '/outsourcing/account',
        meta: {
          icon: 'mdi:account-group',
          title: $t('page.outsourcing.account'),
        },
      },
    ],
  },
];

export default routes;
