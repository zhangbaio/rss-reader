package com.rssreader.service.impl;

import com.rssreader.common.PageResult;
import com.rssreader.dto.WebArticleDTO;
import com.rssreader.dto.WebArticleQueryDTO;
import com.rssreader.entity.WebArticle;
import com.rssreader.exception.RssReaderException;
import com.rssreader.mapper.WebArticleMapper;
import com.rssreader.service.IWebArticleService;
import com.rssreader.util.ContentExtractor;
import com.rssreader.util.HttpUtils;
import com.rssreader.vo.WebArticleVO;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class WebArticleServiceImpl implements IWebArticleService {

    private static final Logger logger = LoggerFactory.getLogger(WebArticleServiceImpl.class);

    @Autowired
    private WebArticleMapper webArticleMapper;

    @Override
    public WebArticleVO addFromUrl(WebArticleDTO dto) {
        String url = dto.getUrl();
        if (url == null || url.trim().isEmpty()) {
            throw new RssReaderException("URL cannot be empty");
        }

        // Check if URL already exists
        WebArticle existing = webArticleMapper.selectByUrl(url);
        if (existing != null) {
            throw new RssReaderException("This URL has already been saved");
        }

        try {
            // Fetch the web page
            String html = HttpUtils.get(url);
            Document doc = Jsoup.parse(html, url);

            // Extract metadata
            String title = extractTitle(doc, dto.getTitle());
            String description = extractDescription(doc);
            String content = ContentExtractor.extractFromHtml(html, url);
            String author = extractAuthor(doc);
            String imageUrl = extractImage(doc, url);
            String domain = extractDomain(url);
            String language = extractLanguage(doc);

            // Calculate word count and reading time
            int wordCount = calculateWordCount(content);
            int readingTime = calculateReadingTime(wordCount);

            // Create entity
            WebArticle webArticle = new WebArticle();
            webArticle.setUrl(url);
            webArticle.setTitle(title);
            webArticle.setDescription(description);
            webArticle.setContent(content);
            webArticle.setAuthor(author);
            webArticle.setDomain(domain);
            webArticle.setImageUrl(imageUrl);
            webArticle.setWordCount(wordCount);
            webArticle.setReadingTime(readingTime);
            webArticle.setProgress(0);
            webArticle.setCategory(dto.getCategory() != null ? dto.getCategory() : "inbox");
            webArticle.setLanguage(language);
            webArticle.setSavedTime(new Date());

            webArticleMapper.insert(webArticle);

            return webArticleMapper.selectVOById(webArticle.getId());
        } catch (Exception e) {
            logger.error("Failed to fetch URL: {}", url, e);
            throw new RssReaderException("Failed to fetch URL: " + e.getMessage());
        }
    }

    @Override
    public WebArticleVO getById(Long id) {
        return webArticleMapper.selectVOById(id);
    }

    @Override
    public PageResult<WebArticleVO> list(WebArticleQueryDTO query) {
        List<WebArticleVO> list = webArticleMapper.selectList(query);
        int total = webArticleMapper.selectCount(query);

        PageResult<WebArticleVO> result = new PageResult<>();
        result.setData(list);
        result.setTotal(total);
        result.setPage(query.getPage());
        result.setSize(query.getLimit());
        result.setTotalPages((total + query.getLimit() - 1) / query.getLimit());

        return result;
    }

    @Override
    public void updateCategory(Long id, String category) {
        if (!isValidCategory(category)) {
            throw new RssReaderException("Invalid category: " + category);
        }
        webArticleMapper.updateCategory(id, category);
    }

    @Override
    public void markAsRead(Long id, Integer isRead) {
        webArticleMapper.updateReadStatus(id, isRead);
    }

    @Override
    public void toggleFavorite(Long id) {
        WebArticle article = webArticleMapper.selectById(id);
        if (article == null) {
            throw new RssReaderException("Article not found");
        }
        int newStatus = article.getIsFavorite() == null || article.getIsFavorite() == 0 ? 1 : 0;
        webArticleMapper.updateFavoriteStatus(id, newStatus);
    }

    @Override
    public void updateProgress(Long id, Integer progress) {
        if (progress < 0 || progress > 100) {
            throw new RssReaderException("Progress must be between 0 and 100");
        }
        webArticleMapper.updateProgress(id, progress);
    }

    @Override
    public void delete(Long id) {
        webArticleMapper.deleteById(id);
    }

    @Override
    public Map<String, Integer> getCategoryCounts() {
        Map<String, Integer> counts = new HashMap<>();
        counts.put("inbox", webArticleMapper.selectCountByCategory("inbox"));
        counts.put("later", webArticleMapper.selectCountByCategory("later"));
        counts.put("archive", webArticleMapper.selectCountByCategory("archive"));
        counts.put("all", webArticleMapper.selectCountByCategory(null));
        return counts;
    }

    private boolean isValidCategory(String category) {
        return "inbox".equals(category) || "later".equals(category) || "archive".equals(category);
    }

    private String extractTitle(Document doc, String providedTitle) {
        if (providedTitle != null && !providedTitle.trim().isEmpty()) {
            return providedTitle.trim();
        }

        // Try og:title first
        Element ogTitle = doc.selectFirst("meta[property=og:title]");
        if (ogTitle != null && !ogTitle.attr("content").isEmpty()) {
            return ogTitle.attr("content");
        }

        // Try Twitter title
        Element twitterTitle = doc.selectFirst("meta[name=twitter:title]");
        if (twitterTitle != null && !twitterTitle.attr("content").isEmpty()) {
            return twitterTitle.attr("content");
        }

        // Fall back to page title
        String title = doc.title();
        return title != null && !title.isEmpty() ? title : "Untitled";
    }

    private String extractDescription(Document doc) {
        // Try og:description
        Element ogDesc = doc.selectFirst("meta[property=og:description]");
        if (ogDesc != null && !ogDesc.attr("content").isEmpty()) {
            return truncate(ogDesc.attr("content"), 500);
        }

        // Try meta description
        Element metaDesc = doc.selectFirst("meta[name=description]");
        if (metaDesc != null && !metaDesc.attr("content").isEmpty()) {
            return truncate(metaDesc.attr("content"), 500);
        }

        // Try Twitter description
        Element twitterDesc = doc.selectFirst("meta[name=twitter:description]");
        if (twitterDesc != null && !twitterDesc.attr("content").isEmpty()) {
            return truncate(twitterDesc.attr("content"), 500);
        }

        // Extract from first paragraph
        Element firstP = doc.selectFirst("article p, main p, .content p, p");
        if (firstP != null) {
            return truncate(firstP.text(), 500);
        }

        return null;
    }

    private String extractAuthor(Document doc) {
        // Try author meta tag
        Element authorMeta = doc.selectFirst("meta[name=author]");
        if (authorMeta != null && !authorMeta.attr("content").isEmpty()) {
            return authorMeta.attr("content");
        }

        // Try article:author
        Element articleAuthor = doc.selectFirst("meta[property=article:author]");
        if (articleAuthor != null && !articleAuthor.attr("content").isEmpty()) {
            return articleAuthor.attr("content");
        }

        // Try common author selectors
        Elements authorElements = doc.select("[rel=author], .author, .byline, [itemprop=author]");
        if (!authorElements.isEmpty()) {
            return authorElements.first().text();
        }

        return null;
    }

    private String extractImage(Document doc, String baseUrl) {
        // Try og:image
        Element ogImage = doc.selectFirst("meta[property=og:image]");
        if (ogImage != null && !ogImage.attr("content").isEmpty()) {
            return resolveUrl(baseUrl, ogImage.attr("content"));
        }

        // Try Twitter image
        Element twitterImage = doc.selectFirst("meta[name=twitter:image]");
        if (twitterImage != null && !twitterImage.attr("content").isEmpty()) {
            return resolveUrl(baseUrl, twitterImage.attr("content"));
        }

        // Try first content image
        Element firstImg = doc.selectFirst("article img[src], main img[src], .content img[src]");
        if (firstImg != null) {
            String src = firstImg.attr("src");
            if (!src.isEmpty() && !src.startsWith("data:")) {
                return resolveUrl(baseUrl, src);
            }
        }

        return null;
    }

    private String extractDomain(String urlStr) {
        try {
            URL url = new URL(urlStr);
            String host = url.getHost();
            // Remove www. prefix
            if (host.startsWith("www.")) {
                host = host.substring(4);
            }
            return host;
        } catch (Exception e) {
            return null;
        }
    }

    private String extractLanguage(Document doc) {
        // Try html lang attribute
        String lang = doc.select("html").attr("lang");
        if (!lang.isEmpty()) {
            return lang.length() > 2 ? lang.substring(0, 2) : lang;
        }

        // Try meta language
        Element langMeta = doc.selectFirst("meta[http-equiv=content-language]");
        if (langMeta != null && !langMeta.attr("content").isEmpty()) {
            String content = langMeta.attr("content");
            return content.length() > 2 ? content.substring(0, 2) : content;
        }

        return null;
    }

    private int calculateWordCount(String content) {
        if (content == null || content.isEmpty()) {
            return 0;
        }
        // Remove HTML tags and count words
        String text = Jsoup.parse(content).text();
        // For Chinese/Japanese, count characters; for others, count words
        if (containsCJK(text)) {
            return text.replaceAll("\\s+", "").length();
        } else {
            return text.split("\\s+").length;
        }
    }

    private int calculateReadingTime(int wordCount) {
        // Average reading speed: 200 words/min for English, 400 chars/min for Chinese
        int wordsPerMinute = 200;
        return Math.max(1, (int) Math.ceil((double) wordCount / wordsPerMinute));
    }

    private boolean containsCJK(String text) {
        for (char c : text.toCharArray()) {
            if (Character.UnicodeScript.of(c) == Character.UnicodeScript.HAN ||
                Character.UnicodeScript.of(c) == Character.UnicodeScript.HIRAGANA ||
                Character.UnicodeScript.of(c) == Character.UnicodeScript.KATAKANA) {
                return true;
            }
        }
        return false;
    }

    private String truncate(String text, int maxLength) {
        if (text == null || text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength - 3) + "...";
    }

    private String resolveUrl(String baseUrl, String relativeUrl) {
        if (relativeUrl.startsWith("http://") || relativeUrl.startsWith("https://")) {
            return relativeUrl;
        }
        try {
            URL base = new URL(baseUrl);
            URL resolved = new URL(base, relativeUrl);
            return resolved.toString();
        } catch (Exception e) {
            return relativeUrl;
        }
    }
}
