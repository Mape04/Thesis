package dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.UUID;

@Data
@AllArgsConstructor
public class ElectionAuthorityDTO {
    private UUID electionAuthorityId;
    private String authorityName;
    private String authorityEmail;
}
