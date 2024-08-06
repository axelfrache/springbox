package io.github.axelfrache.springbox.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import io.github.axelfrache.springbox.exception.UserAlreadyExistsException;
import io.github.axelfrache.springbox.model.User;
import io.github.axelfrache.springbox.repository.UserRepository;

@Controller
@RequestMapping("/springbox")
public class UserController {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final UserDetailsService userDetailsService;

	@Autowired
	public UserController(UserRepository userRepository, PasswordEncoder passwordEncoder, UserDetailsService userDetailsService) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.userDetailsService = userDetailsService;
	}

	@GetMapping("/register")
	public String showRegistrationForm() {
		return "register";
	}

	@PostMapping("/register")
	public String registerUser(User user) {
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

	@GetMapping("/user/edit")
	public String showEditUserForm(Model model, @AuthenticationPrincipal UserDetails userDetails) {
		var optionalUser = userRepository.findByEmail(userDetails.getUsername());
		if (optionalUser.isEmpty()) {
			return "error";
		}
		model.addAttribute("user", optionalUser.get());
		return "edit-user";
	}

	@PostMapping("/user/edit")
	public String editUser(@RequestParam("username") String username,
			@RequestParam("email") String email,
			@RequestParam("password") String password,
			@AuthenticationPrincipal UserDetails userDetails) {
		var optionalUser = userRepository.findByEmail(userDetails.getUsername());
		if (optionalUser.isEmpty()) {
			return "error";
		}
		var user = optionalUser.get();
		user.setUsername(username);
		user.setEmail(email);
		if (!password.isBlank()) {
			user.setPassword(passwordEncoder.encode(password));
		}
		userRepository.save(user);

		var updatedUserDetails = userDetailsService.loadUserByUsername(user.getEmail());
		SecurityContextHolder.getContext().setAuthentication(
				new UsernamePasswordAuthenticationToken(updatedUserDetails, updatedUserDetails.getPassword(), updatedUserDetails.getAuthorities())
		);
		return "redirect:/springbox";
	}
}