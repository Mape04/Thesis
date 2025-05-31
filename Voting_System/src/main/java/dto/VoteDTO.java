package dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
public class VoteDTO {
    private UUID voterId;
    private UUID electionId;
    private String voterToken;
    private Set<UUID> candidateIds;
    private LocalDateTime timestamp;
}
