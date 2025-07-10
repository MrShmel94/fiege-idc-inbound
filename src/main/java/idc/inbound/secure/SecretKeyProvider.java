package idc.inbound.secure;

import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Component
@Getter
public class SecretKeyProvider {

    private final SecretKey secretKey;

    public SecretKeyProvider(@Value("${tokenSecret}") String tokenSecret) {
        byte[] secretKeyBytes = Base64.getDecoder().decode(tokenSecret.getBytes());
        this.secretKey = new SecretKeySpec(secretKeyBytes, SignatureAlgorithm.HS512.getJcaName());
    }
}
