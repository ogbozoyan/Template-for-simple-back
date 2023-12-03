package com.og.templateback.configuration.core.utils;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ContentDisposition;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * @author ogbozoyan
 * @since 28.07.2023
 */
@SuppressWarnings("unused")
@Slf4j
@Data
@NoArgsConstructor
public class EncodingFileUtils {
    public static String encodeFilename(String filename) {
        return URLEncoder.encode(filename, StandardCharsets.UTF_8);
    }

    public static String encodeRfc5987(String value) {
        final byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        final StringBuilder sb = new StringBuilder(bytes.length << 1);
        final char[] digits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        final byte[] attrChar = {'!', '#', '$', '&', '+', '-', '.', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H',
                'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '^', '_', '`', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h',
                'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '|', '~'};
        for (final byte b : bytes) {
            log.info("Processing byte: {}", b);
            if (Arrays.binarySearch(attrChar, b) >= 0) {
                log.info("Appending byte as is: {}", (char) b);

                sb.append((char) b);
            } else {
                log.info("Encoding byte: {}", b);
                sb.append('%');
                sb.append(digits[0x0f & (b >>> 4)]);
                sb.append(digits[b & 0x0f]);
            }
        }
        log.info("Encoded value: {}", sb);
        return sb.toString();
    }

    public static String getContentDispositionUTF8Filename(String filename) {
        return ContentDisposition.attachment()
                .filename(filename, StandardCharsets.UTF_8)
                .build()
                .toString();
    }
}
