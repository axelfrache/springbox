package io.github.axelfrache.springbox.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import io.github.axelfrache.springbox.repository.UserRepository;

@Controller
public class HomeController {

    private final UserRepository userRepository;

    @Autowired
    public HomeController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/")
    public String redirectToHome() {
        return "redirect:/springbox";
    }

    @GetMapping("/springbox")
    public String home(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails != null) {
            var optionalUser = userRepository.findByEmail(userDetails.getUsername());
            optionalUser.ifPresent(user -> model.addAttribute("username", user.getUsername()));
        }
        return "home";
    }
}
