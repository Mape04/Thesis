package dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.UUID;

@Data
@AllArgsConstructor
public class BlindCredentialDTO {
    private UUID blindCredentialId;
    private String signedToken;
    private UUID voterId;
}
