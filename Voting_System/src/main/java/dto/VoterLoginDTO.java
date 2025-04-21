package dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VoterLoginDTO {
    private String voterEmail;
    private String voterPassword;
}
