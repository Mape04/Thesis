package service;

import domain.ElectionAuthority;
import domain.RsaKey;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import repository.ElectionAuthorityRepository;
import repository.ElectionRepository;
import repository.RsaKeyRepository;

import javax.crypto.SecretKey;
import java.io.FileInputStream;
import java.security.*;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class ElectionAuthorityService {
    private final ElectionAuthorityRepository electionAuthorityRepository;
    private final KeyGenerationService keyGenerationService;


    @Autowired
    public ElectionAuthorityService(ElectionAuthorityRepository electionAuthorityRepository, KeyGenerationService keyGenerationService) throws NoSuchAlgorithmException {
        this.electionAuthorityRepository = electionAuthorityRepository;
        this.keyGenerationService = keyGenerationService;
    }

    public ElectionAuthority createElectionAuthority(ElectionAuthority authority) throws Exception {
        // First save authority without RSA key
        authority = electionAuthorityRepository.save(authority);

        // Then generate RSA key
        RsaKey rsaKey = keyGenerationService.generateNewRsaKey(authority.getElectionAuthorityId());

        // Attach RSA key to authority
        authority.setRsaKeyId(rsaKey);

        // ⚡ Important: save the authority again to update the RSA key field!
        authority = electionAuthorityRepository.save(authority);

        return authority;
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

}
