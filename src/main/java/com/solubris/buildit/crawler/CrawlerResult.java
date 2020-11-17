package com.solubris.buildit.crawler;

import java.util.Set;

public class CrawlerResult {
    public static final CrawlerResult EMPTY = new CrawlerResult(Set.of(), Set.of(), Set.of());

    private final Set<String> internalLinks;
    private final Set<String> externalLinks;
    private final Set<String> imgLinks;

    public CrawlerResult(Set<String> internalLinks, Set<String> externalLinks, Set<String> imgLinks) {
        this.internalLinks = Set.copyOf(internalLinks);
        this.externalLinks = Set.copyOf(externalLinks);
        this.imgLinks = Set.copyOf(imgLinks);
    }

    public Set<String> getInternalLinks() {
        return internalLinks;
    }

    public Set<String> getExternalLinks() {
        return externalLinks;
    }

    public Set<String> getImgLinks() {
        return imgLinks;
    }
}
