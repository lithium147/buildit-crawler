package com.solubris.buildit.crawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

public class CrawlerParser {
    private final String path;
    private final String fullPath;
    private final String domain;
    private Document doc;

    public CrawlerParser(String path, String domain) {
        this.path = path;
        this.fullPath = joinPathElements(domain, path);
        this.domain = domain;
    }

    public void connect() {
        try {
            URL url = new URL(fullPath);
            doc = Jsoup.parse(url, 0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Set<String> findInternalLinks() {
        Set<String> result;
        Elements subUrls = doc.select("a");

        result = new HashSet<>();
        for (Element link : subUrls) {
            String linkHref = link.attr("href");

            if (linkHref == null || linkHref.length() == 0) {   // there is no href attribute on the <a ...>
                continue;
            }
            if (linkHref.startsWith("#")) {     // ignore self referencing anchor links
                continue;
            }
            if (linkHref.startsWith("javascript:")) {
                continue;
            }
            if (linkHref.startsWith("mailto:")) {
                continue;
            }
            if (linkHref.startsWith(domain)) {   // absolute link for same site - make it relative
                linkHref = linkHref.substring(domain.length());
                if (!linkHref.startsWith("/")) {
                    linkHref = "/" + linkHref;
                }
            }
            if (external(linkHref)) {
                continue;
            }

            linkHref = stripFragment(linkHref);
            if (!linkHref.startsWith("/")) {     // relative links are relative to the current path
                // is relative to path excluding query params
                String shortPath = path.replaceFirst("[?].*", "");
                linkHref = joinPathElements(shortPath, linkHref);
            }

            if (linkHref.endsWith("/") && !linkHref.contains("?")) {     // trailing "/" is superfluous
                linkHref = linkHref.substring(0, linkHref.length() - 1);
            }

            if("".equals(linkHref)) {   // root path should always be represented as "/"
                linkHref = "/";
            }

            result.add(linkHref);
        }
        return result;
    }

    public Set<String> findExternalLinks() {
        Set<String> result;
        Elements subUrls = doc.select("a");

        result = new HashSet<>();
        for (Element link : subUrls) {
            String linkHref = link.attr("href");

            if (linkHref == null || linkHref.length() == 0) {
                continue;
            }
            if (linkHref.startsWith("#")) {
                continue;
            }
            if (linkHref.startsWith("javascript:")) {
                continue;
            }
            if (linkHref.startsWith("mailto:")) {
                continue;
            }
            if (!external(linkHref)) {
                continue;
            }

            result.add(linkHref);
        }
        return result;
    }

    public Set<String> findImgLinks() {
        Set<String> result;
        Elements subUrls = doc.select("img");

        result = new HashSet<>();
        for (Element link : subUrls) {
            String linkHref = link.attr("src");

            if (linkHref == null || linkHref.length() == 0) {
                continue;
            }
            result.add(linkHref);
        }
        return result;
    }

    private String stripFragment(String linkHref) {
        return linkHref.split("#", 2)[0];
    }

    private boolean external(String url) {
        // relative urls are internal by definition
        return url.startsWith("http") && !url.startsWith(domain);
    }

    private String joinPathElements(String left, String right) {
        if (left.endsWith("/")) {
            left = left.substring(0, left.length() - 1);
        }
        if (right.startsWith("/")) {
            right = right.substring(1);
        }

        return left + "/" + right;
    }
}
