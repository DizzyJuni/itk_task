package blockingQueue;

import java.util.LinkedList;
import java.util.Queue;

public class BlockingQueue<T> {
    private final Queue<T> queue;
    private final int capacity;
    private int size;

    public BlockingQueue(int capacity) {
        this.queue = new LinkedList<>();
        this.capacity = capacity;
        this.size = 0;
    }

    public synchronized void enqueue(T element) throws InterruptedException {
        while (size == capacity) {
            wait();
        }

        queue.add(element);
        size++;
        notifyAll();
    }

    public synchronized T dequeue() throws InterruptedException {
        while (size == 0) {
            wait();
        }

        T element = queue.poll();
        size--;
        notifyAll();

        return element;
    }

    public synchronized int size() {
        return size;
    }
}
