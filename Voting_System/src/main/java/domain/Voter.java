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
    private String voterHashedPassword;
    private boolean voterIsRegistered;

    @OneToMany(mappedBy = "voter", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<BlindCredential> blindCredentialSet = new HashSet<>();

    @Override
    public String toString() {
        return "Voter{" +
                "voterId=" + voterId +
                ", voterName='" + voterName + '\'' +
                ", voterEmail='" + voterEmail + '\'' +
                ", voterHashedPassword='" + voterHashedPassword + '\'' +
                ", voterIsRegistered=" + voterIsRegistered +
                '}';
    }
}
