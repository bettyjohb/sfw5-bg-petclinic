package guru.springframework5.sfw5bgpetclinic.controllers;
//***************************************************************************
//Class:  VisitController 
//
//Primary Controller for visit related pages (identified by @Controller / not extension).
//
//When an HTTP request arrives at server, will be handled by Spring provided 
//DispatcherServlet (front controller) that uses Handler Mapping to determine 
//which Controller to invoke (i.e. VisitController, etc.).  
//
//@RequestMapping is used to map the HTTP Request Path to specific method on 
//the controller (for instance, can say all URL for that Web App ending with
///hello will go to ("/hello") method on controller which might just return a 
//view name "hello." Recall when jsp, ViewResolver added prefix/suffix to 
//tell where to find file and to add ".jsp."  When Thymeleaf, default looks in 
//templates and finds hello.html.
//*************************************************************************** 

import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import guru.springframework5.sfw5bgpetclinic.model.Pet;
import guru.springframework5.sfw5bgpetclinic.model.Visit;
import guru.springframework5.sfw5bgpetclinic.services.PetService;
import guru.springframework5.sfw5bgpetclinic.services.VisitService;

@Controller							// Tell Spring this is an MVC Controller to be instantiated.
@RequestMapping ("/owners/{ownerId}/pets/{petId}") 	// Base mapping prefixed to mappings a/w handler method
public class VisitController {

	// --------------------------------------------------------------
	// Attributes - Interface to various IMPL services (so can swap DB, Map, etc.)
	//              Don't include repository since may use Map or other which doesn't have one. 
	// --------------------------------------------------------------
	private final PetService petService;
	private final VisitService visitService;

	// --------------------------------------------------------------
	// Constructor 
	// --------------------------------------------------------------

	// @Autowired not required in Spring 5. 
	// Component Scan sees @Controller and instantiates VisitController bean; thereby using Constructor and 
	// injecting PetService (which is a @Service and therefore available for Spring Context to see). 
	// Profile determines which IMPL to use.  
	public VisitController (PetService petService, VisitService visitService) {
		this.petService = petService;
		this.visitService = visitService;
	}  // end constructor

	// --------------------------------------------------------------
	// @ModelAttribute addS routinely used attributes to the model 
	//                 (no matter which RequestMapping) for use by the view.
	//				   This way don't repeat setup in each handler method. 
	// --------------------------------------------------------------
	
	@ModelAttribute ("pet")
	// Since base RequestMapping is /pets/{petId}, be sure Pet being scheduled is available. 
	public Pet findPet(@PathVariable String petId) {
		return this.petService.findById(Long.valueOf(petId));
	}

	// --------------------------------------------------------------
	// Web Data Binder 
	// --------------------------------------------------------------
	@InitBinder ({"pet", "visit"})
	public void setAllowedFields (WebDataBinder webDataBinder) {
		// Don't allow Spring to place id in the object (could be typed).  Needs to be generated on Save.   
		webDataBinder.setDisallowedFields("id");
	}

	// --------------------------------------------------------------
	// Create Visit methods.
	// --------------------------------------------------------------
	
	// Controller method to display create/update form to enter new visit data.  Uses empty Visit.
	// Omit @PathVariable String petId from method params due to @ModelAttribute("pet") above.
	@GetMapping("/visits/new")    // pets/petId at class level
	public String initCreateVisitForm(Model model) throws Exception {

		// Create an empty Visit and add it to the Model for the create view. 
		// The Pet being scheduled is in the Model from @ModelAttribute methods above.
		Visit visit = Visit.builder().build();

		if (model.getAttribute("pet") == null)
			System.out.println("PET WAS NOT SET UP !!!!!!!!!!!!!!!!!!!!! \n\n\n");
		((Pet)model.getAttribute("pet")).add(visit);  // Adds visit to pet (which sets pet attribute of visit)  
		                                              // Not saved to DB yet, so will reset on submit.
													  // Remaining visit attributes filled out in form.
		model.addAttribute("visit", visit);
		return "pets/createOrUpdateVisit";
	}
	
	// Controller method to process create/update form submit to save new data.  Uses entered Visit data.  
	// Omit @PathVariable String petId from method params due to @ModelAttribute("pet") above.
	@PostMapping("/visits/new")    // pets/petId at class level
	public String processCreateVisitForm(@Validated Visit visit, BindingResult bindingResult, Model model) throws Exception {
		// @ModelAttribute retrieves Pet from DB and places in Model since needed each time.
		// Therefore, Pet is from DB and has no knowledge of new visit.  Add Visit to Pet. 
		Object o;
		Pet pet;
		if ( (o = model.getAttribute("pet")) != null)  {
			pet = (Pet)o;
			pet.add(visit);  // Adds visit to pet and pet to visit (bidirectional)
		} else {
			bindingResult.rejectValue("pet", "null", "Pet not found.");
		}
		
		// If Pet updates had errors, return to Update Form.  
		// Otherwise, save and redirect to show Owner view which shows pets.  
		if (bindingResult.hasErrors()) {
			// Pet updates had errors.  Return to update form. 
			model.addAttribute("visit", visit);
			return "pets/createOrUpdateVisit";
		} else {
			// Updates valid.  Save and display pet with updated visit info.
			// When redirect, controller will re-retrive pet and grab all visits (including new saved visit).
			visitService.save(visit);
			return "redirect:/owners/" + visit.getPet().getOwner().getId();
		}
	}


	// --------------------------------------------------------------
	// Update Visit methods.
	// --------------------------------------------------------------

	// Controller method to display create/update form to update existing visit data.  Uses existing Visit.
	// Omit @PathVariable String petId from method params due to @ModelAttribute("pet") above.
	@GetMapping("/visits/{visitId}/update")    // owners/ownerId/pets/petId at class level
	public String initUpdateVisitForm(@PathVariable Long visitId, Model model) throws Exception {
		
		System.out.println("INSIDE INIT UPDATE VISIT FORM &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& ]n]n]n ");
		// Invoke VisitService to retrieve Visit to be updated (noted by visitId) - Place in Model for update view. 
    	// Therefore, Visit is all intact (including id and Pet reference) since from DB. 
		// In addition, the Pet is in the Model from @ModelAttribute methods above.
		Visit visit = visitService.findById(visitId);
		System.out.println("Got visit id = " + visit.getId() + "   INSIDE INIT UPDATE VISIT FORM &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& ]n]n]n ");
		    
		model.addAttribute("visit", visit);

		System.out.println("Added visit to model.");
		
		return "/pets/createOrUpdateVisit";
	}

	// Controller method to process create/update form submit to save updated Visit data.    
	// Omit @PathVariable String petId from method params due to @ModelAttribute("pet") above.
	@PostMapping("/visits/{visitId}/update")    // owners/ownerId/pets/petId at class level
	public String processUpdateVisitForm(@PathVariable Long visitId, @Validated Visit visit, BindingResult bindingResult, Model model) throws Exception {
  	    // InitBinder above prevents from prepopulating ID when passed from form.  Set it from param here. 
  	    visit.setId(Long.valueOf(visitId));

  	    // IMPORTANT NOTE ABOUT WHAT IS AVAILABLE AND/OR PASSED FROM FORM: 
		// (1) @ModelAttribute (above) retrieves Pet from DB and places in Model since needed each time.
    	// (2) Pet attribute in Visit does not get forwarded from form (just fields).  Pet reference (id)
		// is stored in same Visit Table in DB (as foreign key) so when save Visit with null Pet, lose that link and 
		// that Visit is no longer linked back to its Pet by foreign key.  Therefore, have to set Pet here. 
		Object o;
		Pet pet = null;
		if ( (o = model.getAttribute("pet")) != null)  {
			pet = (Pet)o;
			visit.setPet(pet);

		} else {
			// ERROR - Owner not found!
			bindingResult.rejectValue("pet", "null", "Pet for visit not found.");
		}

		// If Visit updates had errors, return to Update Form.  
		// Otherwise, save and redirect to show Owner view which shows pets and their visits.  
		if (bindingResult.hasErrors()) {
			// Pet updates had errors.  Return to update form. 
			model.addAttribute("visit", visit);
			return "/pets/createOrUpdateVisit";
		} else {
			// Updates valid.  Save and display owner with updated pet info.
			visitService.save(visit);
			return "redirect:/owners/" + pet.getOwner().getId();
		}
	}

}  // end class VisitController
