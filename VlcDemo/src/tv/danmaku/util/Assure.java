package tv.danmaku.util;

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
}
