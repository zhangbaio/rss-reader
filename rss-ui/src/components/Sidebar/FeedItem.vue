<template>
  <router-link
    :to="`/rss/feed/${feed.id}`"
    class="feed-item"
    :class="{ indent, active: isActive }"
    active-class="active"
  >
    <img
      v-if="feed.feedImageUrl && !collapsed"
      :src="feed.feedImageUrl"
      class="feed-icon"
      @error="handleIconError"
    />
    <svg v-else-if="!collapsed" viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2" class="feed-icon-svg">
      <path d="M4 11a9 9 0 0 1 9 9"/>
      <path d="M4 4a16 16 0 0 1 16 16"/>
      <circle cx="5" cy="19" r="1"/>
    </svg>
    <span v-show="!collapsed" class="feed-title">{{ feed.feedName }}</span>
    <span class="badge" v-if="feed.unreadCount > 0 && !collapsed">
      {{ feed.unreadCount > 99 ? '99+' : feed.unreadCount }}
    </span>
    <el-tooltip v-if="collapsed" :content="feed.feedName" placement="right">
      <img
        v-if="feed.feedImageUrl"
        :src="feed.feedImageUrl"
        class="feed-icon-collapsed"
        @error="handleIconError"
      />
      <svg v-else viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2">
        <path d="M4 11a9 9 0 0 1 9 9"/>
        <path d="M4 4a16 16 0 0 1 16 16"/>
        <circle cx="5" cy="19" r="1"/>
      </svg>
    </el-tooltip>
  </router-link>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import type { Feed } from '@/types'

const props = defineProps<{
  feed: Feed
  collapsed: boolean
  indent?: boolean
}>()

const route = useRoute()

const isActive = computed(() => {
  return route.name === 'Feed' && Number(route.params.id) === props.feed.id
})

function handleIconError(e: Event) {
  const img = e.target as HTMLImageElement
  img.style.display = 'none'
}
</script>

<style lang="scss" scoped>
.feed-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 6px 12px;
  border-radius: 6px;
  color: var(--text-secondary);
  text-decoration: none;
  cursor: pointer;
  transition: all 0.2s;
  font-size: 13px;

  &.indent {
    padding-left: 48px;
  }

  &:hover {
    background-color: var(--hover-color);
    color: var(--text-primary);
  }

  &.active {
    background-color: var(--hover-color);
    color: var(--text-primary);
  }
}

.feed-icon,
.feed-icon-collapsed {
  width: 16px;
  height: 16px;
  border-radius: 4px;
  object-fit: contain;
  flex-shrink: 0;
}

.feed-icon-svg {
  flex-shrink: 0;
}

.feed-title {
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.badge {
  margin-left: auto;
  padding: 2px 6px;
  background-color: var(--hover-color);
  color: var(--text-secondary);
  font-size: 11px;
  border-radius: 8px;
  flex-shrink: 0;
}
</style>
