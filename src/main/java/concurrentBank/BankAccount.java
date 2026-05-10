package concurrentBank;

import java.math.BigDecimal;
import java.util.concurrent.locks.ReentrantLock;

public class BankAccount {
    private BigDecimal balance;
    private final Long id;
    private static long nextId = 1;
    private final ReentrantLock lock = new ReentrantLock();

    protected BankAccount(BigDecimal balance) {
        this.balance = balance;
        this.id = nextId++;
    }

    public void deposit(BigDecimal amount) {
        lock.lock();
        try {
            if (amount.compareTo(BigDecimal.ZERO) > 0) {
                balance = balance.add(amount);
                System.out.println("Счет " + id + " пополнен на " + amount + ". Баланс: " + balance);
            }
        } finally {
            lock.unlock();
        }
    }

    public boolean withdraw(BigDecimal amount) {
        lock.lock();
        try {
            if (amount.compareTo(BigDecimal.ZERO) > 0 && balance.compareTo(amount) >= 0) {
                balance = balance.subtract(amount);
                System.out.println("Со счета " + id + " снято " + amount + ". Баланс: " + balance);
                return true;
            } else {
                System.out.println("Счет " + id + ": Недостаточно средств. Баланс: " + balance);
                return false;
            }
        } finally {
            lock.unlock();
        }
    }

    public BigDecimal getBalance() {
        lock.lock();
        try {
            return this.balance;
        } finally {
            lock.unlock();
        }
    }

    protected Long getId() {
        return id;
    }

    protected ReentrantLock getLock() {
        return lock;
    }
}
