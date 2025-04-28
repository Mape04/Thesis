package service;

import domain.BlindCredential;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import repository.BlindCredentialRepository;

@Service
public class BlindCredentialService {
    private final BlindCredentialRepository blindCredentialRepository;

    @Autowired
    private BlindCredentialService(BlindCredentialRepository blindCredentialRepository){
        this.blindCredentialRepository = blindCredentialRepository;
    }


    public void save(BlindCredential blindCredential) {
        blindCredentialRepository.save(blindCredential);
    }
}
