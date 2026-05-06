package myStringBuilder;

/*** Реализовал максимально простой свой StringBuilder.
 * Чтобы продемонстрировать паттерн Snapshot думаю будет достаточно.
 * Сделал только метод append и undo.
 * Вспомогательный метод для автоматического расширения массива ensureCapacity.
 * Максимально приближенная логика к StringBuilder. Сильно не пытался его изучать,
 * провалился внутрь его в IDE и полистал методы.
 * --------------------------------------
 * Попробую своими простыми словами рассказать про паттерн Snapshot.
 * Для его реализации нужен организатор действия, в моем случае это MyStringBuilder.
 * Он выполняет какую то свою логику.
 * Второй пункт данного паттерна это снимки текущего состояния объекта.
 * Они должны быть спрятаны от всего остального мира, неизменяемые, чтобы история хранилась действительно правильно.
 * Третий пункт нужно хранилище снимков, тот кто в любой момент времени достанет или добавит снимки в коллекцию.
 * У себя не стал прибегать к отдельному классу, за это так же отвечает MyStringBuilder.
 * Организатор выполняет свою работу, Snapshot делает снимки, Хранилище даже не понимает что хранит внутри себя,
 * просто достает это, а организатор по снимкам определяет как можно восстановить свое состояние.
 ***/

public class MyStringBuilder {
    private char[] string;
    private int size;
    private Snapshot[] history;
    private int historySize;

    private static final int DEFAULT_CAPACITY = 16;

    public MyStringBuilder() {
        string = new char[DEFAULT_CAPACITY];
        size = 0;
        history = new Snapshot[DEFAULT_CAPACITY];
        historySize = 0;
    }

    public MyStringBuilder append(String str) {
        if (str == null) {
            str = "null";
        }
        int strLength = str.length();
        if (strLength == 0) {
            return this;
        }

        if (historySize == history.length) {
            Snapshot[] newHistory = new Snapshot[history.length * 2 + 2];
            System.arraycopy(history, 0, newHistory, 0, historySize);
            history = newHistory;
        }
        history[historySize++] = createSnapshot();

        ensureCapacity(size + strLength);

        for (int i = 0; i < strLength; i++) {
            string[size + i] = str.charAt(i);
        }
        size += strLength;
        return this;
    }

    public void undo() {
        if (historySize > 0) {
            Snapshot snapshot = history[--historySize];
            restoreFromSnapshot(snapshot);
        }
    }

    private void ensureCapacity(int requiredCapacity) {
        if (requiredCapacity > string.length) {
            int newCapacity = string.length * 2 + 2;
            if (newCapacity < requiredCapacity) {
                newCapacity = requiredCapacity;
            }

            char[] newString = new char[newCapacity];
            System.arraycopy(string, 0, newString, 0, size);
            string = newString;
        }
    }

    private Snapshot createSnapshot() {
        return new Snapshot(toString(), this.size);
    }

    private void restoreFromSnapshot(Snapshot snapshot) {
        String restoreString = snapshot.getSnapshot();

        if (restoreString.length() > string.length) {
            string = new char[restoreString.length() * 2];
        }

        for (int i = 0; i < restoreString.length(); i++) {
            string[i] = restoreString.charAt(i);
        }

        size = snapshot.getSize();
    }

    private static class Snapshot {
        private final String snapshot;
        private final int size;

        private Snapshot(String snapshot, int size) {
            this.snapshot = snapshot;
            this.size = size;
        }

        private String getSnapshot() {
            return snapshot;
        }

        private int getSize() {
            return size;
        }
    }

    @Override
    public String toString() {
        return new String(string, 0, size);
    }
}
