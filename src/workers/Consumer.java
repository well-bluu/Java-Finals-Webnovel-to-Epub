package workers;

import interfaces.NovelSite;
import model.Chapter;
import model.Task;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.concurrent.ConcurrentMap;


// Consumes tasks from the shared queue and downloads chapters concurrently.
// Multiple Consumer instances run in parallel, each on their own thread.

public class Consumer implements Runnable {

    private final ChapterQueue queue;
    private final ConcurrentMap<Integer, Chapter> chapterStore;
    private final NovelSite site;
    private final String name;

    public Consumer(ChapterQueue queue, ConcurrentMap<Integer, Chapter> chapterStore, NovelSite site, String name) {
        this.queue = queue;
        this.chapterStore = chapterStore;
        this.site = site;
        this.name = name;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Task task = queue.take();
                if (task.getId() == Task.POISON) break;

                System.out.println("[" + name + "] Fetching chapter " + task.getId());
                try {

                    // This makes it so that our requests looks like a real browser
                    Document doc = Jsoup.connect(task.getUrl())
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                    .header("Accept-Language", "en-US,en;q=0.5")
                    .referrer(site.getReferrer())
                    .ignoreHttpErrors(true)
                    .followRedirects(true)
                    .timeout(10_000)
                    .get();

                    String title = site.parseTitle(doc, task.getId());
                    String content = site.parseContent(doc, task.getUrl());

                    chapterStore.put(task.getId(), new Chapter(task.getId(), title, content));
                    System.out.println("[" + name + "] Done chapter " + task.getId() + " -> " + title);

                    Thread.sleep(3000 + (long)(Math.random() * 1000)); // 3–4 seconds per consumer

                } catch (Exception e) {
                    System.err.println("[" + name + "] Failed chapter " + task.getId() + ": " + e.getMessage());
                }
            }
            System.out.println("[" + name + "] Poison pill taken. Exiting.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}