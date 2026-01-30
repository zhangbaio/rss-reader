<template>
  <div :class="['app', themeClass]">
    <MainLayout v-if="!hideLayout" />
    <router-view v-else />
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import MainLayout from '@/layouts/MainLayout.vue'
import { useSettingsStore } from '@/stores/settings'

const route = useRoute()
const settingsStore = useSettingsStore()

// 是否隐藏主布局
const hideLayout = computed(() => route.meta.hideLayout === true)

// 检测系统主题
const prefersDark = window.matchMedia('(prefers-color-scheme: dark)')

const themeClass = computed(() => {
  const theme = settingsStore.app.theme
  if (theme === 'system') {
    return prefersDark.matches ? 'theme-dark' : 'theme-light'
  }
  return `theme-${theme}`
})
</script>

<style lang="scss">
.app {
  height: 100%;
  transition: $transition-base;
}
</style>
