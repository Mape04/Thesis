package service;

import org.springframework.stereotype.Service;
import java.security.*;
import java.util.Base64;

@Service
public class ElectionAuthority {
    private final KeyPair keyPair;

    public ElectionAuthority() throws NoSuchAlgorithmException {
        this.keyPair = generateKeyPair();
    }

    private KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        return keyGen.generateKeyPair();
    }

    public PublicKey getPublicKey() {
        return keyPair.getPublic();
    }

    public String signMessage(byte[] blindedMessage) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(keyPair.getPrivate());
        signature.update(blindedMessage);
        return Base64.getEncoder().encodeToString(signature.sign());
    }
}
