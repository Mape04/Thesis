package repository;


import domain.Election;
import domain.ElectionAccessLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ElectionRepository extends JpaRepository<Election, UUID> {
    List<Election> findByAccessLevel(ElectionAccessLevel accessLevel);
    @Query("SELECT e.electionId FROM Election e WHERE e.startDate <= CURRENT_TIMESTAMP AND e.endDate >= CURRENT_TIMESTAMP")
    List<UUID> findAllActiveElectionIds();


}
