package utils;

import domain.*;
import dto.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import service.ElectionService;
import service.VoterService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
@Component
public class DTOUtils {
    private final VoterService voterService;
    private final ElectionService electionService;

    // Voter to DTO
    public static VoterDTO toVoterDTO(Voter voter) {
        if (voter == null) return null;
        return new VoterDTO(
                voter.getVoterId(),
                voter.getVoterName(),
                voter.getVoterEmail(),
                voter.getVoterPassword(),
                voter.isVoterIsRegistered(),
                voter.getProfileImagePath(),
                voter.getVoterType(),
                voter.isVerifiedHuman(),
                voter.getRegion(),
                voter.getBirthdate()
        );
    }

    // Ballot to DTO
    public static BallotDTO toBallotDTO(Ballot ballot) {
        if (ballot == null) return null;
        return new BallotDTO(
                ballot.getBallotId(),
                ballot.getElection() != null ? ballot.getElection().getElectionId() : null
        );
    }

    // Candidate to DTO
    public static CandidateDTO toCandidateDTO(Candidate candidate) {
        if (candidate == null) return null;
        return new CandidateDTO(
                candidate.getCandidateId(),
                candidate.getCandidateName(),
                candidate.getCandidateParty(),
                candidate.getNrOfVotes(),
                candidate.getElection().getElectionId()
        );
    }

    // Candidate list to DTO list
    public static List<CandidateDTO> toCandidateDTOList(List<Candidate> candidates) {
        return candidates.stream()
                .map(DTOUtils::toCandidateDTO)
                .collect(Collectors.toList());
    }

    // Election to DTO
    public static ElectionDTO toElectionDTO(Election election) {
        if (election == null) return null;
        return new ElectionDTO(
                election.getElectionId(),
                election.getElectionName(),
                election.getElectionPassword(),
                election.getStartDate(),
                election.getEndDate(),
                election.getElectionVotes(),
                election.getElectionDescription(),
                election.getNrVotesPerVoter(),
                election.getElectionType(),
                election.getElectionAuthority() != null ? election.getElectionAuthority().getElectionAuthorityId() : null,
                election.getRunoffStartDate(),
                election.getRunoffEndDate(),
                election.getRunoffElection() !=null ? election.getRunoffElection().getElectionId() : null,
                election.getAccessLevel()
        );
    }

    // ElectionAuthority to DTO
    public static ElectionAuthorityDTO toElectionAuthorityDTO(ElectionAuthority authority) {
        if (authority == null) return null;
        return new ElectionAuthorityDTO(
                authority.getElectionAuthorityId(),
                authority.getAuthorityName(),
                authority.getAuthorityEmail()
        );
    }

    // VoterDTO to Voter
    public static Voter toVoter(VoterDTO voterDTO) {
        if (voterDTO == null) return null;
        Voter voter = new Voter();
        voter.setVoterId(voterDTO.getVoterId());
        voter.setVoterName(voterDTO.getVoterName());
        voter.setVoterEmail(voterDTO.getVoterEmail());
        voter.setVoterPassword(voterDTO.getVoterPassword());
        voter.setVoterIsRegistered(voterDTO.isVoterIsRegistered());
        voter.setProfileImagePath(voterDTO.getProfileImagePath());
        voter.setVoterType(voterDTO.getVoterType());
        voter.setVerifiedHuman(voterDTO.isVerifiedHuman());
        voter.setRegion(voterDTO.getRegion());
        voter.setBirthdate(voterDTO.getBirthdate());
        return voter;
    }

    public static Voter toVoter(VoterRegistrationDTO voterRegistrationDTO) {
        if (voterRegistrationDTO == null) return null;
        Voter voter = new Voter();
        voter.setVoterName(voterRegistrationDTO.getVoterName());
        voter.setVoterEmail(voterRegistrationDTO.getVoterEmail());
        voter.setVoterPassword(voterRegistrationDTO.getVoterPassword());
        voter.setVoterIsRegistered(false);
        return voter;
    }
    // BallotDTO to Ballot
    public static Ballot toBallot(BallotDTO ballotDTO) {
        if (ballotDTO == null) return null;
        Ballot ballot = new Ballot();
        ballot.setBallotId(ballotDTO.getBallotId());
        // Assuming you have a method to fetch Election and Voter by their IDs
        // ballot.setElection(findElectionById(ballotDTO.getElectionId()));
        // ballot.setVoter(findVoterById(ballotDTO.getVoterId()));
        return ballot;
    }

    // CandidateDTO to Candidate
    public static Candidate toCandidate(CandidateDTO candidateDTO, Election election) {
        if (candidateDTO == null) return null;
        Candidate candidate = new Candidate();
        candidate.setCandidateId(candidateDTO.getCandidateId());
        candidate.setCandidateName(candidateDTO.getCandidateName());
        candidate.setCandidateParty(candidateDTO.getCandidateParty());
        candidate.setNrOfVotes(candidateDTO.getNrOfVotes());
        candidate.setElection(election);
        return candidate;
    }

    // ElectionDTO to Election
    public static Election toElection(ElectionDTO electionDTO) {
        if (electionDTO == null) return null;
        Election election = new Election();
        election.setElectionId(electionDTO.getElectionId());
        election.setElectionName(electionDTO.getElectionName());
        election.setElectionPassword(null); //don't expose
        election.setStartDate(electionDTO.getStartDate());
        election.setEndDate(electionDTO.getEndDate());
        election.setElectionVotes(electionDTO.getElectionVotes());
        election.setElectionDescription(electionDTO.getElectionDescription());
        election.setNrVotesPerVoter(electionDTO.getNrVotesPerVoter());
        // Assuming you have a method to fetch ElectionAuthority by ID
        // election.setElectionAuthority(findElectionAuthorityById(electionDTO.getElectionAuthorityId()));
        election.setElectionType(electionDTO.getElectionType());
        election.setRunoffStartDate(electionDTO.getRunoffStartDate());
        election.setRunoffEndDate(electionDTO.getRunoffEndDate());
        //election.setRunoffElection(); i need a find method or something
        return election;
    }

    // ElectionAuthorityDTO to ElectionAuthority
    public static ElectionAuthority toElectionAuthority(ElectionAuthorityDTO authorityDTO) {
        if (authorityDTO == null) return null;
        ElectionAuthority authority = new ElectionAuthority();
        authority.setElectionAuthorityId(authorityDTO.getElectionAuthorityId());
        authority.setAuthorityName(authorityDTO.getAuthorityName());
        authority.setAuthorityEmail(authorityDTO.getAuthorityEmail());
        return authority;
    }

    public BlindCredential toBlindCredential(BlindCredentialDTO blindCredentialDTO) {
        if (blindCredentialDTO == null) return null;

        BlindCredential blindCredential = new BlindCredential();

        Optional<Voter> voterOptional = voterService.getVoterById(blindCredentialDTO.getVoterId());
        if (voterOptional.isEmpty()) {
            throw new IllegalArgumentException("Voter not found with ID: " + blindCredentialDTO.getVoterId());
        }
        blindCredential.setVoter(voterOptional.get());

        Optional<Election> electionOptional = electionService.getElectionById(blindCredentialDTO.getElectionId());
        if (electionOptional.isEmpty()) {
            throw new IllegalArgumentException("Election not found with ID: " + blindCredentialDTO.getElectionId());
        }
        blindCredential.setElection(electionOptional.get());

        Optional<ElectionAuthority> electionAuthorityoptional = Optional.ofNullable(electionOptional.get().getElectionAuthority());
        if (electionAuthorityoptional.isEmpty()) {
            throw new IllegalArgumentException("Election Authority not found!");
        }
        blindCredential.setElectionAuthority(electionAuthorityoptional.get());

        blindCredential.setSignedToken(blindCredentialDTO.getVoterToken());

        return blindCredential;
    }

    public BlindCredential toBlindCredential(BlindedMessageDTO blindedMessageDTO, String blindedMessage){
        if (blindedMessageDTO == null) return null;

        BlindCredential blindCredential = new BlindCredential();
        Optional<Voter> voterOptional = voterService.getVoterById(blindedMessageDTO.getVoterId());
        if (voterOptional.isEmpty()) {
            throw new IllegalArgumentException("Voter not found with ID: " + blindedMessageDTO.getVoterId());
        }
        blindCredential.setVoter(voterOptional.get());

        Optional<Election> electionOptional = electionService.getElectionById(blindedMessageDTO.getElectionId());
        if (electionOptional.isEmpty()) {
            throw new IllegalArgumentException("Election not found with ID: " + blindedMessageDTO.getElectionId());
        }
        blindCredential.setElection(electionOptional.get());

        Optional<ElectionAuthority> electionAuthorityoptional = Optional.ofNullable(electionOptional.get().getElectionAuthority());
        if (electionAuthorityoptional.isEmpty()) {
            throw new IllegalArgumentException("Election Authority not found!");
        }
        blindCredential.setElectionAuthority(electionAuthorityoptional.get());
        blindCredential.setSignedToken(blindedMessage);

        return blindCredential;
    }
}
