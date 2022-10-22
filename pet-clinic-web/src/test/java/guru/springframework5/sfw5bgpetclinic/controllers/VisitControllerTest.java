package guru.springframework5.sfw5bgpetclinic.controllers;

import java.time.LocalDate;
import java.util.HashSet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import guru.springframework5.sfw5bgpetclinic.model.Visit;
import guru.springframework5.sfw5bgpetclinic.services.OwnerService;
import guru.springframework5.sfw5bgpetclinic.services.PetService;
import guru.springframework5.sfw5bgpetclinic.services.VisitService;
import guru.springframework5.sfw5bgpetclinic.model.Owner;
import guru.springframework5.sfw5bgpetclinic.model.Pet;
import guru.springframework5.sfw5bgpetclinic.model.PetType;

import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

//This is a Unit Test - - Run without Spring Context or DB.   
//@SpringBootTest is used for integration test (include DB, Spring Context, @Autowired)
//Use Mockito MVC to do "mock" injection of VisitService and PetService into VisitController
//AND to provide fake data (absent DB) for VisitService and PetService to return,
//AND provide "mock" dispatcher servlet.  
@ExtendWith(MockitoExtension.class)  // Init Mockito. [JUnit 4 used Runners; 5 uses extensions]
class VisitControllerTest {

	// -------------------------------------
	// Test Data 
	// -------------------------------------
	private static Long VISIT_ID = 1L,
						PET_ID = 2L,
						OWNER_ID = 3L;

	private Visit expectedVisit; // Built in setup()
	private LocalDate dtVisit = LocalDate.of(1999, 7, 4);
			
	// -------------------------------------
	// Mocked classes  
	// -------------------------------------
	@Mock		// Tells Mockito to Mock this class (to be injected into Controller). 
	            // Mockito can tell it what data to return (fake DB call), monitor its calls, etc.)
	private VisitService visitService;
	@Mock		
	private PetService petService;
	
	@InjectMocks  	// Tell Mockito to inject this controller with necessary @Mock classes.
	private VisitController visitController;  // Class to be tested! 

	private MockMvc mockMvc;  // Mocks HTTP requests and Dispatcher Servlet that invokes correct Controller.
		
	// -------------------------------------
	// Before / After Methods
	// -------------------------------------

	@BeforeEach
	void setUp() throws Exception {
		// Create data for Mockito to return for "when" - During normal runtime, Bootstrap fills DB.  No DB in this Unit Test.
		// expectedPet is also used by 
		Pet expectedPet = Pet.builder().name("Sprinkler").petType(new PetType("Dog")).birthDate(LocalDate.of(1999, 12, 12)).build();
		expectedPet.setId(PET_ID);
		
		Owner expectedOwner = Owner.builder().firstName("Bob").lastName("Smith").build();
		expectedOwner.setId(OWNER_ID);
		expectedOwner.add(expectedPet);
		
		// Builder will take all the values between builder() and build() and call parameterized constructor once. 
		expectedVisit = Visit.builder().date(dtVisit).description("Test description.").pet(expectedPet).build();
		expectedVisit.setId(VISIT_ID);

		System.out.println("\n\n\nIn Setup");
		System.out.println("Pets number of currnet visits " + expectedVisit.getPet().getVisits().size());
		System.out.println("Visit date " + expectedVisit.getDate());
		System.out.println("Visit descrxiption " + expectedVisit.getDescription());  
		System.out.println("Visit Pet ID " + expectedVisit.getPet().getId());
		System.out.println("Visit Pet Type " + expectedVisit.getPet().getPetType().getName());
		System.out.println("visit Pet Owner ID " + expectedVisit.getPet().getOwner().getId());		
		// Set up argumentCaptor - In test method, will call argumentCaptor.capture() below in test method a/w 
		// call to addAttribute on the model.  Therefore, will capture the Set being added.   
		//argumentCaptor = org.mockito.ArgumentCaptor.forClass(Set.class);

		// Set up MockMvc with the controller being tested. 
		// standaloneSetup(...) does NOT start Spring Context (since Unit Test).  
		//                      Give it Controller DispatcherServlet would be calling in to. 
		// webAppContextSetup(...) DOES start Spring Context - If use, then not Unit test. 
		mockMvc = MockMvcBuilders.standaloneSetup(visitController).build();
		
		// VisitController has a @ModelAttribute method "findPed" that invokes PetService.findById  
		// so Spring can add the pet in question to the Model each time the controller is invoked  
		// since the pet is always needed.  Specify that the PetService should return expectedPet
		// (since not attached to DB in unit test, can't get it there). 
		org.mockito.Mockito.when (petService.findById(PET_ID))
                           .thenReturn(expectedPet);   // This is the saved visit
	}
		
	// -------------------------------------
	// Test Create Owner
	// -------------------------------------

	@Test
	// Test displaying create/update form to enter new visit data.  Displays empty Visit.
	public void testInitCreateVisitForm() throws Exception {
		System.out.println("\n\nIn VisitControllerTest::testInitCreateVisitForm()!");
		// "perform" mock GET for mapping /visits/new.  Display page with empty Visit.
		// Verify status "ok", create or update view returned, and an (empty) visit is in Model.
		// Full path includes owner ID and pet ID.  Visit is new, so no id (just /visits/new).
		mockMvc.perform(MockMvcRequestBuilders.get("/owners/" + OWNER_ID + "/pets/" + PET_ID + "/visits/new"))    // request mapping to GET new
			   .andExpect(MockMvcResultMatchers.status().isOk())
			   .andExpect(MockMvcResultMatchers.view().name("pets/createOrUpdateVisit"))
			   .andExpect(MockMvcResultMatchers.model().attributeExists("visit"));

		// Verify visit service not invoked.  Not saving or retrieving from DB. 
		org.mockito.Mockito.verify(visitService, org.mockito.Mockito.times(0)).findById(VISIT_ID);
		
		System.out.println("Leaving VisitControllerTest::testInitCreateVisitForm()!");
	}  // end testInitCreateVisitForm 

	@Test
	// Test controller method to process a create/update form submit to save new visit data.  
	void testProcessCreateVisitForm() throws Exception { 
		System.out.println("\n\nIn VisitControllerTest::testProcessCreateVisitForm()!");
		// Tell Mockito to return dummy Visit when VisitService save is called.  This is not called directly 
		// by test, but by the VisitController method being tested.  The Controller method adds the Visit to the
		// Model.  We don't have to. 
		org.mockito.Mockito.when (visitService.save(org.mockito.ArgumentMatchers.any()))
		                                      .thenReturn(expectedVisit);   // This is the saved visit
		// "perform" mock POST for mapping /visits/new.
		// Verify have visit (returned by VisitService save) and redirects to show detail of new saved Visit.
		mockMvc.perform(MockMvcRequestBuilders.post("/owners/" + OWNER_ID + "/pets/" + PET_ID + "/visits/new"))  // RequestMapping to POST new
			   .andExpect(MockMvcResultMatchers.status().is3xxRedirection())  // Show new Visit saved 
			   .andExpect(MockMvcResultMatchers.view().name("redirect:/owners/" + OWNER_ID))  // Expected Visit->Pet->Owner->Id=1
			   .andExpect(MockMvcResultMatchers.model().attributeExists("visit"));
		
		// Verify /owners/new mapping results in Controller invoking save once.  
		org.mockito.Mockito.verify(visitService, org.mockito.Mockito.times(1)).save(org.mockito.ArgumentMatchers.any());
		
		System.out.println("Leaving VisitControllerTest::testProcessCreateVisitForm()!");
	}
		
	// --------------------------------------------------------------
	// Test Update Visit methods.
	// --------------------------------------------------------------

	@Test
	// Test display create/update form to update existing Visit data.  Displays Visit to be modified. 
	void testInitUpdateVisitForm() throws Exception {
		System.out.println("\n\nIn VisitControllerTest::testInitUpdateVisitForm()!");

		// Tell Mockito to return dummy Visit when VisitService save is called.  This is not called directly 
		// by test, but by the VisitController method being tested.  The Controller method adds the Visit to the
		// Model.  We don't have to. 
		org.mockito.Mockito.when (visitService.findById(VISIT_ID)).thenReturn(expectedVisit);
		
		// "perform" mock GET for mapping /visits/update. Will retrieve Visit with ID and display form.
		// Verify status "ok", create or update view returned, and Visit to update is in Model.
		mockMvc.perform(MockMvcRequestBuilders.get("/owners/" + OWNER_ID + "/pets/" + PET_ID + "/visits/" + VISIT_ID + "/update"))    // RequestMapping to GET update
			   .andExpect(MockMvcResultMatchers.status().isOk())
			   .andExpect(MockMvcResultMatchers.view().name("/pets/createOrUpdateVisit"))  // form to updated data
			   .andExpect(MockMvcResultMatchers.model().attributeExists("visit"));

		// Verify Bisit Service find by ID invoked only once to get the Visit to be modified from the DB. 
		org.mockito.Mockito.verify(visitService, org.mockito.Mockito.times(1)).findById(VISIT_ID);

		System.out.println("Leaving VisitControllerTest::testInitUpdateVisitForm()!");
	}

	@Test
	// Test co9ntroller method to process create/update form submit to save updated Visit data.  
	void testProcessUpdateVisitForm() throws Exception { 
		System.out.println("\n\nIn VisitControllerTest::testProcessUpdateVisitForm()!");

		// Tell Mockito to return dummy Visit when VisitService save is called.  This is not called directly 
		// by test, but by the VisitController method being tested.  The Controller method adds the Visit to the
		// Model.  We don't have to. 
		org.mockito.Mockito.when (visitService.save(org.mockito.ArgumentMatchers.any()))
		                                               .thenReturn(expectedVisit);
		
		// "perform" mock POST for mapping /visits/update.  Save and redirect to show updated Visit. 
		// Verify have visit (returned by service save) and redirects to show detail of owner.
		mockMvc.perform(MockMvcRequestBuilders.post("/owners/" + OWNER_ID + "/pets/" + PET_ID + "/visits/" + VISIT_ID + "/update"))    // request mapping to POST update
			   .andExpect(MockMvcResultMatchers.status().is3xxRedirection())  // Show updated owner saved 
			   .andExpect(MockMvcResultMatchers.view().name("redirect:/owners/" + OWNER_ID)) // Back to controller to show
			   .andExpect(MockMvcResultMatchers.model().attributeExists("visit"));
		
		// Verify /owners/new mapping results in Controller invoking save once.  
		org.mockito.Mockito.verify(visitService, org.mockito.Mockito.times(1)).save(org.mockito.ArgumentMatchers.any());
		
		System.out.println("Leaving VisitControllerTest::testProcessUpdateVisitForm()!");

	}

}  // end class VisitCOntrollerTest 