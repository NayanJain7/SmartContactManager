package com.smartcontactmanager.nayan.Controller;

import java.security.Principal;
import java.util.List;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.smartcontactmanager.nayan.Dao.UserRepository;
import com.smartcontactmanager.nayan.Entity.User;
import com.smartcontactmanager.nayan.Service.ReCaptchaValidationService;
import com.smartcontactmanager.nayan.message.Message;

@Controller
public class HomeController {

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private ReCaptchaValidationService validator;

	@GetMapping("/")
	public String home(Model model) {
		model.addAttribute("title", "Home Page");
		return "home";
	}

	@GetMapping("/signup")
	public String signup(Model model) {
		model.addAttribute("user", new User());
		return "signup";
	}
	@GetMapping("/show")
	@ResponseBody
	public List<User> showData() {
		List<User> findAll = userRepo.findAll();
		return findAll;
	}

	// handling form action of signUp page

	@PostMapping("/do_register")
	public String do_register(@Valid @ModelAttribute("user") User user, BindingResult result,
			@RequestParam(value = "aggrement", defaultValue = "false") boolean aggrement,
			@RequestParam(name = "g-recaptcha-response") String captcha, Model model, HttpSession session) {

		try {

			if (result.hasErrors()) {
				model.addAttribute("user", user);
				return "signup";
			}
			
			if(validator.validateCaptcha(captcha))
	        {    
				user.setEnables(true);
				user.setRole("ROLE_USER");
				user.setImageUrl("default.png");

				user.setPassword(passwordEncoder.encode(user.getPassword()));

				userRepo.save(user);
				// System.out.println(user);

				model.addAttribute("user", new User());
				session.setAttribute("message", new Message("Successfully Registered ", "alert-success"));
			 } 
		     else { 
		    	 session.setAttribute("message", new Message("Please Verify Captcha", "alert-danger"));
		    	 }      



		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("user", user);
			session.setAttribute("message", new Message("Something went wrong ", "alert-danger"));

		}

		return "signup";
	}

	// display sign in page
	@GetMapping("/signin")
	public String CustomLogin(Model model) {

		model.addAttribute("title", "Signin - Smart Contact Manager");
		return "signin";
	}

	// display forget page
	@GetMapping("/forgot")
	public String forgotPassword(Model model) {

		model.addAttribute("title", "Forgot Password - Smart Contact Manager");
		return "forgotPassword";
	}

	// handle forgot form
	@PostMapping("/processForgot")
	public String handleForgetPassowrd(@RequestParam("email") String username,
			@RequestParam("question") String question, @RequestParam("answer") String answer, HttpSession session,
			Principal principal, Model model) {

		User user = userRepo.getUserByUserName(username);

		if (user == null) {
			session.setAttribute("message", new Message("Invalid email, Try again!! ", "alert-danger"));

			return "forgotPassword";
		}

		if (user.getQuestion().equals(question) && user.getAnswer().equals(answer)) {
			model.addAttribute("userId", user.getId());
			return "reset";
		}

		else {
			session.setAttribute("message", new Message("Invalid Answer, Try again!! ", "alert-danger"));

			return "forgotPassword";
		}

	}

	@PostMapping("/resetPassword/{userId}")
	public String resetPassword(@PathVariable("userId") Integer userId, @RequestParam("password") String pass,
			@RequestParam("confirmPassword") String confirmPass, HttpSession session) {

		if (!pass.equals(confirmPass)) {
			session.setAttribute("message",
					new Message("Password and Confirm Password are not matched ", "alert-danger"));
			return "reset";

		}
		if (pass.length() < 8 || confirmPass.length() < 8) {
			session.setAttribute("message",
					new Message("Password length must be greater than 8 character", "alert-danger"));
			return "reset";

		}

		User user = userRepo.findById(userId).get();

		user.setPassword(passwordEncoder.encode(pass));

		userRepo.save(user);

		session.setAttribute("message", new Message("	Password reset successfully !!", "alert-success"));
		return "signin";

	}

}
