<script setup>
import { ref } from 'vue';

import { SearchTable } from '@vben/common-ui';

import { ElButton, ElMessage, ElMessageBox, ElTableColumn } from 'element-plus';

import OutsourcingDialog from './components/OutsourcingDialog.vue';

const api = {
  list: '/api/outsourcing/list',
  add: '/api/outsourcing/add',
  update: '/api/outsourcing/update',
  delete: '/api/outsourcing/delete',
};

const columns = [
  { prop: 'name', label: '服务商名称', width: '180' },
  { prop: 'contactPerson', label: '联系人', width: '120' },
  { prop: 'contactNumber', label: '联系方式', width: '150' },
  { prop: 'serviceScope', label: '服务范围' },
  { prop: 'qualification', label: '资质证明', slot: true },
];

const searchItems = [
  { prop: 'name', label: '服务商名称', type: 'input' },
  { prop: 'contactPerson', label: '联系人', type: 'input' },
];

const searchTable = ref(null);
const dialogVisible = ref(false);
const dialogTitle = ref('');
const dialogForm = ref({});

const handleAdd = () => {
  dialogTitle.value = '添加服务商';
  dialogVisible.value = true;
  dialogForm.value = {};
};

const handleEdit = (row) => {
  dialogTitle.value = '编辑服务商';
  dialogVisible.value = true;
  dialogForm.value = { ...row };
};

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确认删除该服务商信息吗？', '提示');
    // 调用删除接口
    await request.post(api.delete, { id: row.id });
    ElMessage.success('删除成功');
    searchTable.value.refresh();
  } catch (error) {
    console.error(error);
  }
};

const handleDialogSubmit = async (formData) => {
  try {
    const url = formData.id ? api.update : api.add;
    await request.post(url, formData);
    ElMessage.success(formData.id ? '更新成功' : '添加成功');
    dialogVisible.value = false;
    searchTable.value.refresh();
  } catch (error) {
    console.error(error);
  }
};

const fetchData = async () => {
  const mockData = Array.from({ length: 13 }).map((_, index) => ({
    id: index + 1,
    name: `张三${index + 1}`,
    contactPerson: `李四${index + 1}`,
    contactNumber: `1380000${String(index + 1).padStart(4, '0')}`,
    serviceScope: '服务范围',
    qualification: '资质证明',
  }));
  return {
    list: mockData,
    total: 100,
  };
};
</script>

<template>
  <div class="outsourcing-info">
    <SearchTable
      ref="searchTable"
      :api="api"
      :columns="columns"
      :fetch-data="fetchData"
      :search-items="searchItems"
    >
      <template #columns>
        <ElTableColumn fixed="right" label="操作" width="180">
          <template #default="scope">
            <ElButton link type="primary" @click="handleAdd"> 添加 </ElButton>
            <ElButton link type="primary" @click="handleEdit(scope.row)">
              编辑
            </ElButton>
            <ElButton link type="danger" @click="handleDelete(scope.row)">
              删除
            </ElButton>
          </template>
        </ElTableColumn>
      </template>
    </SearchTable>

    <!-- 添加/编辑弹窗 -->
    <OutsourcingDialog
      v-model:visible="dialogVisible"
      :form-data="dialogForm"
      :title="dialogTitle"
      @submit="handleDialogSubmit"
    />
  </div>
</template>

<style scoped>
.outsourcing-info {
  padding: 20px;
}

.qualification-img {
  width: 50px;
  height: 50px;
  cursor: pointer;
}
</style>
