import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    name: 'Dashboard',
    component: () => import('@/views/Dashboard.vue'),
    meta: { title: '主页', hideLayout: true }
  },
  {
    path: '/articles',
    name: 'Articles',
    component: () => import('@/views/ArticlesPage.vue'),
    meta: { title: 'Articles' }
  },
  {
    path: '/rss',
    name: 'RssHome',
    component: () => import('@/views/Home.vue'),
    meta: { title: '全部文章' }
  },
  {
    path: '/rss/feed/:id',
    name: 'Feed',
    component: () => import('@/views/Feed.vue'),
    meta: { title: '订阅源' }
  },
  {
    path: '/reader/:id',
    name: 'Reader',
    component: () => import('@/views/Reader.vue'),
    meta: { title: '阅读', hideLayout: true }
  },
  {
    path: '/articles/:id',
    name: 'WebArticleReader',
    component: () => import('@/views/WebArticleReader.vue'),
    meta: { title: 'Article', hideLayout: true }
  },
  {
    path: '/review',
    name: 'DailyReview',
    component: () => import('@/views/DailyReview.vue'),
    meta: { title: 'Daily Review', hideLayout: true }
  },
  {
    path: '/review/config',
    name: 'ReviewConfig',
    component: () => import('@/views/ReviewConfig.vue'),
    meta: { title: 'Configure Review', hideLayout: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, _from, next) => {
  document.title = `${to.meta.title || 'RSS Reader'} - RSS Reader`
  next()
})

export default router
