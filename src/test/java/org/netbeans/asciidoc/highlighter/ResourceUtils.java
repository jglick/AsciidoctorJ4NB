package org.netbeans.asciidoc.highlighter;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public final class ResourceUtils {
    public static String readResource(Class<?> baseClass, String relPath) {
        return readResource(baseClass, relPath, StandardCharsets.UTF_8);
    }

    public static String readResource(Class<?> baseClass, String relPath, Charset encoding) {
        try (InputStream input = baseClass.getResourceAsStream(relPath)) {
            if (input == null) {
                throw new IllegalArgumentException("Missing resource: " + relPath + " for class " + baseClass.getName());
            }

            Reader reader = new InputStreamReader(input, encoding);

            StringBuilder result = new StringBuilder(256);
            char[] buffer = new char[8 * 1024];
            while (true) {
                int readCount = reader.read(buffer);
                if (readCount <= 0) {
                    break;
                }
                result.append(buffer, 0, readCount);
            }
            return result.toString();
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    private ResourceUtils() {
        throw new AssertionError();
    }
}
