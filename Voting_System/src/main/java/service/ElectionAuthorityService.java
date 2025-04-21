package service;

import domain.ElectionAuthority;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import repository.ElectionAuthorityRepository;
import repository.ElectionRepository;

import java.security.*;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
public class ElectionAuthorityService {
    private final ElectionAuthorityRepository electionAuthorityRepository;
    private final KeyPair keyPair;

    @Autowired
    public ElectionAuthorityService(ElectionAuthorityRepository electionAuthorityRepository) throws NoSuchAlgorithmException {
        this.keyPair = generateKeyPair();
        this.electionAuthorityRepository = electionAuthorityRepository;
    }

    public ElectionAuthority createElectionAuthority(ElectionAuthority authority) {
        return electionAuthorityRepository.save(authority);
    }

    public List<ElectionAuthority> getAllElectionAuthorities() {
        return electionAuthorityRepository.findAll();
    }

    public ElectionAuthority getElectionAuthorityById(UUID id) {
        return electionAuthorityRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Election Authority not found!"));
    }

    public void deleteElectionAuthority(UUID id) {
        electionAuthorityRepository.deleteById(id);
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
