<template>
  <div
    class="article-row"
    :class="{ unseen: article.isRead === 0, seen: article.isRead === 1 }"
    @click="handleClick"
  >
    <!-- Thumbnail -->
    <div class="row-thumbnail" v-if="displayImageUrl">
      <img :src="displayImageUrl" :alt="article.title" @error="handleImageError" />
    </div>
    <div class="row-thumbnail favicon" v-else-if="faviconUrl">
      <img :src="faviconUrl" :alt="article.feedName" @error="handleFaviconError" />
    </div>
    <div class="row-thumbnail placeholder" v-else>
      <svg viewBox="0 0 24 24" width="24" height="24" fill="none" stroke="currentColor" stroke-width="1.5">
        <rect x="3" y="3" width="18" height="18" rx="2"/>
        <circle cx="8.5" cy="8.5" r="1.5"/>
        <path d="M21 15l-5-5L5 21"/>
      </svg>
    </div>

    <!-- Content -->
    <div class="row-content">
      <h3 class="row-title">{{ article.title }}</h3>
      <p class="row-description" v-if="article.description">
        {{ truncateSummary(article.description) }}
      </p>
      <div class="row-meta">
        <img
          v-if="faviconUrl"
          class="feed-favicon"
          :src="faviconUrl"
          :alt="article.feedName"
          @error="handleMetaFaviconError"
        />
        <span class="feed-icon" v-else>
          <svg viewBox="0 0 24 24" width="14" height="14" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M4 11a9 9 0 0 1 9 9"/>
            <path d="M4 4a16 16 0 0 1 16 16"/>
            <circle cx="5" cy="19" r="1"/>
          </svg>
        </span>
        <span class="feed-name">{{ article.feedName }}</span>
        <span class="separator" v-if="article.author">·</span>
        <span class="author" v-if="article.author">{{ article.author }}</span>
        <span class="separator">·</span>
        <span class="reading-time">{{ getReadTime(article.description) }} min</span>
      </div>
    </div>

    <!-- Status indicator -->
    <div class="row-status">
      <span class="status-dot" v-if="article.isRead === 0"></span>
      <span class="time-ago">{{ formatTime(article.pubDate) }}</span>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import type { Article } from '@/types'

const props = defineProps<{
  article: Article
}>()

const emit = defineEmits<{
  (e: 'click'): void
}>()

const router = useRouter()

const imageError = ref(false)
const faviconError = ref(false)
const metaFaviconError = ref(false)

// Get article image URL (from imageUrl field or enclosure if it's an image)
const displayImageUrl = computed(() => {
  if (imageError.value) return null

  // Priority 1: imageUrl field (extracted from RSS)
  if (props.article.imageUrl) {
    return props.article.imageUrl
  }

  // Priority 2: enclosure if it's an image type
  if (props.article.enclosureUrl && props.article.enclosureType?.startsWith('image/')) {
    return props.article.enclosureUrl
  }

  return null
})

// Get favicon URL
const faviconUrl = computed(() => {
  if (faviconError.value && metaFaviconError.value) return null

  // Priority 1: feedImageUrl from the feed
  if (props.article.feedImageUrl) {
    return props.article.feedImageUrl
  }

  // Priority 2: Google favicon service
  if (props.article.link) {
    try {
      const domain = new URL(props.article.link).hostname
      return `https://www.google.com/s2/favicons?domain=${domain}&sz=64`
    } catch {
      return null
    }
  }

  return null
})

function handleImageError() {
  imageError.value = true
}

function handleFaviconError() {
  faviconError.value = true
}

function handleMetaFaviconError(e: Event) {
  metaFaviconError.value = true
  const img = e.target as HTMLImageElement
  img.style.display = 'none'
}

function handleClick() {
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

  if (minutes < 1) return 'just now'
  if (minutes < 60) return `${minutes}m ago`
  if (hours < 24) return `${hours}h ago`
  if (days < 7) return `${days}d ago`

  return date.toLocaleDateString('en-US', {
    month: 'short',
    day: 'numeric'
  })
}

function truncateSummary(text: string): string {
  const maxLength = 150
  const cleaned = text.replace(/<[^>]*>/g, '').trim()
  if (cleaned.length <= maxLength) return cleaned
  return cleaned.slice(0, maxLength) + '...'
}

function getReadTime(text: string): number {
  if (!text) return 1
  const cleaned = text.replace(/<[^>]*>/g, '').trim()
  return Math.max(1, Math.ceil(cleaned.length / 300))
}
</script>

<style lang="scss" scoped>
.article-row {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  padding: 16px 24px;
  cursor: pointer;
  transition: background-color 0.15s;
  border-bottom: 1px solid var(--border-color);

  &:hover {
    background-color: var(--hover-color);
  }

  &:last-child {
    border-bottom: none;
  }

  &.unseen {
    .row-title {
      font-weight: 600;
      color: var(--text-primary);
    }
  }

  &.seen {
    .row-title {
      font-weight: 500;
      color: var(--text-secondary);
    }

    .row-description {
      color: var(--text-muted);
    }

    .row-thumbnail {
      opacity: 0.7;
    }
  }
}

.row-thumbnail {
  flex-shrink: 0;
  width: 100px;
  height: 70px;
  border-radius: 8px;
  overflow: hidden;
  background-color: var(--bg-secondary);

  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }

  &.favicon {
    display: flex;
    align-items: center;
    justify-content: center;

    img {
      width: 32px;
      height: 32px;
      object-fit: contain;
    }
  }

  &.placeholder {
    display: flex;
    align-items: center;
    justify-content: center;
    color: var(--text-muted);
  }
}

.row-content {
  flex: 1;
  min-width: 0;
}

.row-title {
  font-size: 15px;
  line-height: 1.4;
  margin: 0 0 6px 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.row-description {
  font-size: 13px;
  color: var(--text-secondary);
  line-height: 1.5;
  margin: 0 0 8px 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.row-meta {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: var(--text-muted);

  .feed-favicon {
    width: 14px;
    height: 14px;
    border-radius: 2px;
    object-fit: contain;
  }

  .feed-icon {
    display: flex;
    align-items: center;
  }

  .feed-name {
    color: var(--primary-color);
    font-weight: 500;
  }

  .separator {
    color: var(--border-color);
  }
}

.row-status {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 8px;
  flex-shrink: 0;
  min-width: 70px;
}

.status-dot {
  width: 8px;
  height: 8px;
  background-color: var(--primary-color);
  border-radius: 50%;
}

.time-ago {
  font-size: 12px;
  color: var(--text-muted);
  white-space: nowrap;
}
</style>
