package com.scm.controllers;

import java.util.UUID;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.scm.entities.Contact;
import com.scm.entities.User;
import com.scm.forms.ContactForm;
import com.scm.forms.ContactSearchForm;
import com.scm.helpers.AppConstants;
import com.scm.helpers.Helper;
import com.scm.helpers.Message;
import com.scm.helpers.MessageType;
import com.scm.services.ContactService;
import com.scm.services.ImageService;
import com.scm.services.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/user/contacts")
public class ContactController {

	@Autowired
	ContactService contactService;
	@Autowired
	UserService userService;
	
    @Autowired
    private ImageService imageService;
	
	private Logger logger=org.slf4j.LoggerFactory.getLogger(ContactController.class);
	
	
@GetMapping("/add")
public String addContactView(Model model) {
	ContactForm contactForm=new ContactForm();
	 contactForm.setFavorite(true);
	model.addAttribute("contactForm", contactForm);
	return "user/add_contact";
}
@PostMapping("/add")
public String saveContact(@Valid @ModelAttribute ContactForm contactForm,BindingResult result,Authentication authentication,HttpSession session) {
	
	
    if (result.hasErrors()) {

        result.getAllErrors().forEach(error -> logger.info(error.toString()));

        session.setAttribute("message", Message.builder()
                .content("Please correct the following errors")
                .type(MessageType.red)
                .build());
        return "user/add_contact";
    }

	String username = Helper.getEmailOfLoggedInUser(authentication);
	
	// form ---> contact

    User user = userService.getUserByEmail(username);

	//image process
    
    // uplod karne ka code
    
   
    Contact contact = new Contact();
    contact.setName(contactForm.getName());
    contact.setFavourite(contactForm.isFavorite());
    contact.setEmail(contactForm.getEmail());
    contact.setPhoneNumber(contactForm.getPhoneNumber());
    contact.setAdress(contactForm.getAddress());
    contact.setDescripstion(contactForm.getDescription());
    contact.setUser(user);
    contact.setLinkedInLink(contactForm.getLinkedInLink());
    contact.setWebLink(contactForm.getWebsiteLink());
    if (contactForm.getContactImage() != null && !contactForm.getContactImage().isEmpty()) {
    String filename = UUID.randomUUID().toString();
    String fileURL = imageService.uploadImage(contactForm.getContactImage(), filename);
      contact.setPic(fileURL);
     contact.setCloudinaryImagePublicId(filename);
}


    contactService.save(contact);
	System.out.println(contactForm);
	
	// 4 `set message to be displayed on the view

    session.setAttribute("message",
            Message.builder()
                    .content("You have successfully added a new contact")
                    .type(MessageType.green)
                    .build());

	return "redirect:/user/contacts/add";
}
@RequestMapping
public String viewContacts(@RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = AppConstants.PAGE_SIZE + "") int size,
        @RequestParam(defaultValue = "name") String sortBy,
        @RequestParam(defaultValue = "asc") String direction, Model model,
        Authentication authentication) {

	String username=Helper.getEmailOfLoggedInUser(authentication);
	User user=userService.getUserByEmail(username);
	Page<Contact> pageContact = contactService.getByUser(user, page, size, sortBy, direction);
    model.addAttribute("pageContact", pageContact);
    model.addAttribute("pageSize", AppConstants.PAGE_SIZE);
    model.addAttribute("contactSearchForm", new ContactSearchForm());
	
	return "user/contacts";
}


@GetMapping("/search")
public String searchHandler(

        @ModelAttribute ContactSearchForm contactSearchForm,
        @RequestParam(defaultValue = AppConstants.PAGE_SIZE + "") int size,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "name") String sortBy,
        @RequestParam(defaultValue = "asc") String direction,
        Model model,
        Authentication authentication) {

    logger.info("field {} keyword {}",contactSearchForm.getField(),contactSearchForm.getValue());

    var user = userService.getUserByEmail(Helper.getEmailOfLoggedInUser(authentication));

    Page<Contact> pageContact = null;
    if (contactSearchForm.getField().equalsIgnoreCase("name")) {
        pageContact = contactService.searchByName(contactSearchForm.getValue(), size, page, sortBy, direction,
                user);
    } else if (contactSearchForm.getField().equalsIgnoreCase("email")) {
        pageContact = contactService.searchByEmail(contactSearchForm.getValue(), size, page, sortBy, direction,
                user);
    } else if (contactSearchForm.getField().equalsIgnoreCase("phone")) {
        pageContact = contactService.searchByPhoneNumber(contactSearchForm.getValue(), size, page, sortBy,
                direction, user);
    }

    logger.info("pageContact {}", pageContact);

    model.addAttribute("contactSearchForm", contactSearchForm);

    model.addAttribute("pageContact", pageContact);

    model.addAttribute("pageSize", AppConstants.PAGE_SIZE);

    return "user/search";
}
@GetMapping("/delete/{contactId}")
public String deleteContact(
        @PathVariable String contactId,
        HttpSession session) {
    contactService.delete(contactId);
    logger.info("contactId {} deleted", contactId);

    session.setAttribute("message",
            Message.builder()
                    .content("Contact is Deleted successfully !! ")
                    .type(MessageType.green)
                    .build()

    );

    return "redirect:/user/contacts";
}
//update contact form view
@GetMapping("/view/{contactId}")
public String updateContactFormView(
        @PathVariable String contactId,
        Model model) {

    var contact = contactService.getById(contactId);
    ContactForm contactForm = new ContactForm();
    contactForm.setName(contact.getName());
    contactForm.setEmail(contact.getEmail());
    contactForm.setPhoneNumber(contact.getPhoneNumber());
    contactForm.setAddress(contact.getAdress());
    contactForm.setDescription(contact.getDescripstion());
    contactForm.setFavorite(contact.isFavourite());
    contactForm.setWebsiteLink(contact.getWebLink());
    contactForm.setLinkedInLink(contact.getLinkedInLink());
    contactForm.setPicture(contact.getPic());
    ;
    model.addAttribute("contactForm", contactForm);
    model.addAttribute("contactId", contactId);

    return "user/update_contact_view";
}
@PostMapping("/update/{contactId}")
public String updateContact(@PathVariable String contactId,
        @Valid @ModelAttribute ContactForm contactForm,
        BindingResult bindingResult,
        Model model) {

    // update the contact
    if (bindingResult.hasErrors()) {
        return "user/update_contact_view";
    }

    var con = contactService.getById(contactId);
    con.setId(contactId);
    con.setName(contactForm.getName());
    con.setEmail(contactForm.getEmail());
    con.setPhoneNumber(contactForm.getPhoneNumber());
    con.setAdress(contactForm.getAddress());
    con.setDescripstion(contactForm.getDescription());
    con.setFavourite(contactForm.isFavorite());
    con.setWebLink(contactForm.getWebsiteLink());
    con.setLinkedInLink(contactForm.getLinkedInLink());

    // process image:

    if (contactForm.getContactImage() != null && !contactForm.getContactImage().isEmpty()) {
        logger.info("file is not empty");
        String fileName = UUID.randomUUID().toString();
        String imageUrl = imageService.uploadImage(contactForm.getContactImage(), fileName);
        con.setCloudinaryImagePublicId(fileName);
        con.setPic(imageUrl);
        contactForm.setPicture(imageUrl);

    } else {
        logger.info("file is empty");
    }

    var updateCon = contactService.update(con);
    logger.info("updated contact {}", updateCon);

    model.addAttribute("message", Message.builder().content("Contact Updated !!").type(MessageType.green).build());

    return "redirect:/user/contacts/view/" + contactId;
}


}

