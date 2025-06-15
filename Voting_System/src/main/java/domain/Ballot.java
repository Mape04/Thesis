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

    @OneToMany(mappedBy = "ballot", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Vote> votes;

    @Column
    private String region;

    @Override
    public String toString() {
        return "Ballot{" +
                "ballotId=" + ballotId +
                ", election=" + (election != null ? election.getElectionId() : "null") +
                ", region='" + region + '\'' +
                '}';
    }
}
