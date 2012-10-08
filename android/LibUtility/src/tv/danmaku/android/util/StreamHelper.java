package tv.danmaku.android.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class StreamHelper {
    public static String readStreamLineByLine(InputStream inputStream) {
        InputStreamReader reader = new InputStreamReader(inputStream);
        BufferedReader buffReader = new BufferedReader(reader);

        StringBuilder text = new StringBuilder();
        String resultText = null;

        try {
            String line;
            while ((line = buffReader.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            resultText = text.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                buffReader.close();
                reader.close();
            } catch (Exception e) {

            }
        }

        return resultText;
    }
    
    public static void closeStream(InputStream inputStream) {
        if (inputStream == null)
            return;

        try {
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void closeReader(BufferedReader reader) {
        if (reader == null)
            return;

        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } 
    }
}
