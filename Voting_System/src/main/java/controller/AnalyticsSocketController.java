package controller;
import dto.analytics.*;
import service.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
public class AnalyticsSocketController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private AnalyticsService analyticsService;

    @Scheduled(fixedRate = 10000) // every 10s
    public void broadcastAnalytics() {
        List<UUID> activeElectionIds = analyticsService.getAllActiveElectionIds();
        for (UUID electionId : activeElectionIds) {
            List<TurnoutStatDTO> turnout = analyticsService.getTurnoutStats(electionId);
            List<CandidateVoteDTO> votes = analyticsService.getVotesPerCandidate(electionId);
            List<RegionParticipationDTO> regions = analyticsService.getRegionParticipation(electionId);

            messagingTemplate.convertAndSend("/topic/analytics/" + electionId,
                    Map.of("turnout", turnout, "votes", votes, "regions", regions)
            );
        }
    }
}
