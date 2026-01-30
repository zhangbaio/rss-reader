<template>
  <el-popover
    placement="bottom-end"
    :width="320"
    trigger="click"
  >
    <template #reference>
      <el-button text circle>
        <el-icon :size="18"><Setting /></el-icon>
      </el-button>
    </template>

    <div class="reading-settings">
      <h4 class="settings-title">阅读设置</h4>

      <!-- 字体大小 -->
      <div class="setting-item">
        <div class="setting-label">
          <span>字体大小</span>
          <span class="setting-value">{{ settingsStore.reading.fontSize }}px</span>
        </div>
        <el-slider
          v-model="settingsStore.reading.fontSize"
          :min="14"
          :max="24"
          :step="1"
          :show-tooltip="false"
        />
      </div>

      <!-- 行高 -->
      <div class="setting-item">
        <div class="setting-label">
          <span>行高</span>
          <span class="setting-value">{{ settingsStore.reading.lineHeight.toFixed(1) }}</span>
        </div>
        <el-slider
          v-model="settingsStore.reading.lineHeight"
          :min="1.4"
          :max="2.4"
          :step="0.1"
          :show-tooltip="false"
        />
      </div>

      <!-- 内容宽度 -->
      <div class="setting-item">
        <div class="setting-label">
          <span>内容宽度</span>
          <span class="setting-value">{{ settingsStore.reading.contentWidth }}px</span>
        </div>
        <el-slider
          v-model="settingsStore.reading.contentWidth"
          :min="600"
          :max="1000"
          :step="50"
          :show-tooltip="false"
        />
      </div>

      <!-- 字体 -->
      <div class="setting-item">
        <div class="setting-label">
          <span>字体</span>
        </div>
        <el-select
          v-model="settingsStore.reading.fontFamily"
          style="width: 100%"
          size="small"
        >
          <el-option
            v-for="font in settingsStore.fontOptions"
            :key="font.value"
            :label="font.label"
            :value="font.value"
          />
        </el-select>
      </div>

      <!-- 主题 -->
      <div class="setting-item">
        <div class="setting-label">
          <span>主题</span>
        </div>
        <el-radio-group v-model="settingsStore.app.theme" size="small">
          <el-radio-button label="light">浅色</el-radio-button>
          <el-radio-button label="dark">深色</el-radio-button>
          <el-radio-button label="system">跟随系统</el-radio-button>
        </el-radio-group>
      </div>

      <!-- 重置按钮 -->
      <div class="setting-actions">
        <el-button size="small" @click="settingsStore.resetReadingSettings">
          恢复默认
        </el-button>
      </div>
    </div>
  </el-popover>
</template>

<script setup lang="ts">
import { useSettingsStore } from '@/stores/settings'

const settingsStore = useSettingsStore()
</script>

<style lang="scss" scoped>
.reading-settings {
  padding: 8px;
}

.settings-title {
  font-size: 14px;
  font-weight: 600;
  margin-bottom: 16px;
  color: var(--text-primary);
}

.setting-item {
  margin-bottom: 16px;
}

.setting-label {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
  font-size: 13px;
  color: var(--text-secondary);
}

.setting-value {
  font-family: monospace;
  color: var(--text-muted);
}

.setting-actions {
  display: flex;
  justify-content: flex-end;
  padding-top: 8px;
  border-top: 1px solid var(--border-color);
}
</style>
