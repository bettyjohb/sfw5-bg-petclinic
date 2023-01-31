package guru.springframework5.sfw5bgpetclinic.controllers;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.HashSet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import guru.springframework5.sfw5bgpetclinic.model.Owner;
import guru.springframework5.sfw5bgpetclinic.model.Pet;
import guru.springframework5.sfw5bgpetclinic.model.PetType;
import guru.springframework5.sfw5bgpetclinic.services.OwnerService;
import guru.springframework5.sfw5bgpetclinic.services.PetService;
import guru.springframework5.sfw5bgpetclinic.services.PetTypeService;

//This is a Unit Test - - Run without Spring Context or DB.   
//@SpringBootTest is used for integration test (include DB, Spring Context, @Autowired)
//Use Mockito MVC to do "mock" injection of PetService into PetController
//AND to provide fake data (absent DB) for PetService to return,
//AND provide "mock" dispatcher servlet.  
@ExtendWith(MockitoExtension.class)  // Init Mockito. [JUnit 4 used Runners; 5 uses extensions]
class PetControllerTest {

	// -------------------------------------
	// Test Data 
	// -------------------------------------
	private HashSet<PetType> expectedTypes = new HashSet<PetType>();
	private Owner expectedOwner; 
	
	// -------------------------------------
	// Mocked classes  
	// -------------------------------------
	@Mock		// Tells Mockito to Mock this class (to be injected into Controller). 
	            // Mockito can tell it what data to return (fake DB call), monitor its calls, etc.)
	private PetService petService; 
	
	// Owner and PetType services are used by @ModelAttribute methods of PetController and possibly other.
	@Mock  // Tell Mockito to inject this into Controller 
	private OwnerService ownerService; 
	@Mock
	private PetTypeService petTypeService; 

	@InjectMocks  	// Tell Mockito to inject this controller with necessary @Mock classes above.
	private PetController petController;  // CLASS TO BE TESTED! 

	private MockMvc mockMvc;  // Mocks HTTP requests and Dispatcher Servlet that invokes correct Controller.	

	// -------------------------------------
	// Before / After Methods
	// -------------------------------------

	@BeforeEach
	void setUp() throws Exception {
		
		// -------------------------------------
		// Reused data across tests.
		// -------------------------------------

		// Create mock Owner to be returned by OwnerService.  
		// Used by @ModelAttribute of Controller; maybe other. 
		expectedOwner = Owner.builder().firstName("Bob").lastName("Smith").build();
		expectedOwner.setId(1L);
		
		// Create mock PetTypes to be returned by PetTypeService:findAll called by 
		// @ModelAttribute method of PetController being tested.
		expectedTypes = new HashSet<>();
		PetType type = PetType.builder().name("dog").build();
		type.setId(1L);
		expectedTypes.add(type);
		type = PetType.builder().name("cat").build();
		type.setId(2L);
		expectedTypes.add(type);
		
		// -------------------------------------
		// Set up MockMvc with Controller being tested.
		// -------------------------------------
		// standaloneSetup(...) does NOT start Spring Context (since Unit Test).  
		//                      Give it Controller DispatcherServlet would be calling in to. 
		// webAppContextSetup(...) DOES start Spring Context - If use, then not Unit test. 
		// -------------------------------------
		mockMvc = MockMvcBuilders.standaloneSetup(petController).build();
	}

	// -------------------------------------
	// Test Create or Update Owner 
	// -------------------------------------

	@Test
	// Test controller method to create empty pet for create/update form to enter new pet data. 
	void initCreatePetForm() throws Exception {
		// Set up return values for @ModelAttribute of PetController that get invoked. 
		// Don't do Pet because controller method will create new "empty" pet with builder since create Pet. 
		org.mockito.Mockito.when(ownerService.findById(org.mockito.Mockito.anyLong())).thenReturn(expectedOwner);
		org.mockito.Mockito.when(petTypeService.findAll()).thenReturn(expectedTypes);
		
		// "perform" mock GET for mapping /owners/{ownerId}/new.  Display page with empty Pet.
		// Verify status "ok", create or update view returned, and an (empty) Pet is in Model.
		mockMvc.perform(MockMvcRequestBuilders.get("/owners/1/pets/new"))    // request mapping to GET new
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.view().name("pets/createOrUpdatePet"))
			.andExpect(MockMvcResultMatchers.model().attributeExists("owner"))
			.andExpect(MockMvcResultMatchers.model().attributeExists("types"))  
			.andExpect(MockMvcResultMatchers.model().attributeExists("pet"));

		// Verify pet service not invoked.  Not saving or retrieving pet from DB.
		org.mockito.Mockito.verify(petService, org.mockito.Mockito.times(0)).findById(1L);	
	}

	@Test
	// Test Controller method to process submit of a create/update pet form for new pet. 
	// After save, show results in show owner form which includes all pets owned. 
	void processCreatePetForm() throws Exception { 
		// Set up return values for @ModelAttribute of PetController that get invoked. 
		// Don't do Pet because controller method will create new "empty" pet with builder since create Pet. 
		org.mockito.Mockito.when(ownerService.findById(1L)).thenReturn(expectedOwner);
		org.mockito.Mockito.when(petTypeService.findAll()).thenReturn(expectedTypes);

		// Create dummy Pet to return when service save is called.  That pet is added to model by controller. 
		Pet expectedPet = Pet.builder().build();
		expectedPet.setId(1L);
		org.mockito.Mockito.when (petService.save(org.mockito.ArgumentMatchers.any()))
		                                               .thenReturn(expectedPet);
		// "perform" mock POST for mapping /owners/new.
		// Verify redirects to show detail of Owner of saved Pet (will look up, no model data needed). 
		mockMvc.perform(MockMvcRequestBuilders.post("/owners/1/pets/new"))    // request mapping to POST new
			   .andExpect(MockMvcResultMatchers.status().is3xxRedirection())  // Show owner with new Pet saved 
			   .andExpect(MockMvcResultMatchers.view().name("redirect:/owners/1"));  // Back to controller to show
		
		// Verify /owners/new mapping results in Controller invoking save once.  
		org.mockito.Mockito.verify(petService, org.mockito.Mockito.times(1)).save(org.mockito.ArgumentMatchers.any());
	}

	@Test
	// Test method to set up create/update form for an update of existing pet. 
	void initUpdatePetForm() throws Exception { 
		// Set up return values for @ModelAttribute of PetController that get invoked. 
		org.mockito.Mockito.when(ownerService.findById(1L)).thenReturn(expectedOwner);
		org.mockito.Mockito.when(petTypeService.findAll()).thenReturn(expectedTypes);

		// Create dummy Pet to return when PetService findById is called to get Pet to update. 
		Pet expectedPet = Pet.builder().build();
		expectedPet.setId(1L);
		org.mockito.Mockito.when (petService.findById(1L)).thenReturn(expectedPet);
		
		// "perform" mock GET for mapping /owners/{1}/pets/{1}/update. Will retrieve Pet with ID and display form.
		// Verify status "ok", create or update view returned, and pet to update is in Model.
		mockMvc.perform(MockMvcRequestBuilders.get("/owners/1/pets/1/update"))    // request mapping to GET update
			   .andExpect(MockMvcResultMatchers.status().isOk())
			   .andExpect(MockMvcResultMatchers.view().name("/pets/createOrUpdatePet"))
			   .andExpect(MockMvcResultMatchers.model().attributeExists("owner"))
     		   .andExpect(MockMvcResultMatchers.model().attributeExists("pet"))
			   .andExpect(MockMvcResultMatchers.model().attributeExists("types"));

		// Verify owner service find by ID invoked to get the Pet to be modified from the DB. 
		org.mockito.Mockito.verify(petService, org.mockito.Mockito.times(1)).findById(1L);
	}

	@Test
	// Test method to test submit from create/update form being used to update exiting Pet. 
	void processUpdatePetForm() throws Exception { 
		// Set up return values for @ModelAttribute of PetController that get invoked. 
		org.mockito.Mockito.when(ownerService.findById(1L)).thenReturn(expectedOwner);
		org.mockito.Mockito.when(petTypeService.findAll()).thenReturn(expectedTypes);

		// Create dummy Pet to return when service save is called. 
		Pet expectedPet = Pet.builder().build();
		expectedPet.setId(1L);
		org.mockito.Mockito.when (petService.save(org.mockito.ArgumentMatchers.any()))
															.thenReturn(expectedPet);
		// "perform" mock POST for mapping /owners/1/pets/1/update.  
		// Save and redirect to show Owner view which will list the updated Pet info. 
		// Don't need model data because view retrieves from DB.
		mockMvc.perform(MockMvcRequestBuilders.post("/owners/1/pets/1/update"))    // request mapping to POST update
			   .andExpect(MockMvcResultMatchers.status().is3xxRedirection())  // Show updated owner saved 
			   .andExpect(MockMvcResultMatchers.view().name("redirect:/owners/1")); // Back to controller to show
		
		// Verify /owners/1/pets/1/update mapping results in Controller invoking save once.  
		org.mockito.Mockito.verify(petService, org.mockito.Mockito.times(1)).save(org.mockito.ArgumentMatchers.any());
	}

}  // end class PetControllerTest