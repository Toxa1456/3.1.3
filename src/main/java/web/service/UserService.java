package web.service;

import web.model.User;
import java.util.List;

public interface UserService {
    User findById(Long id);
    void save(User user);
    void deleteById(Long id);
    List<User> findAllUsers();
}
