package dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
public class ElectionDTO {
    private UUID electionId;
    private String electionName;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer electionVotes;
    private String electionDescription;
    private Integer nrVotesPerVoter;
    private UUID electionAuthorityId;
}
