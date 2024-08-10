package fr.kosmosuniverse.kems.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author KosmosUniverse
 */
public class FileUtils {
    private FileUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static String readFileContent(InputStream in) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        StringBuilder sb = new StringBuilder();

        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line).append(System.lineSeparator());
        }

        return sb.toString();
    }
}
