package service;

import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.engines.RSABlindedEngine;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.util.PrivateKeyFactory;
import org.bouncycastle.crypto.util.PublicKeyFactory;
import org.bouncycastle.util.encoders.Hex;
import org.springframework.stereotype.Service;

import java.security.PrivateKey;
import java.security.PublicKey;

@Service
public class BlindSignatureService {

    public byte[] signBlindedMessage(byte[] blindedMessage, PrivateKey privateKey) throws Exception {
        RSAKeyParameters privateKeyParams = (RSAKeyParameters) PrivateKeyFactory.createKey(privateKey.getEncoded());
        AsymmetricBlockCipher rsa = new RSABlindedEngine();
        rsa.init(true, privateKeyParams);
        return rsa.processBlock(blindedMessage, 0, blindedMessage.length);
    }

    public boolean verifyUnblindedSignature(byte[] message, byte[] signature, PublicKey publicKey) throws Exception {
        RSAKeyParameters publicKeyParams = (RSAKeyParameters) PublicKeyFactory.createKey(publicKey.getEncoded());
        AsymmetricBlockCipher rsa = new RSABlindedEngine();
        rsa.init(false, publicKeyParams);

        byte[] decrypted = rsa.processBlock(signature, 0, signature.length);

        // Convert to hex string manually using Hex.encode
        String decryptedHex = new String(Hex.encode(decrypted));
        String messageHex = new String(Hex.encode(message));

        return decryptedHex.equals(messageHex);
    }
}
