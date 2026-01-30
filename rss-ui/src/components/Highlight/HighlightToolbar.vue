<template>
  <Teleport to="body">
    <div
      v-if="visible"
      class="highlight-toolbar"
      :style="toolbarStyle"
      @mousedown.stop
    >
      <!-- 颜色选择 - 点击直接保存 -->
      <div class="color-picker">
        <button
          v-for="color in colors"
          :key="color.value"
          class="color-btn"
          :style="{ backgroundColor: color.bg }"
          @click="saveWithColor(color.value)"
          :title="color.label"
        ></button>
      </div>

      <div class="divider"></div>

      <!-- 带标签保存 -->
      <el-popover
        placement="bottom"
        :width="220"
        trigger="click"
        v-model:visible="showTagPopover"
      >
        <template #reference>
          <button class="action-btn" title="添加标签后保存">
            <el-icon><PriceTag /></el-icon>
          </button>
        </template>
        <div class="tag-selector">
          <div class="popover-title">选择标签</div>
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
            placeholder="新标签名称"
            @keyup.enter="addNewTag"
            style="margin-top: 8px;"
          >
            <template #append>
              <el-button @click="addNewTag">
                <el-icon><Plus /></el-icon>
              </el-button>
            </template>
          </el-input>
          <div class="popover-actions">
            <el-button size="small" @click="showTagPopover = false">取消</el-button>
            <el-button size="small" type="primary" @click="saveWithTags">保存</el-button>
          </div>
        </div>
      </el-popover>

      <!-- 带备注保存 -->
      <el-popover
        placement="bottom"
        :width="280"
        trigger="click"
        v-model:visible="showNotePopover"
      >
        <template #reference>
          <button class="action-btn" title="添加备注后保存">
            <el-icon><EditPen /></el-icon>
          </button>
        </template>
        <div class="note-editor">
          <div class="popover-title">添加备注</div>
          <el-input
            v-model="noteContent"
            type="textarea"
            :rows="3"
            placeholder="输入备注内容..."
          />
          <div class="popover-actions">
            <el-button size="small" @click="showNotePopover = false">取消</el-button>
            <el-button size="small" type="primary" @click="saveWithNote">保存</el-button>
          </div>
        </div>
      </el-popover>

      <div class="divider"></div>

      <!-- 取消按钮 -->
      <button class="action-btn cancel-btn" @click="cancel" title="取消">
        <el-icon><Close /></el-icon>
      </button>
    </div>
  </Teleport>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { PriceTag, Plus, EditPen, Close } from '@element-plus/icons-vue'
import type { Tag } from '@/types'

const props = defineProps<{
  visible: boolean
  position: { x: number; y: number }
  selectedText: string
  allTags: Tag[]
}>()

const emit = defineEmits<{
  (e: 'save', data: { color: string; note: string; tags: string[] }): void
  (e: 'cancel'): void
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
const showTagPopover = ref(false)
const showNotePopover = ref(false)

const toolbarStyle = computed(() => ({
  left: `${props.position.x}px`,
  top: `${props.position.y}px`
}))

// 重置状态
watch(() => props.visible, (val) => {
  if (val) {
    selectedColor.value = 'yellow'
    selectedTags.value = []
    noteContent.value = ''
    newTagName.value = ''
    showTagPopover.value = false
    showNotePopover.value = false
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

// 直接点击颜色保存
function saveWithColor(color: string) {
  emit('save', {
    color,
    note: '',
    tags: []
  })
}

// 带标签保存
function saveWithTags() {
  showTagPopover.value = false
  emit('save', {
    color: selectedColor.value,
    note: '',
    tags: selectedTags.value
  })
}

// 带备注保存
function saveWithNote() {
  showNotePopover.value = false
  emit('save', {
    color: selectedColor.value,
    note: noteContent.value,
    tags: []
  })
}

function cancel() {
  emit('cancel')
}
</script>

<style lang="scss">
.highlight-toolbar {
  position: fixed;
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 8px 12px;
  background: white;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  z-index: 9999;
  transform: translateX(-50%);
}

.color-picker {
  display: flex;
  gap: 4px;
}

.color-btn {
  width: 24px;
  height: 24px;
  border: 2px solid transparent;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.2s;

  &:hover {
    transform: scale(1.15);
    border-color: #333;
  }
}

.divider {
  width: 1px;
  height: 24px;
  background-color: #e4e7ed;
  margin: 0 4px;
}

.action-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  background: none;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 16px;
  color: #606266;
  transition: all 0.2s;

  &:hover {
    background-color: #f5f7fa;
    color: #409eff;
  }

  &.cancel-btn:hover {
    color: #f56c6c;
  }
}

.tag-selector {
  .popover-title {
    font-size: 13px;
    font-weight: 500;
    color: #303133;
    margin-bottom: 12px;
  }

  .tag-list {
    display: flex;
    flex-wrap: wrap;
    gap: 6px;
    margin-bottom: 8px;

    .el-check-tag {
      cursor: pointer;
    }
  }

  .popover-actions {
    display: flex;
    justify-content: flex-end;
    gap: 8px;
    margin-top: 12px;
    padding-top: 12px;
    border-top: 1px solid #ebeef5;
  }
}

.note-editor {
  .popover-title {
    font-size: 13px;
    font-weight: 500;
    color: #303133;
    margin-bottom: 12px;
  }

  .popover-actions {
    display: flex;
    justify-content: flex-end;
    gap: 8px;
    margin-top: 12px;
  }
}
</style>
