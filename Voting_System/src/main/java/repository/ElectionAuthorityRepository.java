package repository;

import domain.ElectionAuthority;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ElectionAuthorityRepository extends JpaRepository<ElectionAuthority, UUID> {
}
