package dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class VoterVerificationRequestDTO {
    private String ssn;
    private String region;
    private LocalDate birthdate;
}
