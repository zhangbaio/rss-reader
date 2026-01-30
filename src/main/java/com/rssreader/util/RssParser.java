package com.rssreader.util;

import com.rssreader.entity.Article;
import com.rssreader.rss.*;
import com.rssreader.rss.DateTime;
import org.apache.abdera.Abdera;
import org.apache.abdera.model.*;
import org.apache.abdera.parser.Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RssParser {

    private static final Logger logger = LoggerFactory.getLogger(RssParser.class);

    private static final Abdera abdera = new Abdera();

    public static Channel parseFeed(String xml) throws Exception {
        Parser parser = abdera.getParser();
        InputStream inputStream = new ByteArrayInputStream(xml.getBytes("UTF-8"));
        Document<Element> doc = parser.parse(inputStream);
        Element root = doc.getRoot();

        Channel channel = new Channel();

        if (root instanceof Feed) {
            parseFeedAtom((Feed) root, channel);
        } else {
            parseFeedRss(xml, channel);
        }

        return channel;
    }

    private static void parseFeedAtom(Feed feed, Channel channel) {
        channel.setTitle(feed.getTitle());
        if (feed.getLinks() != null && !feed.getLinks().isEmpty()) {
            channel.setLink(feed.getLinks().get(0).getHref().toString());
        }
        if (feed.getSubtitle() != null) {
            channel.setDescription(feed.getSubtitle());
        }

        if (feed.getUpdated() != null) {
            channel.setLastBuildDate(new DateTime(feed.getUpdated(), feed.getUpdated().toString()));
        }

        List<Entry> entries = feed.getEntries();
        for (Entry entry : entries) {
            Item item = new Item();

            item.setTitle(entry.getTitle());
            if (entry.getId() != null) {
                item.setGuid(entry.getId().toString());
            }
            if (entry.getLinks() != null && !entry.getLinks().isEmpty()) {
                item.setLink(entry.getLinks().get(0).getHref().toString());
            }
            if (entry.getSummary() != null) {
                item.setDescription(entry.getSummary());
            } else if (entry.getContent() != null) {
                item.setDescription(entry.getContent());
            }

            if (entry.getAuthors() != null && !entry.getAuthors().isEmpty()) {
                Person author = entry.getAuthors().get(0);
                item.setAuthor(author.getName());
            }

            if (entry.getUpdated() != null) {
                item.setPubDate(new DateTime(entry.getUpdated(), entry.getUpdated().toString()));
            } else if (entry.getPublished() != null) {
                item.setPubDate(new DateTime(entry.getPublished(), entry.getPublished().toString()));
            }

            if (entry.getCategories() != null && !entry.getCategories().isEmpty()) {
                Category category = entry.getCategories().get(0);
                item.setCategory(category.getTerm());
            }

            // Extract image from Atom entry
            String imageUrl = extractImageFromAtomEntry(entry, item);
            item.setImageUrl(imageUrl);

            channel.addItem(item);
        }
    }

    /**
     * Extract image URL from Atom entry
     */
    private static String extractImageFromAtomEntry(Entry entry, Item item) {
        // Try to find image links
        if (entry.getLinks() != null) {
            for (Link link : entry.getLinks()) {
                String rel = link.getRel();
                String type = link.getMimeType() != null ? link.getMimeType().toString() : "";
                if ("enclosure".equals(rel) && type.startsWith("image/")) {
                    return link.getHref().toString();
                }
            }
        }

        // Try to extract from content
        if (entry.getContent() != null) {
            String content = entry.getContent();
            String imgUrl = extractFirstImageFromHtml(content);
            if (imgUrl != null) {
                return imgUrl;
            }
        }

        // Try to extract from summary
        if (entry.getSummary() != null) {
            String summary = entry.getSummary();
            String imgUrl = extractFirstImageFromHtml(summary);
            if (imgUrl != null) {
                return imgUrl;
            }
        }

        return null;
    }

    private static void parseFeedRss(String xml, Channel channel) throws Exception {
        org.w3c.dom.Document doc = javax.xml.parsers.DocumentBuilderFactory.newInstance()
                .newDocumentBuilder()
                .parse(new ByteArrayInputStream(xml.getBytes("UTF-8")));

        org.w3c.dom.Element rssElement = doc.getDocumentElement();
        org.w3c.dom.NodeList channelNodes = rssElement.getElementsByTagName("channel");

        if (channelNodes.getLength() > 0) {
            org.w3c.dom.Element channelElement = (org.w3c.dom.Element) channelNodes.item(0);

            channel.setTitle(getElementText(channelElement, "title"));
            channel.setLink(getElementText(channelElement, "link"));
            channel.setDescription(getElementText(channelElement, "description"));
            channel.setLanguage(getElementText(channelElement, "language"));
            channel.setGenerator(getElementText(channelElement, "generator"));
            channel.setCopyright(getElementText(channelElement, "copyright"));

            String pubDateStr = getElementText(channelElement, "pubDate");
            if (pubDateStr != null) {
                channel.setPubDate(DateTimeParser.parse(pubDateStr));
            }

            String lastBuildDateStr = getElementText(channelElement, "lastBuildDate");
            if (lastBuildDateStr != null) {
                channel.setLastBuildDate(DateTimeParser.parse(lastBuildDateStr));
            }

            org.w3c.dom.NodeList imageNodes = channelElement.getElementsByTagName("image");
            if (imageNodes.getLength() > 0) {
                org.w3c.dom.Element imageElement = (org.w3c.dom.Element) imageNodes.item(0);
                Image image = new Image();
                image.setUrl(getElementText(imageElement, "url"));
                image.setTitle(getElementText(imageElement, "title"));
                image.setLink(getElementText(imageElement, "link"));
                channel.setImage(image);
            }

            org.w3c.dom.NodeList itemNodes = channelElement.getElementsByTagName("item");
            for (int i = 0; i < itemNodes.getLength(); i++) {
                org.w3c.dom.Element itemElement = (org.w3c.dom.Element) itemNodes.item(i);
                Item item = new Item();

                item.setTitle(getElementText(itemElement, "title"));
                item.setLink(getElementText(itemElement, "link"));
                item.setDescription(getElementText(itemElement, "description"));
                item.setAuthor(getElementText(itemElement, "author"));
                item.setCategory(getElementText(itemElement, "category"));

                String guid = getElementText(itemElement, "guid");
                if (guid == null || guid.isEmpty()) {
                    guid = getElementText(itemElement, "link");
                }
                item.setGuid(guid);

                String pubDateStr2 = getElementText(itemElement, "pubDate");
                if (pubDateStr2 != null) {
                    item.setPubDate(DateTimeParser.parse(pubDateStr2));
                }

                org.w3c.dom.NodeList enclosureNodes = itemElement.getElementsByTagName("enclosure");
                if (enclosureNodes.getLength() > 0) {
                    org.w3c.dom.Element enclosureElement = (org.w3c.dom.Element) enclosureNodes.item(0);
                    Enclosure enclosure = new Enclosure();
                    enclosure.setUrl(enclosureElement.getAttribute("url"));
                    enclosure.setType(enclosureElement.getAttribute("type"));
                    String lengthStr = enclosureElement.getAttribute("length");
                    if (lengthStr != null && !lengthStr.isEmpty()) {
                        try {
                            enclosure.setLength(Long.parseLong(lengthStr));
                        } catch (NumberFormatException e) {
                            logger.warn("Invalid enclosure length: {}", lengthStr);
                        }
                    }
                    item.setEnclosure(enclosure);
                }

                // Extract image URL from various sources
                String imageUrl = extractImageUrl(itemElement, item);
                item.setImageUrl(imageUrl);

                channel.addItem(item);
            }
        }
    }

    private static String getElementText(org.w3c.dom.Element parent, String tagName) {
        org.w3c.dom.NodeList nodes = parent.getElementsByTagName(tagName);
        if (nodes.getLength() > 0) {
            org.w3c.dom.Element element = (org.w3c.dom.Element) nodes.item(0);
            return element.getTextContent();
        }
        return null;
    }

    /**
     * Extract image URL from item element
     * Priority: media:content > media:thumbnail > enclosure (image type) > img in description
     */
    private static String extractImageUrl(org.w3c.dom.Element itemElement, Item item) {
        // 1. Try media:content
        org.w3c.dom.NodeList mediaContentNodes = itemElement.getElementsByTagName("media:content");
        if (mediaContentNodes.getLength() > 0) {
            org.w3c.dom.Element mediaElement = (org.w3c.dom.Element) mediaContentNodes.item(0);
            String url = mediaElement.getAttribute("url");
            String type = mediaElement.getAttribute("type");
            if (url != null && !url.isEmpty()) {
                if (type == null || type.isEmpty() || type.startsWith("image/")) {
                    return url;
                }
            }
        }

        // 2. Try media:thumbnail
        org.w3c.dom.NodeList thumbnailNodes = itemElement.getElementsByTagName("media:thumbnail");
        if (thumbnailNodes.getLength() > 0) {
            org.w3c.dom.Element thumbnailElement = (org.w3c.dom.Element) thumbnailNodes.item(0);
            String url = thumbnailElement.getAttribute("url");
            if (url != null && !url.isEmpty()) {
                return url;
            }
        }

        // 3. Try enclosure if it's an image
        if (item.getEnclosure() != null) {
            String type = item.getEnclosure().getType();
            if (type != null && type.startsWith("image/")) {
                return item.getEnclosure().getUrl();
            }
        }

        // 4. Try to extract first img from description
        String description = item.getDescription();
        if (description != null && !description.isEmpty()) {
            return extractFirstImageFromHtml(description);
        }

        return null;
    }

    /**
     * Extract first image URL from HTML content
     */
    private static String extractFirstImageFromHtml(String html) {
        if (html == null || html.isEmpty()) {
            return null;
        }

        // Pattern to match img src attribute
        Pattern pattern = Pattern.compile("<img[^>]+src\\s*=\\s*[\"']([^\"']+)[\"']", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(html);

        if (matcher.find()) {
            String imgUrl = matcher.group(1);
            // Filter out common tracking pixels and tiny images
            if (imgUrl != null && !imgUrl.isEmpty()
                && !imgUrl.contains("feedburner")
                && !imgUrl.contains("pixel")
                && !imgUrl.contains("tracker")
                && !imgUrl.contains("1x1")) {
                return imgUrl;
            }
        }

        return null;
    }

    public static List<Article> convertToArticles(Channel channel, Long feedId) {
        List<Article> articles = new ArrayList<>();

        for (Item item : channel.getItems()) {
            Article article = new Article();
            article.setFeedId(feedId);
            article.setArticleGuid(item.getGuid() != null ? item.getGuid() : item.getLink());
            article.setTitle(item.getTitle());
            article.setLink(item.getLink());
            article.setDescription(item.getDescription());
            article.setAuthor(item.getAuthor());
            article.setCategory(item.getCategory());

            if (item.getPubDate() != null) {
                article.setPubDate(item.getPubDate().getDate());
            } else {
                article.setPubDate(new Date());
            }

            if (item.getEnclosure() != null) {
                article.setEnclosureUrl(item.getEnclosure().getUrl());
                article.setEnclosureType(item.getEnclosure().getType());
            }

            // Set image URL
            article.setImageUrl(item.getImageUrl());

            article.setIsRead(0);
            article.setIsFavorite(0);

            articles.add(article);
        }

        return articles;
    }
}
