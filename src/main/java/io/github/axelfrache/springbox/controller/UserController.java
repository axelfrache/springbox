package io.github.axelfrache.springbox.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import io.github.axelfrache.springbox.exception.UserAlreadyExistsException;
import io.github.axelfrache.springbox.model.User;
import io.github.axelfrache.springbox.repository.UserRepository;

@Controller
@RequestMapping("/springbox")
public class UserController {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@GetMapping("/register")
	public String showRegistrationForm() {
		return "register";
	}

	@PostMapping("/register")
	public String registerUser(User user, Model model) {
		if (userRepository.findByEmail(user.getEmail()).isPresent()) {
			throw new UserAlreadyExistsException();
		}

		user.setPassword(passwordEncoder.encode(user.getPassword()));
		userRepository.save(user);
		return "redirect:/springbox/login";
	}

	@GetMapping("/login")
	public String showLoginForm() {
		return "login";
	}
}
