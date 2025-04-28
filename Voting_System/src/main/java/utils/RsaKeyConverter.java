package utils;

import org.springframework.stereotype.Component;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Component
public class RsaKeyConverter {

    // Convert the public key to Base64 for storage (if necessary)
    public String toBase64PublicKey(PublicKey publicKey) {
        return Base64.getEncoder().encodeToString(publicKey.getEncoded());
    }

    // Convert the private key to Base64 for storage (if necessary)
    public String toBase64PrivateKey(PrivateKey privateKey) {
        return Base64.getEncoder().encodeToString(privateKey.getEncoded());
    }

    // Convert from byte[] (database format) to PublicKey
    public PublicKey fromByteArrayPublicKey(byte[] publicKeyBytes) throws Exception {
        X509EncodedKeySpec spec = new X509EncodedKeySpec(publicKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(spec);
    }

    // Convert from byte[] (database format) to PrivateKey
    public PrivateKey fromByteArrayPrivateKey(byte[] privateKeyBytes) throws Exception {
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(keySpec);
    }

    // If you want to directly convert from Base64 (for legacy reasons or different use cases):
    public PublicKey fromBase64PublicKey(String base64PublicKey) throws Exception {
        byte[] decoded = Base64.getDecoder().decode(base64PublicKey);
        return fromByteArrayPublicKey(decoded);
    }

    public PrivateKey fromBase64PrivateKey(String privateKeyBase64) throws Exception {
        byte[] decodedBytes = Base64.getDecoder().decode(privateKeyBase64);
        return fromByteArrayPrivateKey(decodedBytes);
    }
}
