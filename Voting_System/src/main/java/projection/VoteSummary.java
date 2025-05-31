package projection;

import domain.Candidate;
import domain.Ballot;

import java.time.LocalDateTime;
import java.util.Set;

public interface VoteSummary {
    LocalDateTime getTimestamp();
    Set<Candidate> getSelectedCandidates();
    Ballot getBallot();
}
