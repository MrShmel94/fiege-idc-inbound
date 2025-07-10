package idc.inbound.utils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class CompressionUtils {

    public static String compress(String str) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             GZIPOutputStream gzipOS = new GZIPOutputStream(bos)) {
            gzipOS.write(str.getBytes(StandardCharsets.UTF_8));
            gzipOS.close();
            byte[] compressed = bos.toByteArray();
            return Base64.getEncoder().encodeToString(compressed);
        } catch (IOException e) {
            throw new RuntimeException("Failed to compress string", e);
        }
    }

    public static String decompress(String compressedStr) {
        byte[] compressed = Base64.getDecoder().decode(compressedStr);
        try (ByteArrayInputStream bis = new ByteArrayInputStream(compressed);
             GZIPInputStream gzipIS = new GZIPInputStream(bis);
             InputStreamReader isr = new InputStreamReader(gzipIS, StandardCharsets.UTF_8);
             BufferedReader br = new BufferedReader(isr)) {

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        } catch (IOException e) {
            throw new RuntimeException("Failed to decompress string", e);
        }
    }
}
