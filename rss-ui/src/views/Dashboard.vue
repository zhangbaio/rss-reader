<template>
  <div class="dashboard">
    <!-- 顶部导航栏 -->
    <header class="top-nav">
      <div class="nav-left">
        <div class="logo">
          <span class="logo-icon">R</span>
        </div>
        <nav class="nav-links">
          <a href="#" class="nav-link">Connect & Sync</a>
          <a href="#" class="nav-link">Browse</a>
          <a href="#" class="nav-link">Chat</a>
          <a href="#" class="nav-link">Resources</a>
          <router-link to="/rss" class="nav-link">Reader</router-link>
        </nav>
      </div>
      <div class="nav-right">
        <div class="nav-icon lightning">
          <svg viewBox="0 0 24 24" width="20" height="20" fill="currentColor">
            <path d="M13 2L3 14h9l-1 8 10-12h-9l1-8z"/>
          </svg>
          <span class="badge">1</span>
        </div>
        <div class="nav-icon">
          <svg viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="currentColor" stroke-width="2">
            <circle cx="11" cy="11" r="8"/>
            <path d="M21 21l-4.35-4.35"/>
          </svg>
        </div>
        <div class="nav-icon">
          <svg viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"/>
            <path d="M13.73 21a2 2 0 0 1-3.46 0"/>
          </svg>
        </div>
        <div class="nav-icon user">
          <svg viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="currentColor" stroke-width="2">
            <circle cx="12" cy="8" r="4"/>
            <path d="M4 20c0-4 4-6 8-6s8 2 8 6"/>
          </svg>
        </div>
      </div>
    </header>

    <!-- 主内容区域 -->
    <main class="main-content">
      <!-- Daily Review 卡片 -->
      <section class="daily-review">
        <div class="review-content">
          <h1 class="review-title">Daily Review</h1>
          <p class="review-subtitle">
            <svg viewBox="0 0 24 24" width="16" height="16" fill="currentColor">
              <path d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41L9 16.17z"/>
            </svg>
            You reviewed {{ stats.todayReviewed }} highlights today
          </p>
        </div>
        <div class="review-actions">
          <router-link to="/review/config" class="btn btn-outline">Configure</router-link>
          <router-link to="/review" class="btn btn-primary">Review more ›</router-link>
        </div>
      </section>

      <!-- 功能卡片区域 -->
      <section class="feature-cards">
        <!-- Connect & Sync Highlights -->
        <div class="feature-card">
          <h2 class="card-title">Connect & Sync Highlights</h2>

          <div class="card-item">
            <div class="item-icon import">
              <svg viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/>
                <polyline points="7 10 12 15 17 10"/>
                <line x1="12" y1="15" x2="12" y2="3"/>
              </svg>
            </div>
            <span class="item-label">Import</span>
            <span class="item-status">1 connection</span>
          </div>

          <div class="card-item">
            <div class="item-icon export">
              <svg viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/>
                <polyline points="16 17 21 12 16 7"/>
                <line x1="21" y1="12" x2="9" y2="12"/>
              </svg>
            </div>
            <span class="item-label">Export</span>
            <span class="item-status empty">0 connections</span>
          </div>
        </div>

        <!-- RSS Reader 入口卡片 -->
        <div class="feature-card">
          <h2 class="card-title">RSS Reader</h2>

          <router-link to="/rss" class="card-item clickable">
            <div class="item-icon reader">
              <svg viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M4 11a9 9 0 0 1 9 9"/>
                <path d="M4 4a16 16 0 0 1 16 16"/>
                <circle cx="5" cy="19" r="1"/>
              </svg>
            </div>
            <span class="item-label">My Feeds</span>
            <span class="item-action">Open ›</span>
          </router-link>

          <div class="card-item">
            <div class="item-icon articles">
              <svg viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/>
                <polyline points="14 2 14 8 20 8"/>
                <line x1="16" y1="13" x2="8" y2="13"/>
                <line x1="16" y1="17" x2="8" y2="17"/>
              </svg>
            </div>
            <span class="item-label">Articles</span>
            <span class="item-status">View all</span>
          </div>
        </div>
      </section>
    </main>
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { storeToRefs } from 'pinia'
import { useReviewStore } from '@/stores/review'

const reviewStore = useReviewStore()
const { stats } = storeToRefs(reviewStore)

onMounted(async () => {
  await reviewStore.fetchStats()
})
</script>

<style lang="scss" scoped>
.dashboard {
  min-height: 100vh;
  background-color: #f8f9fa;
}

// 顶部导航栏
.top-nav {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 24px;
  background-color: #fff;
  border-bottom: 1px solid #eee;
}

.nav-left {
  display: flex;
  align-items: center;
  gap: 32px;
}

.logo {
  .logo-icon {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 32px;
    height: 32px;
    background-color: #1a1a1a;
    color: #fff;
    font-weight: bold;
    font-size: 18px;
    border-radius: 6px;
  }
}

.nav-links {
  display: flex;
  gap: 24px;
}

.nav-link {
  color: #333;
  text-decoration: none;
  font-size: 14px;
  font-weight: 500;
  transition: color 0.2s;

  &:hover {
    color: #5c7cfa;
  }

  &.router-link-active {
    color: #5c7cfa;
  }
}

.nav-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.nav-icon {
  position: relative;
  color: #666;
  cursor: pointer;
  transition: color 0.2s;

  &:hover {
    color: #333;
  }

  &.lightning {
    color: #ff6b35;
  }

  &.user {
    color: #666;
  }

  .badge {
    position: absolute;
    top: -6px;
    right: -8px;
    min-width: 16px;
    height: 16px;
    padding: 0 4px;
    background-color: #ff6b35;
    color: #fff;
    font-size: 11px;
    font-weight: 600;
    line-height: 16px;
    text-align: center;
    border-radius: 8px;
  }
}

// 主内容区域
.main-content {
  max-width: 900px;
  margin: 0 auto;
  padding: 32px 24px;
}

// Daily Review 卡片
.daily-review {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 40px 48px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 50%, #6B8DD6 100%);
  border-radius: 12px;
  color: #fff;
  margin-bottom: 32px;
}

.review-content {
  .review-title {
    font-size: 36px;
    font-weight: 600;
    margin: 0 0 12px 0;
  }

  .review-subtitle {
    display: flex;
    align-items: center;
    gap: 8px;
    margin: 0;
    font-size: 14px;
    opacity: 0.9;

    svg {
      opacity: 0.8;
    }
  }
}

.review-actions {
  display: flex;
  gap: 12px;
}

.btn {
  display: inline-block;
  padding: 10px 20px;
  font-size: 14px;
  font-weight: 500;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s;
  border: none;
  text-decoration: none;

  &.btn-outline {
    background-color: transparent;
    color: #fff;
    border: 1px solid rgba(255, 255, 255, 0.5);

    &:hover {
      background-color: rgba(255, 255, 255, 0.1);
      border-color: #fff;
    }
  }

  &.btn-primary {
    background-color: #fff;
    color: #5c6bc0;

    &:hover {
      background-color: #f5f5f5;
    }
  }
}

// 功能卡片区域
.feature-cards {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 24px;
}

.feature-card {
  background-color: #fff;
  border-radius: 12px;
  padding: 24px;
  border: 1px solid #e8e8e8;
}

.card-title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin: 0 0 20px 0;
  padding-bottom: 16px;
  border-bottom: 2px solid #333;
  display: inline-block;
}

.card-item {
  display: flex;
  align-items: center;
  padding: 16px 0;
  border-bottom: 1px solid #f0f0f0;
  text-decoration: none;
  color: inherit;

  &:last-child {
    border-bottom: none;
  }

  &.clickable {
    cursor: pointer;
    border-radius: 8px;
    padding: 16px;
    margin: -8px;
    margin-bottom: 8px;
    transition: background-color 0.2s;

    &:hover {
      background-color: #f8f9fa;
    }
  }
}

.item-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  margin-right: 16px;
  color: #fff;

  &.import {
    background-color: #6c757d;
  }

  &.export {
    background-color: #6c757d;
  }

  &.reader {
    background-color: #ff6b35;
  }

  &.articles {
    background-color: #5c7cfa;
  }
}

.item-label {
  flex: 1;
  font-size: 15px;
  font-weight: 500;
  color: #333;
}

.item-status {
  font-size: 14px;
  color: #5c7cfa;

  &.empty {
    color: #999;
  }
}

.item-action {
  font-size: 14px;
  color: #5c7cfa;
  font-weight: 500;
}

// 响应式适配
@media (max-width: 768px) {
  .feature-cards {
    grid-template-columns: 1fr;
  }

  .daily-review {
    flex-direction: column;
    text-align: center;
    padding: 32px 24px;

    .review-content {
      margin-bottom: 24px;

      .review-subtitle {
        justify-content: center;
      }
    }
  }

  .nav-links {
    display: none;
  }
}
</style>
