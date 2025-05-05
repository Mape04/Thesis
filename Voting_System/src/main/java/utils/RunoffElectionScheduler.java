package utils;

import domain.Election;
import domain.ElectionType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import service.ElectionService;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class RunoffElectionScheduler {

    private final ElectionService electionService;

    public RunoffElectionScheduler(ElectionService electionService) {
        this.electionService = electionService;
    }

    @Scheduled(cron = "0 40 18 * * *") // Every day at 6:20 PM
    public void checkForRunoffElections() {
        List<Election> elections = electionService.getAllElections();

        for (Election election : elections) {
            if (
                    election.getElectionType() == ElectionType.TOP_TWO_RUNOFF &&
                            election.getEndDate().isBefore(LocalDateTime.now()) &&
                            election.getRunoffElection() == null
            ) {
                try {
                    electionService.createRunoffFromTopTwo(election);
                    System.out.println("Created runoff for election: " + election.getElectionName());
                } catch (Exception e) {
                    System.err.println("Error creating runoff for: " + election.getElectionId());
                    e.printStackTrace();
                }
            }
        }
    }
}
