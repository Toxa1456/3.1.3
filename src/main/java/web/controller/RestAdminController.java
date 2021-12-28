package web.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import web.model.Role;
import web.model.User;
import web.service.RoleService;
import web.service.UserService;
import java.util.*;


@RestController
@RequestMapping("/admin")
public class RestAdminController {

    private final UserService userService;
    private final RoleService roleService;

    @Autowired
    public RestAdminController(UserService userRepository, RoleService roleRepository) {
        this.userService = userRepository;
        this.roleService = roleRepository;
    }

    @GetMapping("/table")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.findAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/user")
    public ResponseEntity<User> getAuthUser(@AuthenticationPrincipal User user) {
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PostMapping("/users/{role}")
    public ResponseEntity<String> addNewUser(@RequestBody User user, @PathVariable("role") Optional<String> role) {
        setRoles(role, user);
        userService.save(user);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getOneUser(@PathVariable("id") long id) {
        User user = userService.findById(id);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }


    @PutMapping("/users/{id}/{role}")
    public ResponseEntity<String> updateUser(@PathVariable("id") long id,  @PathVariable("role") Optional<String> role, @RequestBody User user) {

        User original = userService.findById(id);
        setRoles(role, user);
        if (user.getPassword().equals("")) {
            user.setPassword(original.getPassword());
        }

        userService.save(user);
        return new ResponseEntity<>(HttpStatus.OK);

    }

    @DeleteMapping("users/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable("id") long id) {
        userService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private void setRoles(@RequestParam Optional<String> role, User user) {
        if (role.get().equalsIgnoreCase("1,2")) {
            Set<Role> roles = roleService.findAllRoles();
            user.setRoles(roles);
        } else {
            if (role.get().equals("1")) {
                user.setRoles(Collections.singleton(roleService.findRoleById(2L)));
            } else {
                if (role.get().equals("2")) {
                    user.setRoles(Collections.singleton(roleService.findRoleById(1L)));
                }
            }
        }
    }
}
