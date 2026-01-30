<template>
  <el-dialog
    v-model="dialogVisible"
    title="编辑划线"
    width="400px"
    :close-on-click-modal="false"
  >
    <div class="highlight-edit-form" v-if="highlight">
      <!-- 划线内容预览 -->
      <div class="highlight-preview" :class="[`highlight-${selectedColor}`]">
        "{{ highlight.selectedText }}"
      </div>

      <!-- 颜色选择 -->
      <div class="form-section">
        <div class="section-label">高亮颜色</div>
        <div class="color-picker">
          <button
            v-for="color in colors"
            :key="color.value"
            class="color-btn"
            :class="{ active: selectedColor === color.value }"
            :style="{ backgroundColor: color.bg }"
            @click="selectedColor = color.value"
            :title="color.label"
          ></button>
        </div>
      </div>

      <!-- 标签选择 -->
      <div class="form-section">
        <div class="section-label">标签</div>
        <div class="tag-list" v-if="allTags.length > 0">
          <el-check-tag
            v-for="tag in allTags"
            :key="tag.id"
            :checked="selectedTags.includes(tag.name)"
            @change="toggleTag(tag.name)"
          >
            {{ tag.name }}
          </el-check-tag>
        </div>
        <el-input
          v-model="newTagName"
          size="small"
          placeholder="添加新标签"
          @keyup.enter="addNewTag"
          style="margin-top: 8px;"
        >
          <template #append>
            <el-button @click="addNewTag">
              <el-icon><Plus /></el-icon>
            </el-button>
          </template>
        </el-input>
      </div>

      <!-- 备注 -->
      <div class="form-section">
        <div class="section-label">备注</div>
        <el-input
          v-model="noteContent"
          type="textarea"
          :rows="3"
          placeholder="添加备注..."
        />
      </div>
    </div>

    <template #footer>
      <div class="dialog-footer">
        <el-button type="danger" text @click="handleDelete">
          <el-icon><Delete /></el-icon>
          删除划线
        </el-button>
        <div class="footer-right">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSave" :loading="saving">保存</el-button>
        </div>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, watch, computed } from 'vue'
import { Plus, Delete } from '@element-plus/icons-vue'
import { ElMessageBox } from 'element-plus'
import type { Highlight, Tag } from '@/types'

const props = defineProps<{
  visible: boolean
  highlight: Highlight | null
  allTags: Tag[]
}>()

const emit = defineEmits<{
  (e: 'update:visible', value: boolean): void
  (e: 'save', data: { id: number; color: string; note: string; tags: string[] }): void
  (e: 'delete', id: number): void
}>()

const colors = [
  { value: 'yellow', bg: '#fef08a', label: '黄色' },
  { value: 'green', bg: '#bbf7d0', label: '绿色' },
  { value: 'blue', bg: '#bfdbfe', label: '蓝色' },
  { value: 'pink', bg: '#fbcfe8', label: '粉色' },
  { value: 'purple', bg: '#ddd6fe', label: '紫色' }
]

const selectedColor = ref('yellow')
const selectedTags = ref<string[]>([])
const noteContent = ref('')
const newTagName = ref('')
const saving = ref(false)

const dialogVisible = computed({
  get: () => props.visible,
  set: (val) => emit('update:visible', val)
})

// 当对话框打开时，初始化表单数据
watch(() => props.visible, (val) => {
  if (val && props.highlight) {
    selectedColor.value = props.highlight.color || 'yellow'
    selectedTags.value = props.highlight.tags?.map(t => t.name) || []
    noteContent.value = props.highlight.note || ''
    newTagName.value = ''
  }
})

function toggleTag(tagName: string) {
  const index = selectedTags.value.indexOf(tagName)
  if (index > -1) {
    selectedTags.value.splice(index, 1)
  } else {
    selectedTags.value.push(tagName)
  }
}

function addNewTag() {
  if (newTagName.value.trim()) {
    if (!selectedTags.value.includes(newTagName.value.trim())) {
      selectedTags.value.push(newTagName.value.trim())
    }
    newTagName.value = ''
  }
}

async function handleSave() {
  if (!props.highlight) return

  saving.value = true
  try {
    emit('save', {
      id: props.highlight.id,
      color: selectedColor.value,
      note: noteContent.value,
      tags: selectedTags.value
    })
    dialogVisible.value = false
  } finally {
    saving.value = false
  }
}

async function handleDelete() {
  if (!props.highlight) return

  try {
    await ElMessageBox.confirm(
      '确定要删除这条划线吗？',
      '删除确认',
      {
        confirmButtonText: '删除',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    emit('delete', props.highlight.id)
    dialogVisible.value = false
  } catch {
    // 用户取消
  }
}
</script>

<style lang="scss" scoped>
.highlight-edit-form {
  .highlight-preview {
    padding: 12px 16px;
    border-radius: 8px;
    font-size: 14px;
    line-height: 1.6;
    margin-bottom: 20px;
    border-left: 3px solid;

    &.highlight-yellow {
      background-color: rgba(254, 240, 138, 0.4);
      border-left-color: #fef08a;
    }

    &.highlight-green {
      background-color: rgba(187, 247, 208, 0.4);
      border-left-color: #bbf7d0;
    }

    &.highlight-blue {
      background-color: rgba(191, 219, 254, 0.4);
      border-left-color: #bfdbfe;
    }

    &.highlight-pink {
      background-color: rgba(251, 207, 232, 0.4);
      border-left-color: #fbcfe8;
    }

    &.highlight-purple {
      background-color: rgba(221, 214, 254, 0.4);
      border-left-color: #ddd6fe;
    }
  }

  .form-section {
    margin-bottom: 20px;

    .section-label {
      font-size: 13px;
      font-weight: 500;
      color: #606266;
      margin-bottom: 8px;
    }
  }

  .color-picker {
    display: flex;
    gap: 8px;
  }

  .color-btn {
    width: 32px;
    height: 32px;
    border: 2px solid transparent;
    border-radius: 6px;
    cursor: pointer;
    transition: all 0.2s;

    &:hover {
      transform: scale(1.1);
    }

    &.active {
      border-color: #333;
    }
  }

  .tag-list {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;

    .el-check-tag {
      cursor: pointer;
    }
  }
}

.dialog-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;

  .footer-right {
    display: flex;
    gap: 8px;
  }
}
</style>
