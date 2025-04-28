package domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class RsaKey {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID rsaKeyId;

    @Basic(fetch = FetchType.LAZY) // Use byte[] with lazy loading to handle large binary data
    @Column(nullable = false)
    private byte[] publicKey;  // Store the public key as binary

    @Basic(fetch = FetchType.LAZY)  // Same for the private key
    @Column(nullable = false)
    private byte[] privateKey;  // Store the private key as binary

    @Column(nullable = false, unique = true)  // Add electionId field, assuming each RSA key is tied to an election
    private UUID electionAuthorityId;  // This associates the RSA key with a specific election
}
