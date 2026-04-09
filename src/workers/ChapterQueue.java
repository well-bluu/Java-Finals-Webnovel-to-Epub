package workers;

import model.Task;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


// Thread-safe wrapper around a BlockingQueue.
// Ensures safe task passing between the Producer and Consumers.

public class ChapterQueue {

    private final BlockingQueue<Task> queue;

    // Creates a bounded queue with a capacity of 100 tasks
    public ChapterQueue() {
        this.queue = new LinkedBlockingQueue<>(100);
    }

    // Blocks if the queue is full until space is available
    public void put(Task task) throws InterruptedException {
        queue.put(task);
    }

    // Blocks if the queue is empty until a task is available
    public Task take() throws InterruptedException {
        return queue.take();
    }
}