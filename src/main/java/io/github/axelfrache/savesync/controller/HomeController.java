package io.github.axelfrache.savesync.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String redirect() {
        return "redirect:/savesync";
    }

    @GetMapping("/savesync")
    public String home() {
        return "home";
    }
}
