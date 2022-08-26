//***************************************************************************
//Class:  OwnerController 
//
//Primary Controller for owner related pages (identified by @Controller / not extension).
//
//When an HTTP request arrives at server, will be handled by Spring provided 
//DispatcherServlet (front controller) that uses Handler Mapping to determine 
//which Controller to invoke (i.e. OwnerController, etc.).  
//
//@RequestMapping is used to map the HTTP Request Path to specific method on 
//the controller (for instance, can say all URL for that Web App ending with
///hello will go to ("/hello") method on controller which might just return a 
//view name "hello." Recall when jsp, ViewResolver added prefix/suffix to 
//tell where to find file and to add ".jsp."  When Thymeleaf, default looks in 
//templates and finds hello.html.
//*************************************************************************** 
package guru.springframework5.sfw5bgpetclinic.controllers;

import java.util.HashSet;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import guru.springframework5.sfw5bgpetclinic.model.Owner;
import guru.springframework5.sfw5bgpetclinic.services.OwnerService;

@Controller                   // Tell Spring this is a Spring MVC Controller to be instantiated.
@RequestMapping("/owners")    // Set up that this controller looks in owners 
public class OwnerController {

	// --------------------------------------------------------------
	// Attriutes
	// --------------------------------------------------------------
	private final OwnerService ownerService;   // Interface - So actual instance can be MAP, DB, etc. 
	
	// --------------------------------------------------------------
	// Constructor 
	// --------------------------------------------------------------

	// @Autowired not required in Spring 5. 
	// Component Scan sees @Controller and instantiates OwnerController bean; thereby using Constructor and 
	// injecting OwnerService (which is a @Service and therefore available for Spring Context to see). 
	// So far, only one OwnerService IMPL (for Map) therefore no need Profile to determine which to use. 
	public OwnerController (OwnerService ownerService) {
		this.ownerService = ownerService;
	}  // end constructor

	// --------------------------------------------------------------
	// Web Data Binder 
	// --------------------------------------------------------------
	@InitBinder
	public void setAllowedFields (WebDataBinder webDataBinder) {
		webDataBinder.setDisallowedFields("id");
	}
	
	// --------------------------------------------------------------
	// --------------------------------------------------------------
	// Find and show a multiple Owner list or a Single Owner Detail
	// --------------------------------------------------------------
	// --------------------------------------------------------------

    // -------------------------
    // Initialize "find" form (URL /owners/find):
    // -------------------------
	// Tell Spring this is a Controller method to handle additional HTTP requests.
	// Specifically, this method handles URL with "owners/find". 
    // [Get /owners from class level RequestMapping above, "/find" from here.]
	// If you run the app will see RequestMappingHandlerMapping list this as handled.
    @RequestMapping({"/find"})   // Controller level RequestMapping looks for these in "owners"
	public String initFindForm(Model model) {
    	model.addAttribute("owner", Owner.builder().build());  // find form expects an Owner. 
	  	return "owners/findOwners";  // Spring looks in templates since Thymeleaf, then owners to see find.html. 
	  	                             // If JSP, ViewResolver would provide pre/suffix to build full jsp file/path. 
	 }

    // -------------------------
    // Process "find" form:  URL /owners
    // -------------------------
    // Process requested Owner search and redirect to appropriate page to display results.
    // - If none found, go back to findOwners with not found message
    // - If 1 found, show details on that Owner. 
    // - If multiple, show list and let user select Owner to detail.
    // @param Owner owner - lastName attribute holds search string
    // @param Model model - return results as attribute of model 
    @GetMapping("")    // "/owners" a/w class + "" equals "/owners"
    public String processFindForm(Owner owner, BindingResult result, Model model) {
    	
    	// If no last name (full or partial) specified to find, search all owners
    	if (owner.getLastName() == null)
    		owner.setLastName("");
    	
    	// Find Owner(s) by last name.  Spring Data JPA allows "%" to search any chars before/after string.  
    	HashSet<Owner> results = this.ownerService.findAllByLastNameLike("%" + owner.getLastName() + "%");
    	if (results.isEmpty()) {  
    		// No owners found - Find Owners page displays "not found" 
    		result.rejectValue("lastName",  "notFound", "Not found");
    		return "owners/findOwners";  // returns page to display
    	} else if (results.size() == 1) {
    		// 1 Owner found.  Display details of that Owner with showOwner page 
    		Owner retrievedOwner = results.iterator().next();
    		model.addAttribute("owner", retrievedOwner);
    		return "redirect:/owners/" + retrievedOwner.getId(); // redirects so invokes controller again
    	} else {
    		// Multiple Owners found.  Display as list and user will select Owner to display.
    		model.addAttribute("owners", results);
    		return "owners/listOwners";       // returns page to display 
    	}
    }  // end procesFindForm 

    
    @GetMapping("/{ownerId}")  // /owners is a/w controller class; add /{ownerId}
    public String showOwner(@PathVariable String ownerId, Model model) {
    	Owner owner = ownerService.findById(Long.valueOf(ownerId));
    	model.addAttribute("owner", owner);
    	return "owners/ownerDetails";
    }

    
    // -----------------------------------------------------------------------------
    // Create Owner 
    // 	-----------------------------------------------------------------------------

    // initCreateOwnerForm 
    // Create an empty Owner for Model and return createOrUpdateOwner form. 
    // No call to service to get or save to DB. 
    // @param Model model - return empty Owner as attribute of model 
    @GetMapping("/new")    // Controller mapping "/owners" + "/new"
    public String initCreateOwnerForm(Model model) {
    	model.addAttribute("owner", Owner.builder().build());
    	return ("owners/createOrUpdateOwner");
    }

    // processCreateOwnerForm 
    // Invoke OwnerService to save new Owner and redirect to show details on that owner.  
    // @param Model model - return results as attribute of model 
    @PostMapping("/new")    // Controller mapping "/owners" + "/new"
	public String processCreateOwnerForm(Owner owner) throws Exception { 
		
    	Owner savedOwner = ownerService.save(owner);      // will have generaed id. 
    	return "redirect:/owners/" + savedOwner.getId();  // Redirect to mapping /owners/{id}
   	}

    // -----------------------------------------------------------------------------
    // Update Owner 
    // 	-----------------------------------------------------------------------------

    // initUpdateOwnerForm 
    // Retrieve Owner with given Id, add it to Model, and return createOrUpdateOwner form. 
    // @param Model model - return empty Owner as attribute of model 
    @GetMapping("/{ownerId}/update")    // Controller mapping "/owners" + "/new"
	public String initUpdateOwnerForm(@PathVariable String ownerId, Model model) throws Exception { 
    	// Invoke OwnerService to retrieve Owner to be updated (noted by owneId).
    	// Therefore, all intact (id, Set<Pet>, etc. since from DB. 
    	Owner owner = ownerService.findById(Long.valueOf(ownerId));
    	model.addAttribute("owner", owner);
    	return "owners/createOrUpdateOwner";
    	
   	}
	
    // processUpdateOwnerForm 
    // Invoke OwnerService to save updated Owner and redirect to show details on that owner. 
    // No model needed.  Will redirect and show owner page will retrieve its Owner. 
    @PostMapping("/{ownerId}/update")    // Controller mapping "/owners" + "/id/update"
	public String processUpdateOwnerForm(@PathVariable String ownerId, Owner owner) throws Exception { 
    	// InitBinder above prevents from prepopulating ID when passed from form.  Set it from param here. 
    	owner.setId(Long.valueOf(ownerId));
    
    	// NOTE:  The Set<Pet> attribute of Owner does not get forwarded from the form (just fields).  However, Pets are 
    	// stored in separate Table in DB with foreign key to link them back to the Owner, and Pets are created/mod/del 
    	// in their own Pet form.  Only Owner basics in Owners Table are updated here. When save with null Pet list, does not 
    	// affect Owner table.  
    	Owner updatedOwner = ownerService.save(owner);
    	return "redirect:/owners/" + updatedOwner.getId();
	}
}  // end class OwnerController
