import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { ReviewHighlight, ReviewAction, ReviewConfig, ArticleFrequency } from '@/types'
import * as reviewApi from '@/api/review'

export const useReviewStore = defineStore('review', () => {
  const reviewList = ref<ReviewHighlight[]>([])
  const currentIndex = ref(0)
  const loading = ref(false)
  const config = ref<ReviewConfig>({
    dailyCount: 10,
    recency: 50,
    includeNew: true,
    includeMastered: false
  })
  const stats = ref({
    todayReviewed: 0,
    totalHighlights: 0,
    masteredCount: 0,
    streak: 0
  })
  const articleFrequencies = ref<ArticleFrequency[]>([])

  const currentHighlight = computed(() => {
    return reviewList.value[currentIndex.value] || null
  })

  const progress = computed(() => ({
    current: currentIndex.value + 1,
    total: reviewList.value.length
  }))

  const hasNext = computed(() => currentIndex.value < reviewList.value.length - 1)
  const hasPrev = computed(() => currentIndex.value > 0)
  const isCompleted = computed(() => currentIndex.value >= reviewList.value.length && reviewList.value.length > 0)

  async function fetchDailyReview() {
    loading.value = true
    try {
      reviewList.value = await reviewApi.getDailyReviewList()
      currentIndex.value = 0
    } catch {
      // 如果API未实现，使用模拟数据
      reviewList.value = getMockReviewList()
      currentIndex.value = 0
    } finally {
      loading.value = false
    }
  }

  function getMockReviewList(): ReviewHighlight[] {
    return [
      {
        id: 1,
        articleId: 1,
        selectedText: '导出你的标注Hypothesis原生网站里是不支持导出功能。但它为开发者们提供了API接口，我们也可以动动小脑筋，写两行代码把标注导出来。我在上面的图片中的一篇少数派文章里拿到了作者提供的一个小插件：插件支持将最后一篇文章的高亮部分导出，而我自己希望可以在此基础上，可以将标注部分一起导出，并可以选择导出的文章。于是就在原作者的基础上对代码进行了一定修改。例如在我的这篇文章里的标注导出后是这样的：注意：导出的是markown格式，需要markdown编辑器来承接。不懂markdown是什么的同学也可以移步到下面这篇文章查看。',
        startOffset: 0,
        endOffset: 100,
        color: '#5c7cfa',
        note: '这是一个很好的导出工具',
        tags: [
          { id: 1, name: '工具', color: '#5c7cfa' },
          { id: 2, name: '效率', color: '#10b981' }
        ],
        createTime: '2024-01-15T10:30:00Z',
        article: {
          id: 1,
          title: 'Hypothesis | 网页荧光笔',
          link: 'https://zhuanlan.zhihu.com/p/123456',
          feedName: 'Hypothesis',
          feedImageUrl: ''
        },
        reviewCount: 3,
        lastReviewTime: '2024-01-14T08:00:00Z',
        masteryLevel: 1
      },
      {
        id: 2,
        articleId: 2,
        selectedText: 'Vue 3 引入了 Composition API，这是一种全新的组织组件逻辑的方式。它允许开发者更灵活地组织和复用代码，特别是在处理复杂组件时。',
        startOffset: 0,
        endOffset: 80,
        color: '#10b981',
        note: '',
        tags: [
          { id: 3, name: 'Vue', color: '#42b883' }
        ],
        createTime: '2024-01-14T15:20:00Z',
        article: {
          id: 2,
          title: 'Vue 3 Composition API 完全指南',
          link: 'https://vuejs.org/guide/introduction.html',
          feedName: 'Vue.js Blog',
          feedImageUrl: ''
        },
        reviewCount: 1,
        lastReviewTime: '2024-01-13T09:00:00Z',
        masteryLevel: 0
      },
      {
        id: 3,
        articleId: 3,
        selectedText: '间歇性重复学习是一种被科学证明有效的记忆技术。通过在特定时间间隔复习信息，可以显著提高长期记忆的保持率。',
        startOffset: 0,
        endOffset: 60,
        color: '#f59e0b',
        note: '这就是为什么Daily Review如此重要',
        tags: [
          { id: 4, name: '学习方法', color: '#f59e0b' }
        ],
        createTime: '2024-01-13T20:45:00Z',
        article: {
          id: 3,
          title: '如何高效学习：间歇性重复的科学',
          link: 'https://example.com/spaced-repetition',
          feedName: '学习科学',
          feedImageUrl: ''
        },
        reviewCount: 5,
        lastReviewTime: '2024-01-12T10:00:00Z',
        masteryLevel: 2
      },
      {
        id: 4,
        articleId: 4,
        selectedText: 'TypeScript 的类型系统不仅可以帮助我们在编译时捕获错误，还可以作为代码文档，提高代码的可读性和可维护性。',
        startOffset: 0,
        endOffset: 70,
        color: '#3b82f6',
        note: '',
        tags: [
          { id: 5, name: 'TypeScript', color: '#3178c6' }
        ],
        createTime: '2024-01-12T11:30:00Z',
        article: {
          id: 4,
          title: 'TypeScript 最佳实践',
          link: 'https://www.typescriptlang.org/',
          feedName: 'TypeScript Official',
          feedImageUrl: ''
        },
        reviewCount: 2,
        lastReviewTime: '2024-01-11T14:00:00Z',
        masteryLevel: 1
      },
      {
        id: 5,
        articleId: 5,
        selectedText: 'RSS 阅读器可以帮助我们从信息过载中解脱出来，让我们能够主动选择想要获取的信息，而不是被动接受算法推荐的内容。',
        startOffset: 0,
        endOffset: 65,
        color: '#ec4899',
        note: '回归RSS，掌控自己的信息流',
        tags: [
          { id: 6, name: 'RSS', color: '#ff6b35' },
          { id: 7, name: '信息管理', color: '#8b5cf6' }
        ],
        createTime: '2024-01-11T09:15:00Z',
        article: {
          id: 5,
          title: '为什么我们需要RSS阅读器',
          link: 'https://example.com/why-rss',
          feedName: '数字生活',
          feedImageUrl: ''
        },
        reviewCount: 4,
        lastReviewTime: '2024-01-10T16:00:00Z',
        masteryLevel: 1
      }
    ]
  }

  async function recordAction(action: ReviewAction) {
    const highlight = currentHighlight.value
    if (!highlight) return

    try {
      await reviewApi.recordReviewAction(highlight.id, action)
    } catch {
      // API可能未实现，继续执行
    }

    // 移动到下一个
    if (hasNext.value) {
      currentIndex.value++
    } else {
      // 标记为完成
      currentIndex.value = reviewList.value.length
    }

    stats.value.todayReviewed++
  }

  function goNext() {
    if (hasNext.value) {
      currentIndex.value++
    }
  }

  function goPrev() {
    if (hasPrev.value) {
      currentIndex.value--
    }
  }

  async function fetchStats() {
    try {
      stats.value = await reviewApi.getReviewStats()
    } catch {
      // 使用默认值
    }
  }

  async function fetchConfig() {
    try {
      config.value = await reviewApi.getReviewConfig()
    } catch {
      // 使用默认值
    }
  }

  async function updateConfig(newConfig: Partial<ReviewConfig>) {
    try {
      config.value = await reviewApi.updateReviewConfig(newConfig)
    } catch {
      // 本地更新
      config.value = { ...config.value, ...newConfig }
    }
  }

  function reset() {
    currentIndex.value = 0
  }

  async function fetchArticleFrequencies(keyword?: string) {
    try {
      articleFrequencies.value = await reviewApi.getArticlesWithFrequency(keyword)
    } catch {
      articleFrequencies.value = []
    }
  }

  async function updateArticleFrequency(articleId: number, frequency: number) {
    try {
      await reviewApi.updateArticleFrequency(articleId, frequency)
      // 更新本地状态
      const article = articleFrequencies.value.find(a => a.articleId === articleId)
      if (article) {
        article.frequency = frequency
      }
    } catch {
      // 忽略错误
    }
  }

  async function batchUpdateFrequency(articleIds: number[], frequency: number) {
    try {
      await reviewApi.batchUpdateArticleFrequency(articleIds, frequency)
      // 更新本地状态
      articleFrequencies.value.forEach(article => {
        if (articleIds.includes(article.articleId)) {
          article.frequency = frequency
        }
      })
    } catch {
      // 忽略错误
    }
  }

  return {
    reviewList,
    currentIndex,
    loading,
    config,
    stats,
    articleFrequencies,
    currentHighlight,
    progress,
    hasNext,
    hasPrev,
    isCompleted,
    fetchDailyReview,
    recordAction,
    goNext,
    goPrev,
    fetchStats,
    fetchConfig,
    updateConfig,
    reset,
    fetchArticleFrequencies,
    updateArticleFrequency,
    batchUpdateFrequency
  }
})
