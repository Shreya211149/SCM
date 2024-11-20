package com.scm.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.scm.entities.User;
import com.scm.helpers.Helper;
import com.scm.services.UserService;

@ControllerAdvice
public class RouteController {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	 private UserService userService;

	 @ModelAttribute
	 public void addLoggedInUserInformation(Model model,Authentication authentication) {
		 if(authentication==null) {
			 return;
		 }
		   System.out.println("adding logged in user to model");
		 String username=Helper.getEmailOfLoggedInUser(authentication);
			logger.info("User logged in {}",username);
			User user = userService.getUserByEmail(username);
			if(user==null) {
				model.addAttribute("loggedInuser", null);
			}
			else {
				System.out.println(user);
			System.out.println(user.getName());
			System.out.println(user.getEmail());
			System.out.println(user.getProfilePic());
			model.addAttribute("loggedInuser", user);
			}
	 }
}
