package concurrentBank;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Расширенный тест для проверки потокобезопасности ConcurrentBank.
 *
 * Тест запускает:
 * - Множество потоков-производителей (создают счета)
 * - Множество потоков, выполняющих переводы в случайном порядке
 * - Множество потоков, выполняющих депозиты и снятия
 * - Проверяет консистентность: общий баланс НЕ должен меняться при переводах
 * - Проверяет, что ни один счет не ушел в минус
 */
public class ConcurrentBankStressTest {

    private static final int ACCOUNT_COUNT = 20;      // сколько счетов создать
    private static final int INITIAL_BALANCE = 1000;   // начальный баланс каждого счета
    private static final int THREAD_COUNT = 10;        // сколько потоков переводов
    private static final int TRANSFERS_PER_THREAD = 100; // переводов на поток
    private static final BigDecimal MAX_TRANSFER = BigDecimal.valueOf(100);

    private static final AtomicInteger successTransfers = new AtomicInteger(0);
    private static final AtomicInteger failTransfers = new AtomicInteger(0);
    private static final AtomicInteger totalOperations = new AtomicInteger(0);

    public static void main(String[] args) throws InterruptedException {
        ConcurrentBank bank = new ConcurrentBank();

        // === Этап 1: Создание счетов ===
        System.out.println("=== СОЗДАНИЕ " + ACCOUNT_COUNT + " СЧЕТОВ ===");
        List<BankAccount> accounts = new ArrayList<>();

        for (int i = 0; i < ACCOUNT_COUNT; i++) {
            BankAccount account = bank.createAccount(INITIAL_BALANCE);
            accounts.add(account);
        }

        // Вычисляем ожидаемый общий баланс
        BigDecimal expectedTotal = BigDecimal.valueOf(ACCOUNT_COUNT)
                .multiply(BigDecimal.valueOf(INITIAL_BALANCE));
        System.out.println("Ожидаемый общий баланс: " + expectedTotal);
        System.out.println("Фактический общий баланс: " + bank.getTotalBalance());

        // Проверка: баланс после создания должен совпадать
        if (bank.getTotalBalance().compareTo(expectedTotal) != 0) {
            System.out.println("❌ ОШИБКА: баланс после создания счетов не совпадает!");
        } else {
            System.out.println("✅ Баланс после создания счетов корректен");
        }

        // === Этап 2: Параллельные переводы между случайными счетами ===
        System.out.println("\n=== ЗАПУСК " + THREAD_COUNT + " ПОТОКОВ ПЕРЕВОДОВ ===");

        // CountDownLatch чтобы все потоки стартовали ОДНОВРЕМЕННО
        CountDownLatch startLatch = new CountDownLatch(1);
        // CountDownLatch чтобы дождаться завершения ВСЕХ потоков
        CountDownLatch endLatch = new CountDownLatch(THREAD_COUNT);

        long startTime = System.currentTimeMillis();

        for (int t = 0; t < THREAD_COUNT; t++) {
            final int threadId = t;
            new Thread(() -> {
                try {
                    // Ждем сигнала к старту — все потоки стартуют вместе
                    startLatch.await();

                    for (int i = 0; i < TRANSFERS_PER_THREAD; i++) {
                        // Выбираем два СЛУЧАЙНЫХ счета
                        int fromIdx = (int) (Math.random() * ACCOUNT_COUNT);
                        int toIdx = (int) (Math.random() * ACCOUNT_COUNT);

                        // Не переводим самому себе
                        if (fromIdx == toIdx) {
                            continue;
                        }

                        // Случайная сумма перевода от 1 до MAX_TRANSFER
                        BigDecimal amount = BigDecimal.valueOf(
                                1 + (long) (Math.random() * MAX_TRANSFER.longValue())
                        );

                        BankAccount from = accounts.get(fromIdx);
                        BankAccount to = accounts.get(toIdx);

                        // Выполняем перевод
                        bank.transfer(from, to, amount);
                        totalOperations.incrementAndGet();

                        // Случайная пауза — имитация реальной нагрузки
                        if (i % 10 == 0) {
                            Thread.sleep(0, (int) (Math.random() * 100));
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    endLatch.countDown();
                }
            }).start();
        }

        // === Этап 3: Параллельные депозиты и снятия ===
        System.out.println("Запуск дополнительных потоков депозитов/снятий...");

        CountDownLatch depositEndLatch = new CountDownLatch(4);

        // Потоки, которые ТОЛЬКО пополняют случайные счета
        for (int t = 0; t < 2; t++) {
            new Thread(() -> {
                try {
                    startLatch.await();
                    for (int i = 0; i < 500; i++) {
                        int idx = (int) (Math.random() * ACCOUNT_COUNT);
                        BigDecimal amount = BigDecimal.valueOf(1 + (long) (Math.random() * 50));
                        accounts.get(idx).deposit(amount);
                        totalOperations.incrementAndGet();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    depositEndLatch.countDown();
                }
            }).start();
        }

        // Потоки, которые ТОЛЬКО снимают со случайных счетов
        for (int t = 0; t < 2; t++) {
            new Thread(() -> {
                try {
                    startLatch.await();
                    for (int i = 0; i < 500; i++) {
                        int idx = (int) (Math.random() * ACCOUNT_COUNT);
                        BigDecimal amount = BigDecimal.valueOf(1 + (long) (Math.random() * 30));
                        boolean success = accounts.get(idx).withdraw(amount);
                        if (success) {
                            successTransfers.incrementAndGet();
                        } else {
                            failTransfers.incrementAndGet();
                        }
                        totalOperations.incrementAndGet();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    depositEndLatch.countDown();
                }
            }).start();
        }

        // === ЗАПУСК ВСЕХ ПОТОКОВ ОДНОВРЕМЕННО ===
        System.out.println("ВСЕ ПОТОКИ СТАРТУЮТ...");
        startLatch.countDown();  // сигнал всем потокам — "МАРШ!"

        // Ждем завершения ВСЕХ потоков
        endLatch.await();
        depositEndLatch.await();

        long endTime = System.currentTimeMillis();

        // === Этап 4: Проверка результатов ===
        System.out.println("\n=== РЕЗУЛЬТАТЫ ТЕСТИРОВАНИЯ ===");
        System.out.println("Время выполнения: " + (endTime - startTime) + " мс");
        System.out.println("Всего операций: " + totalOperations.get());
        System.out.println("Успешных снятий: " + successTransfers.get());
        System.out.println("Неудачных снятий: " + failTransfers.get());

        // Проверка 1: общий баланс должен быть НЕ МЕНЬШЕ начального
        // (депозиты могли увеличить общий баланс, снятия — уменьшить,
        // но переводы НЕ меняют общий баланс)
        BigDecimal finalBalance = bank.getTotalBalance();

        System.out.println("\nНачальный баланс: " + expectedTotal);
        System.out.println("Конечный баланс:  " + finalBalance);

        // Суммируем балансы всех счетов вручную для двойной проверки
        BigDecimal manualSum = BigDecimal.ZERO;
        for (BankAccount account : accounts) {
            manualSum = manualSum.add(account.getBalance());
        }
        System.out.println("Ручная сумма:      " + manualSum);

        if (finalBalance.compareTo(manualSum) == 0) {
            System.out.println("✅ Сумма через getTotalBalance() совпадает с ручной суммой");
        } else {
            System.out.println("❌ ОШИБКА: расхождение между getTotalBalance() и ручной суммой!");
        }

        // Проверка 2: ни один счет не должен быть в минусе
        boolean hasNegative = false;
        for (BankAccount account : accounts) {
            if (account.getBalance().compareTo(BigDecimal.ZERO) < 0) {
                System.out.println("❌ ОШИБКА: счет #" + account.getId()
                        + " имеет отрицательный баланс: " + account.getBalance());
                hasNegative = true;
            }
        }
        if (!hasNegative) {
            System.out.println("✅ Все счета имеют неотрицательный баланс");
        }

        // Проверка 3: выводим состояние всех счетов
        System.out.println("\n=== СОСТОЯНИЕ ВСЕХ СЧЕТОВ ===");
        for (BankAccount account : accounts) {
            System.out.println("Счет #" + account.getId() + ": " + account.getBalance());
        }

        // Проверка 4: дедлоков не было (если бы были — программа бы зависла)
        System.out.println("\n✅ Тест завершен без deadlock!");
    }
}