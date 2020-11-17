package com.solubris.buildit.crawler;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.RecursiveAction;

import static java.lang.System.Logger.Level.INFO;

public class CrawlerTask extends RecursiveAction {
    private final static System.Logger LOGGER = System.getLogger("buildit");

    private final String domain;
    private final String path;

    private final Map<String, CrawlerResult> results;

    private final int maxDepth;
    private final int depth;

    public CrawlerTask(String domain, String path, Map<String, CrawlerResult> results, int maxDepth, int depth) {
        this.domain = domain;
        this.path = path;
        this.results = results;
        this.maxDepth = maxDepth;
        this.depth = depth;
    }

    public CrawlerTask(String url, Map<String, CrawlerResult> results, int maxDepth) {
        this(withoutTrailingSlash(url), "/", results, maxDepth, 1);
    }

    private static String withoutTrailingSlash(String url) {
        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }

    @Override
    protected void compute() {
        LOGGER.log(INFO, "{0} - entering", path);

        // update results immediately so no other task tries to visit the same url
        // contended statement
        CrawlerResult existingResult = results.putIfAbsent(path, CrawlerResult.EMPTY);
        if (existingResult != null) {
            LOGGER.log(INFO, "{0} - leaving, already have result", path);
            return;
        }
        Set<String> internalLinks;
        Set<String> externalLinks;
        Set<String> imgLinks;

        CrawlerParser crawlerParser = new CrawlerParser(path, domain);
        crawlerParser.connect();
        internalLinks = crawlerParser.findInternalLinks();

        LOGGER.log(INFO, "{0} - processing {1} internalLinks", path, internalLinks.size());

        if (depth < maxDepth) {
            for (String internalLink : internalLinks) {
                if (!visited(internalLink)) {
                    LOGGER.log(INFO, "{0} - forking task for {1}", path, internalLink);
                    CrawlerTask subTask = new CrawlerTask(domain, internalLink, results, maxDepth, depth + 1);

                    // subTask will be submitted to the queue of the current worker
                    // so don't need reference to the actual enclosing ForkJoinPool
                    subTask.fork();
                } else {
                    LOGGER.log(INFO, "{0} - already visited {1}", path, internalLink);
                }
            }
        } else {
            LOGGER.log(INFO, "{0} - not following children, tooDeep", path);
        }

        externalLinks = crawlerParser.findExternalLinks();
        imgLinks = crawlerParser.findImgLinks();
        // contended - could use mutable CrawlerResult so don't need to update results here
        results.put(path, new CrawlerResult(internalLinks, externalLinks, imgLinks));

        LOGGER.log(INFO, "{0} - leaving", path);
    }

    private boolean visited(String url) {
        // contended
        return results.containsKey(url);
    }
}
