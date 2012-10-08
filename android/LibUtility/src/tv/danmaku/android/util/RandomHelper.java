package tv.danmaku.android.util;

import java.util.Random;

public class RandomHelper {
    public static int getRandomInt() {
        return new Random(System.currentTimeMillis()).nextInt();
    }
}
