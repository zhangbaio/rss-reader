<template>
  <el-popover
    placement="bottom-end"
    :width="240"
    trigger="click"
  >
    <template #reference>
      <el-button text circle>
        <el-icon :size="18"><Keyboard /></el-icon>
      </el-button>
    </template>

    <div class="shortcuts-help">
      <h4 class="help-title">键盘快捷键</h4>
      <ul class="shortcuts-list">
        <li v-for="shortcut in shortcuts" :key="shortcut.key" class="shortcut-item">
          <kbd class="shortcut-key">{{ shortcut.key }}</kbd>
          <span class="shortcut-desc">{{ shortcut.description }}</span>
        </li>
      </ul>
    </div>
  </el-popover>
</template>

<script setup lang="ts">
const shortcuts = [
  { key: 'J', description: '下一篇文章' },
  { key: 'K', description: '上一篇文章' },
  { key: 'Space', description: '标记已读/未读' },
  { key: 'S', description: '收藏/取消收藏' },
  { key: 'O', description: '打开原文' },
  { key: 'F', description: '全屏阅读' },
  { key: 'R', description: '刷新订阅' },
  { key: 'Esc', description: '退出全屏' }
]
</script>

<style lang="scss" scoped>
.shortcuts-help {
  padding: 8px;
}

.help-title {
  font-size: 14px;
  font-weight: 600;
  margin-bottom: 12px;
  color: var(--text-primary);
}

.shortcuts-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.shortcut-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 6px 0;
}

.shortcut-key {
  min-width: 50px;
  padding: 4px 8px;
  font-family: monospace;
  font-size: 12px;
  background-color: var(--bg-sidebar);
  border: 1px solid var(--border-color);
  border-radius: 4px;
  text-align: center;
}

.shortcut-desc {
  font-size: 13px;
  color: var(--text-secondary);
}
</style>
