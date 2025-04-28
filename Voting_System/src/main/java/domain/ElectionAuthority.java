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
public class ElectionAuthority {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID electionAuthorityId;

    private String authorityName;

    private String authorityEmail;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "rsa_key_id")
    private RsaKey rsaKeyId;

    @OneToMany(mappedBy = "electionAuthority", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<BlindCredential> blindCredentials = new HashSet<>();

    @OneToMany(mappedBy = "electionAuthority", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Election> elections = new HashSet<>();

    @Override
    public String toString() {
        return "ElectionAuthorityService{" +
                "electionAuthorityId=" + electionAuthorityId + '\'' +
                ", authorityName='" + authorityName + '\'' +
                ", authorityEmail='" + authorityEmail + '\'' +
                '}';
    }
}
