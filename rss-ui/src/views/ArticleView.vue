<template>
  <div class="article-view">
    <div class="article-header">
      <el-button text @click="goBack">
        <el-icon><ArrowLeft /></el-icon>
        返回
      </el-button>
    </div>
    <ArticleDetail :article="articleStore.currentArticle" />
  </div>
</template>

<script setup lang="ts">
import { watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import ArticleDetail from '@/components/ArticleDetail/ArticleDetail.vue'
import { useArticleStore } from '@/stores/article'

const route = useRoute()
const router = useRouter()
const articleStore = useArticleStore()

watch(
  () => route.params.id,
  (id) => {
    if (id) {
      articleStore.fetchArticle(Number(id))
    }
  },
  { immediate: true }
)

function goBack() {
  router.back()
}
</script>

<style lang="scss" scoped>
.article-view {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.article-header {
  padding: 16px 20px;
  border-bottom: 1px solid var(--border-color);
}
</style>
