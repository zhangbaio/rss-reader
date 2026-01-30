<template>
  <div class="article-detail" v-if="article" ref="detailRef" @scroll="handleScroll">
    <header class="detail-header">
      <div class="header-meta">
        <span class="feed-name">{{ article.feedName }}</span>
        <span class="separator">·</span>
        <span class="publish-time">{{ formatDate(article.pubDate) }}</span>
        <span class="separator" v-if="article.author">·</span>
        <span class="author" v-if="article.author">{{ article.author }}</span>
      </div>
      <h1 class="detail-title">{{ article.title }}</h1>
      <div class="header-actions">
        <!-- 内容切换 -->
        <el-radio-group v-model="contentMode" size="small" class="content-mode-switch">
          <el-radio-button label="summary">摘要</el-radio-button>
          <el-radio-button label="full">原文</el-radio-button>
        </el-radio-group>

        <el-button
          :type="article.isRead === 1 ? 'default' : 'primary'"
          text
          @click="toggleReadStatus"
        >
          <el-icon><View v-if="article.isRead === 0" /><Hide v-else /></el-icon>
          {{ article.isRead === 1 ? '标为未读' : '标为已读' }}
        </el-button>
        <el-button
          :type="article.isFavorite === 1 ? 'warning' : 'default'"
          text
          @click="toggleFavoriteStatus"
        >
          <el-icon><StarFilled v-if="article.isFavorite === 1" /><Star v-else /></el-icon>
          {{ article.isFavorite === 1 ? '取消收藏' : '收藏' }}
        </el-button>
        <el-button text @click="openOriginal">
          <el-icon><Link /></el-icon>
          查看原文
        </el-button>
        <el-button text @click="settingsStore.toggleFullscreen">
          <el-icon><FullScreen v-if="!settingsStore.app.fullscreenReading" /><Close v-else /></el-icon>
        </el-button>

        <div class="header-tools">
          <ReadingSettings />
          <ShortcutsHelp />
        </div>
      </div>
    </header>

    <!-- 加载原文状态 -->
    <div v-if="loadingFullContent" class="loading-content">
      <el-icon class="is-loading" :size="32"><Loading /></el-icon>
      <p>正在加载原文内容...</p>
    </div>

    <!-- 加载失败状态 -->
    <div v-else-if="fullContentError" class="error-content">
      <el-icon :size="32"><Warning /></el-icon>
      <p>{{ fullContentError }}</p>
      <el-button type="primary" size="small" @click="fetchFullContent">
        重试
      </el-button>
      <el-button size="small" @click="contentMode = 'summary'">
        查看摘要
      </el-button>
    </div>

    <!-- 文章内容 -->
    <article
      v-else
      class="detail-content article-content"
      :style="contentStyle"
      v-html="displayContent"
    ></article>

    <!-- 阅读进度指示器 -->
    <div class="reading-progress" v-if="showProgress">
      <div class="progress-bar" :style="{ width: progressPercent + '%' }"></div>
    </div>
  </div>

  <div class="article-placeholder" v-else>
    <el-icon :size="64"><Document /></el-icon>
    <p>选择一篇文章开始阅读</p>
    <p class="placeholder-hint">按 <kbd>J</kbd> / <kbd>K</kbd> 切换文章</p>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, nextTick, onUnmounted } from 'vue'
import type { Article } from '@/types'
import { useArticleStore } from '@/stores/article'
import { useSettingsStore } from '@/stores/settings'
import { getFullContent } from '@/api/article'
import ReadingSettings from '@/components/ReadingSettings/ReadingSettings.vue'
import ShortcutsHelp from '@/components/ReadingSettings/ShortcutsHelp.vue'

const props = defineProps<{
  article: Article | null
}>()

const articleStore = useArticleStore()
const settingsStore = useSettingsStore()

const detailRef = ref<HTMLElement | null>(null)
const progressPercent = ref(0)
const showProgress = ref(false)

// 内容模式：summary（摘要）或 full（原文）
const contentMode = ref<'summary' | 'full'>('summary')
const fullContent = ref<string | null>(null)
const loadingFullContent = ref(false)
const fullContentError = ref<string | null>(null)

// 原文内容缓存
const fullContentCache = new Map<number, string>()

// 显示的内容
const displayContent = computed(() => {
  if (contentMode.value === 'full' && fullContent.value) {
    return fullContent.value
  }
  return props.article?.description || ''
})

// 根据设置生成内容样式
const contentStyle = computed(() => {
  const { fontSize, lineHeight, fontFamily, contentWidth } = settingsStore.reading

  let fontFamilyValue = ''
  switch (fontFamily) {
    case 'serif':
      fontFamilyValue = 'Georgia, "Times New Roman", serif'
      break
    case 'sans-serif':
      fontFamilyValue = '-apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif'
      break
    case 'monospace':
      fontFamilyValue = '"Fira Code", Consolas, Monaco, monospace'
      break
    default:
      fontFamilyValue = 'inherit'
  }

  return {
    fontSize: `${fontSize}px`,
    lineHeight: lineHeight,
    fontFamily: fontFamilyValue,
    maxWidth: `${contentWidth}px`
  }
})

// 监听内容模式变化
watch(contentMode, async (mode) => {
  if (mode === 'full' && props.article) {
    await fetchFullContent()
  }
})

// 监听文章变化
watch(
  () => props.article?.id,
  async (newId, oldId) => {
    // 重置状态
    contentMode.value = 'summary'
    fullContent.value = null
    fullContentError.value = null

    // 保存旧文章的进度
    if (oldId && detailRef.value) {
      const position = detailRef.value.scrollTop
      if (position > 100) {
        settingsStore.saveProgress(oldId, position)
      }
    }

    // 恢复新文章的进度
    if (newId) {
      await nextTick()
      const savedPosition = settingsStore.getProgress(newId)
      if (savedPosition > 0 && detailRef.value) {
        detailRef.value.scrollTop = savedPosition
      }

      // 如果缓存中有原文，直接使用
      if (fullContentCache.has(newId)) {
        fullContent.value = fullContentCache.get(newId) || null
      }
    }
  }
)

// 获取原文内容
async function fetchFullContent() {
  if (!props.article) return

  const articleId = props.article.id

  // 检查缓存
  if (fullContentCache.has(articleId)) {
    fullContent.value = fullContentCache.get(articleId) || null
    return
  }

  loadingFullContent.value = true
  fullContentError.value = null

  try {
    const content = await getFullContent(articleId)
    fullContent.value = content
    fullContentCache.set(articleId, content)
  } catch (error: any) {
    fullContentError.value = error.message || '获取原文失败，请稍后重试'
  } finally {
    loadingFullContent.value = false
  }
}

// 滚动时更新进度
let scrollTimer: number | null = null
function handleScroll() {
  if (!detailRef.value) return

  const { scrollTop, scrollHeight, clientHeight } = detailRef.value
  const maxScroll = scrollHeight - clientHeight

  if (maxScroll > 0) {
    progressPercent.value = Math.min(100, (scrollTop / maxScroll) * 100)
    showProgress.value = true
  }

  // 防抖保存进度
  if (scrollTimer) {
    clearTimeout(scrollTimer)
  }
  scrollTimer = window.setTimeout(() => {
    if (props.article && detailRef.value) {
      settingsStore.saveProgress(props.article.id, detailRef.value.scrollTop)
    }
  }, 500)
}

// 组件卸载时保存进度
onUnmounted(() => {
  if (props.article && detailRef.value) {
    settingsStore.saveProgress(props.article.id, detailRef.value.scrollTop)
  }
  if (scrollTimer) {
    clearTimeout(scrollTimer)
  }
})

function formatDate(dateStr: string): string {
  return new Date(dateStr).toLocaleString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}

async function toggleReadStatus() {
  if (!props.article) return

  if (props.article.isRead === 1) {
    await articleStore.markAsUnread(props.article.id)
  } else {
    await articleStore.markAsRead(props.article.id)
  }
}

async function toggleFavoriteStatus() {
  if (!props.article) return
  await articleStore.toggleFavorite(props.article.id)
}

function openOriginal() {
  if (props.article?.link) {
    window.open(props.article.link, '_blank')
  }
}

// 暴露方法给父组件
defineExpose({
  toggleReadStatus,
  toggleFavoriteStatus,
  openOriginal
})
</script>

<style lang="scss" scoped>
.article-detail {
  height: 100%;
  overflow-y: auto;
  padding: 32px;
  position: relative;
}

.detail-header {
  margin-bottom: 32px;
  padding-bottom: 24px;
  border-bottom: 1px solid var(--border-color);
  max-width: v-bind('settingsStore.reading.contentWidth + "px"');
  margin-left: auto;
  margin-right: auto;
}

.header-meta {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 8px;
  font-size: 14px;
  color: var(--text-muted);
  margin-bottom: 16px;
}

.separator {
  color: var(--border-color);
}

.feed-name {
  color: $primary-color;
}

.detail-title {
  font-size: 28px;
  font-weight: 700;
  color: var(--text-primary);
  line-height: 1.3;
  margin-bottom: 20px;
}

.header-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  align-items: center;
}

.content-mode-switch {
  margin-right: 8px;
}

.header-tools {
  display: flex;
  gap: 4px;
  margin-left: auto;
}

.detail-content {
  color: var(--text-primary);
  margin: 0 auto;
}

.loading-content,
.error-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 64px 32px;
  color: var(--text-muted);
  text-align: center;

  p {
    margin: 16px 0;
  }

  .el-button {
    margin-top: 8px;
  }
}

.error-content {
  color: var(--text-secondary);

  .el-icon {
    color: $warning-color;
  }
}

.reading-progress {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  background-color: var(--border-color);
  z-index: 100;
}

.progress-bar {
  height: 100%;
  background-color: $primary-color;
  transition: width 0.1s ease;
}

.article-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: var(--text-muted);

  p {
    margin-top: 16px;
    font-size: 16px;
  }

  .placeholder-hint {
    font-size: 14px;
    margin-top: 8px;

    kbd {
      padding: 2px 6px;
      font-family: monospace;
      background-color: var(--bg-sidebar);
      border: 1px solid var(--border-color);
      border-radius: 4px;
      margin: 0 2px;
    }
  }
}
</style>
