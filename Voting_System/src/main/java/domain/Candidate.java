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

    @ManyToOne
    @JoinColumn(name = "election_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE) // 🔥 Add this!
    private Election election; // Candidates belong to an Election

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
