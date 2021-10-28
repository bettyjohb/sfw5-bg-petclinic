package guru.springframework5.sfw5bgpetclinic.services.map;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import guru.springframework5.sfw5bgpetclinic.model.Owner;
import guru.springframework5.sfw5bgpetclinic.services.OwnerService;

class OwnerServiceMapImplTest {

	final Long ownerId = 1L;
	final String ownerFirstName = "Bob",
			     ownerLastName = "Smith",
			     ownerFirstName2 = "Bob2",
			     ownerLastName2 = "Smith2";
	
	// Class being tested.  
	//    Usually use Mockito for mock DI of PetService and PetTypeService 
	//    For this example, will instantiate manually in setup().
	private OwnerService ownerService;  
	
	// -----------------------------------------------------
	// Setup - Run before each @Test method.  
	// -----------------------------------------------------

	@BeforeEach
	void setUp() throws Exception {
		// Usually use Mockito for mock dependency injection, but will do manually for this example.  
		ownerService = new OwnerServiceMapImpl(
				           new PetServiceMapImpl(new PetTypeServiceMapImpl(), new VisitServiceMapImpl()));
		
		// Save 1 Owner object - "Bob Smith" with generated id (expect 1 since first Owner). 
		// Recall added Lombok @Builder to Owner constructor.  Pass just first and last name (rest of fields blank).
		// Our abstract service class generates an id (so I never pass one in via constructor). 
		ownerService.save(Owner.builder().firstName(ownerFirstName).lastName(ownerLastName).build());
	}

	// -----------------------------------------------------
	// Test findByLastName 
	// -----------------------------------------------------

	@Test
	void testFindByLastName() {
		// Before each tests, setUp() creates an OwnerService with a map with one Owner.
		Owner owner = ownerService.findByLastName(ownerLastName);
		
		assertNotNull(owner);
		assertTrue((owner.getId().compareTo(ownerId) == 0)      &&
				    owner.getFirstName().equals(ownerFirstName) &&
		            owner.getLastName().equals(ownerLastName) );
	}

	@Test
	void testFindByLastNameNotFound() {
		// Before each tests, setUp() creates an OwnerService with a map with one Owner.
		Owner owner = ownerService.findByLastName(ownerLastName2);  // Look for wrong last name.
		
		assertNull(owner);
	}

	// -----------------------------------------------------
	// Test save 
	// -----------------------------------------------------

	@Test
	void testSaveNewOwner() {
		// Before each tests, setUp() creates an OwnerService with a map with one Owner.
		int numOwnersBeforeSave = ownerService.findAll().size();
		
		// Create and save an Owner to save (second so generated id will be 2L). 
		Owner owner = Owner.builder().firstName(ownerFirstName2).lastName(ownerLastName2).build();
		
		// Save the owner (generates id = 2) and get the new size of the HashMap
		ownerService.save(owner);
		int numOwnersAfterSave = ownerService.findAll().size();
		Owner retrievedOwner = ownerService.findById(2L);
		
		assertTrue ( (numOwnersBeforeSave == 1) && (numOwnersAfterSave == 2) &&
				     retrievedOwner.getFirstName().equals(ownerFirstName2)  &&
				     retrievedOwner.getLastName().equals(ownerLastName2) );
	}

	@Test
	void testSaveExistingOwner() {
		// Before each tests, setUp() creates an OwnerService with a map with one Owner.
		int numOwnersBeforeSave = ownerService.findAll().size();
		
		// Retrieve that Owner, modify it, and re-save it.  
		// It will replace the object currently there.  Behavior of HashMap  
		// does not allow duplicate key - replaces object a/w the key.   
		Owner owner = ownerService.findById(ownerId);
		owner.setLastName("Smith-Updated");
		ownerService.save(owner);
		
		// Expect the count to be the same, since just an update. Save the owner (generates id = 2) and get the new size of the HashMap
		ownerService.save(owner);
		int numOwnersAfterSave = ownerService.findAll().size();
		
		// Retrieve the owner again and be sure has updates. 
		Owner retrievedOwner = ownerService.findById(ownerId);
		
		assertTrue ( (numOwnersBeforeSave == 1) && (numOwnersAfterSave == 1) &&
				      retrievedOwner.getFirstName().equals(ownerFirstName)  &&
				      retrievedOwner.getLastName().equals("Smith-Updated") );
	}

	// -----------------------------------------------------
	// Test findAll 
	// -----------------------------------------------------

	@Test
	void testFindAll() {
		// Before each tests, setUp() creates an OwnerService with a map with one Owner. 
		Set<Owner> owners = ownerService.findAll();
		
		// FYI output
		System.out.println("Owners found = " + owners.size());
		for (Owner curOwner : owners) {
			System.out.println(curOwner.getId() + ", " + curOwner.getFirstName() + " " + curOwner.getLastName());
		}

		// Expect single owner. 
		assertTrue(owners.size() == 1);
	}

	// -----------------------------------------------------
	// Test findById 
	// -----------------------------------------------------

	@Test
	void testFindById() {
		// Before each tests, setUp() creates an OwnerService with a map with one Owner.
		Owner owner = ownerService.findById(ownerId);
		
		assertTrue((owner.getId().compareTo(ownerId) == 0) &&
				    owner.getFirstName().equals(ownerFirstName) &&
		            owner.getLastName().equals(ownerLastName) );
	}

	// -----------------------------------------------------
	// Test deleteOwner 
	// -----------------------------------------------------

	@Test
	void testDeleteOwner() {
		// Before each tests, setUp() creates an OwnerService with a map with one Owner.
		int numOwnersBeforeDelete = ownerService.findAll().size();
		
		// Create an Owner logically equal to the one in the map. 
		Owner owner = Owner.builder().firstName(ownerFirstName).lastName(ownerLastName).build();
		owner.setId(ownerId);
		
		// Delete the owner and get the new size of the HashMap
		ownerService.delete(owner);
		int numOwnersAfterDelete = ownerService.findAll().size();
		
		assertTrue ( (numOwnersBeforeDelete == 1) && (numOwnersAfterDelete == 0) );
	}

	// -----------------------------------------------------
	// Test deleteById 
	// -----------------------------------------------------

	@Test
	void testDeleteByIdLong() {
		// Before each tests, setUp() creates an OwnerService with a map with one Owner.
		int numOwnersBeforeDelete = ownerService.findAll().size();
		
		// Delete the owner and get the new size of the HashMap
		ownerService.deleteById(ownerId);
		int numOwnersAfterDelete = ownerService.findAll().size();
		
		assertTrue ( (numOwnersBeforeDelete == 1) && (numOwnersAfterDelete == 0) );
	}

}
