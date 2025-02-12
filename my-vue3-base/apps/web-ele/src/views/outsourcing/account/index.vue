<script setup>
import { reactive, ref } from 'vue';

// 搜索表单
const searchForm = reactive({
  name: '',
  status: '',
});

// 表格数据
const loading = ref(false);
const tableData = ref([]);

// 分页
const page = reactive({
  current: 1,
  size: 10,
  total: 0,
});

// 弹窗控制
const dialogVisible = ref(false);
const dialogType = ref('add');
const formRef = ref(null);
const formData = reactive({
  name: '',
  contact: '',
  phone: '',
  email: '',
});

// 表单校验规则
const rules = {
  name: [{ required: true, message: '请输入供应商名称', trigger: 'blur' }],
  contact: [{ required: true, message: '请输入联系人', trigger: 'blur' }],
  phone: [{ required: true, message: '请输入联系电话', trigger: 'blur' }],
  email: [{ required: true, message: '请输入邮箱', trigger: 'blur' }],
};

// 查询方法
const handleSearch = () => {
  // TODO: 调用接口获取数据
};

// 重置搜索
const resetSearch = () => {
  searchForm.name = '';
  searchForm.status = '';
  handleSearch();
};

// 新增
const handleAdd = () => {
  dialogType.value = 'add';
  dialogVisible.value = true;
  formData.name = '';
  formData.contact = '';
  formData.phone = '';
  formData.email = '';
};

// 编辑
const handleEdit = (row) => {
  dialogType.value = 'edit';
  dialogVisible.value = true;
  Object.assign(formData, row);
};

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return;
  await formRef.value.validate();
  // TODO: 调用新增或编辑接口
  dialogVisible.value = false;
  handleSearch();
};

// 修改状态
const handleStatusChange = () => {
  // TODO: 调用修改状态接口
};

// 页码改变
const handlePageChange = (val) => {
  page.current = val;
  handleSearch();
};
</script>

<template>
  <div class="outsourcing-account">
    <!-- 搜索栏 -->
    <el-card class="search-card">
      <el-form ref="searchForm" :inline="true" :model="searchForm">
        <el-form-item label="供应商名称">
          <el-input v-model="searchForm.name" placeholder="请输入供应商名称" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态">
            <el-option label="启用" value="1" />
            <el-option label="禁用" value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 表格区域 -->
    <el-card class="table-card">
      <div class="table-operations">
        <el-button type="primary" @click="handleAdd">新增供应商账号</el-button>
      </div>

      <el-table :data="tableData" v-loading="loading">
        <el-table-column label="供应商名称" prop="name" />
        <el-table-column label="联系人" prop="contact" />
        <el-table-column label="联系电话" prop="phone" />
        <el-table-column label="邮箱" prop="email" />
        <el-table-column label="状态" prop="status">
          <template #default="{ row }">
            <el-tag :type="row.status === '1' ? 'success' : 'danger'">
              {{ row.status === '1' ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" prop="createTime" />
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button type="text" @click="handleEdit(row)">编辑</el-button>
            <el-button
              :class="row.status === '1' ? 'danger' : 'success'"
              type="text"
              @click="handleStatusChange(row)"
            >
              {{ row.status === '1' ? '禁用' : '启用' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          :current-page="page.current"
          :page-size="page.size"
          :total="page.total"
          layout="total, prev, pager, next"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>

    <!-- 新增/编辑弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'add' ? '新增供应商账号' : '编辑供应商账号'"
      width="500px"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="供应商名称" prop="name">
          <el-input v-model="formData.name" />
        </el-form-item>
        <el-form-item label="联系人" prop="contact">
          <el-input v-model="formData.contact" />
        </el-form-item>
        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="formData.phone" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="formData.email" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style lang="scss" scoped>
.outsourcing-account {
  padding: 20px;

  .search-card {
    margin-bottom: 20px;
  }

  .table-card {
    .table-operations {
      margin-bottom: 16px;
    }

    .pagination {
      margin-top: 16px;
      text-align: right;
    }
  }
}
</style>
