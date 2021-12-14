package web.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import web.model.Role;
import web.model.User;
import web.repositories.RoleRepository;
import web.repositories.UserRepository;

import javax.validation.Valid;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Autowired
    public AdminController(UserRepository userRepository, RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
    }

    @RequestMapping("/")
    public String getUsers(ModelMap model) {
        model.addAttribute("Users", userRepository.findAll());
        return "admin";
    }

    @RequestMapping("/adduser")
    public String addUser(@RequestParam Optional<String> role, @Valid User user, BindingResult result) {
        if (result.hasErrors()) {
            return "addUser";
        }
        if (role.get().equalsIgnoreCase("1,2")){
            Set<Role> roles = new HashSet<>();
            roles.add(roleRepository.findById(1L).get());
            roles.add(roleRepository.findById(2L).get());
            user.setRoles(roles);
        } else {
            if (role.get().equals("1")) {
                user.setRoles(Collections.singleton(roleRepository.findById(2L).get()));
            } else {
                if (role.get().equals("2")) {
                    user.setRoles(Collections.singleton(roleRepository.findById(1L).get()));
                } else {
                    return "addUser";
                }
            }
        }
        userRepository.save(user);
        return "redirect:/admin/";
    }

    @RequestMapping("/update/{id}")
    public String updateUser(@RequestParam Optional<String> role, @PathVariable("id") long id, @Valid User user, BindingResult result) {
        if (result.hasErrors()) {
            User original = userRepository.findById(id).get();
            user.setName(original.getName());
            user.setPassword(original.getPassword());
            user.setEmail(original.getEmail());
            user.setRoles(original.getRoles());
            user.setId(id);
            return "changeUser";
        }
        if (role.get().equalsIgnoreCase("1,2")) {
            Set<Role> roles = new HashSet<>();
            roles.add(roleRepository.findById(1L).get());
            roles.add(roleRepository.findById(2L).get());
            user.setRoles(roles);
        } else {
            if (role.get().equals("1")) {
                user.setRoles(Collections.singleton(roleRepository.findById(2L).get()));
            } else {
                if (role.get().equals("2")) {
                    user.setRoles(Collections.singleton(roleRepository.findById(1L).get()));
                } else {
                    return "changeUser";
                }
            }
        }
        userRepository.save(user);

        return "redirect:/admin/";
    }

    @RequestMapping("/delete/{id}")
    public String deleteUser(@PathVariable("id") long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
        userRepository.delete(user);
        return "redirect:/admin/";
    }


}
