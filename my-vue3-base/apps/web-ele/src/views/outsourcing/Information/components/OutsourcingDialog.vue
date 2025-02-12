<script lang="ts" setup>
import { computed, reactive, watch } from 'vue';

import { ElButton, ElDialog, ElForm, ElFormItem, ElInput } from 'element-plus';

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
  contactPerson: '',
  phone: '',
  serviceScope: '',
  qualification: '',
});

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
<!-- 服务商名称、联系人、联系方式、服务范围、资质证明 -->
<template>
  <ElDialog
    v-model="dialogVisible"
    title="编辑员工信息"
    width="50%"
    @close="handleClose"
  >
    <ElForm :model="editForm" label-width="100px">
      <ElFormItem label="服务商名称">
        <ElInput v-model="editForm.name" />
      </ElFormItem>
      <ElFormItem label="联系人">
        <ElInput v-model="editForm.contactPerson" />
      </ElFormItem>
      <ElFormItem label="联系方式">
        <ElInput v-model="editForm.phone" />
      </ElFormItem>
      <ElFormItem label="服务范围">
        <ElInput v-model="editForm.serviceScope" />
      </ElFormItem>
      <ElFormItem label="资质证明">
        <ElInput v-model="editForm.qualification" />
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
