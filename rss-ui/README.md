# RSS Reader - Frontend

A modern RSS reader web application built with Vue 3, TypeScript, and Pinia.

## Tech Stack

- **Vue 3** - Progressive JavaScript framework
- **TypeScript** - Type-safe JavaScript
- **Pinia** - State management
- **Vue Router** - Client-side routing
- **Axios** - HTTP client
- **SCSS** - CSS preprocessor
- **Element Plus** - UI component library

## Features

### Feed Management
- Subscribe to RSS feeds
- Organize feeds by categories
- Auto-fetch new articles
- Unread count display

### Article Reading
- Clean reading experience
- Seen/Unseen status tracking
- Article sorting (date, title, author, feed)
- Text highlighting with colors
- Document notes
- Table of contents navigation
- Reading progress tracking

### Web Articles
- Save web pages by URL
- Inbox/Later/Archive categories
- Full article content extraction

### Daily Review
- Spaced repetition for highlights
- Mastery level tracking
- Review frequency settings

## Project Structure

```
rss-ui/
├── src/
│   ├── api/              # API service modules
│   │   ├── article.ts
│   │   ├── feed.ts
│   │   ├── highlight.ts
│   │   ├── note.ts
│   │   ├── review.ts
│   │   └── webArticle.ts
│   ├── components/       # Reusable components
│   │   ├── ArticleList/
│   │   ├── Highlight/
│   │   ├── Sidebar/
│   │   └── AddUrlDialog.vue
│   ├── composables/      # Vue composables
│   ├── layouts/          # Layout components
│   ├── router/           # Route definitions
│   ├── stores/           # Pinia stores
│   │   ├── article.ts
│   │   ├── feed.ts
│   │   ├── highlight.ts
│   │   ├── note.ts
│   │   ├── settings.ts
│   │   └── webArticle.ts
│   ├── styles/           # Global styles
│   ├── types/            # TypeScript types
│   ├── views/            # Page components
│   │   ├── Home.vue
│   │   ├── Feed.vue
│   │   ├── Reader.vue
│   │   ├── ArticlesPage.vue
│   │   ├── WebArticleReader.vue
│   │   └── DailyReview.vue
│   ├── App.vue           # Root component
│   └── main.ts           # Entry point
├── public/
│   └── index.html
├── package.json
├── tsconfig.json
└── vue.config.js
```

## Getting Started

### Prerequisites

- Node.js >= 16
- npm >= 8

### Installation

```bash
# Install dependencies
npm install
```

### Development

```bash
# Start development server
npm run serve
```

The app will be available at `http://localhost:8081`

### Build

```bash
# Build for production
npm run build
```

Build output will be in the `dist/` directory.

### Lint

```bash
# Run ESLint
npm run lint
```

## Configuration

### API Proxy

The development server proxies API requests to the backend. Configure in `vue.config.js`:

```javascript
module.exports = {
  devServer: {
    port: 8081,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
}
```

### Environment Variables

Create `.env.local` for local configuration:

```
VUE_APP_API_BASE_URL=http://localhost:8080
```

## Key Components

### ArticleList
Displays articles in a row format with:
- Article thumbnail or feed favicon
- Title and description
- Seen/Unseen status
- Sorting options

### Reader
Full article reading experience with:
- Table of contents sidebar
- Text selection for highlighting
- Notes panel
- Reading progress bar

### Sidebar
Navigation component with:
- Feed list with unread counts
- Web articles section
- Add feed/URL buttons

## State Management

Pinia stores handle application state:

| Store | Purpose |
|-------|---------|
| `feed` | Feed subscriptions |
| `article` | RSS articles |
| `webArticle` | Web articles |
| `highlight` | Text highlights |
| `note` | Document notes |
| `settings` | User preferences |

## License

MIT
