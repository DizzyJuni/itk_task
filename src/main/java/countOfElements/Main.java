package countOfElements;

import static countOfElements.CountOfElements.countOfElements;

/*** Второе задание в Collection.
 * Так же используем дженерики, чтобы принимать массив любого типа.
 * В статическом методе класса CountOfElements создаю новую Map,
 * после циклом прохожу по всем элемента массива и считаю сколько раз они встречаются.
 ***/
public class Main {
    public static void main(String[] args) {
        String[] strings = {"Str1", "Str2", "Str3", "Str3"};
        System.out.println(countOfElements(strings));
    }
}
