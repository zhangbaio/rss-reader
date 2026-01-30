<template>
  <div class="reader-page" :class="{ 'hide-left': !showLeft, 'hide-right': !showRight }">
    <!-- 左侧目录栏 -->
    <aside class="reader-left" v-show="showLeft">
      <div class="panel-header">
        <h3>Contents</h3>
        <el-button text circle size="small" @click="showLeft = false">
          <el-icon><Close /></el-icon>
        </el-button>
      </div>
      <nav class="toc-list">
        <div
          v-for="(heading, index) in tableOfContents"
          :key="index"
          class="toc-item"
          :class="[`level-${heading.level}`, { active: activeHeading === index }]"
          @click="scrollToHeading(index)"
        >
          {{ heading.text }}
        </div>
        <div v-if="tableOfContents.length === 0 && !loading" class="toc-empty">
          暂无目录
        </div>
      </nav>
    </aside>

    <!-- 左侧展开按钮 -->
    <button class="toggle-btn toggle-left" v-show="!showLeft" @click="showLeft = true">
      <el-icon><DArrowRight /></el-icon>
    </button>

    <!-- 中间内容区 -->
    <main class="reader-main" ref="mainRef" @scroll="handleScroll" @mouseup="handleTextSelection">
      <div class="main-container">
        <!-- 顶部工具栏 -->
        <div class="main-toolbar">
          <el-button text @click="goBack">
            <el-icon><ArrowLeft /></el-icon>
            返回
          </el-button>
          <div class="toolbar-right">
            <ReadingSettings />
          </div>
        </div>

        <!-- 文章来源标签 -->
        <div class="source-tag" v-if="article">
          <el-tag size="small" type="info">{{ article.feedName }}</el-tag>
        </div>

        <!-- 文章标题 -->
        <h1 class="article-title" v-if="article">{{ article.title }}</h1>

        <!-- 文章元信息 -->
        <div class="article-meta" v-if="article">
          <span v-if="article.author">{{ article.author }}</span>
          <span class="separator" v-if="article.author">·</span>
          <span>{{ readingTime }} min read</span>
          <span class="separator">·</span>
          <span>{{ formatDate(article.pubDate) }}</span>
        </div>

        <!-- 加载状态 -->
        <div v-if="loading" class="loading-state">
          <el-icon class="is-loading" :size="32"><Loading /></el-icon>
          <p>正在加载原文内容...</p>
        </div>

        <!-- 错误状态 -->
        <div v-else-if="error" class="error-state">
          <el-icon :size="32"><Warning /></el-icon>
          <p>{{ error }}</p>
          <el-button type="primary" @click="loadArticle">重试</el-button>
        </div>

        <!-- 文章内容 -->
        <article
          v-else
          class="article-content"
          :style="contentStyle"
          ref="contentRef"
          v-html="highlightedContent"
          @click="handleContentClick"
        ></article>

        <!-- 底部操作栏 -->
        <div class="article-footer" v-if="article && !loading">
          <el-button @click="toggleReadStatus">
            <el-icon><View v-if="article.isRead === 0" /><Hide v-else /></el-icon>
            {{ article.isRead === 1 ? '标为未读' : '标为已读' }}
          </el-button>
          <el-button @click="toggleFavorite">
            <el-icon><StarFilled v-if="article.isFavorite === 1" /><Star v-else /></el-icon>
            {{ article.isFavorite === 1 ? '取消收藏' : '收藏' }}
          </el-button>
          <el-button @click="openOriginal">
            <el-icon><Link /></el-icon>
            查看原文
          </el-button>
        </div>
      </div>
    </main>

    <!-- 右侧展开按钮 -->
    <button class="toggle-btn toggle-right" v-show="!showRight" @click="showRight = true">
      <el-icon><DArrowLeft /></el-icon>
    </button>

    <!-- 右侧信息栏 -->
    <aside class="reader-right" v-show="showRight">
      <div class="panel-header">
        <div class="panel-tabs">
          <span
            class="tab"
            :class="{ active: activeTab === 'info' }"
            @click="activeTab = 'info'"
          >Info</span>
          <span
            class="tab"
            :class="{ active: activeTab === 'notebook' }"
            @click="activeTab = 'notebook'"
          >Notebook</span>
        </div>
        <el-button text circle size="small" @click="showRight = false">
          <el-icon><Close /></el-icon>
        </el-button>
      </div>

      <!-- Info 面板 -->
      <div class="info-content" v-if="article && activeTab === 'info'">
        <!-- 元数据 -->
        <div class="info-section">
          <h4 class="section-title">METADATA</h4>
          <div class="metadata-list">
            <div class="metadata-item">
              <span class="meta-label">Type</span>
              <span class="meta-value">RSS Feed</span>
            </div>
            <div class="metadata-item">
              <span class="meta-label">Source</span>
              <span class="meta-value">{{ article.feedName }}</span>
            </div>
            <div class="metadata-item">
              <span class="meta-label">Domain</span>
              <span class="meta-value">{{ domain }}</span>
            </div>
            <div class="metadata-item">
              <span class="meta-label">Published</span>
              <span class="meta-value">{{ formatDate(article.pubDate) }}</span>
            </div>
            <div class="metadata-item">
              <span class="meta-label">Words</span>
              <span class="meta-value">{{ wordCount }} 字</span>
            </div>
            <div class="metadata-item">
              <span class="meta-label">Progress</span>
              <div class="progress-info">
                <el-progress
                  :percentage="readProgress"
                  :stroke-width="4"
                  :show-text="false"
                />
                <span class="progress-text">{{ readProgress }}%</span>
              </div>
            </div>
            <div class="metadata-item">
              <span class="meta-label">Status</span>
              <span class="meta-value">
                <el-tag :type="article.isRead === 1 ? 'info' : 'success'" size="small">
                  {{ article.isRead === 1 ? '已读' : '未读' }}
                </el-tag>
                <el-tag v-if="article.isFavorite === 1" type="warning" size="small" style="margin-left: 4px">
                  已收藏
                </el-tag>
              </span>
            </div>
          </div>
        </div>
      </div>

      <!-- Notebook 面板 -->
      <div class="notebook-content" v-if="article && activeTab === 'notebook'">
        <!-- 文档笔记 -->
        <div class="info-section">
          <h4 class="section-title">DOCUMENT NOTE</h4>
          <div class="note-input-area">
            <el-input
              v-model="documentNote"
              type="textarea"
              :rows="4"
              placeholder="Write your notes about this article..."
              @blur="saveDocumentNote"
            />
            <div class="note-actions" v-if="documentNote !== savedNote">
              <el-button size="small" type="primary" @click="saveDocumentNote" :loading="noteStore.saving">
                Save
              </el-button>
            </div>
          </div>
        </div>

        <!-- 划线列表 -->
        <div class="info-section">
          <h4 class="section-title">
            HIGHLIGHTS
            <span class="highlight-count">({{ highlightStore.highlights.length }})</span>
          </h4>
          <div class="highlights-list" v-if="highlightStore.highlights.length > 0">
            <div
              v-for="hl in highlightStore.highlights"
              :key="hl.id"
              class="highlight-item clickable"
              :class="[`highlight-${hl.color}`]"
              @click="openEditDialog(hl)"
            >
              <div class="highlight-text">"{{ hl.selectedText }}"</div>
              <div class="highlight-note" v-if="hl.note">{{ hl.note }}</div>
              <div class="highlight-tags" v-if="hl.tags && hl.tags.length > 0">
                <el-tag
                  v-for="tag in hl.tags"
                  :key="tag.id"
                  size="small"
                  type="info"
                >
                  {{ tag.name }}
                </el-tag>
              </div>
              <div class="highlight-footer">
                <span class="highlight-time">{{ formatTime(hl.createTime) }}</span>
                <span class="edit-hint">点击编辑</span>
              </div>
            </div>
          </div>
          <div class="highlights-empty" v-else>
            <p>No highlights yet</p>
            <p class="hint">Select text in the article to create highlights</p>
          </div>
        </div>
      </div>
    </aside>

    <!-- 阅读进度条 -->
    <div class="reading-progress-bar">
      <div class="progress-fill" :style="{ width: readProgress + '%' }"></div>
    </div>

    <!-- 划线工具栏 -->
    <HighlightToolbar
      :visible="showHighlightToolbar"
      :position="toolbarPosition"
      :selected-text="selectedText"
      :all-tags="highlightStore.tags"
      @save="handleSaveHighlight"
      @cancel="hideHighlightToolbar"
    />

    <!-- 划线编辑对话框 -->
    <HighlightEditDialog
      v-model:visible="showEditDialog"
      :highlight="editingHighlight"
      :all-tags="highlightStore.tags"
      @save="handleUpdateHighlight"
      @delete="handleDeleteHighlight"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted, onUnmounted, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useArticleStore } from '@/stores/article'
import { useSettingsStore } from '@/stores/settings'
import { useHighlightStore } from '@/stores/highlight'
import { useNoteStore } from '@/stores/note'
import { getArticle, getFullContent } from '@/api/article'
import ReadingSettings from '@/components/ReadingSettings/ReadingSettings.vue'
import HighlightToolbar from '@/components/Highlight/HighlightToolbar.vue'
import HighlightEditDialog from '@/components/Highlight/HighlightEditDialog.vue'
import type { Article, CreateHighlightParams, Highlight } from '@/types'

interface HeadingItem {
  text: string
  level: number
  element?: HTMLElement
}

const route = useRoute()
const router = useRouter()
const articleStore = useArticleStore()
const settingsStore = useSettingsStore()
const highlightStore = useHighlightStore()
const noteStore = useNoteStore()

const mainRef = ref<HTMLElement | null>(null)
const contentRef = ref<HTMLElement | null>(null)

// 面板显示状态
const showLeft = ref(true)
const showRight = ref(true)
const activeTab = ref<'info' | 'notebook'>('info')

// 文章数据
const article = ref<Article | null>(null)
const loading = ref(false)
const error = ref<string | null>(null)

// 原文内容
const fullContent = ref<string | null>(null)

// 目录
const tableOfContents = ref<HeadingItem[]>([])
const activeHeading = ref(-1)

// 阅读进度
const readProgress = ref(0)

// 划线工具栏
const showHighlightToolbar = ref(false)
const toolbarPosition = ref({ x: 0, y: 0 })
const selectedText = ref('')
const selectionRange = ref<Range | null>(null)

// 文档笔记
const documentNote = ref('')
const savedNote = ref('')

// 编辑划线对话框
const showEditDialog = ref(false)
const editingHighlight = ref<Highlight | null>(null)

// 显示的内容（优先原文，失败则使用RSS内容）
const displayContent = computed(() => {
  return fullContent.value || article.value?.description || ''
})

// 带划线标记的内容
const highlightedContent = computed(() => {
  const content = displayContent.value
  if (!content || highlightStore.highlights.length === 0) {
    return content
  }

  // 按startOffset降序排列，从后往前替换避免偏移问题
  const sortedHighlights = [...highlightStore.highlights].sort(
    (a, b) => b.startOffset - a.startOffset
  )

  let result = content
  const plainText = content.replace(/<[^>]*>/g, '')

  for (const hl of sortedHighlights) {
    // 在纯文本中找到对应位置
    const text = hl.selectedText
    const textIndex = plainText.indexOf(text)
    if (textIndex === -1) continue

    // 在HTML中查找并替换
    const escapedText = text.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')
    const regex = new RegExp(`(?<=>)[^<]*?${escapedText}[^<]*?(?=<)`, 'g')

    result = result.replace(text,
      `<mark class="highlight-mark highlight-${hl.color}" data-highlight-id="${hl.id}">${text}</mark>`
    )
  }

  return result
})

// 内容样式
const contentStyle = computed(() => {
  const { fontSize, lineHeight, fontFamily } = settingsStore.reading

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
    fontFamily: fontFamilyValue
  }
})

// 计算字数
const wordCount = computed(() => {
  const content = displayContent.value
  const text = content.replace(/<[^>]*>/g, '')
  return text.length
})

// 计算阅读时间（假设每分钟阅读300字）
const readingTime = computed(() => {
  return Math.max(1, Math.ceil(wordCount.value / 300))
})

// 获取域名
const domain = computed(() => {
  if (!article.value?.link) return ''
  try {
    const url = new URL(article.value.link)
    return url.hostname
  } catch {
    return ''
  }
})

// 加载文章和原文
async function loadArticle() {
  const id = Number(route.params.id)
  if (!id) return

  loading.value = true
  error.value = null
  fullContent.value = null

  try {
    // 先加载文章基本信息
    article.value = await getArticle(id)

    // 自动标记为已读
    if (article.value && article.value.isRead === 0) {
      await articleStore.markAsRead(id)
      article.value.isRead = 1
    }

    // 并行加载原文内容、划线、笔记
    const promises: Promise<any>[] = []

    // 加载原文内容
    promises.push(
      getFullContent(id)
        .then(content => { fullContent.value = content })
        .catch(() => { console.warn('Failed to load full content, using RSS content instead') })
    )

    // 加载划线
    promises.push(highlightStore.fetchHighlights(id))

    // 加载笔记
    promises.push(
      noteStore.fetchNote(id).then(() => {
        documentNote.value = noteStore.note?.noteContent || ''
        savedNote.value = documentNote.value
      })
    )

    // 加载标签
    promises.push(highlightStore.fetchTags())

    await Promise.all(promises)

    // 提取目录
    await nextTick()
    extractTableOfContents()
  } catch (e: any) {
    error.value = e.message || '加载文章失败'
  } finally {
    loading.value = false
  }
}

// 处理文本选择
function handleTextSelection(e: MouseEvent) {
  const target = e.target as HTMLElement

  // 如果点击的是工具栏，不处理
  const toolbar = document.querySelector('.highlight-toolbar')
  if (toolbar && toolbar.contains(target)) {
    return
  }

  // 如果点击的是已有的高亮标记，不处理（由 handleContentClick 处理）
  if (target.classList.contains('highlight-mark')) {
    return
  }

  // 延迟执行，确保selection已经更新
  setTimeout(() => {
    const selection = window.getSelection()

    // 没有选择文本时，隐藏工具栏
    if (!selection || selection.isCollapsed || !selection.toString().trim()) {
      if (showHighlightToolbar.value) {
        hideHighlightToolbar()
      }
      return
    }

    const text = selection.toString().trim()
    if (text.length < 2) return // 至少选择2个字符

    // 检查选择是否在文章内容区域内
    const articleContent = contentRef.value
    if (!articleContent) {
      return
    }

    // 检查选择的起点是否在文章内容中
    const anchorNode = selection.anchorNode
    if (!anchorNode || !articleContent.contains(anchorNode)) {
      return
    }

    selectedText.value = text
    selectionRange.value = selection.getRangeAt(0).cloneRange()

    // 计算工具栏位置
    const rect = selection.getRangeAt(0).getBoundingClientRect()
    toolbarPosition.value = {
      x: rect.left + rect.width / 2,
      y: rect.top - 10
    }

    showHighlightToolbar.value = true
  }, 10)
}

// 隐藏划线工具栏
function hideHighlightToolbar() {
  showHighlightToolbar.value = false
  selectedText.value = ''
  selectionRange.value = null
  window.getSelection()?.removeAllRanges()
}

// 保存划线
async function handleSaveHighlight(data: { color: string; note: string; tags: string[] }) {
  if (!article.value || !selectionRange.value) return

  const range = selectionRange.value

  // 计算偏移量
  const contentText = contentRef.value?.textContent || ''
  const preSelectionRange = document.createRange()
  preSelectionRange.selectNodeContents(contentRef.value!)
  preSelectionRange.setEnd(range.startContainer, range.startOffset)
  const startOffset = preSelectionRange.toString().length
  const endOffset = startOffset + selectedText.value.length

  const params: CreateHighlightParams = {
    articleId: article.value.id,
    selectedText: selectedText.value,
    startOffset,
    endOffset,
    color: data.color,
    note: data.note || undefined,
    tagNames: data.tags.length > 0 ? data.tags : undefined
  }

  try {
    await highlightStore.createHighlight(params)
    hideHighlightToolbar()
  } catch (e) {
    console.error('Failed to save highlight:', e)
  }
}

// 处理文章内容点击（检查是否点击了高亮文本）
function handleContentClick(e: MouseEvent) {
  const target = e.target as HTMLElement

  // 检查是否点击了高亮标记
  if (target.classList.contains('highlight-mark')) {
    e.preventDefault()
    e.stopPropagation()

    const highlightId = target.dataset.highlightId
    if (highlightId) {
      const highlight = highlightStore.highlights.find(h => h.id === Number(highlightId))
      if (highlight) {
        openEditDialog(highlight)
      }
    }
  }
}

// 打开编辑对话框
function openEditDialog(highlight: Highlight) {
  editingHighlight.value = highlight
  showEditDialog.value = true
}

// 更新划线
async function handleUpdateHighlight(data: { id: number; color: string; note: string; tags: string[] }) {
  try {
    await highlightStore.updateHighlight(data.id, {
      color: data.color,
      note: data.note,
      tagNames: data.tags
    })
  } catch (e) {
    console.error('Failed to update highlight:', e)
  }
}

// 删除划线
async function handleDeleteHighlight(id: number) {
  try {
    await highlightStore.deleteHighlight(id)
  } catch (e) {
    console.error('Failed to delete highlight:', e)
  }
}

// 保存文档笔记
async function saveDocumentNote() {
  if (!article.value || documentNote.value === savedNote.value) return

  try {
    await noteStore.saveNote(article.value.id, documentNote.value)
    savedNote.value = documentNote.value
  } catch (e) {
    console.error('Failed to save note:', e)
  }
}

// 格式化时间
function formatTime(dateStr: string): string {
  const date = new Date(dateStr)
  const now = new Date()
  const diff = now.getTime() - date.getTime()

  if (diff < 60000) return 'just now'
  if (diff < 3600000) return `${Math.floor(diff / 60000)}m ago`
  if (diff < 86400000) return `${Math.floor(diff / 3600000)}h ago`
  if (diff < 604800000) return `${Math.floor(diff / 86400000)}d ago`

  return date.toLocaleDateString('zh-CN', { month: 'short', day: 'numeric' })
}

// 提取目录
function extractTableOfContents() {
  if (!contentRef.value) return

  const headings = contentRef.value.querySelectorAll('h1, h2, h3, h4, h5, h6')
  tableOfContents.value = Array.from(headings).map((el) => {
    const level = parseInt(el.tagName.charAt(1))
    return {
      text: el.textContent?.trim() || '',
      level,
      element: el as HTMLElement
    }
  })
}

// 滚动到指定标题
function scrollToHeading(index: number) {
  const heading = tableOfContents.value[index]
  if (heading?.element) {
    heading.element.scrollIntoView({ behavior: 'smooth', block: 'start' })
    activeHeading.value = index
  }
}

// 处理滚动
function handleScroll() {
  if (!mainRef.value) return

  const { scrollTop, scrollHeight, clientHeight } = mainRef.value
  const maxScroll = scrollHeight - clientHeight

  // 更新阅读进度
  if (maxScroll > 0) {
    readProgress.value = Math.round((scrollTop / maxScroll) * 100)
  }

  // 更新当前高亮的目录项
  if (contentRef.value) {
    const headings = tableOfContents.value
    for (let i = headings.length - 1; i >= 0; i--) {
      const el = headings[i].element
      if (el && el.getBoundingClientRect().top <= 100) {
        activeHeading.value = i
        break
      }
    }
  }
}

// 监听路由变化
watch(() => route.params.id, () => {
  loadArticle()
}, { immediate: true })

// 格式化日期
function formatDate(dateStr: string): string {
  return new Date(dateStr).toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'short',
    day: 'numeric'
  })
}

// 返回
function goBack() {
  router.back()
}

// 切换已读状态
async function toggleReadStatus() {
  if (!article.value) return
  if (article.value.isRead === 1) {
    await articleStore.markAsUnread(article.value.id)
    article.value.isRead = 0
  } else {
    await articleStore.markAsRead(article.value.id)
    article.value.isRead = 1
  }
}

// 切换收藏状态
async function toggleFavorite() {
  if (!article.value) return
  await articleStore.toggleFavorite(article.value.id)
  article.value.isFavorite = article.value.isFavorite === 1 ? 0 : 1
}

// 打开原文
function openOriginal() {
  if (article.value?.link) {
    window.open(article.value.link, '_blank')
  }
}

// 键盘快捷键
function handleKeydown(e: KeyboardEvent) {
  if (e.key === 'Escape') {
    goBack()
  } else if (e.key === '[') {
    showLeft.value = !showLeft.value
  } else if (e.key === ']') {
    showRight.value = !showRight.value
  }
}

onMounted(() => {
  window.addEventListener('keydown', handleKeydown)
})

onUnmounted(() => {
  window.removeEventListener('keydown', handleKeydown)
  // 清理store状态
  highlightStore.clearHighlights()
  noteStore.clearNote()
})
</script>

<style lang="scss" scoped>
.reader-page {
  display: flex;
  height: 100vh;
  background-color: var(--bg-content);
  position: relative;
}

// 左侧目录栏
.reader-left {
  width: 260px;
  border-right: 1px solid var(--border-color);
  background-color: var(--bg-sidebar);
  display: flex;
  flex-direction: column;
  flex-shrink: 0;
  transition: width 0.3s ease;
}

.panel-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 20px;
  border-bottom: 1px solid var(--border-color);

  h3 {
    font-size: 14px;
    font-weight: 600;
    color: var(--text-primary);
    margin: 0;
  }
}

.panel-tabs {
  display: flex;
  gap: 16px;

  .tab {
    font-size: 14px;
    color: var(--text-muted);
    cursor: pointer;
    padding: 4px 0;
    border-bottom: 2px solid transparent;
    transition: all 0.2s;

    &:hover {
      color: var(--text-secondary);
    }

    &.active {
      color: var(--text-primary);
      font-weight: 600;
      border-bottom-color: $primary-color;
    }
  }
}

.toc-list {
  flex: 1;
  overflow-y: auto;
  padding: 12px;
}

.toc-item {
  padding: 8px 12px;
  font-size: 13px;
  color: var(--text-secondary);
  cursor: pointer;
  border-radius: 6px;
  transition: all 0.2s ease;
  margin-bottom: 2px;

  &:hover {
    background-color: var(--hover-color);
    color: var(--text-primary);
  }

  &.active {
    background-color: var(--hover-color);
    color: $primary-color;
    font-weight: 500;
  }

  &.level-1 { padding-left: 12px; font-weight: 500; }
  &.level-2 { padding-left: 24px; }
  &.level-3 { padding-left: 36px; font-size: 12px; }
  &.level-4 { padding-left: 48px; font-size: 12px; }
}

.toc-empty {
  padding: 32px 16px;
  text-align: center;
  color: var(--text-muted);
  font-size: 13px;
}

// 中间内容区
.reader-main {
  flex: 1;
  overflow-y: auto;
  display: flex;
  justify-content: center;
}

.main-container {
  width: 100%;
  max-width: 720px;
  padding: 24px 40px 80px;
}

.main-toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 24px;

  .toolbar-right {
    display: flex;
    align-items: center;
    gap: 12px;
  }
}

.source-tag {
  margin-bottom: 16px;
}

.article-title {
  font-size: 32px;
  font-weight: 700;
  color: var(--text-primary);
  line-height: 1.3;
  margin-bottom: 16px;
}

.article-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: var(--text-muted);
  margin-bottom: 32px;
  padding-bottom: 24px;
  border-bottom: 1px solid var(--border-color);

  .separator {
    color: var(--border-color);
  }
}

.article-content {
  color: var(--text-primary);
  user-select: text;
  cursor: text;

  :deep(img) {
    max-width: 100%;
    height: auto;
    border-radius: 8px;
    margin: 16px 0;
  }

  :deep(a) {
    color: $primary-color;
  }

  :deep(blockquote) {
    border-left: 4px solid $primary-color;
    padding-left: 16px;
    margin: 16px 0;
    color: var(--text-secondary);
  }

  :deep(pre) {
    background-color: var(--bg-sidebar);
    padding: 16px;
    border-radius: 8px;
    overflow-x: auto;
  }

  :deep(h1, h2, h3, h4, h5, h6) {
    margin-top: 32px;
    margin-bottom: 16px;
    scroll-margin-top: 80px;
  }

  :deep(p) {
    margin: 16px 0;
  }

  // 划线标记样式
  :deep(.highlight-mark) {
    padding: 2px 0;
    border-radius: 2px;
    cursor: pointer;
    transition: all 0.2s;
    position: relative;

    &::after {
      content: '';
      position: absolute;
      left: 0;
      right: 0;
      bottom: -2px;
      height: 2px;
      background-color: currentColor;
      opacity: 0;
      transition: opacity 0.2s;
    }

    &:hover::after {
      opacity: 0.5;
    }

    &.highlight-yellow {
      background-color: #fef08a;
    }

    &.highlight-green {
      background-color: #bbf7d0;
    }

    &.highlight-blue {
      background-color: #bfdbfe;
    }

    &.highlight-pink {
      background-color: #fbcfe8;
    }

    &.highlight-purple {
      background-color: #ddd6fe;
    }

    &:hover {
      filter: brightness(0.95);
    }
  }
}

.article-footer {
  display: flex;
  gap: 12px;
  padding-top: 32px;
  margin-top: 32px;
  border-top: 1px solid var(--border-color);
}

.loading-state,
.error-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 0;
  color: var(--text-muted);

  p {
    margin: 16px 0;
  }
}

// 右侧信息栏
.reader-right {
  width: 280px;
  border-left: 1px solid var(--border-color);
  background-color: var(--bg-sidebar);
  display: flex;
  flex-direction: column;
  flex-shrink: 0;
  transition: width 0.3s ease;
}

.info-content {
  flex: 1;
  overflow-y: auto;
  padding: 16px 20px;
}

.info-section {
  margin-bottom: 24px;
}

.section-title {
  font-size: 11px;
  font-weight: 600;
  color: var(--text-muted);
  text-transform: uppercase;
  letter-spacing: 0.5px;
  margin-bottom: 12px;
}

.metadata-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.metadata-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;

  .meta-label {
    width: 80px;
    flex-shrink: 0;
    font-size: 13px;
    color: var(--text-muted);
  }

  .meta-value {
    font-size: 13px;
    color: var(--text-primary);
    word-break: break-word;
  }
}

.progress-info {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 8px;

  .el-progress {
    flex: 1;
  }

  .progress-text {
    font-size: 12px;
    color: var(--text-muted);
    min-width: 36px;
  }
}

// Notebook 面板
.notebook-content {
  flex: 1;
  overflow-y: auto;
  padding: 16px 20px;
}

.note-input-area {
  .el-textarea {
    margin-bottom: 8px;
  }

  .note-actions {
    display: flex;
    justify-content: flex-end;
  }
}

.highlight-count {
  font-weight: normal;
  color: var(--text-muted);
  margin-left: 4px;
}

.highlights-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.highlight-item {
  padding: 12px;
  border-radius: 8px;
  border-left: 3px solid;
  transition: all 0.2s;

  &.clickable {
    cursor: pointer;

    &:hover {
      transform: translateX(2px);
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    }
  }

  &.highlight-yellow {
    background-color: rgba(254, 240, 138, 0.3);
    border-left-color: #fef08a;
  }

  &.highlight-green {
    background-color: rgba(187, 247, 208, 0.3);
    border-left-color: #bbf7d0;
  }

  &.highlight-blue {
    background-color: rgba(191, 219, 254, 0.3);
    border-left-color: #bfdbfe;
  }

  &.highlight-pink {
    background-color: rgba(251, 207, 232, 0.3);
    border-left-color: #fbcfe8;
  }

  &.highlight-purple {
    background-color: rgba(221, 214, 254, 0.3);
    border-left-color: #ddd6fe;
  }

  .highlight-text {
    font-size: 13px;
    color: var(--text-primary);
    line-height: 1.5;
    margin-bottom: 8px;
  }

  .highlight-note {
    font-size: 12px;
    color: var(--text-secondary);
    font-style: italic;
    margin-bottom: 8px;
    padding-left: 8px;
    border-left: 2px solid var(--border-color);
  }

  .highlight-tags {
    display: flex;
    flex-wrap: wrap;
    gap: 4px;
    margin-bottom: 8px;
  }

  .highlight-footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .highlight-time {
    font-size: 11px;
    color: var(--text-muted);
  }

  .edit-hint {
    font-size: 11px;
    color: var(--text-muted);
    opacity: 0;
    transition: opacity 0.2s;
  }

  &:hover .edit-hint {
    opacity: 1;
  }
}

.highlights-empty {
  padding: 32px 16px;
  text-align: center;

  p {
    color: var(--text-muted);
    font-size: 13px;
    margin: 0;

    &.hint {
      font-size: 12px;
      margin-top: 8px;
    }
  }
}

// 切换按钮
.toggle-btn {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  width: 24px;
  height: 48px;
  border: 1px solid var(--border-color);
  background-color: var(--bg-sidebar);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--text-muted);
  transition: all 0.2s ease;
  z-index: 10;

  &:hover {
    color: var(--text-primary);
    background-color: var(--hover-color);
  }

  &.toggle-left {
    left: 0;
    border-left: none;
    border-radius: 0 8px 8px 0;
  }

  &.toggle-right {
    right: 0;
    border-right: none;
    border-radius: 8px 0 0 8px;
  }
}

// 阅读进度条
.reading-progress-bar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  background-color: transparent;
  z-index: 100;
}

.progress-fill {
  height: 100%;
  background-color: $primary-color;
  transition: width 0.1s ease;
}

// 隐藏侧栏时的样式
.reader-page.hide-left .reader-main {
  margin-left: 0;
}

.reader-page.hide-right .reader-main {
  margin-right: 0;
}

// 响应式
@media (max-width: 1200px) {
  .reader-left {
    width: 220px;
  }
  .reader-right {
    width: 240px;
  }
}

@media (max-width: 900px) {
  .reader-left,
  .reader-right {
    position: fixed;
    top: 0;
    bottom: 0;
    z-index: 20;
  }

  .reader-left {
    left: 0;
  }

  .reader-right {
    right: 0;
  }
}
</style>
