package repository;


import domain.Election;
import domain.ElectionAccessLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ElectionRepository extends JpaRepository<Election, UUID> {
    List<Election> findByAccessLevel(ElectionAccessLevel accessLevel);

}
