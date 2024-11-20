package com.scm.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.scm.entities.User;
import com.scm.forms.UserForm;
import com.scm.helpers.Message;
import com.scm.helpers.MessageType;
import com.scm.services.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class PageController {
	
	@Autowired
	private UserService userService;
	
    @GetMapping("/")
	public String home(Model model) {
		System.out.println("Home page handler");
		model.addAttribute("name", "SCM2.0");
		return "home";
	}
    
 //about
  @GetMapping("/about")
    public String aboutPage() {
    	System.out.println("About page loaded");
    	
    	return "about";
    }
    
 //services
  @GetMapping("/services")
  public String servicesPage() {
  	System.out.println("Services page loaded");
  	
  	return "services";
  }
  
//contact
  @GetMapping("/contact")
  public String contactUs() {
  	System.out.println("Contact page loaded");
  	
  	return "contact";
  }
  
//login
  @GetMapping("/login")
  public String loginUser() {
  	System.out.println("Login page loaded");
  	
  	return "login";
  }
  
//Register
  @GetMapping("/register")
  public String register(Model model) {
  	System.out.println("Register page loaded");
  	UserForm userForm=new UserForm();
  	//userForm.setName("Shreya");
  	model.addAttribute("userForm", userForm);
  	
  	return "register";
  }
  
  @PostMapping("/do-register")
  public String processRegister(@Valid @ModelAttribute UserForm userForm,BindingResult rbBindingResult, HttpSession session) {
	  System.out.println("Process register");
	  System.out.println(userForm);
	  
	  if(rbBindingResult.hasErrors()) {
		  return "register";
	  }
	  
//	  UserForm---->user
//	  User user = User.builder()
//			  .name(userForm.getName())
//			  .email(userForm.getEmail())
//			  .password(userForm.getPassword())
//			  .about(userForm.getAbout())
//			  .phoneNumber(userForm.getPhoneNumber())
//			  .profilePic("https://www.google.com/url?sa=i&url=https%3A%2F%2Fwww.freepik.com%2Ffree-photos-vectors%2Fdefault-user&psig=AOvVaw3ELYPPiMoGYWV0fdywD6aa&ust=1730661958297000&source=images&cd=vfe&opi=89978449&ved=0CBEQjRxqFwoTCIjm5b-wvokDFQAAAAAdAAAAABAE")
//			  .build();
	  
	  User user = new User();
      user.setName(userForm.getName());
      user.setEmail(userForm.getEmail());
      user.setPassword(userForm.getPassword());
      user.setAbout(userForm.getAbout());
      user.setPhoneNumber(userForm.getPhoneNumber());
      user.setEnabled(false);
      user.setProfilePic("https://www.google.com/url?sa=i&url=https%3A%2F%2Fwww.freepik.com%2Ffree-photos-vectors%2Fdefault-user&psig=AOvVaw3ELYPPiMoGYWV0fdywD6aa&ust=1730661958297000&source=images&cd=vfe&opi=89978449&ved=0CBEQjRxqFwoTCIjm5b-wvokDFQAAAAAdAAAAABAE");
	  User savedUser = userService.saveUser(user);
	  System.out.println("User Saved");
	 
	  Message message = Message.builder().content("Registration sucessfull").type(MessageType.green).build();
	  session.setAttribute("message",message);
	  
	  return "redirect:/register";
  }
}
