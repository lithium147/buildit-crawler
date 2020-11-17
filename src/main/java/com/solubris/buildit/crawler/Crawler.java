package com.solubris.buildit.crawler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.TimeUnit;

public class Crawler {
    private final int maxDepth;
    private final ForkJoinPool forkJoinPool = ForkJoinPool.commonPool();
    private final Map<String, CrawlerResult> results = new ConcurrentHashMap<>();
    private final String url;

    public Crawler(int maxDepth, String url) {
        this.maxDepth = maxDepth;
        this.url = url;
    }

    public void run() {
        ForkJoinTask<Void> crawlerTask = new CrawlerTask(url, results, maxDepth);
        forkJoinPool.invoke(crawlerTask);
        forkJoinPool.awaitQuiescence(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
    }

    public void report() {
        System.out.println("report for url: " + url);
        System.out.println("total pages (visited): " + results.size());
        for (Map.Entry<String, CrawlerResult> entry : results.entrySet()) {
            System.out.println("page: " + entry.getKey());
            System.out.println("internal links: " + entry.getValue().getInternalLinks());
            System.out.println("external links: " + entry.getValue().getExternalLinks());
            System.out.println("image links: " + entry.getValue().getImgLinks());
        }
    }
}
