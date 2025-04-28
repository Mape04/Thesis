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
    private final BlindCredentialRepository blindCredentialRepository;

    public String submitVote(VoteDTO voteDTO) {
        try {
            // 1️⃣ Fetch the Voter
            Voter voter = voterRepository.findById(voteDTO.getVoterId())
                    .orElseThrow(() -> new IllegalArgumentException("Voter not found."));

            // 2️⃣ Validate Blind Token
            boolean validToken = voter.getBlindCredentialSet().stream()
                    .anyMatch(cred -> cred.getSignedToken().equals(voteDTO.getVoterToken())
                            && cred.getElection().getElectionId().equals(voteDTO.getElectionId()));

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

            // 🔥 No need to create new BlindCredential here anymore!
            // The voter already received a signed token during the "sign blinded message" phase.

            return "Vote submitted successfully!";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error processing the vote: " + e.getMessage();
        }
    }
}
