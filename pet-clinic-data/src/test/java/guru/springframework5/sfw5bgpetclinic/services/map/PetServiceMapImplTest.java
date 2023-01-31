package guru.springframework5.sfw5bgpetclinic.services.map;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import guru.springframework5.sfw5bgpetclinic.model.Owner;
import guru.springframework5.sfw5bgpetclinic.model.Pet;
import guru.springframework5.sfw5bgpetclinic.model.PetType;

//This is Unit Test - It runs without Spring Context or DB.
//                    @SpringBootTest is for integration test (it runs DB, Spring Context, @Autowired)
//---------------------------------------------------------------------------------------------
//THIS IS WHY WE DO NOT USE Mockito FOR MAP IMPL (but use it for JPA Impl tests, etc.).  
// 
//Use Mockito to do "@Mock" injection of PetService to test JPA (PetServiceJpaImpl).
//Do not use it for testing MapImpl.  
//
//When @Mock is used, an actual object of given class type is not injected and 
//called.  Rather, more of a façade.  For instance, when @Mock OwnerServiceMapImpl, 
//an "instance of" OwnerServiceMapImpl is injected/mocked; however, when you make an 
//actual call to ownerServiceMapImpl methods (like ownerServiceMapImpl.save(owner))
//form the test (where Spring, DB, etc. is not running), you don't enter the Impl's 
//actual code.  Instead, you must do "when" (when(OwnerServiceMapImpl.save(..).thenReturn(..)" 
//to tell it what to return when the mocked service is called.  That is the value that gets 
//returned to the class being tested that calls on the mocked service.  
//Therefore, if you don’t provide the “when” (façade) then get “null” error.    
//
//Since the Map is manual (with the actual Map managed within the base class instance (NOT DB) 
//of each Impl type (Owner, Pet, PetType, etc.), we will just use "new" to 
//instantiate so use actual MAP and call actual code (w/o Mockito @Mock / when).  
//
//When test JPA, test with @Mock because DB is not running in unit test (which does 
//not bring up Spring, DB, etc.) so need to "mock" what DB would return.  
class PetServiceMapImplTest {

	// ------------------------------------
	// Test Data
	// ------------------------------------
	final String	PET1_NAME = "Sprinkler",
			       	PET1_TYPE = "DOG",
					OWNER1_FIRSTNAME = "Bob",
					OWNER1_LASTNAME = "Smith";
	
	// Pets must have an existing Owner (has Id) to be saved. 
	Owner owner1 = Owner.builder().firstName(OWNER1_FIRSTNAME).lastName(OWNER1_LASTNAME).build();
	// Pets can have a new PetType (no id) when saved.  PetService will also save PetType if new. 
	PetType petType1 = PetType.builder().name(PET1_TYPE).build();
	
	// ------------------------------------
	// Service being tested.
	// ------------------------------------
	private PetServiceMapImpl petServiceMapImpl = new PetServiceMapImpl(new PetTypeServiceMapImpl(), 
			                                                            new VisitServiceMapImpl());   

	@BeforeEach
	void setUp() throws Exception {

		// PetServiceMapImpl is constructed for each new test.  MAP (Set<Pet>) will be empty.     
		// Fill the MAP with one Pet object - DOG ("Sprinkler").   
		// - Recall added Lombok @Builder to Pet constructor so use builder().
		// - Recall our AbstractBaseService class manages the MAP and also mimics generating the 
		//   id's when "save" (like DB does for us if using JPA, etc.).  Therefore, DO NOT setId.  
		//   [Recall, Constructor does not accept ID because it should be generated and unique.]
		// - Recall, must have existing owner (so create OwnerService here and create an owner).
		//   It is not used by PetServiceMapImpl so local to setup method.  
		OwnerServiceMapImpl ownerService = new OwnerServiceMapImpl(petServiceMapImpl);
		ownerService.save(owner1);
		
		// Pet Type can be new when save pet.  Don't call on PetTypeService.save here. 
		Pet pet1 = Pet.builder().name(PET1_NAME).petType(petType1).owner(owner1).birthDate(LocalDate.now()).build();
		
		// Map belongs to abstract base class.  It is private.  Must use methods to base class methods to modify.   
		petServiceMapImpl.save(pet1);
	}  // end setUp

	// --------------------------------------------------
	// Test save for new pet
	// --------------------------------------------------
	@Test
	void testSaveSuccessNewPet() {
		// Create a Pet ("Fluffy") with same owner and pet type as dummy pet created in setUp.
		// [Recall - Must have existing owner to save.  PetType can be new and will be saved when saving Pet.]  
		Pet petToSave = Pet.builder().name("Fluffy").petType(petType1).owner(owner1).birthDate(LocalDate.now()).build();

		// Test save (which will generate ID - Map Impl class mimics default id generation provided by JPA). 
		Pet savedPet = petServiceMapImpl.save(petToSave);
		
		// Verify save was successful. 
		// - Saved Pet is not null.
		// - Non-0L ID means ID was generated during save().
		// - Save Pet name is correct. 
		// ----------------------------
		assertNotNull(savedPet);  
		assertTrue((savedPet.getId().compareTo(0L) != 0) &&
		            savedPet.getName().equals("Fluffy"));

		// There are now 2 pets in the Map. 
		assertEquals(petServiceMapImpl.findAll().size(), 2);
	}  // end testSaveSuccessNewPet

	@Test
	void testSaveFailedInvalidOwner() {
		// Create a Pet ("Fluffy") without an owner, but with same pet type as dummy pet created in setUp.
		// [Recall - Must have existing owner to save.  PetType can be new and will be saved when saving Pet.]  
		Pet petToSave = Pet.builder().name("Fluffy").petType(petType1).birthDate(LocalDate.now()).build();

		// Test save (which generates ID - Map Impl class mimics default id generation provided by JPA).
		// Without an existing Owner (non-null or one with ID because saved), throws exception. 
		try {
			petServiceMapImpl.save(petToSave);
			assert(false);
		} catch (java.lang.RuntimeException e) {
			assert(true);
		}
		
		// Save failed - There is still only 1 pet in the Map. 
		assertEquals(petServiceMapImpl.findAll().size(), 1);
	}  // end testSaveFailedInvalidOwner()

	@Test
	void testSaveFailedNoPetType() {
		// Create a Pet ("Fluffy") without a pet type, but with same owner as dummy pet created in setUp.
		// [Recall - Must have existing owner to save.  PetType can be new and will be saved when saving Pet.]  
		Pet petToSave = Pet.builder().name("Fluffy").owner(owner1).birthDate(LocalDate.now()).build();

		// Test save (which generates ID - Map Impl class mimics default id generation provided by JPA).
		// Without an existing Owner (non-null or one with ID because saved), throws exception. 
		try {
			petServiceMapImpl.save(petToSave);
			assert(false);
		} catch (java.lang.RuntimeException e) {
			assert(true);
		}
		
		// Save failed - There is still only 1 pet in the Map. 
		assertEquals(petServiceMapImpl.findAll().size(), 1);
	}  // end testSaveFailedNoPetType

	// --------------------------------------------------
	// Test save as update for existing pet
	// --------------------------------------------------

	@Test
	void testSaveSuccessUpdatePet() {
		Pet petToUpdate = (Pet)(petServiceMapImpl.findAll().iterator().next());
		petToUpdate.setName("Updated");
		
		// Test save.  Since ID exists, treats as update.  If existing not found, will fail.
		Pet savedPet = petServiceMapImpl.save(petToUpdate);  
		
		// Verify save 
		// - Saved pet has updated name.
		// - There is still only 1 pet in the Map (updated not created new). 
		assertTrue(savedPet.getName().equals("Updated"));
		assertEquals(petServiceMapImpl.findAll().size(), 1);
	}  // end testSaveSuccessUpdatePet
	
	@Test
	void testSaveFailedPetNotFoundForUpdate() {
		Pet petToUpdate = (Pet)(petServiceMapImpl.findAll().iterator().next());
		
		// Test save.  Since ID exists, treats as update.  If existing not found, will fail.
		petToUpdate.setId(petToUpdate.getId() + 1000L);  // Alter ID so not found. 
		try {
			petServiceMapImpl.save(petToUpdate);  
			assert(false);  // Test failed if exception not thrown for id not found. 
		} catch (java.lang.RuntimeException e) {
			assert(true);   // Test success.  Exception thrown for id not found. 
		}
		
		// Save failed - There is still only 1 pet in the Map. 
		assertEquals(petServiceMapImpl.findAll().size(), 1);
	}  // end testSaveFailedPetNotFoundForUpdate()

	// --------------------------------------------------
	// Test find methods.
	// --------------------------------------------------

	@Test
	void testFindAll() {
		int count1 = petServiceMapImpl.findAll().size();
		
		// Add an additional pet and verify count again.
		Pet pet = Pet.builder().name("Fluffy").petType(petType1).owner(owner1).birthDate(LocalDate.now()).build();
		petServiceMapImpl.save(pet);
		int count2 = petServiceMapImpl.findAll().size();
		
		// Verify save was successful. 
		// - Initial count is 1. 
		// - Final count is 2. 
		// ----------------------------
		assertEquals(count1, 1);
		assertEquals(count2, 2);
	}  // testFindAll()

	@Test
	void testFindByIdPetExists() {

		// Add an additional pet so there are two.  The save will generate an ID that can in turn be searched on. 
		Pet pet = Pet.builder().name("Fluffy").petType(petType1).owner(owner1).birthDate(LocalDate.now()).build();
		Pet savedPet = petServiceMapImpl.save(pet);

		// Test findById using the generated 	
		Pet searchedPet = petServiceMapImpl.findById(savedPet.getId());
		
		// Verify findById was successful. 
		// - Id of savedPet and searchedPet is the same. 
		// ----------------------------
		assertTrue(savedPet.getId().compareTo(searchedPet.getId()) == 0);
	}  // end testFindByIdPetExists

	@Test
	void testFindByIdPetDoesNotExist() {

		// Add an additional pet so there are two.  The save will generate an ID that can in turn be searched on. 
		Pet pet = Pet.builder().name("Fluffy").petType(petType1).owner(owner1).birthDate(LocalDate.now()).build();
		Pet savedPet = petServiceMapImpl.save(pet);

		// Test findById using the generated 	
		Pet searchedPet = petServiceMapImpl.findById(savedPet.getId() + 1000);
		
		// Verify findByIdDoesNotExist was successful. 
		// - searchedPet is null.  
		// ----------------------------
		assertNull(searchedPet);
	}  // end testFindByIdPetDoesNotExist

	// --------------------------------------------------
	// Test delete
	// --------------------------------------------------

	@Test
	void testDeletePet() {
	
		// Add an additional pet so there are two.  The save will generate an ID that can in turn be delete. 
		Pet pet = Pet.builder().name("Fluffy").petType(petType1).owner(owner1).birthDate(LocalDate.now()).build();
		Pet savedPet = petServiceMapImpl.save(pet);

		int countBeforeDelete = petServiceMapImpl.findAll().size();  // Expect 2. 
		
		// Test deleteById using the generated 	
		petServiceMapImpl.delete(savedPet);
		int countAfterDelete = petServiceMapImpl.findAll().size();  // Expect 1.
		
		// Verify findById was successful. 
		// - Count before delete was 2. 
		// - Count after delete is 1.  
		// ----------------------------
		assertEquals(countBeforeDelete, 2);
		assertEquals(countAfterDelete, 1);
	}  // end testDeletePet

	@Test
	void testDeleteById() {
		// Add an additional pet so there are two.  The save will generate an ID that can in turn be delete. 
		Pet pet = Pet.builder().name("Fluffy").petType(petType1).owner(owner1).birthDate(LocalDate.now()).build();
		Pet savedPet = petServiceMapImpl.save(pet);

		int countBeforeDelete = petServiceMapImpl.findAll().size();  // Expect 2. 
		
		// Test deleteById using the generated 	
		petServiceMapImpl.deleteById(savedPet.getId());
		int countAfterDelete = petServiceMapImpl.findAll().size();  // Expect 1.
		
		// Verify findById was successful. 
		// - Count before delete was 2. 
		// - Count after delete is 1.  
		// ----------------------------
		assertEquals(countBeforeDelete, 2);
		assertEquals(countAfterDelete, 1);
	}  // end testDeleteById()

	@Test
	void testDeletePetNull() {
		
		// Test delete with null Pet. 
		petServiceMapImpl.delete(null);  
		
		// Verify 
		// - Delete expected to fail - There is still 1 pet in the Map. 
		assertEquals(petServiceMapImpl.findAll().size(), 1);

	}  // end testDeletePetNull()

	@Test
	void testDeleteByIdNotFound() {
		
		// Test delete with Id not found. 
		petServiceMapImpl.deleteById(0L);  
		
		// Verify 
		// - Delete expected to fail - There is still 1 pet in the Map. 
		assertEquals(petServiceMapImpl.findAll().size(), 1);

	}  // end testDeleteByIdNotFound()
	
}  // end PetServiceMapImplTest

