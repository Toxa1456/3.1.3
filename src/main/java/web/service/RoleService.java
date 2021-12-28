package web.service;

import web.model.Role;
import java.util.Set;

public interface RoleService {
    Set<Role> findAllRoles();
    Role findRoleById(Long id);
}
