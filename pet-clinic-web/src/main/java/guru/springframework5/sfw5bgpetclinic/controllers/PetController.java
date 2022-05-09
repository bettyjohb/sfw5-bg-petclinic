package guru.springframework5.sfw5bgpetclinic.controllers;
//***************************************************************************
//Class:  PetController 
//
//Primary Controller for pet related pages (identified by @Controller / not extension).
//
//When an HTTP request arrives at server, will be handled by Spring provided 
//DispatcherServlet (front controller) that uses Handler Mapping to determine 
//which Controller to invoke (i.e. PetController, etc.).  
//
//@RequestMapping is used to map the HTTP Request Path to specific method on 
//the controller (for instance, can say all URL for that Web App ending with
///hello will go to ("/hello") method on controller which might just return a 
//view name "hello." Recall when jsp, ViewResolver added prefix/suffix to 
//tell where to find file and to add ".jsp."  When Thymeleaf, default looks in 
//templates and finds hello.html.
//*************************************************************************** 

import guru.springframework5.sfw5bgpetclinic.model.Owner;
import guru.springframework5.sfw5bgpetclinic.model.PetType;
import guru.springframework5.sfw5bgpetclinic.services.OwnerService;
import guru.springframework5.sfw5bgpetclinic.services.PetService;
import guru.springframework5.sfw5bgpetclinic.services.PetTypeService;

import java.util.Collection;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller								// Tell Spring this is an MVC Controller to be instantiated.
@RequestMapping ("/owners/{ownerId}") 	// Base mapping prefixed to mappings a/w handler method
public class PetController {

	// --------------------------------------------------------------
	// Attributes - Interface to various IMPL services (so can swap DB, Map, etc.)
	//              Don't include repository since may use Map or other which doesn't have one. 
	// --------------------------------------------------------------
	private final PetService petService;    
	private final PetTypeService petTypeService;
	private final OwnerService ownerService;

	// --------------------------------------------------------------
	// Constructor 
	// --------------------------------------------------------------

	// @Autowired not required in Spring 5. 
	// Component Scan sees @Controller and instantiates PetController bean; thereby using Constructor and 
	// injecting PetService (which is a @Service and therefore available for Spring Context to see). 
	// Profile determines which IMPL to use.  
	public PetController (PetService petService, PetTypeService petTypeService, OwnerService ownerService) {
		this.petService = petService;
		this.petTypeService = petTypeService;
		this.ownerService = ownerService;
	}  // end constructor

	// --------------------------------------------------------------
	// @ModelAttribute ensures adding routinely used attributes to the 
	//                 model (no matter which RequestMapping) for use by the view.
	//				   This way don't repeat setup in each handler method. 
	// --------------------------------------------------------------
	
	@ModelAttribute ("types")	
	public Collection<PetType> populatePetTypes() {
		return this.petTypeService.findAll();
	}
	
	@ModelAttribute ("owner")
	// Since base RequestMapping is /owners/{ownerId}, be sure Pet's Owner is available. 
	public Owner findOwner(@PathVariable String ownerId) {
		return this.ownerService.findById(Long.valueOf(ownerId));
	}
	
	// --------------------------------------------------------------
	// Web Data Binder 
	// --------------------------------------------------------------
	@InitBinder ("owner")
	public void setAllowedFields (WebDataBinder webDataBinder) {
		// Don't allow Spring to place id in the object (could be typed).  Needs to be generated on Save.   
		webDataBinder.setDisallowedFields("id");
	}

	// --------------------------------------------------------------
	// Find and show a multiple Owner list or a Single Owner Detail
	// --------------------------------------------------------------

}  // end PetController
