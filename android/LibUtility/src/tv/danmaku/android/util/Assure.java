package tv.danmaku.android.util;

import java.util.Collection;

import android.text.TextUtils;

public class Assure {
    public static void checkTrue(boolean value) {
        if (!value)
            throw new IllegalArgumentException("AssureTrue");
    }

    public static void checkNotNull(Object obj) {
        if (obj == null)
            throw new IllegalArgumentException("AssureNotNull");
    }

    public static void CheckNotEqual(int expectNot, int real) {
        if (expectNot == real)
            throw new IllegalArgumentException("AssureNotEqual");
    }

    public static void CheckEqual(int expect, int real) {
        if (expect != real)
            throw new IllegalArgumentException("AssureEqual");
    }

    public static void CheckEqualNoCase(String expect, String real) {
        if (expect == null && real == null)
            return;

        if (expect == null || real == null)
            throw new IllegalArgumentException("AssureEqual");

        if (!expect.equalsIgnoreCase(real))
            throw new IllegalArgumentException("AssureEqual");
    }

    public static void checkNotEmptyString(String webUrl) {
        if (TextUtils.isEmpty(webUrl))
            throw new IllegalArgumentException("AssureNotEmptyString");
    }

    public static <E> void checkNotEmptyCollection(Collection<E> collection) {
        if (collection == null || collection.isEmpty())
            throw new IllegalArgumentException("AssureNotEmptyCollection");
    }

    public static <E> void checkNotEmptyArray(E[] collection) {
        if (collection == null || collection.length <= 0)
            throw new IllegalArgumentException("checkNotEmptyArray");
    }
}
