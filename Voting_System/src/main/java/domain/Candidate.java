package domain;

import jakarta.persistence.*;
import lombok.*;

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
    private Election election; // Candidates belong to an Election

    @Override
    public String toString() {
        return "Candidate{" +
                "candidateId=" + candidateId +
                ", candidateName='" + candidateName + '\'' +
                ", candidateParty='" + candidateParty + '\'' +
                '}';
    }
}
