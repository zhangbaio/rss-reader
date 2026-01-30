<template>
  <div class="home-view">
    <div class="home-content">
      <!-- Article List -->
      <div class="articles-section">
        <ArticleList
          title="All Articles"
          @select="handleSelectArticle"
          @hover="handleHoverArticle"
        />
      </div>

      <!-- Article Detail Panel -->
      <aside class="detail-panel" v-if="hoveredArticle">
        <div class="panel-tabs">
          <span class="tab active">Info</span>
        </div>
        <div class="panel-content">
          <div class="info-section">
            <h4 class="section-title">METADATA</h4>
            <div class="metadata-list">
              <div class="metadata-item">
                <span class="meta-label">Type</span>
                <span class="meta-value">Article</span>
              </div>
              <div class="metadata-item">
                <span class="meta-label">Source</span>
                <span class="meta-value">{{ hoveredArticle.feedName }}</span>
              </div>
              <div class="metadata-item">
                <span class="meta-label">Published</span>
                <span class="meta-value">{{ formatDate(hoveredArticle.pubDate) }}</span>
              </div>
              <div class="metadata-item" v-if="hoveredArticle.author">
                <span class="meta-label">Author</span>
                <span class="meta-value">{{ hoveredArticle.author }}</span>
              </div>
              <div class="metadata-item">
                <span class="meta-label">Status</span>
                <span class="meta-value">
                  <span class="status-badge" :class="hoveredArticle.isRead === 1 ? 'seen' : 'unseen'">
                    {{ hoveredArticle.isRead === 1 ? 'Seen' : 'Unseen' }}
                  </span>
                </span>
              </div>
              <div class="metadata-item">
                <span class="meta-label">Reading Time</span>
                <span class="meta-value">{{ getReadTime(hoveredArticle.description) }} min</span>
              </div>
              <div class="metadata-item" v-if="hoveredArticle.isFavorite === 1">
                <span class="meta-label">Favorite</span>
                <span class="meta-value">
                  <span class="status-badge favorite">Yes</span>
                </span>
              </div>
            </div>
          </div>

          <div class="info-section" v-if="hoveredArticle.description">
            <h4 class="section-title">PREVIEW</h4>
            <p class="preview-text">{{ truncateText(hoveredArticle.description, 200) }}</p>
          </div>

          <div class="info-section">
            <h4 class="section-title">ACTIONS</h4>
            <div class="action-buttons">
              <button class="action-btn primary" @click="openArticle(hoveredArticle)">
                <svg viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/>
                  <circle cx="12" cy="12" r="3"/>
                </svg>
                Read Article
              </button>
              <button class="action-btn" @click="toggleReadStatus(hoveredArticle)">
                <svg viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2">
                  <polyline points="20 6 9 17 4 12"/>
                </svg>
                {{ hoveredArticle.isRead === 1 ? 'Mark Unseen' : 'Mark Seen' }}
              </button>
              <a v-if="hoveredArticle.link" :href="hoveredArticle.link" target="_blank" class="action-btn">
                <svg viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M18 13v6a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2h6"/>
                  <polyline points="15 3 21 3 21 9"/>
                  <line x1="10" y1="14" x2="21" y2="3"/>
                </svg>
                Open Original
              </a>
            </div>
          </div>
        </div>
      </aside>

      <!-- Empty State for Detail Panel -->
      <aside class="detail-panel empty" v-else>
        <div class="empty-hint">
          <svg viewBox="0 0 24 24" width="32" height="32" fill="none" stroke="currentColor" stroke-width="1.5">
            <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/>
            <polyline points="14 2 14 8 20 8"/>
            <line x1="16" y1="13" x2="8" y2="13"/>
            <line x1="16" y1="17" x2="8" y2="17"/>
          </svg>
          <p>Hover over an article to see details</p>
        </div>
      </aside>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import ArticleList from '@/components/ArticleList/ArticleList.vue'
import type { Article } from '@/types'
import { useArticleStore } from '@/stores/article'

const router = useRouter()
const articleStore = useArticleStore()

const hoveredArticle = ref<Article | null>(null)

async function handleSelectArticle(article: Article) {
  if (article.isRead === 0) {
    await articleStore.markAsRead(article.id)
  }
}

function handleHoverArticle(article: Article | null) {
  hoveredArticle.value = article
}

function openArticle(article: Article) {
  router.push(`/reader/${article.id}`)
}

async function toggleReadStatus(article: Article) {
  if (article.isRead === 1) {
    await articleStore.markAsUnread(article.id)
  } else {
    await articleStore.markAsRead(article.id)
  }
}

function formatDate(dateStr: string): string {
  const date = new Date(dateStr)
  return date.toLocaleDateString('en-US', {
    year: 'numeric',
    month: 'short',
    day: 'numeric'
  })
}

function getReadTime(text: string): number {
  if (!text) return 1
  const cleaned = text.replace(/<[^>]*>/g, '').trim()
  return Math.max(1, Math.ceil(cleaned.length / 300))
}

function truncateText(text: string, maxLength: number): string {
  const cleaned = text.replace(/<[^>]*>/g, '').trim()
  if (cleaned.length <= maxLength) return cleaned
  return cleaned.slice(0, maxLength) + '...'
}
</script>

<style lang="scss" scoped>
.home-view {
  height: 100%;
  overflow: hidden;
}

.home-content {
  display: flex;
  height: 100%;
}

.articles-section {
  flex: 1;
  min-width: 0;
  overflow: hidden;
}

.detail-panel {
  width: 280px;
  flex-shrink: 0;
  border-left: 1px solid var(--border-color);
  background-color: var(--bg-sidebar);
  display: flex;
  flex-direction: column;

  &.empty {
    justify-content: center;
    align-items: center;
  }
}

.panel-tabs {
  display: flex;
  gap: 16px;
  padding: 16px 20px;
  border-bottom: 1px solid var(--border-color);

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

.panel-content {
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
  gap: 10px;
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

.status-badge {
  display: inline-block;
  padding: 2px 8px;
  font-size: 11px;
  border-radius: 4px;

  &.unseen {
    background-color: #e3f2fd;
    color: #1976d2;
  }

  &.seen {
    background-color: var(--bg-secondary);
    color: var(--text-muted);
  }

  &.favorite {
    background-color: #fff3e0;
    color: #f57c00;
  }
}

.preview-text {
  font-size: 13px;
  color: var(--text-secondary);
  line-height: 1.6;
  margin: 0;
}

.action-buttons {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.action-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 10px 16px;
  border: 1px solid var(--border-color);
  background: var(--bg-primary);
  color: var(--text-secondary);
  font-size: 13px;
  cursor: pointer;
  border-radius: 8px;
  text-decoration: none;
  transition: all 0.2s;

  &:hover {
    border-color: var(--text-muted);
    color: var(--text-primary);
  }

  &.primary {
    border: none;
    background-color: var(--primary-color);
    color: white;

    &:hover {
      opacity: 0.9;
    }
  }
}

.empty-hint {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  color: var(--text-muted);
  text-align: center;
  padding: 24px;

  p {
    font-size: 13px;
    margin: 0;
  }
}
</style>
