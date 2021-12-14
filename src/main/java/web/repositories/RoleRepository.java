package web.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import web.model.Role;


@Repository
public interface RoleRepository extends CrudRepository<Role, Long> {
}
