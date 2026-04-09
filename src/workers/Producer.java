package workers;

import model.Task;

import java.util.List;


// Produces tasks by adding chapter URLs to the shared queue.
// Sends one poison pill per consumer when done to signal shutdown.

public class Producer implements Runnable {

    private final ChapterQueue queue;
    private final List<String> urls;
    private final int consumerCount;

    public Producer(ChapterQueue queue, List<String> urls, int consumerCount) {
        this.queue = queue;
        this.urls = urls;
        this.consumerCount = consumerCount;
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < urls.size(); i++) {
                System.out.println("[Producer] Queuing chapter " + (i + 1));
                queue.put(new Task(i + 1, urls.get(i)));
            }
            for (int i = 0; i < consumerCount; i++) {
                queue.put(new Task(Task.POISON, null)); // one per consumer
                System.out.println("[Producer] Poison pill " + (i + 1) + " added.");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}