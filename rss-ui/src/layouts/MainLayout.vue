<template>
  <div class="main-layout" :class="{ fullscreen: settingsStore.app.fullscreenReading }">
    <Sidebar
      v-show="!settingsStore.app.fullscreenReading"
      :collapsed="settingsStore.app.sidebarCollapsed"
      @toggle="settingsStore.toggleSidebar"
      @add-feed="showAddFeed = true"
      @add-url="showAddUrl = true"
    />
    <main class="main-content" :class="{ expanded: settingsStore.app.sidebarCollapsed }">
      <router-view v-slot="{ Component }">
        <transition name="fade" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </main>

    <!-- 添加订阅弹窗 -->
    <FeedManager
      v-model:visible="showAddFeed"
      @success="handleFeedAdded"
    />

    <!-- 添加URL弹窗 -->
    <AddUrlDialog
      v-model:visible="showAddUrl"
      @success="handleUrlAdded"
    />

    <!-- 全屏模式退出提示 -->
    <transition name="fade">
      <div class="fullscreen-hint" v-if="showFullscreenHint && settingsStore.app.fullscreenReading">
        按 <kbd>Esc</kbd> 或 <kbd>F</kbd> 退出全屏模式
      </div>
    </transition>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import Sidebar from '@/components/Sidebar/Sidebar.vue'
import FeedManager from '@/components/FeedManager/FeedManager.vue'
import AddUrlDialog from '@/components/AddUrlDialog.vue'
import { useFeedStore } from '@/stores/feed'
import { useSettingsStore } from '@/stores/settings'
import { useWebArticleStore } from '@/stores/webArticle'

const feedStore = useFeedStore()
const settingsStore = useSettingsStore()
const webArticleStore = useWebArticleStore()

const showAddFeed = ref(false)
const showAddUrl = ref(false)
const showFullscreenHint = ref(false)

onMounted(async () => {
  await feedStore.fetchFeeds()
})

// 进入全屏时显示提示
watch(
  () => settingsStore.app.fullscreenReading,
  (isFullscreen) => {
    if (isFullscreen) {
      showFullscreenHint.value = true
      setTimeout(() => {
        showFullscreenHint.value = false
      }, 3000)
    }
  }
)

function handleFeedAdded() {
  showAddFeed.value = false
}

function handleUrlAdded() {
  showAddUrl.value = false
  // Refresh article counts in sidebar
  webArticleStore.fetchCategoryCounts()
}
</script>

<style lang="scss" scoped>
.main-layout {
  display: flex;
  height: 100%;
  overflow: hidden;

  &.fullscreen {
    .main-content {
      margin-left: 0;
    }
  }
}

.main-content {
  flex: 1;
  overflow: hidden;
  background-color: var(--bg-content);
  transition: margin-left 0.3s ease;

  &.expanded {
    margin-left: 0;
  }
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

.fullscreen-hint {
  position: fixed;
  bottom: 24px;
  left: 50%;
  transform: translateX(-50%);
  padding: 12px 24px;
  background-color: rgba(0, 0, 0, 0.8);
  color: white;
  border-radius: 8px;
  font-size: 14px;
  z-index: 1000;

  kbd {
    padding: 2px 8px;
    margin: 0 4px;
    background-color: rgba(255, 255, 255, 0.2);
    border-radius: 4px;
    font-family: monospace;
  }
}
</style>
