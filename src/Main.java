import factory.NovelSiteFactory;
import interfaces.NovelSite;
import model.Chapter;
import workers.ChapterQueue;
import workers.Consumer;
import workers.EpubBuilder;
import workers.Producer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("============================");
        System.out.println("||    WEBNOVEL TO EPUB    ||");
        System.out.println("============================");
        System.out.println("Supported websites: RoyalRoad");
        System.out.print("Enter novel URL: "); 
        String novelUrl = scanner.nextLine();
        scanner.close();

        // Factory pattern, resolves the correct site implementation
        NovelSite site = NovelSiteFactory.getSiteByUrl(novelUrl);
        String novelSlug = site.extractSlug(novelUrl);

        List<String> chapterUrls;
        try {
            chapterUrls = site.getChapterUrls(novelUrl);
        } catch (Exception e) {
            System.err.println("Failed to fetch chapter list: " + e.getMessage());
            return;
        }

        if (chapterUrls.isEmpty()) {
            System.out.println("No chapters found. Exiting.");
            return;
        }

        System.out.println("Found " + chapterUrls.size() + " chapters. Starting download...");

        // Multithreading: Producer-Consumer Pattern
        // Three consumers
        int consumerCount = 3;
        ChapterQueue queue = new ChapterQueue();
        ConcurrentMap<Integer, Chapter> chapterStore = new ConcurrentHashMap<>();

        // Start consumers first so they are ready when producer begins
        Thread[] consumers = new Thread[consumerCount];
        for (int i = 0; i < consumerCount; i++) {
            consumers[i] = new Thread(new Consumer(queue, chapterStore, site, "Consumer-" + (i + 1)));
            consumers[i].start();
        }

        Thread producer = new Thread(new Producer(queue, chapterUrls, consumerCount));
        producer.start();
        producer.join();

        for (Thread c : consumers)
            c.join();

        // Sort by chapter order and build EPUB
        List<Chapter> ordered = new ArrayList<>(chapterStore.values());
        Collections.sort(ordered, (a, b) -> a.getId() - b.getId());

        try {
            new EpubBuilder().build(novelSlug, ordered);
        } catch (Exception e) {
            System.err.println("Failed to build EPUB: " + e.getMessage());
        }

        System.out.println("All done.");
    }
}