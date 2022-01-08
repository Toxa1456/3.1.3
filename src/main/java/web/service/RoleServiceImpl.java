package web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import web.model.Role;
import web.repositories.RoleRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class RoleServiceImpl implements RoleService{

    RoleRepository roleRepository;

    @Autowired
    public RoleServiceImpl(RoleRepository roleRepository){
        this.roleRepository = roleRepository;
    }

    @Override
    public Set<Role> findAllRoles() {
        List<Role> list = (List<Role>) roleRepository.findAll();
        Set<Role> roles = new HashSet<>();
        roles.addAll(list);
        return roles;
    }

    @Override
    public Role findRoleById(Long id) {
        return roleRepository.findById(id).get();
    }
}
