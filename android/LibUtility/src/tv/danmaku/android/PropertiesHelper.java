package tv.danmaku.android;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import tv.danmaku.android.util.StreamHelper;
import android.content.Context;
import android.content.res.AssetManager;

public class PropertiesHelper {
    public static Properties createFromAsset(Context context, String path) {
        AssetManager am = context.getAssets();
        if (am == null)
            return null;

        InputStream inputStream = null;
        Properties properties = null;
        try {
            inputStream = am.open(path);

            properties = new Properties();
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        StreamHelper.closeStream(inputStream);
        return properties;
    }

    public static String getString(Properties prop, String name) {
        return prop.getProperty(name);
    }

    public static int getInt(Properties prop, String name, int defaultValue) {
        String value = prop.getProperty(name);
        if (value == null)
            return defaultValue;

        try {
            return Integer.valueOf(value);
        } catch (NumberFormatException e) {
        }

        return defaultValue;
    }
}
