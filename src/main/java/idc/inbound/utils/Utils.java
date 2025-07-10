package idc.inbound.utils;

import com.opencsv.CSVReader;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.exceptions.CsvValidationException;
import idc.inbound.customError.NotFoundException;
import idc.inbound.dto.unloading.BookingCSV;
import idc.inbound.secure.SecretKeyProvider;
import idc.inbound.secure.SecurityConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.SecretKey;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.*;

@Component
@AllArgsConstructor
public class Utils {

    private final SecretKeyProvider secretKeyProvider;
    private static final Random RANDOM = new SecureRandom();

    private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";
    private static final String SPECIAL = "!@#$%^&*()-_=+[]{}|;:,.<>?";

    private static final String ALL = UPPER + LOWER + DIGITS + SPECIAL;

    private static final Set<String> REQUIRED_HEADERS = Set.of(
            "Delivery type",
            "QTY PAL TOTAL",
            "QTY BOXES TOTAL",
            "QTY ITEMS TOTAL",
            "Planowana godzina przyjazdu",
            "Numer awizacji",
            "Booking ID",
            "Product Type",
            "Process Type",
            "Supplier type",
            "Pallet Exchange"
    );

    public void validateHeaders(MultipartFile file) {
        try (
                InputStreamReader isr = new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8);
                CSVReader reader = new CSVReader(isr)
        ) {
            String[] headers = reader.readNext();

            if (headers == null) {
                throw new RuntimeException("CSV file is empty");
            }

            Set<String> actualHeaders = new HashSet<>(Arrays.asList(headers));
            for (String required : REQUIRED_HEADERS) {
                if (!actualHeaders.contains(required)) {
                    throw new NotFoundException("Missing required column: " + required);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read CSV file", e);
        } catch (CsvValidationException e) {
            throw new RuntimeException(e);
        }
    }

    public List<BookingCSV> csvUnloadingReport(MultipartFile file) {
        try (
                InputStreamReader reader = new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8)
        ) {
            CsvToBean<BookingCSV> csvToBean = new CsvToBeanBuilder<BookingCSV>(reader)
                    .withType(BookingCSV.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();

            return csvToBean.parse().stream()
                    .filter(b -> {
                        return Arrays.stream(BookingCSV.class.getDeclaredFields())
                                .anyMatch(f -> {
                                    try {
                                        f.setAccessible(true);
                                        Object val = f.get(b);
                                        return val != null && !(val instanceof String s && s.isBlank());
                                    } catch (IllegalAccessException e) {
                                        return false;
                                    }
                                });
                    })
                    .toList();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String generateUserId(int length){
        return generateRandomString(length);
    }

    private String generateRandomString(int length) {
        StringBuilder builder = new StringBuilder();

        for( int i = 0; i < length; i++ ){
            String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
            builder.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }
        return builder.toString();
    }

    public static String generatePassword(int length) {
        if (length < 8) throw new IllegalArgumentException("Password length must be at least 8");

        List<Character> passwordChars = new ArrayList<>();

        passwordChars.add(UPPER.charAt(RANDOM.nextInt(UPPER.length())));
        passwordChars.add(DIGITS.charAt(RANDOM.nextInt(DIGITS.length())));
        passwordChars.add(SPECIAL.charAt(RANDOM.nextInt(SPECIAL.length())));

        for (int i = 3; i < length; i++) {
            passwordChars.add(ALL.charAt(RANDOM.nextInt(ALL.length())));
        }

        Collections.shuffle(passwordChars, RANDOM);

        StringBuilder sb = new StringBuilder(length);
        for (char c : passwordChars) {
            sb.append(c);
        }
        return sb.toString();
    }

    public String generateAccessToken(String userId, HttpServletRequest request) {
        SecretKey secretKey = secretKeyProvider.getSecretKey();
        Instant now = Instant.now();

        return Jwts.builder()
                .setSubject(userId)
                .setExpiration(Date.from(now.plusMillis(SecurityConstants.ACCESS_TOKEN_EXPIRATION)))
                .setIssuedAt(Date.from(now))
                .claim("ip", getClientIp(request))
                .claim("user-agent", request.getHeader("User-Agent"))
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .compact();
    }

    public String getClientIp(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("X-Real-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }
        if (ipAddress != null && ipAddress.contains(",")) {
            ipAddress = ipAddress.split(",")[0].trim();
        }

        return ipAddress;
    }

    public Claims parseToken(String token) {
        SecretKey secretKey = secretKeyProvider.getSecretKey();
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}