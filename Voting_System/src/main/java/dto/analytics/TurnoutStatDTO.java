package dto.analytics;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TurnoutStatDTO {
    private String interval;  // e.g. "2024-06-01 13:00"
    private long voteCount;
}
