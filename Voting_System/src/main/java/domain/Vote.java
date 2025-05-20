package domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

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
    private Set<Candidate> selectedCandidates;

    @Lob
    @Column(name = "encrypted_vote", nullable = false)
    private byte[] encryptedVote; // Store vote anonymously

    // Remove voter reference and signed token to ensure anonymity

    @Override
    public String toString() {
        return "Vote{" +
                "voteId='" + voteId + '\'' +
                ", ballotId=" + (ballot != null ? ballot.getBallotId() : null) +
                ", selectedCandidates=" + selectedCandidates +
                '}';
    }
}
