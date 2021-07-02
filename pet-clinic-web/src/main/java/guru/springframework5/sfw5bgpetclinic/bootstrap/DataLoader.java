// Class:       DataLoader
// Implements:  CommandLineRunner (Spring Boot specific interface says a bean should run() if within Spring project)
// 
// DataLoader.run() will be executed on startup.  Spring and the DataLoader bootstrap class is responsible for determining the current method
// of persistence being utilized (Map [HashMap], Spring Data JPA, or straight up JPA).  Based on this, it will load (mock) data 
// utilizing the appropriate service (i.e., OwnerServiceMapImpl if using Map persistence).  This way, on startup, we have known mock data 
// to utilize to validate the system.
// 
// Recall, there is a primary CRUD service interface from which multiple IMPL types implement (MAP, JPA, etc.).  The appropriate IMPL is 
// utilized based on Spring profiles (to be implemented).  [Similar to properties file for Regis class that determined which IMPL to 
// use.  Based on the profile, the data loader will use the correct service (i.e., OwnerServieMapImpl) to input mock data it creates. 
//
// NOTE:  Id management will be an issue (particularly for Maps).  Currently, can input a duplicate ID into the map.  Therefore, will
// want to move the responsibility of ID generation to the Map classes.  This is like DB that generate IDs. 
package guru.springframework5.sfw5bgpetclinic.bootstrap;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import guru.springframework5.sfw5bgpetclinic.model.Owner;
import guru.springframework5.sfw5bgpetclinic.model.Pet;
import guru.springframework5.sfw5bgpetclinic.model.PetType;
import guru.springframework5.sfw5bgpetclinic.model.Specialty;
import guru.springframework5.sfw5bgpetclinic.model.Vet;
import guru.springframework5.sfw5bgpetclinic.services.OwnerService;
import guru.springframework5.sfw5bgpetclinic.services.VetService;
import guru.springframework5.sfw5bgpetclinic.services.PetTypeService;
import guru.springframework5.sfw5bgpetclinic.services.SpecialtyService;
import guru.springframework5.sfw5bgpetclinic.services.map.OwnerServiceMapImpl;
import guru.springframework5.sfw5bgpetclinic.services.map.PetTypeServiceMapImpl;
import guru.springframework5.sfw5bgpetclinic.services.map.VetServiceMapImpl;

@Component   // Make this a Spring bean loaded into Spring Context, therefore Spring sees CommandLineRunner and executes with run(). 
public class DataLoader implements CommandLineRunner {

	// Reference to interface types
	private final OwnerService ownerService;
	private final VetService vetService;
	private final PetTypeService petTypeService;
	private final SpecialtyService specialtyService;
	
	// Don't need @Autowired in Spring 5. 
	// Since @Component, component scan will instantiate at startup thereby calling default constructor.
	// Since only one IMPL (MapImpl), Spring finds it and injects it for you since the IMPLs are @Service.
	// Default is that only one instance ever made, so all get reference to same <entity>Service, therefore,
	// if MapImpl, will have same service with same HashMap containing all entities of a given type. 
	public DataLoader(OwnerService ownerService, VetService vetService, PetTypeService petTypeService, SpecialtyService specialtyService) {
		this.ownerService = ownerService;
		this.vetService = vetService;
		this.petTypeService = petTypeService;
		this.specialtyService = specialtyService;
	}  // end Constructor
	
	@Override
	public void run(String... args) throws Exception {
		
		// When running Map based version, not a problem.  Data dumped and reloaded with each run. 
		// When running JPA / Hibernate, DB stays around so loadData would keep trying to add dups. 
		// Quirky check, see if findAll returns anything.  If so, don't call.
		if (ownerService.findAll().size() == 0)
			loadData();
	}  // end run()
	
	
	// ------------------------------------------------------
	// Private Worker Methods
	// ------------------------------------------------------
	
	/** 
	 * Load dummy data into system. 
	 * Keep separate so can skip when using DB IMPLs and don't want to use this.  
	 */
	private void loadData() {
		// Utilize existing services the implement the CRUD operations to initialize mock data.

		// ----------------------------------------------
		// Objects that are reused.
		// 
		// MUST save them here.  Don't rely on composite saving because reusing so need reference returned from save(). 
		// ----------------------------------------------
		
		// When PetTypeService saves the PetType, will address that there is no "id" (BaseEntity) and will generate.
		// This is done in AbstractMapService so as not to mess up model or regular services for JPA/Hibernate DB 
		// that provided this by default. 
		PetType dog = new PetType();
		dog.setName("DOG");
		PetType saveDogPetType = petTypeService.save(dog);

		PetType cat = new PetType();
		cat.setName("CAT");
		PetType saveCatPetType = petTypeService.save(cat);

		Specialty radiology = new Specialty();
		radiology.setDescription("Radiology");
		radiology = specialtyService.save(radiology);
		
		Specialty surgery = new Specialty();
		surgery.setDescription("Surgery");
		surgery = specialtyService.save(surgery);
		
		Specialty dentistry = new Specialty();
		dentistry.setDescription("Dentistry");
		dentistry = specialtyService.save(dentistry);

		// Create Dummy Data 
		// UNDERSTANDING....
		// When OwnerService saves Owner, will address calling PetServiceMapImpl to save all its Pets.
		//(Recall AbstractMapService will generate IDs for new Pet entities.)  In turn, PetServiceMapImpl
		// will invoke PetTypeServiceMapImpl to make sure that PetTypes are saved (which they were above 
		// so it will have to do nothing).  
		// NOTE:  THIS LOGIC W/ MANUALLY SAVING COMPOSITE OBJECTS AND ID GENERATION IS IN THE MAP SERVICE 
		// IMPLs BECAUSE WE DO NOT WANT TO MUCK UP THE MODEL OBJECTS (ENTITIES) BY GENERATING ID'S ETC SO 
		// THEY CAN BE USED BY JPA/HIBERNATE.
		
		// OWNER 1 
		Owner owner1 = new Owner();
		owner1.setFirstName("Michael");
		owner1.setLastName("Weston");
		owner1.setAddress("123 Brickerel");
		owner1.setCity("Miami");
		owner1.setTelephone("1231231234");

		// Pet constructor will set Pet's owner to specified owner, and call add(Pet) passing itself. 
		Pet pet1 = new Pet("Dog1Name", saveDogPetType, owner1, java.time.LocalDate.now());
		Pet pet2 = new Pet("Cat1Name", saveCatPetType, owner1, java.time.LocalDate.now());
		
		ownerService.save(owner1);

		// OWNER 2 
		Owner owner2 = new Owner();
		owner2.setFirstName("Fionna");
		owner2.setLastName("Glenanne");
		owner2.setAddress("456 Brickerel");
		owner2.setCity("Miami");
		owner2.setTelephone("7897897890");
		
		Pet pet3 = new Pet("Dog2Name", saveDogPetType, owner2, java.time.LocalDate.now());
		Pet pet4 = new Pet("Cat2Name", saveCatPetType, owner2, java.time.LocalDate.now());

		ownerService.save(owner2);
		
		System.out.println("Loaded owners and their pets....");
		System.out.println("OWNER 1 = " + owner1);
		System.out.println("\nOWNER 2 = " + owner2);
		
		// VET 1
		Vet vet1 = new Vet();
		vet1.setFirstName("Sam");
		vet1.setLastName("Axe");
		vet1.add(radiology);
				
		vetService.save(vet1);
		
		// VET 2 
		Vet vet2 = new Vet();
		vet2.setFirstName("Jessie");
		vet2.setLastName("Porter");
		vet2.add(dentistry);    
		vet2.add(surgery);
		
		vetService.save(vet2);
		
		System.out.println("Loaded vets....");
		System.out.println("VET 1 (has 1 specialty) = " + vet1);
		System.out.println("\nVET 2 (has 2 specialties) = " + vet2);		
	}  // end loadData
	
}  // end DataLoader
