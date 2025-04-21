package dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VoterRegistrationDTO {
    private String voterName;
    private String voterEmail;
    private String voterPassword;
}
