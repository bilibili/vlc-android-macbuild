package tv.danmaku.android.util;

import java.util.Collection;

import android.text.TextUtils;

public class CollectionHelper {
    public final static <T> void Append(Collection<T> collection, T[] array) {
        if (collection != null && array != null && array.length > 0) {
            for (T element : array) {
                collection.add(element);
            }
        }
    }

    public final static <T> void Append(Collection<T> collection,
            T[][] array_array) {
        if (collection != null && array_array != null && array_array.length > 0) {
            for (T[] array : array_array) {
                Append(collection, array);
            }
        }
    }

    public final static String[] toArray(Collection<String> collection) {
        return collection.toArray(new String[collection.size()]);
    }

    public final static boolean isAnyStringEmpty(String[] array) {
        if (array == null || array.length <= 0)
            return false;

        for (String elem : array) {
            if (TextUtils.isEmpty(elem))
                return true;
        }

        return false;
    }

    public final static boolean isAnyStringEmpty(Collection<String> collection) {
        if (collection == null || collection.isEmpty())
            return false;

        for (String elem : collection) {
            if (TextUtils.isEmpty(elem))
                return true;
        }

        return false;
    }
}
