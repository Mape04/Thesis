package dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.UUID;

@Data
@AllArgsConstructor
public class BallotDTO {
    private UUID ballotId;
    private UUID electionId;
    private UUID voterId;
}
