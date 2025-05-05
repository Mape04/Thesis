package service;

import domain.*;
import dto.CandidateDTO;
import dto.ElectionDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import repository.CandidateRepository;
import repository.ElectionAuthorityRepository;
import repository.ElectionRepository;
import repository.VoterRepository;
import utils.DTOUtils;

import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ElectionService {

    private final ElectionRepository electionRepository;
    private final ElectionAuthorityRepository electionAuthorityRepository;
    private final CandidateRepository candidateRepository;

    @Autowired
    public ElectionService(ElectionRepository electionRepository, ElectionAuthorityRepository electionAuthorityRepository, CandidateRepository candidateRepository) {
        this.electionRepository = electionRepository;
        this.electionAuthorityRepository = electionAuthorityRepository;
        this.candidateRepository = candidateRepository;
    }

    public Election createElection(UUID authorityId, ElectionDTO electionData) {
        ElectionAuthority authority = electionAuthorityRepository.findById(authorityId)
                .orElseThrow(() -> new RuntimeException("Authority not found."));

        Election newElection = new Election();
        newElection.setElectionName(electionData.getElectionName());
        newElection.setStartDate(electionData.getStartDate());
        newElection.setEndDate(electionData.getEndDate());
        newElection.setElectionVotes(0);
        newElection.setElectionDescription(electionData.getElectionDescription());
        newElection.setNrVotesPerVoter(electionData.getNrVotesPerVoter());
        newElection.setElectionType(electionData.getElectionType());
        newElection.setElectionAuthority(authority);

        // Password hashing (optional)
        String rawPassword = electionData.getElectionPassword();
        if (rawPassword != null && !rawPassword.isBlank()) {
            String hashedPassword = BCrypt.hashpw(rawPassword, BCrypt.gensalt());
            newElection.setElectionPassword(hashedPassword);
        } else {
            newElection.setElectionPassword(null);
        }

        // Store runoff dates if needed
        if (electionData.getElectionType() == ElectionType.TOP_TWO_RUNOFF) {
            newElection.setNrVotesPerVoter(1); // force 1 vote for runoff mode
            newElection.setRunoffStartDate(electionData.getRunoffStartDate());
            newElection.setRunoffEndDate(electionData.getRunoffEndDate());
        }

        return electionRepository.save(newElection);
    }



    public Election updateElection(UUID electionId, Election updatedData) {
        Election election = electionRepository.findById(electionId)
                .orElseThrow(() -> new RuntimeException("Election not found"));

        if (LocalDateTime.now().isAfter(election.getEndDate())) {
            throw new IllegalStateException("Cannot edit a finished election.");
        }

        election.setElectionName(updatedData.getElectionName());
        election.setElectionDescription(updatedData.getElectionDescription());
        election.setStartDate(updatedData.getStartDate());
        election.setEndDate(updatedData.getEndDate());
        election.setNrVotesPerVoter(updatedData.getNrVotesPerVoter());
        election.setElectionType(updatedData.getElectionType());
        election.setElectionPassword(updatedData.getElectionPassword());

        return electionRepository.save(election);
    }



    // Create or update an election
    public Election saveElection(Election election) {
        return electionRepository.save(election);
    }

    // Get all elections
    public List<Election> getAllElections() {
        return electionRepository.findAll();
    }

    // Get an election by ID
    public Optional<Election> getElectionById(UUID id) {
        return electionRepository.findById(id);
    }

    // Delete an election by ID
    public void deleteElection(UUID id) {
        electionRepository.deleteById(id);
    }

    public void createRunoffFromTopTwo(Election original) {
        if (original.getElectionType() != ElectionType.TOP_TWO_RUNOFF) {
            throw new IllegalArgumentException("This election is not a TOP_TWO_RUNOFF type.");
        }

        // Use a Pageable to get top 2 candidates
        Pageable topTwoPage = PageRequest.of(0, 2);
        List<Candidate> topTwo = candidateRepository.findTopNCandidatesByElectionId(original.getElectionId(), topTwoPage);

        if (topTwo.size() < 2) {
            throw new IllegalStateException("Not enough candidates to create a runoff election.");
        }

        // Create the runoff election
        Election runoff = new Election();
        runoff.setElectionName(original.getElectionName() + " - Runoff");
        runoff.setStartDate(original.getRunoffStartDate()); // Customize as needed
        runoff.setEndDate(original.getRunoffEndDate());
        runoff.setElectionVotes(0);
        runoff.setNrVotesPerVoter(1);
        runoff.setElectionType(ElectionType.STANDARD);
        runoff.setElectionDescription("Runoff election for top two candidates of " + original.getElectionName());
        runoff.setElectionAuthority(original.getElectionAuthority());

        electionRepository.save(runoff);

        // Copy the top 2 candidates to the new election
        for (Candidate c : topTwo) {
            Candidate clone = new Candidate();
            clone.setCandidateName(c.getCandidateName());
            clone.setCandidateParty(c.getCandidateParty());
            clone.setElection(runoff);
            clone.setNrOfVotes(0); // Reset votes
            candidateRepository.save(clone);
        }

        // Link runoff to original
        original.setRunoffElection(runoff);
        electionRepository.save(original);

        //return runoff;
    }


}
