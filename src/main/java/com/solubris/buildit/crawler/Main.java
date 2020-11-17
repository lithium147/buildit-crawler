package com.solubris.buildit.crawler;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        int maxDepth=10;
        Crawler crawler = new Crawler(maxDepth, "https://wiprodigital.com/");
        crawler.run();
        crawler.report();
    }
    static {
        String path = Main.class.getClassLoader()
                .getResource("logging.properties")
                .getFile();
        System.setProperty("java.util.logging.config.file", path);
    }
}
