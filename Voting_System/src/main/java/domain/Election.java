package domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
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

    @Column(nullable = true)
    private String electionPassword;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private Integer electionVotes;

    @Column(length = 500)
    private String electionDescription;

    private Integer nrVotesPerVoter;

    @Enumerated(EnumType.STRING)
    private ElectionType electionType;

    // 🔥 Runoff-related fields
    private LocalDateTime runoffStartDate;
    private LocalDateTime runoffEndDate;

    @OneToOne
    @JoinColumn(name = "runoff_election_id")
    private Election runoffElection;

    @OneToMany(mappedBy = "election", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Ballot> ballots;

    // Many-to-One relationship to ElectionAuthority
    @ManyToOne
    @JoinColumn(name = "election_authority_id", nullable = false)
    private ElectionAuthority electionAuthority;

    @Override
    public String toString() {
        return "Election{" +
                "electionId='" + electionId + '\'' +
                ", electionName='" + electionName + '\'' +
                ", startDate=" + startDate + '\'' +
                ", endDate=" + endDate + '\'' +
                ", electionDescription='" + electionDescription + '\'' +
                ", electionVotes=" + electionVotes + '\'' +
                ", nrVotesPerVoter=" + nrVotesPerVoter +
                ", electionType=" + electionType + '\'' +
                ", linkElectionId=" + runoffElection.electionId + '\'' +
                ", runOffStartDate=" + runoffStartDate + '\'' +
                ", runOffEndDate=" + runoffEndDate + '\'' +
                ", electionAuthority=" + electionAuthority +
                '}';
    }
}
