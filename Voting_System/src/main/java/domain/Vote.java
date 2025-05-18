package domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Vote {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String voteId;

    @ManyToOne
    @JoinColumn(name = "ballot_id", nullable = false)
    private Ballot ballot;

    @ManyToMany
    @JoinTable(
            name = "vote_selected_candidates",
            joinColumns = @JoinColumn(name = "vote_id"),
            inverseJoinColumns = @JoinColumn(name = "candidate_id")
    )
    private Set<Candidate> selectedCandidates;  // <-- Changed to Set for multiple candidates!

    @ManyToOne
    @JoinColumn(name = "voter_id", nullable = false)
    private Voter voter;

    @Column(name = "voter_signed_token", columnDefinition = "TEXT", nullable = false)
    private String voterSignedToken; // Unique voter authentication token

    @Override
    public String toString() {
        return "Vote{" +
                "voteId='" + voteId + '\'' +
                ", ballotId=" + (ballot != null ? ballot.getBallotId() : null) + '\'' +
                ", chosenCandidate=" + selectedCandidates + '\'' +
                ", voterId=" + (voter != null ? voter.getVoterId() : null) +
                '}';
    }
}
