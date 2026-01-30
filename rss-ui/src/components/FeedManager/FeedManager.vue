<template>
  <el-dialog
    :model-value="visible"
    @update:model-value="$emit('update:visible', $event)"
    title="添加订阅源"
    width="500px"
    :close-on-click-modal="false"
  >
    <el-form
      ref="formRef"
      :model="form"
      :rules="rules"
      label-width="80px"
    >
      <el-form-item label="RSS地址" prop="feedUrl">
        <el-input
          v-model="form.feedUrl"
          placeholder="请输入RSS订阅地址"
          clearable
        />
      </el-form-item>
      <el-form-item label="名称" prop="feedName">
        <el-input
          v-model="form.feedName"
          placeholder="订阅源名称（可选，自动获取）"
          clearable
        />
      </el-form-item>
      <el-form-item label="分类" prop="feedCategory">
        <el-autocomplete
          v-model="form.feedCategory"
          :fetch-suggestions="queryCategories"
          placeholder="输入或选择分类（可选）"
          clearable
          style="width: 100%"
        />
      </el-form-item>
    </el-form>

    <template #footer>
      <el-button @click="$emit('update:visible', false)">取消</el-button>
      <el-button type="primary" :loading="loading" @click="handleSubmit">
        添加
      </el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, watch, computed } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import { ElMessage } from 'element-plus'
import { useFeedStore } from '@/stores/feed'

const props = defineProps<{
  visible: boolean
}>()

const emit = defineEmits<{
  (e: 'update:visible', value: boolean): void
  (e: 'success'): void
}>()

const feedStore = useFeedStore()

const formRef = ref<FormInstance>()
const loading = ref(false)

const form = reactive({
  feedUrl: '',
  feedName: '',
  feedCategory: ''
})

const rules: FormRules = {
  feedUrl: [
    { required: true, message: '请输入RSS地址', trigger: 'blur' },
    { type: 'url', message: '请输入有效的URL', trigger: 'blur' }
  ]
}

const existingCategories = computed(() => {
  return feedStore.categories.map(cat => ({ value: cat }))
})

function queryCategories(queryString: string, cb: (results: { value: string }[]) => void) {
  const results = queryString
    ? existingCategories.value.filter(item =>
        item.value.toLowerCase().includes(queryString.toLowerCase())
      )
    : existingCategories.value
  cb(results)
}

watch(() => props.visible, (val) => {
  if (!val) {
    form.feedUrl = ''
    form.feedName = ''
    form.feedCategory = ''
    formRef.value?.resetFields()
  }
})

async function handleSubmit() {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (!valid) return

    loading.value = true
    try {
      await feedStore.addFeed({
        feedUrl: form.feedUrl,
        feedName: form.feedName || undefined,
        feedCategory: form.feedCategory || undefined
      })
      ElMessage.success('添加成功')
      emit('success')
    } catch {
      // 错误已在axios拦截器中处理
    } finally {
      loading.value = false
    }
  })
}
</script>
