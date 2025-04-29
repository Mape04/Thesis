package dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class ElectionDTO {
    private UUID electionId;
    private String electionName;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer electionVotes;
    private String electionDescription;
    private Integer nrVotesPerVoter;
    private UUID electionAuthorityId;
}
