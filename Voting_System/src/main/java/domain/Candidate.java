package domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Candidate {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID candidateId;
    private String candidateName;
    private String candidateParty;

    @Column(nullable = false)
    private int nrOfVotes = 0;

    @ManyToOne
    @JoinColumn(name = "election_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Election election;

    @Override
    public String toString() {
        return "Candidate{" +
                "candidateId=" + candidateId +
                ", candidateName='" + candidateName + '\'' +
                ", candidateParty='" + candidateParty + '\'' +
                ", election=" + (election != null ? election.getElectionName() : "null") +
                '}';
    }
}
