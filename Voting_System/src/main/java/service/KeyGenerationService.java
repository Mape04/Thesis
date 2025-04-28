package service;

import domain.RsaKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import repository.RsaKeyRepository;
import utils.RsaKeyConverter;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.UUID;

@Service
public class KeyGenerationService {

    private final RsaKeyRepository rsaKeyRepository;

    @Autowired
    public KeyGenerationService(RsaKeyRepository rsaKeyRepository) {
        this.rsaKeyRepository = rsaKeyRepository;
    }

    public RsaKey generateNewRsaKey(UUID electionId) throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);  // 2048 is secure enough for now

        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        RsaKey rsaKey = new RsaKey();
        rsaKey.setPublicKey(keyPair.getPublic().getEncoded());  // Store the public key as byte[]
        rsaKey.setPrivateKey(keyPair.getPrivate().getEncoded());  // Store the private key as byte[]
        rsaKey.setElectionAuthorityId(electionId);  // Set the electionAuthorityId
        rsaKeyRepository.save(rsaKey);

        return rsaKey;
    }

}
