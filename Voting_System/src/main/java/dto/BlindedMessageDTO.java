package dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BlindedMessageDTO {
    private UUID electionId;
    private String blindedMessage;
    private UUID voterId;
}
