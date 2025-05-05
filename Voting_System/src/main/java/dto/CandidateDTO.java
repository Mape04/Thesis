package dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CandidateDTO {
    private UUID candidateId;
    private String candidateName;
    private String candidateParty;
    private int nrOfVotes;
    private UUID candidateElectionId;
}
