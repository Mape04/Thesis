package service;

import domain.*;
import dto.VoteDTO;
import repository.*;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VoteService {

    private final VoteRepository voteRepository;
    private final BallotRepository ballotRepository;
    private final CandidateRepository candidateRepository;
    private final VoterRepository voterRepository;
    private final ElectionRepository electionRepository;
    private final ElectionAuthorityService electionAuthorityService;  // Injecting the service responsible for signing the blinded token
    private final BlindCredentialRepository blindCredentialRepository; // Assuming a repository to store BlindCredential

    public String submitVote(VoteDTO voteDTO) {
        try {
            // 1️⃣ Fetch the Voter
            Voter voter = voterRepository.findById(voteDTO.getVoterId())
                    .orElseThrow(() -> new IllegalArgumentException("Voter not found."));

            // 2️⃣ Validate Blind Token
            boolean validToken = voter.getBlindCredentialSet().stream()
                    .anyMatch(cred -> cred.getSignedToken().equals(voteDTO.getVoterToken()));

            if (!validToken) {
                return "Invalid voting token.";
            }

            // 3️⃣ Fetch Election
            Election election = electionRepository.findById(voteDTO.getElectionId())
                    .orElseThrow(() -> new IllegalArgumentException("Election not found."));

            // 4️⃣ Check Votes Per Voter Limit
            long alreadyVotedCount = voteRepository.countByVoterAndBallot_Election(voter, election);
            if (alreadyVotedCount >= election.getNrVotesPerVoter()) {
                return "Vote limit exceeded for this election.";
            }

            // 5️⃣ Create Ballot
            Ballot ballot = new Ballot();
            ballot.setElection(election);
            ballot.setBallotSignedToken(voteDTO.getBallotToken());  // Assuming the ballot token is provided in the request
            ballot.setVoter(voter);
            ballotRepository.save(ballot);

            // 6️⃣ Create Vote with Selected Candidates
            Set<Candidate> selectedCandidates = voteDTO.getCandidateIds().stream()
                    .map(candidateId -> candidateRepository.findById(candidateId)
                            .orElseThrow(() -> new IllegalArgumentException("Candidate not found: " + candidateId)))
                    .collect(Collectors.toSet());

            Vote vote = new Vote();
            vote.setBallot(ballot);
            vote.setVoter(voter);
            vote.setSelectedCandidates(selectedCandidates);
            vote.setVoterSignedToken(voteDTO.getVoterToken());

            voteRepository.save(vote);

            // 7️⃣ Sign the Blinded Token using the ElectionAuthorityService
            byte[] blindedMessage = voteDTO.getBlindedMessage();  // Assuming the request contains the blinded message
            String signedToken = electionAuthorityService.signMessage(blindedMessage);  // Sign the blinded message

            // 8️⃣ Save the BlindCredential
            BlindCredential blindCredential = new BlindCredential();
            blindCredential.setVoter(voter);
            blindCredential.setElectionAuthority(election.getElectionAuthority());  // The Election Authority associated with the election
            blindCredential.setSignedToken(signedToken);
            blindCredentialRepository.save(blindCredential);  // Save the blind credential

            return "Vote submitted successfully!";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error processing the vote: " + e.getMessage();
        }
    }
}
