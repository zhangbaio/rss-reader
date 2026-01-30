import { defineStore } from 'pinia'
import { ref, watch } from 'vue'

export interface ReadingSettings {
  fontSize: number      // 字体大小 14-24
  lineHeight: number    // 行高 1.5-2.5
  fontFamily: string    // 字体
  contentWidth: number  // 内容宽度 600-1000
}

export interface AppSettings {
  theme: 'light' | 'dark' | 'system'
  sidebarCollapsed: boolean
  fullscreenReading: boolean
}

const DEFAULT_READING_SETTINGS: ReadingSettings = {
  fontSize: 16,
  lineHeight: 1.8,
  fontFamily: 'system',
  contentWidth: 800
}

const DEFAULT_APP_SETTINGS: AppSettings = {
  theme: 'system',
  sidebarCollapsed: false,
  fullscreenReading: false
}

const FONT_OPTIONS = [
  { value: 'system', label: '系统默认' },
  { value: 'serif', label: '衬线字体' },
  { value: 'sans-serif', label: '无衬线字体' },
  { value: 'monospace', label: '等宽字体' }
]

export const useSettingsStore = defineStore('settings', () => {
  // 从localStorage加载设置
  const loadSettings = <T>(key: string, defaults: T): T => {
    try {
      const saved = localStorage.getItem(key)
      if (saved) {
        return { ...defaults, ...JSON.parse(saved) }
      }
    } catch (e) {
      console.error('Failed to load settings:', e)
    }
    return defaults
  }

  const reading = ref<ReadingSettings>(loadSettings('rss-reading-settings', DEFAULT_READING_SETTINGS))
  const app = ref<AppSettings>(loadSettings('rss-app-settings', DEFAULT_APP_SETTINGS))

  // 阅读进度缓存 { articleId: scrollPosition }
  const readingProgress = ref<Record<number, number>>(
    loadSettings('rss-reading-progress', {})
  )

  // 监听变化自动保存
  watch(reading, (val) => {
    localStorage.setItem('rss-reading-settings', JSON.stringify(val))
  }, { deep: true })

  watch(app, (val) => {
    localStorage.setItem('rss-app-settings', JSON.stringify(val))
  }, { deep: true })

  watch(readingProgress, (val) => {
    // 只保留最近100篇文章的进度
    const entries = Object.entries(val)
    if (entries.length > 100) {
      const trimmed = Object.fromEntries(entries.slice(-100))
      localStorage.setItem('rss-reading-progress', JSON.stringify(trimmed))
    } else {
      localStorage.setItem('rss-reading-progress', JSON.stringify(val))
    }
  }, { deep: true })

  // 阅读设置方法
  function updateReadingSettings(settings: Partial<ReadingSettings>) {
    reading.value = { ...reading.value, ...settings }
  }

  function resetReadingSettings() {
    reading.value = { ...DEFAULT_READING_SETTINGS }
  }

  // 应用设置方法
  function setTheme(theme: AppSettings['theme']) {
    app.value.theme = theme
  }

  function toggleSidebar() {
    app.value.sidebarCollapsed = !app.value.sidebarCollapsed
  }

  function toggleFullscreen() {
    app.value.fullscreenReading = !app.value.fullscreenReading
  }

  function setFullscreen(value: boolean) {
    app.value.fullscreenReading = value
  }

  // 阅读进度方法
  function saveProgress(articleId: number, position: number) {
    readingProgress.value[articleId] = position
  }

  function getProgress(articleId: number): number {
    return readingProgress.value[articleId] || 0
  }

  function clearProgress(articleId: number) {
    delete readingProgress.value[articleId]
  }

  return {
    reading,
    app,
    readingProgress,
    fontOptions: FONT_OPTIONS,
    updateReadingSettings,
    resetReadingSettings,
    setTheme,
    toggleSidebar,
    toggleFullscreen,
    setFullscreen,
    saveProgress,
    getProgress,
    clearProgress
  }
})
