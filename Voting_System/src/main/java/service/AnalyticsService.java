package service;

import dto.analytics.CandidateVoteDTO;
import dto.analytics.RegionParticipationDTO;
import dto.analytics.TurnoutStatDTO;
import dto.analytics.VoteLightDTO;
import repository.ElectionRepository;
import repository.VoteRepository;

import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {

    private final VoteRepository voteRepository;
    private final ElectionRepository electionRepository;

    public AnalyticsService(VoteRepository voteRepository, ElectionRepository electionRepository) {
        this.voteRepository = voteRepository;
        this.electionRepository = electionRepository;
    }

    public List<TurnoutStatDTO> getTurnoutStats(UUID electionId) {
        List<VoteLightDTO> votes = voteRepository.getVoteLightByElection(electionId);
        Map<String, Long> grouped = new HashMap<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:00");

        for (VoteLightDTO vote : votes) {
            String interval = vote.getTimestamp().format(formatter);
            grouped.put(interval, grouped.getOrDefault(interval, 0L) + 1);
        }

        return grouped.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(e -> new TurnoutStatDTO(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }

    public List<CandidateVoteDTO> getVotesPerCandidate(UUID electionId) {
        List<VoteLightDTO> votes = voteRepository.getVoteLightByElection(electionId);
        Map<String, Long> countMap = new HashMap<>();

        for (VoteLightDTO vote : votes) {
            String name = vote.getCandidate().getCandidateName();
            countMap.put(name, countMap.getOrDefault(name, 0L) + 1);
        }

        return countMap.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(e -> new CandidateVoteDTO(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }

    public List<RegionParticipationDTO> getRegionParticipation(UUID electionId) {
        List<VoteLightDTO> votes = voteRepository.getVoteLightByElection(electionId);
        Map<String, Long> regionMap = new HashMap<>();

        for (VoteLightDTO vote : votes) {
            String region = vote.getRegion();
            if (region != null) {
                regionMap.put(region, regionMap.getOrDefault(region, 0L) + 1);
            }
        }

        return regionMap.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .map(e -> new RegionParticipationDTO(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }

    public List<UUID> getAllActiveElectionIds() {
        return electionRepository.findAllActiveElectionIds();
    }

}
