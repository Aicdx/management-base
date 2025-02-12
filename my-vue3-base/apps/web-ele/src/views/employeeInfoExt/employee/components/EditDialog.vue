<script lang="ts" setup>
import { computed, reactive, watch } from 'vue';

import {
  ElButton,
  ElDatePicker,
  ElDialog,
  ElForm,
  ElFormItem,
  ElInput,
  ElOption,
  ElSelect,
} from 'element-plus';

const props = defineProps({
  visible: Boolean,
  rowData: {
    type: Object,
    default: () => ({}),
  },
});

const emit = defineEmits(['update:visible', 'save']);

const editForm = reactive({
  name: '',
  gender: '',
  birthDate: '',
  phone: '',
  idCard: '',
  education: '',
  profession: '',
  workExperience: '',
});

// 性别选项
const genderOptions = [
  { label: '男', value: '男' },
  { label: '女', value: '女' },
];

// 学历选项
const educationOptions = [
  { label: '高中', value: '高中' },
  { label: '专科', value: '专科' },
  { label: '本科', value: '本科' },
  { label: '硕士', value: '硕士' },
  { label: '博士', value: '博士' },
];

// 监听数据变化，更新表单
watch(
  () => props.rowData,
  (newVal) => {
    Object.assign(editForm, newVal);
  },
  { deep: true },
);

const handleClose = () => {
  emit('update:visible', false);
};

const handleSave = () => {
  emit('save', editForm);
  handleClose();
};

const dialogVisible = computed({
  get: () => props.visible,
  set: (val) => emit('update:visible', val),
});
</script>

<template>
  <ElDialog
    v-model="dialogVisible"
    title="编辑员工信息"
    width="50%"
    @close="handleClose"
  >
    <ElForm :model="editForm" label-width="100px">
      <ElFormItem label="姓名">
        <ElInput v-model="editForm.name" />
      </ElFormItem>
      <ElFormItem label="性别">
        <ElSelect v-model="editForm.gender" placeholder="请选择性别">
          <ElOption
            v-for="item in genderOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </ElSelect>
      </ElFormItem>
      <ElFormItem label="出生日期">
        <ElDatePicker
          v-model="editForm.birthDate"
          placeholder="选择日期"
          type="date"
        />
      </ElFormItem>
      <ElFormItem label="联系方式">
        <ElInput v-model="editForm.phone" />
      </ElFormItem>
      <ElFormItem label="身份证号码">
        <ElInput v-model="editForm.idCard" />
      </ElFormItem>
      <ElFormItem label="学历">
        <ElSelect v-model="editForm.education" placeholder="请选择学历">
          <ElOption
            v-for="item in educationOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </ElSelect>
      </ElFormItem>
      <ElFormItem label="专业">
        <ElInput v-model="editForm.profession" />
      </ElFormItem>
      <ElFormItem label="工作经验">
        <ElInput v-model="editForm.workExperience" />
      </ElFormItem>
    </ElForm>
    <template #footer>
      <span class="dialog-footer">
        <ElButton @click="handleClose">取消</ElButton>
        <ElButton type="primary" @click="handleSave">确定</ElButton>
      </span>
    </template>
  </ElDialog>
</template>
