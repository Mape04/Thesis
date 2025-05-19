package dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class VoterDTO {
    private UUID voterId;
    private String voterName;
    private String voterEmail;
    private String voterPassword;
    private boolean voterIsRegistered;
    private String profileImagePath;
}
