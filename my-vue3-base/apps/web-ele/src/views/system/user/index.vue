<script lang="ts" setup>
import type { VxeGridProps } from '#/adapter/vxe-table';

import { h } from 'vue';

import { ElButton, ElMessage, ElSwitch } from 'element-plus';

import { useVbenVxeGrid } from '#/adapter/vxe-table';

interface UserType {
  id: string;
  username: string;
  nickName: string;
  gender: string;
  phone: string;
  email: string;
  dept: {
    id: string;
    name: string;
  };
  enabled: boolean;
  createTime: string;
}

// 模拟获取用户数据的API
async function getUserTableApi(_params: { page: number; pageSize: number }) {
  return new Promise<{ items: UserType[]; total: number }>((resolve) => {
    // 这里替换为实际的API调用
    setTimeout(() => {
      resolve({
        items: [],
        total: 0,
      });
    }, 1000);
  });
}

const handleStatusChange = (row: UserType, value: any) => {
  ElMessage.success(`状态已更新为: ${value}`);
};

const gridOptions: VxeGridProps<UserType> = {
  checkboxConfig: {
    highlight: true,
    labelField: 'username',
  },
  columns: [
    { title: '序号', type: 'seq', width: 50 },
    { type: 'checkbox', width: 50 },
    { field: 'username', title: '用户名' },
    { field: 'nickName', title: '昵称' },
    { field: 'gender', title: '性别' },
    { field: 'phone', title: '电话' },
    { field: 'email', title: '邮箱' },
    { field: 'dept.name', title: '部门' },
    {
      field: 'enabled',
      title: '状态',
      slots: {
        default: ({ row }) => {
          return [
            h(ElSwitch, {
              modelValue: Boolean(row.enabled),
              onChange: (value) => handleStatusChange(row, value),
            }),
          ];
        },
      },
    },
    { field: 'createTime', title: '创建时间' },
  ],
  toolbarConfig: {
    custom: true,
    export: true,
    refresh: true,
    zoom: true,
  },
  proxyConfig: {
    ajax: {
      query: async ({ page }) => {
        return await getUserTableApi({
          page: page.currentPage,
          pageSize: page.pageSize,
        });
      },
    },
  },
};

const [Grid, gridApi] = useVbenVxeGrid({
  gridOptions,
});
</script>

<template>
  <div class="vp-raw w-full">
    <Grid>
      <template #toolbar-tools>
        <ElButton class="mr-2" type="primary" @click="gridApi.query()">
          刷新
        </ElButton>
        <ElButton type="primary" @click="gridApi.reload()"> 重置 </ElButton>
      </template>
    </Grid>
  </div>
</template>
