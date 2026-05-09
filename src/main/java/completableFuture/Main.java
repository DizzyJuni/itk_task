package completableFuture;

public class Main {
    public static void main(String[] args) {
        DataAggregator aggregator = new DataAggregator();

        System.out.println("=== ТЕСТ 1: Одиночный запуск ===");
        System.out.println("Запрашиваем информацию о Ноутбуке...\n");

        long start = System.currentTimeMillis();
        ProductInfo product = aggregator.aggregateProductInfo("Ноутбук");
        long end = System.currentTimeMillis();

        System.out.println("\nРезультат: " + product);
        System.out.println("Время выполнения: " + (end - start) + " мс");
        System.out.println("(Должно быть ~1-3 секунды — доказательство параллельности!)\n");

        System.out.println("=== ТЕСТ 2: 10 запусков для проверки fallback ===");
        System.out.println("(Должны иногда появляться ошибки сервисов)\n");

        for (int i = 1; i <= 10; i++) {
            System.out.println("--- Запуск " + i + " ---");
            ProductInfo p = aggregator.aggregateProductInfo("Ноутбук");
            System.out.println(p);
            System.out.println();
        }
    }
}
