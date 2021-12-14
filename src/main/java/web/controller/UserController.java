package web.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
@RequestMapping("/user")
public class UserController {


    @RequestMapping("/")
    public String helloUser (@AuthenticationPrincipal User user, ModelMap model) {
        model.addAttribute("user", user);
        return "/user";
    }
}
