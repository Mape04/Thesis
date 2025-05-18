package validators;

import domain.Candidate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import repository.CandidateRepository;

import java.util.UUID;

@Component
public class CandidateValidator implements Validator<Candidate> {
    public final CandidateRepository candidateRepository;

    public CandidateValidator(CandidateRepository candidateRepository) {
        this.candidateRepository = candidateRepository;
    }

    public void validateName(Candidate candidate){
        if(candidate.getCandidateName() == null || candidate.getCandidateName().isEmpty()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Candidate name cannot be empty.");
        }
    }

    public void validateParty(Candidate candidate){
        if(candidate.getCandidateParty() == null || candidate.getCandidateParty().isEmpty()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Candidate party cannot be empty.");
        }
    }

    public void validateDuplicate(Candidate candidate) {
        UUID electionId = candidate.getElection().getElectionId();

        boolean exists = candidateRepository.existsByCandidateNameAndCandidatePartyAndElection_ElectionId(
                candidate.getCandidateName(),
                candidate.getCandidateParty(),
                electionId
        );

        if (exists) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Duplicate candidate in this election.");

        }
    }


    @Override
    public void validate(Candidate candidate) {
        if(candidate == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Candidate cannot be null.");

        validateName(candidate);
        validateParty(candidate);
        validateDuplicate(candidate);
    }
}
