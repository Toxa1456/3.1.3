package web.repositories;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import web.model.User;
import java.util.Optional;


@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    @EntityGraph(value = "roles.detail", type = EntityGraph.EntityGraphType.LOAD)
    User findByEmail(String email);
    @EntityGraph(value = "roles.detail", type = EntityGraph.EntityGraphType.LOAD)
    Optional<User> findById(Long id);
}
