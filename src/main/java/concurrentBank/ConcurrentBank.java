package concurrentBank;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class ConcurrentBank {
    private List<BankAccount> bankAccountList;
    private final ReentrantLock bankLock = new ReentrantLock();

    public ConcurrentBank() {
        this.bankAccountList = new ArrayList<>();
    }

    public BankAccount createAccount(long balance) {
        BankAccount bankAccount = new BankAccount(BigDecimal.valueOf(balance));
        bankLock.lock();
        try {
            bankAccountList.add(bankAccount);
            System.out.println("Создан счет #" + bankAccount.getId() + " с балансом: " + balance);
            return bankAccount;
        } finally {
            bankLock.unlock();
        }
    }

    public void transfer(BankAccount fromAccount, BankAccount toAccount, BigDecimal amount) {

        BankAccount first, second;

        if (fromAccount.getId() < toAccount.getId()) {
            first = fromAccount;
            second = toAccount;
        } else {
            first = toAccount;
            second = fromAccount;
        }

        first.getLock().lock();
        second.getLock().lock();

        try {
            System.out.println("Начинаем перевод " + amount + " со счета #" + fromAccount.getId() +
                    " на счет #" + toAccount.getId());
            if (fromAccount.withdraw(amount)) {
                toAccount.deposit(amount);
                System.out.println("Перевод выполнен успешно!");
            } else {
                System.out.println("Перевод не выполнен: недостаточно средств на счете #" + fromAccount.getId());
            }
        } finally {
            second.getLock().unlock();
            first.getLock().unlock();
        }
    }

    public BigDecimal getTotalBalance() {
        bankLock.lock();
        try {
            BigDecimal total = BigDecimal.ZERO;
            for (BankAccount account : bankAccountList) {
                total = total.add(account.getBalance());
            }
            System.out.println("Общий баланс банка: " + total);
            return total;
        } finally {
            bankLock.unlock();
        }
    }
}
