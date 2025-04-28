package service;

import domain.Election;
import domain.ElectionAuthority;
import domain.RsaKey;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import repository.ElectionRepository;
import repository.RsaKeyRepository;
import utils.RsaKeyConverter;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import java.math.BigInteger;
import java.security.*;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;

@Service
public class EncryptionService {
    private final SecretKey SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    @Autowired
    private final RsaKeyConverter rsaKeyConverter;
    private final ElectionRepository electionRepository;

    public EncryptionService(RsaKeyConverter rsaKeyConverter, ElectionRepository electionRepository) {
        this.rsaKeyConverter = rsaKeyConverter;
        this.electionRepository = electionRepository;
    }

    public String generateVoterToken(UUID voterId) {
        return Jwts.builder()
                .setSubject(voterId.toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(SECRET_KEY)
                .compact();
    }

    public String signBlindedMessage(BigInteger blindedMessage, UUID electionId) throws Exception {
        Election election = electionRepository.findById(electionId)
                .orElseThrow(() -> new IllegalArgumentException("Election not found for ID: " + electionId));

        ElectionAuthority authority = election.getElectionAuthority();
        if (authority == null || authority.getRsaKeyId() == null) {
            throw new IllegalArgumentException("ElectionAuthority or RSA key missing for election: " + electionId);
        }

        RsaKey rsaKey = authority.getRsaKeyId();
        RsaKeyConverter rsaKeyConverter = new RsaKeyConverter();
        PrivateKey privateKey = rsaKeyConverter.fromByteArrayPrivateKey(rsaKey.getPrivateKey());

        Cipher cipher = Cipher.getInstance("RSA/ECB/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);

        byte[] blindedBytes = blindedMessage.toByteArray();

        // ⚡ Make sure blindedBytes is exactly the size of modulus (256 bytes)
        int keySizeBytes = 256; // 2048 bits key = 256 bytes
        if (blindedBytes.length > keySizeBytes) {
            // If leading 0 byte exists, remove it
            if (blindedBytes[0] == 0) {
                blindedBytes = Arrays.copyOfRange(blindedBytes, 1, blindedBytes.length);
            } else {
                throw new IllegalArgumentException("Blinded message too large for key size.");
            }
        }
        if (blindedBytes.length < keySizeBytes) {
            // If too short, pad with leading zeros
            byte[] padded = new byte[keySizeBytes];
            System.arraycopy(blindedBytes, 0, padded, keySizeBytes - blindedBytes.length, blindedBytes.length);
            blindedBytes = padded;
        }

        byte[] signedBytes = cipher.doFinal(blindedBytes);

        return new BigInteger(1, signedBytes).toString();
    }





    public Map<String, String> extractPublicKeyComponents(RsaKey rsaKey) throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(rsaKey.getPublicKey());
        RSAPublicKey publicKey = (RSAPublicKey) keyFactory.generatePublic(pubKeySpec);

        BigInteger modulus = publicKey.getModulus();
        BigInteger exponent = publicKey.getPublicExponent();

        Map<String, String> publicKeyMap = new HashMap<>();
        publicKeyMap.put("n", modulus.toString());
        publicKeyMap.put("e", exponent.toString());

        return publicKeyMap;
    }

}
