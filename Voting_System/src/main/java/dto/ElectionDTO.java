package dto;

import domain.ElectionType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class ElectionDTO {
    private UUID electionId;
    private String electionName;
    private String electionPassword;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer electionVotes;
    private String electionDescription;
    private Integer nrVotesPerVoter;
    private ElectionType electionType;
    private UUID electionAuthorityId;
    private LocalDateTime runoffStartDate;
    private LocalDateTime runoffEndDate;
    private UUID runoffElectionId;
}
