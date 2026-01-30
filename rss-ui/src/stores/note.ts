import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { ArticleNote } from '@/types'
import * as noteApi from '@/api/note'

export const useNoteStore = defineStore('note', () => {
  const note = ref<ArticleNote | null>(null)
  const loading = ref(false)
  const saving = ref(false)
  const currentArticleId = ref<number | null>(null)

  async function fetchNote(articleId: number) {
    if (currentArticleId.value === articleId && note.value) {
      return
    }

    loading.value = true
    currentArticleId.value = articleId
    try {
      note.value = await noteApi.getNoteByArticle(articleId)
    } finally {
      loading.value = false
    }
  }

  async function saveNote(articleId: number, content: string) {
    saving.value = true
    try {
      note.value = await noteApi.saveNote(articleId, content)
    } finally {
      saving.value = false
    }
  }

  async function deleteNote(articleId: number) {
    await noteApi.deleteNote(articleId)
    note.value = null
  }

  function clearNote() {
    note.value = null
    currentArticleId.value = null
  }

  return {
    note,
    loading,
    saving,
    currentArticleId,
    fetchNote,
    saveNote,
    deleteNote,
    clearNote
  }
})
