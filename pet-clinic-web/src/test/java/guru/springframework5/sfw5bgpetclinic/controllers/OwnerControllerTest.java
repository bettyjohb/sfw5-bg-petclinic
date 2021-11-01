package guru.springframework5.sfw5bgpetclinic.controllers;

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
	// Test listOwners (get("/owners") then get("get/owners/index")
	// -------------------------------------

	@Test
	// Mock MVC will allow you to test controllers without bringing up Spring Context. 
	// Action:  This test invokes a mock HTTP get request ("/owners") so MockMvc invokes 
	//          OwnerController's listOwners method. 
	// Verification:  HTTP status is OK (200); view name returned is "owners.index"; 
	//                model's "owners" attribute Controller got from OwnerService call is a 
	//                Set<Owner> of size 2. 
	void testListOwners() throws Exception {
		// Before each test, setUp() creates mock data and sets up MockMvc with controller tested. 
		// Indicate what data Mockito is to return (absent a DB) "when" findAll() invoked.
		// findAll() does not use orElse so just return direct reference (not Optional.of(reference)).
		org.mockito.Mockito.when(ownerService.findAll()).thenReturn(expectedOwners);
		
		// Test findOwners - Use MockMvc to fake an HTTP get request "/owners" by the browser.
		// [RequestMapping of Controller class is "/owners" then add RequestMapping over method ("", "/", "/index", "index.html")]
		// We DO NOT prepopulate Model.  OwnerController method will take the "owners" returned from 
		// ownerService.findAll() and addAttibute to model.  In this case, Set of two Owners.
		// Just "mock" what injected service returns since it is not linked to DB.  
		mockMvc.perform(MockMvcRequestBuilders.get("/owners"))					// Fake HTTP get "/owners"
		       .andExpect(MockMvcResultMatchers.status().isOk())       			// Check http status 200
		       .andExpect(MockMvcResultMatchers.view().name("owners/index"))	// Check view name returned
		       .andExpect(MockMvcResultMatchers.model().attribute("owners", org.hamcrest.Matchers.hasSize(2)));  // Data for key "owners" in Model
	}

	@Test
	// Same as above test.  Just get invokes with different path /owners/index
	void testListOwnersPathOwnersIndex() throws Exception {
		org.mockito.Mockito.when(ownerService.findAll()).thenReturn(expectedOwners);
		
		mockMvc.perform(MockMvcRequestBuilders.get("/owners/index"))			// Fake HTTP get "/owners/index"
		       .andExpect(MockMvcResultMatchers.status().isOk())       			// Check http status 200
		       .andExpect(MockMvcResultMatchers.view().name("owners/index"))	// Check view name returned
		       .andExpect(MockMvcResultMatchers.model().attribute("owners", org.hamcrest.Matchers.hasSize(2)));  // Data for key "owners" in Model
	}

	// -------------------------------------
	// Test findOwners
	// -------------------------------------

	@Test
	void testFindOwners() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/owners/find"))
			   .andExpect(MockMvcResultMatchers.status().isOk())
			   .andExpect(MockMvcResultMatchers.view().name("notImplemented"));

		//org.motckito.mockitoverify zero interactions with owner service "mock."  SHould not be 
		// interacting with OwnerServicce until method in class is implemented. 
		org.mockito.Mockito.verify(ownerService, org.mockito.Mockito.times(0)).findAll();
	}
	
	@Test
	void testFail() throws Exception {
		assert(false);
	}
	

}  // end class 
