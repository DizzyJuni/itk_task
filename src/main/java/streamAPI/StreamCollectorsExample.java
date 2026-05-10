package streamAPI;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StreamCollectorsExample {
    public static void main(String[] args) {

        //Создайте список заказов с разными продуктами и их стоимостями.
        List<Order> orders = List.of(
                new Order("Laptop", 1200.0),
                new Order("Smartphone", 800.0),
                new Order("Laptop", 1500.0),
                new Order("Tablet", 500.0),
                new Order("Smartphone", 900.0),
                new Order("Headphones", 200.0)
        );
        System.out.println("Шаг 1. Исходный список заказов.");
        orders.forEach(System.out::println);

        // Группируйте заказы по продуктам.
        System.out.println("\nШаг 2. Список заказов по продуктам.");
        orders.stream().collect(Collectors.groupingBy(Order::getProduct))
                .forEach((order, productList) ->
                        System.out.println(order + ": " + productList));

        //Для каждого продукта найдите общую стоимость всех заказов.
        System.out.println("\nШаг 3. Общая стоимость заказов для каждого продукта.");
        orders.stream().collect(Collectors.groupingBy(
                        Order::getProduct, Collectors.summingDouble(Order::getCost)))
                .forEach((order, amount) -> System.out.printf("%s: %.2f%n", order, amount));

        //Отсортируйте продукты по убыванию общей стоимости.
        System.out.println("\nШаг 4. Продукты по убыванию общей сложности.");
        orders.stream().collect(Collectors.groupingBy(Order::getProduct,
                        Collectors.summingDouble(Order::getCost)))
                .entrySet()
                .stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .forEach(entry -> System.out.printf("%s: %.2f%n",
                        entry.getKey(), entry.getValue()));

        //Выберите три самых дорогих продукта.
        System.out.println("\nШаг 5. Три самых дорогих продукта.");
        orders.stream().collect(Collectors.groupingBy(Order::getProduct,
                        Collectors.summingDouble(Order::getCost)))
                .entrySet()
                .stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(3)
                .forEach(entry -> System.out.printf("%s: %.2f%n",
                        entry.getKey(), entry.getValue()));

        //Выведите результат: список трех самых дорогих продуктов и их общая стоимость.
        System.out.println("\nШаг 6. Три самых дорогих продукта и их общая стоимость.");
        Object result = orders.stream()
                .collect(Collectors.groupingBy(Order::getProduct,
                        Collectors.summingDouble(Order::getCost)))
                .entrySet()
                .stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(3)
                .collect(Collectors.teeing(
                        Collectors.toList(),
                        Collectors.summingDouble(Map.Entry::getValue),
                        (list, sum) -> new Object() {
                            final List<Map.Entry<String, Double>> topList = list;
                            final double total = sum;

                            @Override
                            public String toString() {
                                StringBuilder sb = new StringBuilder();
                                topList.forEach(e -> sb.append(String.format("  %s: %.2f%n",
                                        e.getKey(), e.getValue())));
                                sb.append(String.format("Общая стоимость: %.2f", total));
                                return sb.toString();
                            }
                        }
                ));

        System.out.println(result);
    }
}