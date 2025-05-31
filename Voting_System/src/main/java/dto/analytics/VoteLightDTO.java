package dto.analytics;

import domain.Candidate;

import java.time.LocalDateTime;

public class VoteLightDTO {
    private LocalDateTime timestamp;
    private Candidate candidate;
    private String region;

    public VoteLightDTO(LocalDateTime timestamp, Candidate candidate, String region) {
        this.timestamp = timestamp;
        this.candidate = candidate;
        this.region = region;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public Candidate getCandidate() {
        return candidate;
    }

    public String getRegion() {
        return region;
    }
}
