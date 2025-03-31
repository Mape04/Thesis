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

    @Column(nullable = false, unique = true)
    private String ballotSignedToken;


    @OneToMany(mappedBy = "ballot", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Vote> votes;

    @Override
    public String toString() {
        return "Ballot{" +
                "ballotId=" + ballotId +
                ", ballotSignedToken='" + ballotSignedToken +
                '}';
    }
}
