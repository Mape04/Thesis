package service;

import domain.*;
import dto.VoteDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import repository.*;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import utils.RsaKeyConverter;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.LocalDateTime;
import java.util.*;
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
    private final RsaKeyRepository rsaKeyRepository;
    private final RsaKeyConverter rsaKeyConverter;


    public ResponseEntity<Map<String, String>> submitVote(VoteDTO voteDTO) {
        Map<String, String> response = new HashMap<>();
        try {
            // 1. Fetch voter (for credential/token verification only)
            Voter voter = voterRepository.findById(voteDTO.getVoterId())
                    .orElseThrow(() -> new IllegalArgumentException("Voter not found."));

            // 2. Validate token: must match and not be used
            BlindCredential matchingCredential = voter.getBlindCredentialSet().stream()
                    .filter(cred -> cred.getSignedToken().equals(voteDTO.getVoterToken()) &&
                            cred.getElection().getElectionId().equals(voteDTO.getElectionId()) &&
                            !cred.isUsed())
                    .findFirst()
                    .orElse(null);

            if (matchingCredential == null) {
                response.put("status", "error");
                response.put("message", "Invalid or already used voting token.");
                return ResponseEntity.badRequest().body(response);
            }

            // 3. Fetch election
            Election election = electionRepository.findById(voteDTO.getElectionId())
                    .orElseThrow(() -> new IllegalArgumentException("Election not found."));

            // 4. Validate number of candidates
            if (election.getNrVotesPerVoter() > 0 &&
                    voteDTO.getCandidateIds().size() != election.getNrVotesPerVoter()) {
                response.put("status", "error");
                response.put("message", "You must select exactly " + election.getNrVotesPerVoter() + " candidates.");
                return ResponseEntity.badRequest().body(response);
            }

            // 5. Create a ballot (no voter info stored)
            Ballot ballot = new Ballot();
            ballot.setElection(election);
            ballot.setRegion(voter.getRegion());
            ballotRepository.save(ballot);

            // 6. Collect selected candidates
            Set<Candidate> selectedCandidates = voteDTO.getCandidateIds().stream()
                    .map(candidateId -> {
                        Candidate candidate = candidateRepository.findById(candidateId)
                                .orElseThrow(() -> new IllegalArgumentException("Candidate not found: " + candidateId));
                        candidate.setNrOfVotes(candidate.getNrOfVotes() + 1);
                        candidateRepository.save(candidate);
                        return candidate;
                    })
                    .collect(Collectors.toSet());

            // 7. Encrypt the vote content using RSA
            String voteContent = selectedCandidates.stream()
                    .map(c -> c.getCandidateId().toString())
                    .collect(Collectors.joining(","));

            RsaKey rsaKey = rsaKeyRepository.findByElectionAuthorityId(election.getElectionAuthority().getElectionAuthorityId())
                    .orElseThrow(() -> new IllegalArgumentException("RSA key not found for election authority"));
            PublicKey publicKey = rsaKeyConverter.fromByteArrayPublicKey(rsaKey.getPublicKey());

            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] encryptedVote = cipher.doFinal(voteContent.getBytes(StandardCharsets.UTF_8));

            // 8. Save vote (anonymous)

            Vote vote = new Vote();
            vote.setBallot(ballot);
            vote.setSelectedCandidates(selectedCandidates);
            vote.setEncryptedVote(encryptedVote);
            vote.setTimestamp(LocalDateTime.now());
            voteRepository.save(vote);

            // 9. Mark blind token as used
            matchingCredential.setUsed(true);
            blindCredentialRepository.save(matchingCredential);

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

    public Map<UUID, Integer> decryptAndTallyVotes(UUID electionId) throws Exception {
        List<Vote> votes = voteRepository.findAll(); // you may want to filter by election/ballot
        Map<UUID, Integer> tally = new HashMap<>();

        // Fetch RSA key
        Election election = electionRepository.findById(electionId)
                .orElseThrow(() -> new IllegalArgumentException("Election not found"));

        UUID authorityId = election.getElectionAuthority().getElectionAuthorityId();
        RsaKey rsaKey = rsaKeyRepository.findByElectionAuthorityId(authorityId)
                .orElseThrow(() -> new IllegalArgumentException("RSA key not found"));

        PrivateKey privateKey = rsaKeyConverter.fromByteArrayPrivateKey(rsaKey.getPrivateKey());

        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);

        for (Vote vote : votes) {
            try {
                byte[] decryptedBytes = cipher.doFinal(vote.getEncryptedVote());
                String decrypted = new String(decryptedBytes, StandardCharsets.UTF_8);

                String[] candidateIdStrings = decrypted.split(",");
                for (String idStr : candidateIdStrings) {
                    UUID candidateId = UUID.fromString(idStr);
                    tally.put(candidateId, tally.getOrDefault(candidateId, 0) + 1);
                }
            } catch (Exception e) {
                System.err.println("Failed to decrypt vote: " + e.getMessage());
            }
        }

        // After tallying all candidate votes
        int totalVotes = tally.values().stream().mapToInt(Integer::intValue).sum();
        election.setElectionVotes(totalVotes);
        electionRepository.save(election);


        return tally;
    }

}
