package guru.springframework5.sfw5bgpetclinic.services.springdatajpa;

import static org.junit.jupiter.api.Assertions.*;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import guru.springframework5.sfw5bgpetclinic.model.Owner;
import guru.springframework5.sfw5bgpetclinic.repositories.OwnerRepository;

// This is Unit Test - - Run without Spring Context or DB.   
// @SpringBootTest is used for integration test (include DB, Spring Context, @Autowired)
// Use Mockito to do "mock" injection of OwnerRepository into OwnerServiceSDJpaImpl
//     AND to provide fake data (absent DB) for OwnerRepository to return. 
@ExtendWith(MockitoExtension.class)  // Initializes mocks and handles strict stubbing.
									 // JUnit 4 used Runners.  5 uses extensions.
class OwnerServiceSDJpaImplTest {

	// -------------------------------------
	// Test Data 
	// -------------------------------------
	final Long ownerId = 1L;
	final String ownerFirstName = "Bob",
				 ownerLastName = "Smith";
	private HashSet<Owner> expectedOwners = new HashSet<Owner>();
	private Owner expectedOwner;

	// -------------------------------------
	// Mocked classes  
	// -------------------------------------
	@Mock		// Tells Mockito to Mock this class (to be injected into IMPL). 
	            // Mockito can tell it what data to return (fake DB call), monitor its calls, etc.)
				// This class will be injected into the IMPL class being tested.
	private OwnerRepository ownerRepository; 

	@InjectMocks  	// Tell Mockito to inject this IMPL with necessary @Mock classes.
	private OwnerServiceSDJpaImpl ownerServiceSDJpaImpl;  // Class to be tested! 
	
	// -------------------------------------
	// Before / After Methods
	// -------------------------------------

	@BeforeEach
	void setUp() throws Exception {
		// Create data for Mockito to return for "when" - During normal runtime, Bootstrap fills DB.  No DB in this Unit Test.   
		expectedOwner = Owner.builder().firstName(ownerFirstName).lastName(ownerLastName).build();
		expectedOwner.setId(ownerId); 
		expectedOwners.add(expectedOwner);
	}

	// -------------------------------------
	// Test findByLastName.
	// -------------------------------------
	
	@Test
	void testFindByLastNameValidName() {
		// Before each test, setUp() creates OwnerService with 1 Owner Set.
		// Indicate what data Mockito is to return (absent a DB) "when" findByLastName() invoked.
		// NOTE:  findByLastName is custom Repository method so returns Owner so thenReturn can just put expectedOwner.
		org.mockito.Mockito.when(ownerRepository.findByLastName(ownerLastName)).thenReturn(expectedOwner);

		// Test findByLastName
		Owner actualOwner = ownerServiceSDJpaImpl.findByLastName(ownerLastName);
		
		assertNotNull(actualOwner);

		assertTrue((actualOwner.getId().compareTo(ownerId) == 0)      &&
				    actualOwner.getFirstName().equals(ownerFirstName) &&
		            actualOwner.getLastName().equals(ownerLastName) );

		// Verify repository's findByLastName() invoked once. 
		org.mockito.Mockito.verify(ownerRepository, 
				                   org.mockito.Mockito.times(1)).findByLastName(ownerLastName);
	}  // end testFindByLastNameValidName()

	@Test
	void testFindByLastNameInvalidName() {
		// Before each test, setUp() creates OwnerService with 1 Owner Set.
		// Indicate what data Mockito is to return (absent a DB) "when" findByLastName() invoked.
		// NOTE:  findByLastName is custom Repository method so returns Owner so thenReturn can just put null.
		org.mockito.Mockito.when(ownerRepository.findByLastName("InvalidName")).thenReturn(null);
		
		// Test findByLastName - Invalid name
		Owner actualOwner = ownerServiceSDJpaImpl.findByLastName("InvalidName");
		
		assertNull(actualOwner);

		// Verify repository's findByLastName() invoked once. 
		org.mockito.Mockito.verify(ownerRepository, 
				                   org.mockito.Mockito.times(1)).findByLastName("InvalidName");

	}  // end testFindByLastNameInvalidName()

	// -------------------------------------
	// Test save.
	// -------------------------------------

	@Test
	void testSave() {
		// Before each test, setUp() creates OwnerService with 1 Owner Set.
		// Indicate what data Mockito is to return (absent a DB) "when" findByLastName() invoked.
		// NOTE:  findById is provided JPA Repository method so returns Optional<Owner> BUT my
		//        service IMPL DOES NOT USE orElse(null) [or any other of these methods]
		//        so no more call chains, so don't need to do Optional.of(expectedOwner) like findById test.
		Owner ownerToSave = Owner.builder().firstName(ownerFirstName).lastName(ownerLastName).build();
		ownerToSave.setId(ownerId);
		org.mockito.Mockito.when(ownerRepository.save(ownerToSave)).thenReturn(expectedOwner);
		
		// Test save - Count do any count comparisons with number of owners 
		//             since no real DB to count against.  Only mock returns by Mockito. 
		Owner savedOwner = ownerServiceSDJpaImpl.save(ownerToSave);
		
		assertNotNull(savedOwner);  // Save returns non-null

		assertTrue((savedOwner.getId().compareTo(expectedOwner.getId()) == 0)      &&
		            savedOwner.getFirstName().equals(expectedOwner.getFirstName()) &&
	                savedOwner.getLastName().equals(expectedOwner.getLastName()) );

		// Verify repository's findByLastName() invoked once. 
		org.mockito.Mockito.verify(ownerRepository, 
				                   org.mockito.Mockito.times(1)).save(ownerToSave);
	}

	// -------------------------------------
	// Test findAll.
	// -------------------------------------

	@Test
	void testFindAll() {
		// Before each test, setUp() creates OwnerService with 1 Owner Set.
		// Indicate what data Mockito is to return (absent a DB) "when" findByLastName() invoked.  
		org.mockito.Mockito.when(ownerRepository.findAll()).thenReturn(expectedOwners); 

		// Test findAll
		Set<Owner> actualOwners = ownerServiceSDJpaImpl.findAll();
		
		assertNotNull(actualOwners);
		assertTrue(actualOwners.size() == expectedOwners.size());
		
		// Verify repository's findAll() invoked once. 
		org.mockito.Mockito.verify(ownerRepository, 
				                   org.mockito.Mockito.times(1)).findAll();
	}

	// -------------------------------------
	// Test findById.
	// -------------------------------------

	@Test
	void testFindByIdValid() {
		// Before each test, setUp() creates OwnerService with 1 Owner Set.
		// Indicate what data Mockito is to return (absent a DB) "when" findByLastName() invoked.  
		// NOTE:  findById is provided JPA Repository method so returns Optional<Owner> AND my 
		//        service impl class uses .orElse(null), so thenReturn must put Optional.of(expectedOwner)
		//        or else can get null pointer exception if findById returns null since becomes "null.orElse()".
		org.mockito.Mockito.when(ownerRepository.findById(ownerId)).thenReturn(Optional.of(expectedOwner));

		// Test findById
		Owner actualOwner = ownerServiceSDJpaImpl.findById(ownerId);
		
		assertNotNull(actualOwner);

		assertTrue((actualOwner.getId().compareTo(ownerId) == 0)      &&
				    actualOwner.getFirstName().equals(ownerFirstName) &&
		            actualOwner.getLastName().equals(ownerLastName) );

		// Verify repository's findByLastName() invoked once. 
		org.mockito.Mockito.verify(ownerRepository, 
				                   org.mockito.Mockito.times(1)).findById(ownerId);
	}  // end testFindByIdValid

	@Test
	void testFindByIdInvalid() {
		// Before each test, setUp() creates OwnerService with 1 Owner Set.
		// Indicate what data Mockito is to return (absent a DB) "when" findByLastName() invoked.  
		// NOTE:  findById is a provided JPA Repository method so returns Optional<Owner> AND my 
		//        service impl class uses .orElse(null), so thenReturn must put Optional.empty() 
		//  	  or else can get null pointer exception if findById thenReturn(null) since becomes 
		//        "null.orElse(null)".  Returning Optional.empty() allows java to execute orElse 
		//        and return null.  
		org.mockito.Mockito.when(ownerRepository.findById(-1L)).thenReturn(Optional.empty());

		// Test findById
		Owner actualOwner = ownerServiceSDJpaImpl.findById(-1L);
		
		assertNull(actualOwner);

		// Verify repository's findByLastName() invoked once. 
		org.mockito.Mockito.verify(ownerRepository, 
				                   org.mockito.Mockito.times(1)).findById(-1L);
	}  // end testFindByIdInvalid

	// -------------------------------------
	// Test delete
	// -------------------------------------

	@Test
	void testDelete() {
		// Before each test, setUp() creates OwnerService with 1 Owner Set.
		// NOTE:  delete is a provided JPA Repository method that returns void, so no Mockito return value setup.  
		
		// Cannot test number of Owners after delete (like in Map version).   
		// Here DB is mocked by Mockito so not really there.  Just returns specified.
		// Just verify code is called. 

		// Test Delete the owner and verify method invoked once. 
		ownerServiceSDJpaImpl.delete(expectedOwner);
		org.mockito.Mockito.verify(ownerRepository, 
				                   org.mockito.Mockito.times(1)).delete(expectedOwner);
	}

	@Test
	void testDeleteById() {
		// Before each test, setUp() creates OwnerService with 1 Owner Set.
		// NOTE:  delete is a provided JPA Repository method that returns void, so no Mockito return value setup. 
		
		// Cannot test number of Owners after delete (like in Map version).   
		// Here DB is mocked by Mockito so not really there.  Just returns specified.
		// Just verify code is called. 

		// Test Delete the owner and verify method invoked once. 
		ownerServiceSDJpaImpl.deleteById(ownerId);
		org.mockito.Mockito.verify(ownerRepository, 
				                   org.mockito.Mockito.times(1)).deleteById(ownerId);
	}

}  // end OwnerServiceSDJpaImplTest
