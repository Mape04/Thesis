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

    @OneToMany(mappedBy = "voter", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Ballot> ballots = new HashSet<>(); // <-- Add this to track ballots

    @Column(name = "profile_image_path")
    private String profileImagePath;


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
                '}';
    }
}
