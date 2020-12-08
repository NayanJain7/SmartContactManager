package com.smartcontactmanager.nayan.Controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smartcontactmanager.nayan.Dao.ContactRepository;
import com.smartcontactmanager.nayan.Dao.UserRepository;
import com.smartcontactmanager.nayan.Entity.Contact;
import com.smartcontactmanager.nayan.Entity.User;
import com.smartcontactmanager.nayan.message.Message;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private ContactRepository contactRepo;

	// Method for add common data in all handlers
	@ModelAttribute
	public void addCommonData(Model model, Principal principal) {
		String username = principal.getName();
		User user = userRepo.getUserByUserName(username);
		model.addAttribute("user", user);
	}

	@GetMapping("/index")
	public String dashboard(Model model, Principal principal) {

		model.addAttribute("title", "Dashboard");

		return "normal/user_dashboard";
	}

	// open form handler
	@GetMapping("/add_contact")
	public String addContactForm(Model model) {
		model.addAttribute("title", "Add Contact");

		model.addAttribute("contact", new Contact());

		return "normal/add_contact";
	}

	// handle to add contact in database
	@PostMapping("/process_contact")
	public String processCotactForm(@ModelAttribute Contact contact, @RequestParam("profileImg") MultipartFile file,
			Principal principal, HttpSession session) {

		try {

			String username = principal.getName();

			User user = userRepo.getUserByUserName(username);

			if (file.isEmpty()) {
				contact.setImage("default.png");
			}

			else {

				String filename = file.getOriginalFilename();

				contact.setImage(filename);

				File savedFile = new ClassPathResource("static/images").getFile();

				// make a full path to access the file
				Path path = Paths.get(savedFile.getAbsolutePath() + File.separator + filename);

				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

			}

			contact.setUser(user);
			user.getContacts().add(contact);
			userRepo.save(user);
			session.setAttribute("message", new Message("Contact saved Successfully!!", "alert-success"));

		} catch (Exception e) {

			session.setAttribute("message", new Message("Please try again, Contact not saved ", "alert-danger"));
			e.printStackTrace();
		}

		return "normal/add_contact";

	}

	// url /user/show_contact/1?sortField=name&sortDir=asc
	@GetMapping("/show_contact/{current_page}")
	public String showContact(Model model, Principal principal, @PathVariable("current_page") Integer page_no,
			@RequestParam("sortField") String sortField, @RequestParam("sortDir") String sortDir) {
		model.addAttribute("titlt", "View Contact - Smart Contact Manager");

		String username = principal.getName();

		User user = userRepo.getUserByUserName(username);

		// if sort direction is equal to ascending order then it sort it into ascending
		// else sort it into descending

		Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending()
				: Sort.by(sortField).descending();

		Pageable pageable = PageRequest.of(page_no, 5, sort); // here 5 is for showing 5 contact in a page

		Page<Contact> contacts = contactRepo.findContactByUSer(user.getId(), pageable);

		model.addAttribute("contacts", contacts);
		model.addAttribute("current_page", page_no);

		model.addAttribute("total_pages", contacts.getTotalPages());
		model.addAttribute("total_contacts", contacts.getTotalElements());

		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);

		model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");

		return "normal/show_contact";
	}

	;

	// show s single contact details
	@GetMapping("/{cID}/contact_profile")
	public String contact_profile(@PathVariable("cID") Integer cID, Model model, Principal principal) {

		Contact contact = contactRepo.findById(cID).get();
		String username = principal.getName();
		User user = userRepo.getUserByUserName(username);

		if (user.getId() == contact.getUser().getId()) {

			model.addAttribute("contact", contact);
			model.addAttribute("title","contact-profile");
			return "normal/contact_profile";
		}
		return "";

	}
	//delete the contact handler
	@GetMapping("/delete/{cID}/{page_no}")
	public String deleteContact(@PathVariable("cID") Integer cid, @PathVariable("page_no") Integer page_no,
			HttpSession session, Principal principal) throws IOException {

		Contact contact = contactRepo.findById(cid).get();

		String username = principal.getName();
		User user = userRepo.getUserByUserName(username);

		if (user.getId() == contact.getUser().getId()) {

			File file = new ClassPathResource("static/images").getFile();
			File f = new File(file, contact.getImage());
			f.delete();
			/*
			 * System.out.println(
			 * "##################################################################");
			 * System.out.println("file = "+file+"f = "+f); System.out.println(
			 * "##################################################################");
			 */

			user.getContacts().remove(contact);
			userRepo.save(user);

			// contact.setUser(null); this not delete contact from database
			// contactRepo.delete(contact);
			session.setAttribute("message", new Message("Contact deleted successfully", "alert-success"));
			return "redirect:/user/show_contact/" + page_no + "?sortField=name&sortDir=asc";
		} else
			return "";

	}

	// open page for updating contact
	@PostMapping("/update/{cID}")
	public String updateContact(@PathVariable("cID") Integer cid, Model model) {

		Contact contact = contactRepo.findById(cid).get();
		model.addAttribute("contact", contact);

		return "normal/updateContact";
	}

	// process update contact handler
	@PostMapping("/update_contact")
	public String updateContact(@ModelAttribute Contact contact, @RequestParam("profileImg") MultipartFile file,
			Principal principal, HttpSession session) {

		try {

			Contact oldContact = contactRepo.findById(contact.getCid()).get();

			if (!file.isEmpty()) {

				
				  File filepath = new ClassPathResource("static/images").getFile(); 
				  File f = new File(filepath, oldContact.getImage());
				  f.delete();
				  System.out.println(f);

				File savedFile = new ClassPathResource("static/images").getFile();

				// make a full path to access the file
				Path path = Paths.get(savedFile.getAbsolutePath() + File.separator + file.getOriginalFilename());

				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				contact.setImage(file.getOriginalFilename());

			} else {

				contact.setImage(oldContact.getImage());
			}

			
			  String username = principal.getName();
			  
			  User user = userRepo.getUserByUserName(username);
			  
			  contact.setUser(user);
			 

			contactRepo.save(contact);

			session.setAttribute("message", new Message("Contact updated Successfully!!", "alert-success"));

		} catch (Exception e) {
			e.printStackTrace();
		}

		return "redirect:/user/" + contact.getCid() + "/contact_profile";

	}

	//show user profile
	@GetMapping("/profile")
	public String showProfile(Model m) {
		m.addAttribute("title", "Profile");
		return"/normal/user_profile";
	}


	// show  update user details page
	
	@GetMapping("/user_update")
	public String userProfileUpdate() {
		return"/normal/updateUserProfile";
	}
	// handling the form of update details of user
	@PostMapping("/update_profile")
	public String updateProfile(@ModelAttribute User newuser,HttpSession session) {
		
		userRepo.save(newuser);
		session.setAttribute("message", new Message("Your information is update Successfully!!", "alert-success"));
		
		return "redirect:/user/user_update";
	}
	
	// delete user Account
	
	@GetMapping("/deleteUser")
	public String deleteAccount(Principal principal) {
		String username = principal.getName();
		User user = userRepo.getUserByUserName(username);
		
		List<Contact> contacts = user.getContacts();
		
		for (Contact contact : contacts) {
			try {
				
			 File filepath = new ClassPathResource("static/images").getFile(); 
			  File f = new File(filepath, contact.getImage());
			  f.delete();
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
		
		userRepo.delete(user); 
		return"redirect:/signup";
		
	}
	

}
