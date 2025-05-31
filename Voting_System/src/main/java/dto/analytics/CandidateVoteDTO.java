package dto.analytics;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CandidateVoteDTO {
    private String candidateName;
    private long voteCount;
}
