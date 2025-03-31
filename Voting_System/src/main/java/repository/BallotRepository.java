package repository;

import domain.Ballot;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface BallotRepository extends JpaRepository<Ballot, UUID> {
}
