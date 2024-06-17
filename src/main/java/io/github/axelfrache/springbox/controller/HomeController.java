package io.github.axelfrache.springbox.controller;

import io.github.axelfrache.springbox.model.User;
import io.github.axelfrache.springbox.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Optional;

@Controller
public class HomeController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/springbox")
    public String home(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails != null) {
            Optional<User> optionalUser = userRepository.findByEmail(userDetails.getUsername());
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                model.addAttribute("username", user.getUsername());
            }
        }
        return "home";
    }
}
