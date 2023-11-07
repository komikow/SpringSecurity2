package by.komikow.controller;

import by.komikow.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.security.Principal;

@Controller
public class UserController {

    @Autowired
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/user/about")
    public String getUser(Principal principal, Model model) {
        String username = principal.getName();
        model.addAttribute("readUser", userService.readUser(username));
        return "aboutUser";
    }

    @GetMapping("/user/{newEmail}")
    public String update(Principal principal, @PathVariable("newEmail") String newEmail, Model model) {
        String username = principal.getName();
        userService.updateUserByUsername(username, newEmail);
        model.addAttribute("readUser", userService.readUser(username));
        return "aboutUser";
    }
}
