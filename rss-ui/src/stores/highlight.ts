import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { Highlight, Tag, CreateHighlightParams, UpdateHighlightParams } from '@/types'
import * as highlightApi from '@/api/highlight'

export const useHighlightStore = defineStore('highlight', () => {
  const highlights = ref<Highlight[]>([])
  const tags = ref<Tag[]>([])
  const loading = ref(false)
  const currentArticleId = ref<number | null>(null)

  async function fetchHighlights(articleId: number) {
    if (currentArticleId.value === articleId && highlights.value.length > 0) {
      return
    }

    loading.value = true
    currentArticleId.value = articleId
    try {
      highlights.value = await highlightApi.getHighlightsByArticle(articleId)
    } finally {
      loading.value = false
    }
  }

  async function createHighlight(params: CreateHighlightParams) {
    const highlight = await highlightApi.createHighlight(params)
    highlights.value.push(highlight)
    return highlight
  }

  async function updateHighlight(id: number, params: UpdateHighlightParams) {
    const highlight = await highlightApi.updateHighlight(id, params)
    const index = highlights.value.findIndex(h => h.id === id)
    if (index !== -1) {
      highlights.value[index] = highlight
    }
    return highlight
  }

  async function deleteHighlight(id: number) {
    await highlightApi.deleteHighlight(id)
    highlights.value = highlights.value.filter(h => h.id !== id)
  }

  async function fetchTags() {
    tags.value = await highlightApi.getAllTags()
  }

  async function createTag(name: string, color?: string) {
    const tag = await highlightApi.createTag(name, color)
    if (!tags.value.find(t => t.id === tag.id)) {
      tags.value.push(tag)
    }
    return tag
  }

  function clearHighlights() {
    highlights.value = []
    currentArticleId.value = null
  }

  return {
    highlights,
    tags,
    loading,
    currentArticleId,
    fetchHighlights,
    createHighlight,
    updateHighlight,
    deleteHighlight,
    fetchTags,
    createTag,
    clearHighlights
  }
})
