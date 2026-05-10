package streamAPI;

public class Order {
    private final String product;
    private final double cost;

    public Order(String product, double cost) {
        this.product = product;
        this.cost = cost;
    }

    public String getProduct() {
        return product;
    }

    public double getCost() {
        return cost;
    }

    @Override
    public String toString() {
        return "Product " + product + ": cost " + cost;
    }
}