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
import guru.springframework5.sfw5bgpetclinic.model.Pet;
import guru.springframework5.sfw5bgpetclinic.model.PetType;
import guru.springframework5.sfw5bgpetclinic.services.OwnerService;
import guru.springframework5.sfw5bgpetclinic.services.PetService;
import guru.springframework5.sfw5bgpetclinic.services.PetTypeService;

import java.util.Collection;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
	@InitBinder ({"owner", "pet"})
	public void setAllowedFields (WebDataBinder webDataBinder) {
		// Don't allow Spring to place id in the object (could be typed).  Needs to be generated on Save.   
		webDataBinder.setDisallowedFields("id");
	}

	// --------------------------------------------------------------
	// Create Pet methods.
	// --------------------------------------------------------------
	
	// Controller method to display create/update form to enter new pet data.  Uses empty Pet.
	// Omit @PathVariable String ownerId from method params due to @ModelAttribute("owner") above.
	@GetMapping("/pets/new")    // owners/ownerId at class level
	public String initCreatePetForm(Model model) throws Exception {
		
		// Create an empty Pet and add it to the Model for the create view. 
		// The Owner and Set of PetTypes is in the Model from @ModelAttribute methods above.
		Pet pet = Pet.builder().build();
		((Owner)model.getAttribute("owner")).add(pet);  // Adds owner to pet and pet to owner (bidirectional) 
		                                                // Not saved to DB yet, so will reset on submit. 
		model.addAttribute("pet", pet);
		return "pets/createOrUpdatePet";
	}
	
	// Controller method to process create/update form submit to save new data.  Uses entered Pet data.  
	// Omit @PathVariable String ownerId from method params due to @ModelAttribute("owner") above.
	@PostMapping("/pets/new")    // owners/ownerId at class level
	public String processCreatePetForm(@Validated Pet pet, BindingResult bindingResult, Model model) throws Exception {
		// @ModelAttribute retrieves Owner from DB and places in Model since needed each time.   
		Object o;
		Owner petOwner;
		if ( (o = model.getAttribute("owner")) != null)  {
			petOwner = (Owner)o;
			// Verify not a duplicate pet name for owner. SHOULD MAKE VALIDATOR FOR THIS!
			if (StringUtils.hasLength(pet.getName()) && pet.isNew() && (petOwner.getPet(pet.getName(), true) != null) )
				bindingResult.rejectValue("name", "duplicate", "already exists");
			else
				petOwner.add(pet);  // Adds owner to pet and pet to owner (bidirectional)
		} else {
			bindingResult.rejectValue("owner", "null", "Pet owner not found.");
		}
		
		// If Pet updates had errors, return to Update Form.  
		// Otherwise, save and redirect to show Owner view which shows pets.  
		if (bindingResult.hasErrors()) {
			// Pet updates had errors.  Return to update form. 
			model.addAttribute("pet", pet);
			return "pets/createOrUpdatePet";
		} else {
			// Updates valid.  Save and display owner with updated pet info. 
			petService.save(pet);
			return "redirect:/owners/" + pet.getOwner().getId();
		}
	}

	// --------------------------------------------------------------
	// Update Pet methods.
	// --------------------------------------------------------------

	// Controller method to display create/update form to update existing pet data.  Uses existing Pet.
	// Omit @PathVariable String ownerId from method params due to @ModelAttribute("owner") above.
	@GetMapping("/pets/{petId}/update")    // owners/ownerId at class level
	public String initUpdatePetForm(@PathVariable Long petId, Model model) throws Exception {
		
    	// Invoke PetService to retrieve Pet to be updated (noted by petId) - Place in Model for update view. 
    	// Therefore, Pet is all intact (including id and Owner reference) since from DB. 
		// In addition, the Owner and Set of PetTypes is in the Model from @ModelAttribute methods above.
		Pet pet = petService.findById(petId);
		model.addAttribute("pet", pet);
		return "/pets/createOrUpdatePet";
	}

	// Controller method to process create/update form submit to save updated Pet data.    
	// Omit @PathVariable String ownerId from method params due to @ModelAttribute("owner") above.
	@PostMapping("/pets/{petId}/update")    // owners/ownerId at class level
	public String processUpdatePetForm(@PathVariable Long petId, @Validated Pet pet, BindingResult bindingResult, Model model) throws Exception {
    	// InitBinder above prevents from prepopulating ID when passed from form.  Set it from param here. 
    	pet.setId(Long.valueOf(petId));

		if (pet.getOwner() == null) System.out.println ("Owner not here");
		else System.out.println("Owner is here");

		// NOTE: 
		// (1) @ModelAttribute (above) retrieves Owner from DB and places in Model since needed each time so Owner is available.
    	// (2) Owner attribute of Pet does not get forwarded from form (just fields).  Owner reference (id)
		// is stored in same Pet Table in DB (as foreign key) so when save Pet with null Owner, lose that link and 
		// that Pet is no longer linked back to its Owner by foreign key.  Therefore, have to set Owner here. 
		Object o;
		Owner petOwner = null;
		if ( (o = model.getAttribute("owner")) != null)  {
			pet.setOwner( (Owner)o );

			// In case pet name changed, verify name is not a dup for that owner (pet with same name, different id). 
			// SHOULD MAKE VALIDATOR FOR THIS!
  			if (StringUtils.hasLength(pet.getName()) && (pet.getOwner().getPet(pet.getName(), false) != null) ) 
				// A pet with name exists for owner.  Verify have same id for update (or would be making a new Pet).
				if (pet.getId().equals( pet.getOwner().getPet(pet.getName(),false).getId()) == false)
						bindingResult.rejectValue("name", "duplicate", "Name already exists for owner " + pet.getOwner().getFirstName() + " " + petOwner.getLastName() + ".");
		} else {
			// ERROR - Owner not found!
			bindingResult.rejectValue("owner", "null", "Pet owner not found.");
		}

		// If Pet updates had errors, return to Update Form.  
		// Otherwise, save and redirect to show Owner view which shows pets.  
		if (bindingResult.hasErrors()) {
			// Pet updates had errors.  Return to update form. 
			model.addAttribute("pet", pet);
			return "/pets/createOrUpdatePet";
		} else {
			// Updates valid.  Save and display owner with updated pet info.
			petService.save(pet);
			return "redirect:/owners/" + pet.getOwner().getId();
		}
	}

}  // end PetController
