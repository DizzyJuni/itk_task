package countOfElements;

import java.util.HashMap;
import java.util.Map;

public class CountOfElements {

    public static <T> Map<T, Integer> countOfElements(T[] array) {
        Map<T, Integer> result = new HashMap<>();
        for (T element : array) {
            result.put(element, result.getOrDefault(element, 0) + 1);
        }
        return result;
    }
}
