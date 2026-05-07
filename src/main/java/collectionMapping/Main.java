package collectionMapping;
import java.util.Arrays;
import static collectionMapping.ArrayMapping.arrayMapping;


/*** Реализовал метод applyMapping() в отдельном классе сделав его статическим.
 * Метод принимает массив любых данных, достигается через параметризированный тип.
 * Второй аргумент интерфейс из пакета java.lang.functional
 * Итерируемся по массиву, к каждому элементу применяем функцию apply
 * и возращаем тот же тип с примененной функцией T apply(T o).
 ***/
public class Main {
    public static void main(String[] args) {
        Integer[] integers = {1, 2, 3, 4, 5};
        Integer[] doubles = arrayMapping(integers, x -> x * 2);
        System.out.println(Arrays.toString(doubles));

        String[] lowerCase = {"one", "two", "three"};
        String[] upperCase = arrayMapping(lowerCase, String::toUpperCase);
        System.out.println(Arrays.toString(upperCase));
    }
}
