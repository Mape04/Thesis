package dto.analytics;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegionParticipationDTO {
    private String region;
    private long count;
}
