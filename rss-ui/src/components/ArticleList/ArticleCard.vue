<template>
  <div
    class="article-card"
    :class="{ unread: article.isRead === 0, active }"
    @click="handleClick"
  >
    <div class="card-main">
      <div class="card-header">
        <span class="feed-name">{{ article.feedName }}</span>
        <span class="publish-time">{{ formatTime(article.pubDate) }}</span>
      </div>
      <h3 class="card-title">{{ article.title }}</h3>
      <p class="card-summary" v-if="article.description">
        {{ truncateSummary(article.description) }}
      </p>
      <div class="card-footer">
        <span class="word-count">{{ getWordCount(article.description) }} 字</span>
        <span class="read-time">{{ getReadTime(article.description) }} min</span>
      </div>
    </div>
    <div class="card-thumbnail" v-if="article.enclosureUrl && isImageType">
      <img :src="article.enclosureUrl" :alt="article.title" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import type { Article } from '@/types'

const props = defineProps<{
  article: Article
  active?: boolean
}>()

const emit = defineEmits<{
  (e: 'click'): void
}>()

const router = useRouter()

const isImageType = computed(() => {
  const type = props.article.enclosureType || ''
  return type.startsWith('image/')
})

function handleClick() {
  // 跳转到阅读页面
  router.push(`/reader/${props.article.id}`)
  emit('click')
}

function formatTime(dateStr: string): string {
  const date = new Date(dateStr)
  const now = new Date()
  const diff = now.getTime() - date.getTime()

  const minutes = Math.floor(diff / 60000)
  const hours = Math.floor(diff / 3600000)
  const days = Math.floor(diff / 86400000)

  if (minutes < 1) return '刚刚'
  if (minutes < 60) return `${minutes}分钟前`
  if (hours < 24) return `${hours}小时前`
  if (days < 7) return `${days}天前`

  return date.toLocaleDateString('zh-CN', {
    month: 'short',
    day: 'numeric'
  })
}

function truncateSummary(text: string): string {
  const maxLength = 120
  const cleaned = text.replace(/<[^>]*>/g, '').trim()
  if (cleaned.length <= maxLength) return cleaned
  return cleaned.slice(0, maxLength) + '...'
}

function getWordCount(text: string): number {
  if (!text) return 0
  const cleaned = text.replace(/<[^>]*>/g, '').trim()
  return cleaned.length
}

function getReadTime(text: string): number {
  const count = getWordCount(text)
  return Math.max(1, Math.ceil(count / 300))
}
</script>

<style lang="scss" scoped>
.article-card {
  display: flex;
  gap: 16px;
  padding: 16px;
  border-radius: $border-radius-base;
  cursor: pointer;
  transition: $transition-base;
  border: 1px solid transparent;

  &:hover {
    background-color: var(--hover-color);
  }

  &.active {
    background-color: var(--hover-color);
    border-color: $primary-color;
  }

  &.unread {
    .card-title {
      font-weight: 600;
    }

    &::before {
      content: '';
      position: absolute;
      left: 4px;
      top: 50%;
      transform: translateY(-50%);
      width: 6px;
      height: 6px;
      background-color: $primary-color;
      border-radius: 50%;
    }
  }

  position: relative;
  margin-bottom: 8px;
}

.card-main {
  flex: 1;
  min-width: 0;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 8px;
}

.feed-name {
  font-size: 12px;
  color: $primary-color;
  font-weight: 500;
}

.publish-time {
  font-size: 12px;
  color: var(--text-muted);
}

.card-title {
  font-size: 15px;
  font-weight: 500;
  color: var(--text-primary);
  line-height: 1.4;
  margin-bottom: 8px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-summary {
  font-size: 13px;
  color: var(--text-secondary);
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  margin-bottom: 8px;
}

.card-footer {
  display: flex;
  gap: 12px;
  font-size: 12px;
  color: var(--text-muted);
}

.card-thumbnail {
  flex-shrink: 0;
  width: 100px;
  height: 70px;
  border-radius: $border-radius-base;
  overflow: hidden;

  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
}
</style>
