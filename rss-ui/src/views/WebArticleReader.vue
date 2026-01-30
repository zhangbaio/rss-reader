<template>
  <div class="reader-page" :class="{ 'hide-left': !showLeft, 'hide-right': !showRight }">
    <!-- 左侧目录栏 -->
    <aside class="reader-left" v-show="showLeft">
      <div class="panel-header">
        <h3>Contents</h3>
        <button class="close-panel-btn" @click="showLeft = false">
          <svg viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2">
            <line x1="18" y1="6" x2="6" y2="18"/>
            <line x1="6" y1="6" x2="18" y2="18"/>
          </svg>
        </button>
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
          No table of contents
        </div>
      </nav>
    </aside>

    <!-- 左侧展开按钮 -->
    <button class="toggle-btn toggle-left" v-show="!showLeft" @click="showLeft = true">
      <svg viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2">
        <polyline points="13 17 18 12 13 7"/>
        <polyline points="6 17 11 12 6 7"/>
      </svg>
    </button>

    <!-- 中间内容区 -->
    <main class="reader-main" ref="mainRef" @scroll="handleScroll" @mouseup="handleTextSelection">
      <div class="main-container">
        <!-- 顶部工具栏 -->
        <div class="main-toolbar">
          <button class="back-btn" @click="goBack">
            <svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="2">
              <line x1="19" y1="12" x2="5" y2="12"/>
              <polyline points="12 19 5 12 12 5"/>
            </svg>
            Back
          </button>
          <div class="toolbar-right">
            <div class="category-badge" :class="article?.category">
              {{ article?.category?.toUpperCase() }}
            </div>
          </div>
        </div>

        <!-- 文章来源标签 -->
        <div class="source-tag" v-if="article">
          <span class="domain-tag">{{ article.domain }}</span>
        </div>

        <!-- 文章标题 -->
        <h1 class="article-title" v-if="article">{{ article.title }}</h1>

        <!-- 文章元信息 -->
        <div class="article-meta" v-if="article">
          <span v-if="article.author">{{ article.author }}</span>
          <span class="separator" v-if="article.author">·</span>
          <span>{{ article.readingTime }} min read</span>
          <span class="separator">·</span>
          <span>{{ formatDate(article.savedTime) }}</span>
        </div>

        <!-- 加载状态 -->
        <div v-if="loading" class="loading-state">
          <div class="spinner"></div>
          <p>Loading article...</p>
        </div>

        <!-- 错误状态 -->
        <div v-else-if="error" class="error-state">
          <p>{{ error }}</p>
          <button @click="loadArticle">Retry</button>
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
          <button class="footer-btn" @click="toggleReadStatus">
            <svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="2">
              <path v-if="article.isRead === 0" d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/>
              <circle v-if="article.isRead === 0" cx="12" cy="12" r="3"/>
              <path v-else d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24"/>
              <line v-if="article.isRead === 1" x1="1" y1="1" x2="23" y2="23"/>
            </svg>
            {{ article.isRead === 1 ? 'Mark Unread' : 'Mark Read' }}
          </button>
          <button class="footer-btn" @click="toggleFavorite">
            <svg viewBox="0 0 24 24" width="18" height="18" :fill="article.isFavorite === 1 ? 'currentColor' : 'none'" stroke="currentColor" stroke-width="2">
              <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"/>
            </svg>
            {{ article.isFavorite === 1 ? 'Unfavorite' : 'Favorite' }}
          </button>
          <button class="footer-btn" @click="openOriginal">
            <svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M18 13v6a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2h6"/>
              <polyline points="15 3 21 3 21 9"/>
              <line x1="10" y1="14" x2="21" y2="3"/>
            </svg>
            Open Original
          </button>
        </div>
      </div>
    </main>

    <!-- 右侧展开按钮 -->
    <button class="toggle-btn toggle-right" v-show="!showRight" @click="showRight = true">
      <svg viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2">
        <polyline points="11 17 6 12 11 7"/>
        <polyline points="18 17 13 12 18 7"/>
      </svg>
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
        <button class="close-panel-btn" @click="showRight = false">
          <svg viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2">
            <line x1="18" y1="6" x2="6" y2="18"/>
            <line x1="6" y1="6" x2="18" y2="18"/>
          </svg>
        </button>
      </div>

      <!-- Info 面板 -->
      <div class="info-content" v-if="article && activeTab === 'info'">
        <div class="info-section">
          <h4 class="section-title">METADATA</h4>
          <div class="metadata-list">
            <div class="metadata-item">
              <span class="meta-label">Type</span>
              <span class="meta-value">Web Article</span>
            </div>
            <div class="metadata-item">
              <span class="meta-label">Domain</span>
              <span class="meta-value">{{ article.domain }}</span>
            </div>
            <div class="metadata-item" v-if="article.author">
              <span class="meta-label">Author</span>
              <span class="meta-value">{{ article.author }}</span>
            </div>
            <div class="metadata-item">
              <span class="meta-label">Saved</span>
              <span class="meta-value">{{ formatDate(article.savedTime) }}</span>
            </div>
            <div class="metadata-item">
              <span class="meta-label">Words</span>
              <span class="meta-value">{{ article.wordCount }}</span>
            </div>
            <div class="metadata-item">
              <span class="meta-label">Category</span>
              <span class="meta-value category-value" :class="article.category">
                {{ article.category }}
              </span>
            </div>
            <div class="metadata-item">
              <span class="meta-label">Progress</span>
              <div class="progress-info">
                <div class="progress-bar">
                  <div class="progress-fill" :style="{ width: readProgress + '%' }"></div>
                </div>
                <span class="progress-text">{{ readProgress }}%</span>
              </div>
            </div>
            <div class="metadata-item">
              <span class="meta-label">Status</span>
              <span class="meta-value">
                <span class="status-tag" :class="{ read: article.isRead === 1 }">
                  {{ article.isRead === 1 ? 'Read' : 'Unread' }}
                </span>
                <span class="status-tag favorite" v-if="article.isFavorite === 1">
                  Favorite
                </span>
              </span>
            </div>
          </div>
        </div>

        <!-- Category Actions -->
        <div class="info-section">
          <h4 class="section-title">MOVE TO</h4>
          <div class="category-actions">
            <button
              class="category-btn"
              :class="{ active: article.category === 'inbox' }"
              @click="updateCategory('inbox')"
            >
              <svg viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2">
                <polyline points="22 12 16 12 14 15 10 15 8 12 2 12"/>
                <path d="M5.45 5.11L2 12v6a2 2 0 0 0 2 2h16a2 2 0 0 0 2-2v-6l-3.45-6.89A2 2 0 0 0 16.76 4H7.24a2 2 0 0 0-1.79 1.11z"/>
              </svg>
              Inbox
            </button>
            <button
              class="category-btn"
              :class="{ active: article.category === 'later' }"
              @click="updateCategory('later')"
            >
              <svg viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2">
                <circle cx="12" cy="12" r="10"/>
                <polyline points="12 6 12 12 16 14"/>
              </svg>
              Later
            </button>
            <button
              class="category-btn"
              :class="{ active: article.category === 'archive' }"
              @click="updateCategory('archive')"
            >
              <svg viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2">
                <polyline points="21 8 21 21 3 21 3 8"/>
                <rect x="1" y="3" width="22" height="5"/>
                <line x1="10" y1="12" x2="14" y2="12"/>
              </svg>
              Archive
            </button>
          </div>
        </div>
      </div>

      <!-- Notebook 面板 -->
      <div class="notebook-content" v-if="article && activeTab === 'notebook'">
        <!-- 文档笔记 -->
        <div class="info-section">
          <h4 class="section-title">DOCUMENT NOTE</h4>
          <div class="note-input-area">
            <textarea
              v-model="documentNote"
              rows="4"
              placeholder="Write your notes about this article..."
              @blur="saveDocumentNote"
            ></textarea>
            <div class="note-actions" v-if="documentNote !== savedNote">
              <button class="save-note-btn" @click="saveDocumentNote" :disabled="noteSaving">
                {{ noteSaving ? 'Saving...' : 'Save' }}
              </button>
            </div>
          </div>
        </div>

        <!-- 划线列表 -->
        <div class="info-section">
          <h4 class="section-title">
            HIGHLIGHTS
            <span class="highlight-count">({{ highlights.length }})</span>
          </h4>
          <div class="highlights-list" v-if="highlights.length > 0">
            <div
              v-for="hl in highlights"
              :key="hl.id"
              class="highlight-item clickable"
              :class="[`highlight-${hl.color}`]"
              @click="openEditDialog(hl)"
            >
              <div class="highlight-text">"{{ hl.selectedText }}"</div>
              <div class="highlight-note" v-if="hl.note">{{ hl.note }}</div>
              <div class="highlight-tags" v-if="hl.tags && hl.tags.length > 0">
                <span
                  v-for="tag in hl.tags"
                  :key="tag.id"
                  class="tag-badge"
                >
                  {{ tag.name }}
                </span>
              </div>
              <div class="highlight-footer">
                <span class="highlight-time">{{ formatTime(hl.createTime) }}</span>
                <span class="edit-hint">Click to edit</span>
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
      :all-tags="allTags"
      @save="handleSaveHighlight"
      @cancel="hideHighlightToolbar"
    />

    <!-- 划线编辑对话框 -->
    <HighlightEditDialog
      v-model:visible="showEditDialog"
      :highlight="editingHighlight"
      :all-tags="allTags"
      @save="handleUpdateHighlight"
      @delete="handleDeleteHighlight"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted, onUnmounted, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useWebArticleStore } from '@/stores/webArticle'
import { useHighlightStore } from '@/stores/highlight'
import { useNoteStore } from '@/stores/note'
import { useSettingsStore } from '@/stores/settings'
import HighlightToolbar from '@/components/Highlight/HighlightToolbar.vue'
import HighlightEditDialog from '@/components/Highlight/HighlightEditDialog.vue'
import type { WebArticle, CreateHighlightParams, Highlight } from '@/types'

interface HeadingItem {
  text: string
  level: number
  element?: HTMLElement
}

const route = useRoute()
const router = useRouter()
const webArticleStore = useWebArticleStore()
const highlightStore = useHighlightStore()
const noteStore = useNoteStore()
const settingsStore = useSettingsStore()

const mainRef = ref<HTMLElement | null>(null)
const contentRef = ref<HTMLElement | null>(null)

// 面板显示状态
const showLeft = ref(true)
const showRight = ref(true)
const activeTab = ref<'info' | 'notebook'>('info')

// 文章数据
const article = ref<WebArticle | null>(null)
const loading = ref(false)
const error = ref<string | null>(null)

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
const noteSaving = ref(false)

// 编辑划线对话框
const showEditDialog = ref(false)
const editingHighlight = ref<Highlight | null>(null)

// 划线数据
const highlights = computed(() => highlightStore.highlights)
const allTags = computed(() => highlightStore.tags)

// 带划线标记的内容
const highlightedContent = computed(() => {
  const content = article.value?.content || ''
  if (!content || highlights.value.length === 0) {
    return content
  }

  const sortedHighlights = [...highlights.value].sort(
    (a, b) => b.startOffset - a.startOffset
  )

  let result = content

  for (const hl of sortedHighlights) {
    const text = hl.selectedText
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

// 加载文章
async function loadArticle() {
  const id = Number(route.params.id)
  if (!id) return

  loading.value = true
  error.value = null

  try {
    article.value = await webArticleStore.fetchArticleById(id)

    // 自动标记为已读
    if (article.value && article.value.isRead === 0) {
      await webArticleStore.markArticleAsRead(id)
      article.value.isRead = 1
    }

    // 并行加载划线、笔记、标签
    const promises: Promise<any>[] = []

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
    error.value = e.message || 'Failed to load article'
  } finally {
    loading.value = false
  }
}

// 处理文本选择
function handleTextSelection(e: MouseEvent) {
  const target = e.target as HTMLElement

  const toolbar = document.querySelector('.highlight-toolbar')
  if (toolbar && toolbar.contains(target)) {
    return
  }

  if (target.classList.contains('highlight-mark')) {
    return
  }

  setTimeout(() => {
    const selection = window.getSelection()

    if (!selection || selection.isCollapsed || !selection.toString().trim()) {
      if (showHighlightToolbar.value) {
        hideHighlightToolbar()
      }
      return
    }

    const text = selection.toString().trim()
    if (text.length < 2) return

    const articleContent = contentRef.value
    if (!articleContent) {
      return
    }

    const anchorNode = selection.anchorNode
    if (!anchorNode || !articleContent.contains(anchorNode)) {
      return
    }

    selectedText.value = text
    selectionRange.value = selection.getRangeAt(0).cloneRange()

    const rect = selection.getRangeAt(0).getBoundingClientRect()
    toolbarPosition.value = {
      x: rect.left + rect.width / 2,
      y: rect.top - 10
    }

    showHighlightToolbar.value = true
  }, 10)
}

function hideHighlightToolbar() {
  showHighlightToolbar.value = false
  selectedText.value = ''
  selectionRange.value = null
  window.getSelection()?.removeAllRanges()
}

async function handleSaveHighlight(data: { color: string; note: string; tags: string[] }) {
  if (!article.value || !selectionRange.value) return

  const range = selectionRange.value

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

function handleContentClick(e: MouseEvent) {
  const target = e.target as HTMLElement

  if (target.classList.contains('highlight-mark')) {
    e.preventDefault()
    e.stopPropagation()

    const highlightId = target.dataset.highlightId
    if (highlightId) {
      const highlight = highlights.value.find(h => h.id === Number(highlightId))
      if (highlight) {
        openEditDialog(highlight)
      }
    }
  }
}

function openEditDialog(highlight: Highlight) {
  editingHighlight.value = highlight
  showEditDialog.value = true
}

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

async function handleDeleteHighlight(id: number) {
  try {
    await highlightStore.deleteHighlight(id)
  } catch (e) {
    console.error('Failed to delete highlight:', e)
  }
}

async function saveDocumentNote() {
  if (!article.value || documentNote.value === savedNote.value) return

  noteSaving.value = true
  try {
    await noteStore.saveNote(article.value.id, documentNote.value)
    savedNote.value = documentNote.value
  } catch (e) {
    console.error('Failed to save note:', e)
  } finally {
    noteSaving.value = false
  }
}

function formatTime(dateStr: string): string {
  const date = new Date(dateStr)
  const now = new Date()
  const diff = now.getTime() - date.getTime()

  if (diff < 60000) return 'just now'
  if (diff < 3600000) return `${Math.floor(diff / 60000)}m ago`
  if (diff < 86400000) return `${Math.floor(diff / 3600000)}h ago`
  if (diff < 604800000) return `${Math.floor(diff / 86400000)}d ago`

  return date.toLocaleDateString('en-US', { month: 'short', day: 'numeric' })
}

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

function scrollToHeading(index: number) {
  const heading = tableOfContents.value[index]
  if (heading?.element) {
    heading.element.scrollIntoView({ behavior: 'smooth', block: 'start' })
    activeHeading.value = index
  }
}

function handleScroll() {
  if (!mainRef.value) return

  const { scrollTop, scrollHeight, clientHeight } = mainRef.value
  const maxScroll = scrollHeight - clientHeight

  if (maxScroll > 0) {
    readProgress.value = Math.round((scrollTop / maxScroll) * 100)
  }

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

  // Update progress in store
  if (article.value && readProgress.value > (article.value.progress || 0)) {
    webArticleStore.updateArticleProgress(article.value.id, readProgress.value)
  }
}

watch(() => route.params.id, () => {
  loadArticle()
}, { immediate: true })

function formatDate(dateStr: string): string {
  return new Date(dateStr).toLocaleDateString('en-US', {
    year: 'numeric',
    month: 'short',
    day: 'numeric'
  })
}

function goBack() {
  router.push('/articles')
}

async function toggleReadStatus() {
  if (!article.value) return
  if (article.value.isRead === 1) {
    await webArticleStore.markArticleAsUnread(article.value.id)
    article.value.isRead = 0
  } else {
    await webArticleStore.markArticleAsRead(article.value.id)
    article.value.isRead = 1
  }
}

async function toggleFavorite() {
  if (!article.value) return
  await webArticleStore.toggleArticleFavorite(article.value.id)
  article.value.isFavorite = article.value.isFavorite === 1 ? 0 : 1
}

async function updateCategory(category: 'inbox' | 'later' | 'archive') {
  if (!article.value || article.value.category === category) return
  await webArticleStore.updateArticleCategory(article.value.id, category)
  article.value.category = category
}

function openOriginal() {
  if (article.value?.url) {
    window.open(article.value.url, '_blank')
  }
}

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

.reader-left {
  width: 260px;
  border-right: 1px solid var(--border-color);
  background-color: var(--bg-sidebar);
  display: flex;
  flex-direction: column;
  flex-shrink: 0;
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

.close-panel-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 28px;
  height: 28px;
  border: none;
  background: none;
  color: var(--text-muted);
  cursor: pointer;
  border-radius: 6px;

  &:hover {
    background-color: var(--hover-color);
    color: var(--text-primary);
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

    &:hover {
      color: var(--text-secondary);
    }

    &.active {
      color: var(--text-primary);
      font-weight: 600;
      border-bottom-color: var(--primary-color);
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
  margin-bottom: 2px;

  &:hover {
    background-color: var(--hover-color);
    color: var(--text-primary);
  }

  &.active {
    background-color: var(--hover-color);
    color: var(--primary-color);
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
}

.back-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  border: none;
  background: none;
  color: var(--text-secondary);
  font-size: 14px;
  cursor: pointer;
  border-radius: 6px;

  &:hover {
    background-color: var(--hover-color);
    color: var(--text-primary);
  }
}

.category-badge {
  padding: 4px 10px;
  font-size: 11px;
  font-weight: 600;
  text-transform: uppercase;
  border-radius: 4px;

  &.inbox {
    background-color: #e3f2fd;
    color: #1976d2;
  }
  &.later {
    background-color: #fff3e0;
    color: #f57c00;
  }
  &.archive {
    background-color: #e8f5e9;
    color: #388e3c;
  }
}

.source-tag {
  margin-bottom: 16px;

  .domain-tag {
    display: inline-block;
    padding: 4px 10px;
    background-color: var(--bg-secondary);
    color: var(--text-secondary);
    font-size: 12px;
    border-radius: 4px;
  }
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
    color: var(--primary-color);
  }

  :deep(blockquote) {
    border-left: 4px solid var(--primary-color);
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

  :deep(.highlight-mark) {
    padding: 2px 0;
    border-radius: 2px;
    cursor: pointer;

    &.highlight-yellow { background-color: #fef08a; }
    &.highlight-green { background-color: #bbf7d0; }
    &.highlight-blue { background-color: #bfdbfe; }
    &.highlight-pink { background-color: #fbcfe8; }
    &.highlight-purple { background-color: #ddd6fe; }

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

.footer-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 16px;
  border: 1px solid var(--border-color);
  background: var(--bg-primary);
  color: var(--text-secondary);
  font-size: 14px;
  cursor: pointer;
  border-radius: 8px;

  &:hover {
    border-color: var(--text-muted);
    color: var(--text-primary);
  }
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

  button {
    padding: 10px 20px;
    border: 1px solid var(--border-color);
    background: var(--bg-primary);
    color: var(--text-secondary);
    cursor: pointer;
    border-radius: 8px;

    &:hover {
      border-color: var(--primary-color);
      color: var(--primary-color);
    }
  }
}

.spinner {
  width: 32px;
  height: 32px;
  border: 3px solid var(--border-color);
  border-top-color: var(--primary-color);
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.reader-right {
  width: 280px;
  border-left: 1px solid var(--border-color);
  background-color: var(--bg-sidebar);
  display: flex;
  flex-direction: column;
  flex-shrink: 0;
}

.info-content,
.notebook-content {
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
    width: 70px;
    flex-shrink: 0;
    font-size: 13px;
    color: var(--text-muted);
  }

  .meta-value {
    font-size: 13px;
    color: var(--text-primary);
    word-break: break-word;

    &.category-value {
      text-transform: capitalize;
      font-weight: 500;

      &.inbox { color: #1976d2; }
      &.later { color: #f57c00; }
      &.archive { color: #388e3c; }
    }
  }
}

.progress-info {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 8px;

  .progress-bar {
    flex: 1;
    height: 4px;
    background-color: var(--border-color);
    border-radius: 2px;
    overflow: hidden;

    .progress-fill {
      height: 100%;
      background-color: var(--primary-color);
    }
  }

  .progress-text {
    font-size: 12px;
    color: var(--text-muted);
    min-width: 36px;
  }
}

.status-tag {
  display: inline-block;
  padding: 2px 8px;
  font-size: 11px;
  border-radius: 4px;
  background-color: #e8f5e9;
  color: #388e3c;

  &.read {
    background-color: var(--bg-secondary);
    color: var(--text-muted);
  }

  &.favorite {
    background-color: #fff3e0;
    color: #f57c00;
    margin-left: 4px;
  }
}

.category-actions {
  display: flex;
  gap: 8px;
}

.category-btn {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  padding: 12px 8px;
  border: 1px solid var(--border-color);
  background: var(--bg-primary);
  color: var(--text-secondary);
  font-size: 12px;
  cursor: pointer;
  border-radius: 8px;

  &:hover {
    border-color: var(--text-muted);
  }

  &.active {
    border-color: var(--primary-color);
    color: var(--primary-color);
    background-color: rgba(var(--primary-color-rgb), 0.05);
  }
}

.note-input-area {
  textarea {
    width: 100%;
    padding: 12px;
    border: 1px solid var(--border-color);
    border-radius: 8px;
    font-size: 14px;
    resize: vertical;
    font-family: inherit;
    color: var(--text-primary);
    background: var(--bg-primary);

    &:focus {
      outline: none;
      border-color: var(--primary-color);
    }
  }

  .note-actions {
    display: flex;
    justify-content: flex-end;
    margin-top: 8px;
  }

  .save-note-btn {
    padding: 8px 16px;
    border: none;
    background-color: var(--primary-color);
    color: white;
    font-size: 13px;
    cursor: pointer;
    border-radius: 6px;

    &:disabled {
      opacity: 0.6;
      cursor: not-allowed;
    }
  }
}

.highlight-count {
  font-weight: normal;
  color: var(--text-muted);
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

  .tag-badge {
    padding: 2px 8px;
    background-color: var(--bg-secondary);
    color: var(--text-secondary);
    font-size: 11px;
    border-radius: 4px;
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

.reading-progress-bar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  background-color: transparent;
  z-index: 100;

  .progress-fill {
    height: 100%;
    background-color: var(--primary-color);
    transition: width 0.1s ease;
  }
}

.reader-page.hide-left .reader-main {
  margin-left: 0;
}

.reader-page.hide-right .reader-main {
  margin-right: 0;
}
</style>
