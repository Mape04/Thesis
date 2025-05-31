package controller;

import dto.analytics.CandidateVoteDTO;
import dto.analytics.RegionParticipationDTO;
import dto.analytics.TurnoutStatDTO;
import service.AnalyticsService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/elections/{electionId}/analytics")
public class ElectionAnalyticsController {

    private final AnalyticsService analyticsService;

    public ElectionAnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/turnout")
    public ResponseEntity<List<TurnoutStatDTO>> getTurnoutStats(@PathVariable UUID electionId) {
        List<TurnoutStatDTO> stats = analyticsService.getTurnoutStats(electionId);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/votes-per-candidate")
    public ResponseEntity<List<CandidateVoteDTO>> getVotesPerCandidate(@PathVariable UUID electionId) {
        List<CandidateVoteDTO> stats = analyticsService.getVotesPerCandidate(electionId);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/region-participation")
    public ResponseEntity<List<RegionParticipationDTO>> getRegionParticipation(@PathVariable UUID electionId) {
        List<RegionParticipationDTO> stats = analyticsService.getRegionParticipation(electionId);
        return ResponseEntity.ok(stats);
    }
}
