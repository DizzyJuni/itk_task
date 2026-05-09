package blockingQueue;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        BlockingQueue<Integer> blockingQueue = new BlockingQueue<>(5);

        Thread producer = new Thread(() -> {
            for (int i = 1; i <= 10; i++) {
                try {
                    blockingQueue.enqueue(i);
                    System.out.println("Производитель добавил " + i);
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                System.out.println("Производитель закончил.");
            }
        });

        Thread consumer = new Thread(() -> {
            for (int i = 1; i <= 10; i++) {
                try {
                    Integer item = blockingQueue.dequeue();
                    System.out.println("Потребитель достал элемент " + item);
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        producer.start();
        consumer.start();

        producer.join();
        consumer.join();

        System.out.println("Все потоки завершены");
        System.out.println("Финальный размер очереди: " + blockingQueue.size());
    }
}
