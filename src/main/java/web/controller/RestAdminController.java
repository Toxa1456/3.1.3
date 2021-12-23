package web.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import web.model.Role;
import web.model.User;
import web.repositories.RoleRepository;
import web.repositories.UserRepository;
import java.util.*;


@RestController
@RequestMapping("/admin")
public class RestAdminController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Autowired
    public RestAdminController(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @GetMapping("/table")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = (List<User>) userRepository.findAll();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/user")
    public ResponseEntity<User> getAuthUser(@AuthenticationPrincipal User user) {
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PostMapping("/users/{role}")
    public ResponseEntity addNewUser(@RequestBody User user, @PathVariable("role") Optional<String> role) {
        setRoles(role, user);
        userRepository.save(user);
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getOneUser(@PathVariable("id") long id) {
        User user = userRepository.findById(id).get();
        return new ResponseEntity<>(user, HttpStatus.OK);
    }


    @PutMapping("/users/{id}/{role}")
    public ResponseEntity updateUser(@PathVariable("id") long id,  @PathVariable("role") Optional<String> role, @RequestBody User user) {

        User original = userRepository.findById(id).get();
        setRoles(role, user);
        if (user.getPassword().equals("")) {
            user.setPassword(original.getPassword());
        }

        userRepository.save(user);
        return new ResponseEntity(HttpStatus.OK);

    }

    @DeleteMapping("users/{id}")
    public ResponseEntity deleteUser(@PathVariable("id") long id) {
        User user = userRepository.findById(id).get();
        userRepository.delete(user);
        return new ResponseEntity(HttpStatus.OK);
    }

    private void setRoles(@RequestParam Optional<String> role, User user) {
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
                }
            }
        }
    }
}
