package tv.danmaku.util;

import java.util.Collection;

public class CollectionHelper {
    public static <T> void Append(Collection<T> collection, T[] array) {
        if (collection != null && array != null && array.length > 0) {
            for (T element : array) {
                collection.add(element);
            }
        }
    }
}
