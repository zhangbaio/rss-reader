<template>
  <Teleport to="body">
    <div class="dialog-overlay" v-if="visible" @click="handleClose">
      <div class="dialog" @click.stop>
        <div class="dialog-header">
          <h3>Add URL</h3>
          <button class="close-btn" @click="handleClose">
            <svg viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="currentColor" stroke-width="2">
              <line x1="18" y1="6" x2="6" y2="18"/>
              <line x1="6" y1="6" x2="18" y2="18"/>
            </svg>
          </button>
        </div>
        <div class="dialog-body">
          <div class="form-group">
            <label>URL</label>
            <input
              type="url"
              v-model="url"
              placeholder="https://example.com/article"
              ref="urlInput"
              @keyup.enter="handleSubmit"
            />
          </div>
          <div class="form-group">
            <label>Title (optional)</label>
            <input
              type="text"
              v-model="title"
              placeholder="Article title"
            />
          </div>
          <div class="form-group">
            <label>Save to</label>
            <div class="category-options">
              <button
                class="category-option"
                :class="{ active: category === 'inbox' }"
                @click="category = 'inbox'"
              >
                <svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="2">
                  <polyline points="22 12 16 12 14 15 10 15 8 12 2 12"/>
                  <path d="M5.45 5.11L2 12v6a2 2 0 0 0 2 2h16a2 2 0 0 0 2-2v-6l-3.45-6.89A2 2 0 0 0 16.76 4H7.24a2 2 0 0 0-1.79 1.11z"/>
                </svg>
                <span>Inbox</span>
              </button>
              <button
                class="category-option"
                :class="{ active: category === 'later' }"
                @click="category = 'later'"
              >
                <svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="2">
                  <circle cx="12" cy="12" r="10"/>
                  <polyline points="12 6 12 12 16 14"/>
                </svg>
                <span>Later</span>
              </button>
              <button
                class="category-option"
                :class="{ active: category === 'archive' }"
                @click="category = 'archive'"
              >
                <svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="2">
                  <polyline points="21 8 21 21 3 21 3 8"/>
                  <rect x="1" y="3" width="22" height="5"/>
                  <line x1="10" y1="12" x2="14" y2="12"/>
                </svg>
                <span>Archive</span>
              </button>
            </div>
          </div>
          <div class="error-message" v-if="error">
            {{ error }}
          </div>
        </div>
        <div class="dialog-footer">
          <button class="btn btn-secondary" @click="handleClose">Cancel</button>
          <button class="btn btn-primary" @click="handleSubmit" :disabled="loading || !url">
            {{ loading ? 'Adding...' : 'Add Article' }}
          </button>
        </div>
      </div>
    </div>
  </Teleport>
</template>

<script setup lang="ts">
import { ref, watch, nextTick } from 'vue'
import { useWebArticleStore } from '@/stores/webArticle'

const props = defineProps<{
  visible: boolean
}>()

const emit = defineEmits<{
  (e: 'update:visible', value: boolean): void
  (e: 'success'): void
}>()

const store = useWebArticleStore()

const url = ref('')
const title = ref('')
const category = ref<'inbox' | 'later' | 'archive'>('inbox')
const loading = ref(false)
const error = ref('')
const urlInput = ref<HTMLInputElement | null>(null)

watch(() => props.visible, async (visible) => {
  if (visible) {
    // Reset form
    url.value = ''
    title.value = ''
    category.value = 'inbox'
    error.value = ''
    // Focus input
    await nextTick()
    urlInput.value?.focus()
  }
})

function handleClose() {
  emit('update:visible', false)
}

async function handleSubmit() {
  if (!url.value) {
    error.value = 'Please enter a URL'
    return
  }

  // Validate URL format
  try {
    new URL(url.value)
  } catch {
    error.value = 'Please enter a valid URL'
    return
  }

  loading.value = true
  error.value = ''

  try {
    await store.addArticle(url.value, title.value || undefined, category.value)
    emit('success')
    handleClose()
  } catch (e: any) {
    error.value = e.message || 'Failed to add article'
  } finally {
    loading.value = false
  }
}
</script>

<style lang="scss" scoped>
.dialog-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2000;
}

.dialog {
  width: 480px;
  max-width: 90vw;
  background-color: #ffffff;
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.25), 0 0 0 1px rgba(0, 0, 0, 0.1);
}

.dialog-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px 24px;
  border-bottom: 1px solid #e5e5e5;

  h3 {
    font-size: 18px;
    font-weight: 600;
    color: #1a1a1a;
    margin: 0;
  }
}

.close-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  border: none;
  background: none;
  color: #999;
  cursor: pointer;
  border-radius: 6px;
  transition: all 0.2s;

  &:hover {
    background-color: #f5f5f5;
    color: #333;
  }
}

.dialog-body {
  padding: 24px;
}

.form-group {
  margin-bottom: 20px;

  &:last-child {
    margin-bottom: 0;
  }

  label {
    display: block;
    font-size: 13px;
    font-weight: 500;
    color: #666;
    margin-bottom: 8px;
  }

  input {
    width: 100%;
    padding: 12px 14px;
    border: 1px solid #ddd;
    border-radius: 8px;
    font-size: 14px;
    color: #1a1a1a;
    background-color: #ffffff;
    transition: all 0.2s;

    &:focus {
      outline: none;
      border-color: #4a90d9;
      box-shadow: 0 0 0 3px rgba(74, 144, 217, 0.15);
    }

    &::placeholder {
      color: #aaa;
    }
  }
}

.category-options {
  display: flex;
  gap: 8px;
}

.category-option {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 12px;
  border: 1px solid #ddd;
  background: #ffffff;
  color: #666;
  font-size: 13px;
  cursor: pointer;
  border-radius: 8px;
  transition: all 0.2s;

  &:hover {
    border-color: #999;
  }

  &.active {
    border-color: #4a90d9;
    background-color: rgba(74, 144, 217, 0.08);
    color: #4a90d9;
  }
}

.error-message {
  margin-top: 16px;
  padding: 12px;
  background-color: rgba(231, 76, 60, 0.1);
  border-radius: 8px;
  color: #e74c3c;
  font-size: 13px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 16px 24px;
  border-top: 1px solid #e5e5e5;
}

.btn {
  padding: 10px 20px;
  font-size: 14px;
  font-weight: 500;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;

  &:disabled {
    cursor: not-allowed;
    opacity: 0.6;
  }
}

.btn-secondary {
  border: 1px solid #ddd;
  background: #ffffff;
  color: #666;

  &:hover:not(:disabled) {
    border-color: #999;
    color: #333;
  }
}

.btn-primary {
  border: none;
  background-color: #4a90d9;
  color: white;

  &:hover:not(:disabled) {
    opacity: 0.9;
  }
}
</style>
