package dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
public class VoteDTO {
    private UUID voterId;
    private UUID electionId;
    private String voterToken;  // Blind token provided by the voter
    private String ballotToken;  // Token for the ballot
    private Set<UUID> candidateIds;  // List of candidate IDs chosen by the voter
    private byte[] blindedMessage;  // The blinded message to be signed by the Election Authority
}
