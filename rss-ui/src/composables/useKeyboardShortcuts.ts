import { onMounted, onUnmounted } from 'vue'
import { useArticleStore } from '@/stores/article'
import { useSettingsStore } from '@/stores/settings'

export interface ShortcutHandlers {
  onNextArticle?: () => void
  onPrevArticle?: () => void
  onToggleRead?: () => void
  onToggleFavorite?: () => void
  onOpenOriginal?: () => void
  onToggleFullscreen?: () => void
  onRefresh?: () => void
}

export function useKeyboardShortcuts(handlers: ShortcutHandlers = {}) {
  const articleStore = useArticleStore()
  const settingsStore = useSettingsStore()

  function handleKeydown(e: KeyboardEvent) {
    // 忽略在输入框中的按键
    const target = e.target as HTMLElement
    if (
      target.tagName === 'INPUT' ||
      target.tagName === 'TEXTAREA' ||
      target.isContentEditable
    ) {
      return
    }

    // 不处理带修饰键的快捷键（除了特定组合）
    if (e.ctrlKey || e.metaKey || e.altKey) {
      return
    }

    switch (e.key.toLowerCase()) {
      case 'j': // 下一篇文章
        e.preventDefault()
        handlers.onNextArticle?.()
        break

      case 'k': // 上一篇文章
        e.preventDefault()
        handlers.onPrevArticle?.()
        break

      case ' ': // 标记已读/未读
        e.preventDefault()
        handlers.onToggleRead?.()
        break

      case 's': // 收藏/取消收藏
        e.preventDefault()
        handlers.onToggleFavorite?.()
        break

      case 'o': // 打开原文
        e.preventDefault()
        handlers.onOpenOriginal?.()
        break

      case 'f': // 全屏阅读模式
        e.preventDefault()
        handlers.onToggleFullscreen?.() || settingsStore.toggleFullscreen()
        break

      case 'r': // 刷新
        e.preventDefault()
        handlers.onRefresh?.()
        break

      case 'escape': // 退出全屏
        if (settingsStore.app.fullscreenReading) {
          e.preventDefault()
          settingsStore.setFullscreen(false)
        }
        break
    }
  }

  onMounted(() => {
    window.addEventListener('keydown', handleKeydown)
  })

  onUnmounted(() => {
    window.removeEventListener('keydown', handleKeydown)
  })

  return {
    // 快捷键说明列表
    shortcuts: [
      { key: 'J', description: '下一篇文章' },
      { key: 'K', description: '上一篇文章' },
      { key: 'Space', description: '标记已读/未读' },
      { key: 'S', description: '收藏/取消收藏' },
      { key: 'O', description: '打开原文' },
      { key: 'F', description: '全屏阅读模式' },
      { key: 'R', description: '刷新订阅' },
      { key: 'Esc', description: '退出全屏' }
    ]
  }
}
