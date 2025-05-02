package utils;

import domain.*;
import dto.*;
import lombok.AllArgsConstructor;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
public class DTOUtils {


    // Voter to DTO
    public static VoterDTO toVoterDTO(Voter voter) {
        if (voter == null) return null;
        return new VoterDTO(
                voter.getVoterId(),
                voter.getVoterName(),
                voter.getVoterEmail(),
                voter.getVoterPassword(),
                voter.isVoterIsRegistered()
        );
    }

    // Ballot to DTO
    public static BallotDTO toBallotDTO(Ballot ballot) {
        if (ballot == null) return null;
        return new BallotDTO(
                ballot.getBallotId(),
                ballot.getElection() != null ? ballot.getElection().getElectionId() : null,
                ballot.getVoter() != null ? ballot.getVoter().getVoterId() : null
        );
    }

    // Candidate to DTO
    public static CandidateDTO toCandidateDTO(Candidate candidate) {
        if (candidate == null) return null;
        return new CandidateDTO(
                candidate.getCandidateId(),
                candidate.getCandidateName(),
                candidate.getCandidateParty(),
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
                election.getElectionAuthority() != null ? election.getElectionAuthority().getElectionAuthorityId() : null
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
}
