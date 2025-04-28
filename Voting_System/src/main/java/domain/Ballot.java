package domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Ballot {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID ballotId;

    @ManyToOne
    @JoinColumn(name = "election_id", nullable = false)
    private Election election;

    @ManyToOne
    @JoinColumn(name = "voter_id", nullable = false)
    private Voter voter;  // <-- Added this to link the voter who submitted this ballot.


    @OneToMany(mappedBy = "ballot", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Vote> votes;

    @Override
    public String toString() {
        return "Ballot{" +
                "ballotId=" + ballotId +
                ", voter=" + (voter != null ? voter.getVoterId() : "null") +
                '}';
    }
}
