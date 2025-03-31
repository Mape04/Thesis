package domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "blind_credentials")
public class BlindCredential{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID credentialId;

    @Column(nullable = false, unique = true)
    private String credentialBlindedToken;

    @Column(nullable = false, unique = true)
    private String credentialSignedToken;

    @ManyToOne
    @JoinColumn(name = "voter_id", nullable = false)
    private Voter voter;

    @Override
    public String toString() {
        return "BlindCredential{" +
                "credentialId=" + credentialId +
                ", credentialBlindedToken='" + credentialBlindedToken + '\'' +
                ", credentialSignedToken='" + credentialSignedToken + '\'' +
                ", voter=" + voter +
                '}';
    }
}
