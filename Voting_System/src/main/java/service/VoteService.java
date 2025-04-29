package service;

import domain.*;
import dto.VoteDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import repository.*;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
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

    public ResponseEntity<Map<String, String>> submitVote(VoteDTO voteDTO) {
        Map<String, String> response = new HashMap<>();
        try {
            // 1. Fetch voter
            Voter voter = voterRepository.findById(voteDTO.getVoterId())
                    .orElseThrow(() -> new IllegalArgumentException("Voter not found."));

            // 2. Validate token
            boolean validToken = voter.getBlindCredentialSet().stream()
                    .anyMatch(cred -> cred.getSignedToken().equals(voteDTO.getVoterToken())
                            && cred.getElection().getElectionId().equals(voteDTO.getElectionId()));

            if (!validToken) {
                response.put("status", "error");
                response.put("message", "Invalid voting token.");
                return ResponseEntity.badRequest().body(response);
            }

            // 3. Fetch election
            Election election = electionRepository.findById(voteDTO.getElectionId())
                    .orElseThrow(() -> new IllegalArgumentException("Election not found."));

            // 4. Validate number of candidates
            if (election.getNrVotesPerVoter() > 0) {
                if (voteDTO.getCandidateIds().size() != election.getNrVotesPerVoter()) {
                    response.put("status", "error");
                    response.put("message", "You must select exactly " + election.getNrVotesPerVoter() + " candidates.");
                    return ResponseEntity.badRequest().body(response);
                }
            }

            // 🔥 Only if validation OK: continue to create Ballot and Vote!
            Ballot ballot = new Ballot();
            ballot.setElection(election);
            ballot.setVoter(voter);
            ballotRepository.save(ballot);

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

            response.put("status", "success");
            response.put("message", "Vote submitted successfully!");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            response.put("status", "error");
            response.put("message", "Error processing the vote: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    public long countVotesByCandidate(UUID candidateId) {
        return voteRepository.countVotesByCandidate(candidateId);
    }

    public long countByVoterAndBallot_Election(Voter voter, Election election) {
        return voteRepository.countByVoterAndBallot_Election(voter, election);
    }
}
