package domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Election {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID electionId;
    private String electionName;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer electionVotes;

    @OneToMany(mappedBy = "election", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Ballot> ballots;

    @Override
    public String toString() {
        return "Election{" +
                "electionId='" + electionId + '\'' +
                ", electionName='" + electionName + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", electionVotes=" + electionVotes +
                '}';
    }
}
