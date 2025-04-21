package domain;

import jakarta.persistence.*;
import lombok.*;
import service.ElectionAuthorityService;

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
    private ElectionAuthority electionAuthority;

    @Column(nullable = false)
    private String signedToken; // This will store the signed blinded token

    @Override
    public String toString() {
        return "BlindCredential{" +
                "blindCredentialId=" + blindCredentialId +
                ", voter=" + voter +
                ", electionAuthority=" + electionAuthority +
                ", signedToken='" + signedToken + '\'' +
                '}';
    }
}
