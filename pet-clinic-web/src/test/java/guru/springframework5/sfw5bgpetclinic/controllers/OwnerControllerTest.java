package guru.springframework5.sfw5bgpetclinic.controllers;

import java.util.HashSet;

import org.hibernate.dialect.identity.Oracle12cGetGeneratedKeysDelegate;
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
import guru.springframework5.sfw5bgpetclinic.services.OwnerService;

//This is a Unit Test - - Run without Spring Context or DB.   
//@SpringBootTest is used for integration test (include DB, Spring Context, @Autowired)
//Use Mockito MVC to do "mock" injection of OwnerService into OwnerController
//  AND to provide fake data (absent DB) for OwnerService to return,
//  AND provide "mock" dispatcher servlet.  
@ExtendWith(MockitoExtension.class)  // Init Mockito. [JUnit 4 used Runners; 5 uses extensions]
class OwnerControllerTest {

	// -------------------------------------
	// Test Data 
	// -------------------------------------
	private HashSet<Owner> expectedOwners = new HashSet<Owner>();

	// -------------------------------------
	// Mocked classes  
	// -------------------------------------
	@Mock		// Tells Mockito to Mock this class (to be injected into Controller). 
	            // Mockito can tell it what data to return (fake DB call), monitor its calls, etc.)
	private OwnerService ownerService; 

	@InjectMocks  	// Tell Mockito to inject this controller with necessary @Mock classes.
	private OwnerController ownerController;  // Class to be tested! 

	private MockMvc mockMvc;  // Mocks HTTP requests and Dispatcher Servlet that invokes correct Controller.
	
	// -------------------------------------
	// Before / After Methods
	// -------------------------------------

	@BeforeEach
	void setUp() throws Exception {
		// Create data for Mockito to return for "when" - During normal runtime, Bootstrap fills DB.  No DB in this Unit Test.   
		Owner owner = Owner.builder().firstName("Bob").lastName("Smith").build();
		owner.setId(1L); 
		expectedOwners.add(owner);
		owner = Owner.builder().firstName("Gary").lastName("Owens").build();
		owner.setId(2L); 
		expectedOwners.add(owner);
	
		// Set up argumentCaptor - In test method, will call argumentCaptor.capture() below in test method a/w 
		// call to addAttribute on the model.  Therefore, will capture the Set being added.   
		//argumentCaptor = org.mockito.ArgumentCaptor.forClass(Set.class);

		// Set up MockMvc with the controller being tested. 
		// standaloneSetup(...) does NOT start Spring Context (since Unit Test).  
		//                      Give it Controller DispatcherServlet would be calling in to. 
		// webAppContextSetup(...) DOES start Spring Context - If use, then not Unit test. 
		mockMvc = MockMvcBuilders.standaloneSetup(ownerController).build();
	}


	// -------------------------------------
	// Test findOwners
	// -------------------------------------

	@Test
	void testInitFindForm() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/owners/find"))
			   .andExpect(MockMvcResultMatchers.status().isOk())
			   .andExpect(MockMvcResultMatchers.view().name("owners/findOwners"))
			   .andExpect(MockMvcResultMatchers.model().attributeExists("owner"));

		//org.motckito.mockitoverify zero interactions with owner service "mock."  SHould not be 
		// interacting with OwnerServicce until method in class is implemented. 
		org.mockito.Mockito.verify(ownerService, org.mockito.Mockito.times(0)).findAll();
	}

	@Test
	void testProcessFindFormReturnMany() throws Exception {
		// Spring Data JPA allows OwnerService to do custom find using repository.
		// Format "findAllBy" + property name + "Like" so search anything with that string
		org.mockito.Mockito.when(ownerService.findAllByLastNameLike(org.mockito.Mockito.anyString()))
		                                     .thenReturn(expectedOwners);
		
		mockMvc.perform(MockMvcRequestBuilders.get("/owners"))
			   .andExpect(MockMvcResultMatchers.status().isOk())
			   .andExpect(MockMvcResultMatchers.view().name("owners/listOwners"))
			   .andExpect(MockMvcResultMatchers.model().attribute("owners", org.hamcrest.Matchers.hasSize(2)));

		//org.motckito.mockitoverify zero interactions with owner service "mock."  SHould not be 
		// interacting with OwnerServicce until method in class is implemented. 
		org.mockito.Mockito.verify(ownerService, org.mockito.Mockito.times(1)).findAllByLastNameLike(org.mockito.Mockito.anyString());
	}

	@Test
	void testProcessFindFormReturnOne() throws Exception {
		// Make expectedOwners a set of one. 
		expectedOwners.clear();
		Owner owner = Owner.builder().firstName("Bob").lastName("Smith").build();
		owner.setId(1L); 
		expectedOwners.add(owner);

		// Spring Data JPA allows OwnerService to do custom find.
		// Format "findAllBy" + propert name + "Like" so search anything with that string
		org.mockito.Mockito.when(ownerService.findAllByLastNameLike(org.mockito.Mockito.anyString())).thenReturn(expectedOwners);

		mockMvc.perform(MockMvcRequestBuilders.get("/owners"))
			   .andExpect(MockMvcResultMatchers.status().is3xxRedirection())  // To show details on single owner found 
			   .andExpect(MockMvcResultMatchers.view().name("redirect:/owners/1"));  // Check view name returned lists all ingredients
		
		// then - verify controller calls service only once 
		org.mockito.Mockito.verify(ownerService, org.mockito.Mockito.times(1)).findAllByLastNameLike(org.mockito.Mockito.anyString());
	}

	// -------------------------------------
	// Test showOwner
	// -------------------------------------

	@Test
	void testShowOwner() throws Exception {
		Owner expectedOwner = Owner.builder().firstName("Bob").lastName("Smith").build();
		expectedOwner.setId(1L);
		org.mockito.Mockito.when(ownerService.findById(1L)).thenReturn(expectedOwner);

		// Verify when "perform" call, status ok, html page is showOwnerDetails, and 
		// Owner returned is the expectedOwner that has ID = 1L (retrieved by Service and placed on Model)
		mockMvc.perform(MockMvcRequestBuilders.get("/owners/1"))
			   .andExpect(MockMvcResultMatchers.status().isOk())
			   .andExpect(MockMvcResultMatchers.view().name("owners/ownerDetails"))
			   .andExpect(MockMvcResultMatchers.model().attribute("owner", org.hamcrest.Matchers.hasProperty("id", org.hamcrest.Matchers.equalTo(1L))));
			
		//org.motckito.mockitoverify zero interactions with owner service "mock."  SHould not be 
		// interacting with OwnerServicce until method in class is implemented. 
		org.mockito.Mockito.verify(ownerService, org.mockito.Mockito.times(1)).findById(1L);
	}

	// -------------------------------------
	// Test Create or Update Owner 
	// -------------------------------------

	@Test
	void initCreateOwnerForm() throws Exception { 
		// "perform" mock GET for mapping /owners/new.  Display page with empty Owner.
		// Verify status "ok", create or update view returned, and an (empty) owner is in Model.
		mockMvc.perform(MockMvcRequestBuilders.get("/owners/new"))    // request mapping to GET new
			   .andExpect(MockMvcResultMatchers.status().isOk())
			   .andExpect(MockMvcResultMatchers.view().name("owners/createOrUpdateOwner"))
			   .andExpect(MockMvcResultMatchers.model().attributeExists("owner"));
		// Verify owner service not invoked.  Not saving or retrieving from DB. 
		org.mockito.Mockito.verify(ownerService, org.mockito.Mockito.times(0)).findById(1L);
	}
	
	@Test
	void processCreateOwnerForm() throws Exception { 
		// Create dummy Owner to return when service save is called. 
		Owner expectedOwner = Owner.builder().build();
		expectedOwner.setId(1L);
		org.mockito.Mockito.when (ownerService.save(org.mockito.ArgumentMatchers.any()))
		                                               .thenReturn(expectedOwner);
		// "perform" mock POST for mapping /owners/new.
		// Verify have owner (returned by service save) and redirects to show detail of new saved owner. 
		mockMvc.perform(MockMvcRequestBuilders.post("/owners/new"))    // request mapping to POST new
			   .andExpect(MockMvcResultMatchers.status().is3xxRedirection())  // Show new owner saved 
			   .andExpect(MockMvcResultMatchers.view().name("redirect:/owners/1"))  // Back to controller to show
			   .andExpect(MockMvcResultMatchers.model().attributeExists("owner"));
		
		// Verify /owners/new mapping results in Controller invoking save once.  
		org.mockito.Mockito.verify(ownerService, org.mockito.Mockito.times(1)).save(org.mockito.ArgumentMatchers.any());
	}
	
	@Test
	void initUpdateOwnerForm() throws Exception { 
		// Create dummy Owner to return when service findById is called to get Owner to update. 
		Owner expectedOwner = Owner.builder().build();
		expectedOwner.setId(1L);
		org.mockito.Mockito.when (ownerService.findById(1L)).thenReturn(expectedOwner);
		
		// "perform" mock GET for mapping /owners/update. Will retrieve Owner with ID and display form.
		// Verify status "ok", create or update view returned, and owner to update is in Model.
		mockMvc.perform(MockMvcRequestBuilders.get("/owners/1/update"))    // request mapping to GET update
			   .andExpect(MockMvcResultMatchers.status().isOk())
			   .andExpect(MockMvcResultMatchers.view().name("owners/createOrUpdateOwner"))
			   .andExpect(MockMvcResultMatchers.model().attributeExists("owner"));

		// Verify owner service find by ID invoked to get the Owner to be modified from the DB. 
		org.mockito.Mockito.verify(ownerService, org.mockito.Mockito.times(1)).findById(1L);
	}
	
	@Test
	void processUpdateOwnerForm() throws Exception { 
		// Create dummy Owner to return when service save is called. 
		Owner expectedOwner = Owner.builder().build();
		expectedOwner.setId(1L);
		org.mockito.Mockito.when (ownerService.save(org.mockito.ArgumentMatchers.any()))
		                                               .thenReturn(expectedOwner);
		// "perform" mock POST for mapping /owners/update.  Save and redirect to show updaed Owner. 
		// Verify have owner (returned by service save) and redirects to show detail of owner. 
		mockMvc.perform(MockMvcRequestBuilders.post("/owners/1/update"))    // request mapping to POST update
			   .andExpect(MockMvcResultMatchers.status().is3xxRedirection())  // Show updated owner saved 
			   .andExpect(MockMvcResultMatchers.view().name("redirect:/owners/1"));  // Back to controller to show
		
		// Verify /owners/new mapping results in Controller invoking save once.  
		org.mockito.Mockito.verify(ownerService, org.mockito.Mockito.times(1)).save(org.mockito.ArgumentMatchers.any());
	}

}  // end class 
