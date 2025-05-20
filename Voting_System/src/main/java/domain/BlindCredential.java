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
public class BlindCredential {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID blindCredentialId;

    @ManyToOne
    @JoinColumn(name = "voter_id", nullable = false)
    private Voter voter;

    @ManyToOne
    @JoinColumn(name = "election_authority_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private ElectionAuthority electionAuthority;

    @ManyToOne
    @JoinColumn(name = "election_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Election election;

    @Column(name = "signed_token", columnDefinition = "TEXT", nullable = false)
    private String signedToken;

    @Column(nullable = false)
    private boolean used = false;  // ✅ New field to track usage

    @Override
    public String toString() {
        return "BlindCredential{" +
                "blindCredentialId=" + blindCredentialId +
                ", voterId=" + (voter != null ? voter.getVoterId() : null) +
                ", electionAuthorityId=" + (electionAuthority != null ? electionAuthority.getElectionAuthorityId() : null) +
                ", electionId=" + (election != null ? election.getElectionId() : null) +
                ", signedToken='" + signedToken + '\'' +
                ", used=" + used +  // ✅ include in debug output
                '}';
    }
}
