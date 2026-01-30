package com.rssreader.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

/**
 * 网页正文内容提取工具
 */
public class ContentExtractor {

    private static final Logger logger = LoggerFactory.getLogger(ContentExtractor.class);

    // 可能包含正文的标签选择器（按优先级排序）
    private static final List<String> CONTENT_SELECTORS = Arrays.asList(
            "article",
            "[itemprop=articleBody]",
            ".post-content",
            ".article-content",
            ".entry-content",
            ".content-body",
            ".post-body",
            ".article-body",
            ".story-body",
            "#article-content",
            "#post-content",
            "#content",
            ".content",
            "main",
            ".main"
    );

    // 需要移除的标签
    private static final List<String> REMOVE_SELECTORS = Arrays.asList(
            "script",
            "style",
            "nav",
            "header",
            "footer",
            "aside",
            ".sidebar",
            ".advertisement",
            ".ad",
            ".ads",
            ".social-share",
            ".comments",
            ".comment",
            ".related-posts",
            ".recommended",
            "[class*=share]",
            "[class*=social]",
            "[id*=comment]"
    );

    /**
     * 从URL提取正文内容
     */
    public static String extractFromUrl(String url) {
        try {
            String html = HttpUtils.get(url);
            return extractFromHtml(html, url);
        } catch (Exception e) {
            logger.error("Failed to extract content from URL: {}", url, e);
            return null;
        }
    }

    /**
     * 从HTML提取正文内容
     */
    public static String extractFromHtml(String html, String baseUrl) {
        if (html == null || html.isEmpty()) {
            return null;
        }

        try {
            Document doc = Jsoup.parse(html, baseUrl);

            // 移除不需要的元素
            for (String selector : REMOVE_SELECTORS) {
                doc.select(selector).remove();
            }

            // 尝试找到正文容器
            Element contentElement = null;
            for (String selector : CONTENT_SELECTORS) {
                Elements elements = doc.select(selector);
                if (!elements.isEmpty()) {
                    contentElement = elements.first();
                    break;
                }
            }

            // 如果没找到，使用body
            if (contentElement == null) {
                contentElement = doc.body();
            }

            if (contentElement == null) {
                return null;
            }

            // 清理内容
            cleanContent(contentElement);

            // 转换相对链接为绝对链接
            convertRelativeUrls(contentElement, baseUrl);

            return contentElement.html();
        } catch (Exception e) {
            logger.error("Failed to extract content from HTML", e);
            return null;
        }
    }

    /**
     * 清理内容元素
     */
    private static void cleanContent(Element element) {
        // 移除空段落
        element.select("p:empty").remove();
        element.select("div:empty").remove();

        // 移除隐藏元素
        element.select("[style*=display:none]").remove();
        element.select("[style*=display: none]").remove();
        element.select("[hidden]").remove();

        // 移除脚本和样式
        element.select("script").remove();
        element.select("style").remove();
        element.select("noscript").remove();

        // 移除表单元素
        element.select("form").remove();
        element.select("input").remove();
        element.select("button").remove();
    }

    /**
     * 将相对URL转换为绝对URL
     */
    private static void convertRelativeUrls(Element element, String baseUrl) {
        // 转换图片链接
        for (Element img : element.select("img[src]")) {
            String src = img.attr("src");
            if (!src.startsWith("http") && !src.startsWith("data:")) {
                img.attr("src", resolveUrl(baseUrl, src));
            }
            // 处理懒加载图片
            String dataSrc = img.attr("data-src");
            if (!dataSrc.isEmpty()) {
                if (!dataSrc.startsWith("http")) {
                    dataSrc = resolveUrl(baseUrl, dataSrc);
                }
                img.attr("src", dataSrc);
            }
        }

        // 转换链接
        for (Element a : element.select("a[href]")) {
            String href = a.attr("href");
            if (!href.startsWith("http") && !href.startsWith("#") && !href.startsWith("mailto:")) {
                a.attr("href", resolveUrl(baseUrl, href));
            }
            // 外部链接在新窗口打开
            a.attr("target", "_blank");
            a.attr("rel", "noopener noreferrer");
        }
    }

    /**
     * 解析相对URL
     */
    private static String resolveUrl(String baseUrl, String relativeUrl) {
        try {
            java.net.URL base = new java.net.URL(baseUrl);
            java.net.URL resolved = new java.net.URL(base, relativeUrl);
            return resolved.toString();
        } catch (Exception e) {
            return relativeUrl;
        }
    }
}
