package completableFuture;

import java.util.concurrent.CompletableFuture;

public class DataAggregator {

    public ProductInfo aggregateProductInfo(String productName) {

        CompletableFuture<Double> price = CompletableFuture.supplyAsync(
                        () -> fetchPrice(productName))
                .exceptionally(ex -> {
                    System.out.println("Ошибка получения цены " + ex.getMessage());
                    return 0.0;
                });

        CompletableFuture<String> description = CompletableFuture.supplyAsync(
                        () -> fetchDescription(productName))
                .exceptionally(ex -> {
                    System.out.println("Ошибка получения описания " + ex.getMessage());
                    return "Нет данных";
                });

        CompletableFuture<Double> rating = CompletableFuture.supplyAsync(
                        () -> fetchRating(productName))
                .exceptionally(ex -> {
                    System.out.println("Ошибка получения рейтинга " + ex.getMessage());
                    return 0.0;
                });


        return CompletableFuture.allOf(price, description, rating)
                .thenApply(v -> {
                    double resultPrice = price.join();
                    String resultDescription = description.join();
                    double resultRating = rating.join();
                    return new ProductInfo(productName, resultPrice, resultDescription, resultRating);
                }).join();
    }

    private String fetchDescription(String productName) {
        sleepThread();
        throwIfFalls(0.2);
        return "Мощный игровой ноутбук";
    }

    private double fetchRating(String productName) {
        sleepThread();
        throwIfFalls(0.2);
        return 4.7;
    }

    private double fetchPrice(String productName) {
        sleepThread();
        throwIfFalls(0.2);
        return 899.99;
    }

    private void sleepThread() {
        try {
            long ms = (long) (1000 + (Math.random() * 2000));
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Поток прерван " + e);
        }
    }

    private void throwIfFalls(double probability) {
        if (Math.random() < probability) {
            throw new RuntimeException(" Сервис временно не доступен");
        }
    }
}
