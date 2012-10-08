package tv.danmaku.android.util;

import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternHelper {

    public Pattern mPattern;

    public PatternHelper(String pattern) {
        mPattern = Pattern.compile(pattern);
    }

    public final String getFirstMatchString(String text) {
        Matcher matcher = mPattern.matcher(text);
        if (matcher == null)
            return null;

        if (!matcher.find())
            return null;

        MatchResult matchResult = matcher.toMatchResult();
        return matchResult.group(1);
    }

    public final int getFirstMatchInt(String text, int defaultValue) {
        String firstMatch = getFirstMatchString(text);
        if (firstMatch == null)
            return defaultValue;

        try {
            return Integer.valueOf(firstMatch);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
