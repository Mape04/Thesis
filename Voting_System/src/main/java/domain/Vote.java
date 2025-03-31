package domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @ManyToOne
    @JoinColumn(name = "candidate_id", nullable = false)
    private Candidate chosenCandidate;

    @ManyToOne
    @JoinColumn(name = "voter_id", nullable = false)
    private Voter voter;

    @Column(nullable = false)
    private String voterSignedToken; // Unique voter authentication token

    @Override
    public String toString() {
        return "Vote{" +
                "voteId='" + voteId + '\'' +
                ", ballot=" + ballot +
                ", chosenCandidate=" + chosenCandidate +
                ", voter=" + voter +
                '}';
    }
}
