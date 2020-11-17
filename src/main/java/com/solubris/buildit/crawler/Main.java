package com.solubris.buildit.crawler;

public class Main {
    static {
        String path = Main.class.getClassLoader()
                .getResource("logging.properties")
                .getFile();
        System.setProperty("java.util.logging.config.file", path);
    }

    public static void main(String[] args) {
        int maxDepth=10;
        Crawler crawler = new Crawler(maxDepth, "https://wiprodigital.com/");
        crawler.run();
        crawler.report();
    }
}
