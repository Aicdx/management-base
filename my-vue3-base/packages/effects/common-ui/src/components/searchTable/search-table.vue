<script lang="ts" setup>
import { onMounted, ref } from 'vue';

import { $t } from '@vben/locales';

import {
  ElButton,
  ElCard,
  ElDatePicker,
  ElForm,
  ElFormItem,
  ElInput,
  ElPagination,
  ElTable,
  ElTableColumn,
} from 'element-plus';

interface SearchTableProps {
  columns: {
    fixed?: 'left' | 'right' | boolean;
    label: string;
    prop: string;
    width?: number | string;
  }[];
  searchItems: {
    label: string;
    placeholder?: string;
    prop: string;
    type: 'date' | 'input' | string;
  }[];
  fetchData: (params: any) => Promise<{
    list: any[];
    total: number;
  }>;
}

const props = defineProps<SearchTableProps>();

// 表格数据
const tableData = ref<any[]>([]);
// 加载状态
const loading = ref(false);
// 分页参数
const pagination = ref({
  currentPage: 1,
  pageSize: 10,
  total: 0,
});
// 搜索表单
const searchForm = ref<Record<string, any>>({});

// 初始化搜索表单
props.searchItems.forEach((item) => {
  searchForm.value[item.prop] = '';
});

// 获取表格数据
const getTableData = async () => {
  loading.value = true;
  try {
    const params = {
      ...searchForm.value,
      page: pagination.value.currentPage,
      pageSize: pagination.value.pageSize,
    };
    const { list, total } = await props.fetchData(params);
    tableData.value = list;
    pagination.value.total = total;
  } finally {
    loading.value = false;
  }
};

// 搜索
const handleSearch = () => {
  pagination.value.currentPage = 1;
  getTableData();
};

// 重置搜索
const handleReset = () => {
  props.searchItems.forEach((item) => {
    searchForm.value[item.prop] = '';
  });
  handleSearch();
};

// 页码改变
const handleCurrentChange = (val: number) => {
  pagination.value.currentPage = val;
  getTableData();
};

// 每页条数改变
const handleSizeChange = (val: number) => {
  pagination.value.pageSize = val;
  pagination.value.currentPage = 1;
  getTableData();
};

onMounted(() => {
  getTableData();
});
</script>

<template>
  <div class="search-table-container">
    <!-- 搜索区域 -->
    <ElCard class="search-card">
      <ElForm :inline="true" :model="searchForm" class="flex justify-between">
        <div class="flex flex-wrap">
          <ElFormItem
            v-for="item in searchItems"
            :key="item.prop"
            :label="item.label"
          >
            <ElInput
              v-if="item.type === 'input'"
              v-model="searchForm[item.prop]"
              :placeholder="item.placeholder"
              clearable
            />
            <ElDatePicker
              v-else-if="item.type === 'date'"
              v-model="searchForm[item.prop]"
              :placeholder="item.placeholder"
              format="YYYY-MM-DD"
              type="date"
            />
          </ElFormItem>
        </div>
        <div>
          <ElFormItem>
            <slot name="operate">
              <ElButton type="primary" @click="handleSearch">
                {{ $t('common.search') }}
              </ElButton>
              <ElButton @click="handleReset">{{ $t('common.reset') }}</ElButton>
            </slot>
          </ElFormItem>
        </div>
      </ElForm>
    </ElCard>

    <!-- 表格区域 -->
    <ElCard class="table-card">
      <ElTable :data="tableData" border style="width: 100%" v-loading="loading">
        <ElTableColumn
          v-for="col in columns"
          :key="col.prop"
          :fixed="col.fixed"
          :label="col.label"
          :prop="col.prop"
          :width="col.width"
        />
        <slot name="columns"></slot>
      </ElTable>

      <!-- 分页 -->
      <div class="pagination">
        <ElPagination
          v-model:current-page="pagination.currentPage"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 30, 50]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next"
          @current-change="handleCurrentChange"
          @size-change="handleSizeChange"
        />
      </div>
    </ElCard>
  </div>
</template>

<style scoped>
.search-table-container {
  padding: 20px;
}

.search-card {
  margin-bottom: 20px;
}

.table-card {
  margin-bottom: 20px;
}

.pagination {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}
</style>
