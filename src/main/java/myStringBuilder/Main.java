package myStringBuilder;
/***
 * Простая проверка, ничего тестами не покрывал.
 * Ни каких граничных случаев не рассматривал.
 * Главное надеюсь что паттерн понял правильно.
 ***/
public class Main {
    public static void main(String[] args) {
        MyStringBuilder sb = new MyStringBuilder();
        sb.append("My").append(" first").append(" pattern")
                .append(" Snapshot")
                .append(" perfect!!!");
        System.out.println(sb); //My first pattern Snapshot perfect!!!
        sb.undo();
        sb.append(" not bad.");
        System.out.println(sb); //My first pattern Snapshot not bad.

        for (int i = 0; i < 5; i++) {
            sb.undo();
        }
        sb.append("Ok, i`m trying.");
        System.out.println(sb); //Ok, i`m trying.
    }
}