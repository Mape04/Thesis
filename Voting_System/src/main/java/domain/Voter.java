package domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Voter {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID voterId;

    private String voterName;
    private String voterEmail;
    private String voterPassword;
    private boolean voterIsRegistered;

    @OneToMany(mappedBy = "voter", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<BlindCredential> blindCredentialSet = new HashSet<>();

    // ❌ Removed ballot tracking to ensure voter can't be traced to votes
    // private Set<Ballot> ballots = new HashSet<>();

    @Column(name = "profile_image_path")
    private String profileImagePath;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VoterType voterType = VoterType.BASIC;

    @Column(nullable = false)
    private boolean isVerifiedHuman = false;

    @Column(name = "cnp_hash", unique = true)
    private String cnpHash;

    @Override
    public String toString() {
        return "Voter{" +
                "voterId=" + voterId +
                ", voterName='" + voterName + '\'' +
                ", voterEmail='" + voterEmail + '\'' +
                ", voterPassword='" + voterPassword + '\'' +
                ", voterIsRegistered=" + voterIsRegistered +
                ", blindCredentialSet=" + blindCredentialSet +
                ", profileImagePath='" + profileImagePath + '\'' +
                ", voterType=" + voterType + '\'' +
                ", isVerifiedHuman=" + isVerifiedHuman + '\'' +
                ", cnpHash=" + cnpHash + '\'' +
                '}';
    }
}
