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
import guru.springframework5.sfw5bgpetclinic.model.PetType;
import guru.springframework5.sfw5bgpetclinic.model.Vet;
import guru.springframework5.sfw5bgpetclinic.services.OwnerService;
import guru.springframework5.sfw5bgpetclinic.services.VetService;
import guru.springframework5.sfw5bgpetclinic.services.PetTypeService;
import guru.springframework5.sfw5bgpetclinic.services.map.OwnerServiceMapImpl;
import guru.springframework5.sfw5bgpetclinic.services.map.PetTypeServiceMapImpl;
import guru.springframework5.sfw5bgpetclinic.services.map.VetServiceMapImpl;

@Component   // Make this a Spring bean loaded into Spring Context, therefore Spring sees CommandLineRunner and executes with run(). 
public class DataLoader implements CommandLineRunner {

	// Reference to interface types
	private final OwnerService ownerService;
	private final VetService vetService;
	private final PetTypeService petTypeService;
	
	// Don't need @Autowired in Spring 5. 
	// Since @Component, component scan will instantiate at startup thereby calling default constructor.
	// Since only one IMPL (MapImpl), Spring finds it and injects it for you since the IMPLs are @Service. 
	public DataLoader(OwnerService ownerService, VetService vetService, PetTypeService petTypeService) {
		this.ownerService = ownerService;
		this.vetService = vetService;
		this.petTypeService = petTypeService;
	}  // end Constructor
	
	@Override
	public void run(String... args) throws Exception {
		// Utilize existing services the implement the CRUD operations to initialize mock data.
		PetType dog = new PetType();
		dog.setName("Dog");
		PetType saveDogPetType = petTypeService.save(dog);

		PetType cat = new PetType();
		cat.setName("Cat");
		PetType saveCatPetType = petTypeService.save(cat);

		Owner owner1 = new Owner();
		owner1.setFirstName("Michael");
		owner1.setLastName("Weston");
		
		ownerService.save(owner1);
		
		Owner owner2 = new Owner();
		owner2.setFirstName("Fionna");
		owner2.setLastName("Glenanne");
		
		ownerService.save(owner2);
		
		System.out.println("Loaded owners....");
		
		Vet vet1 = new Vet();
		vet1.setFirstName("Sam");
		vet1.setLastName("Axe");
		
		vetService.save(vet1);
		
		Vet vet2 = new Vet();
		vet2.setFirstName("Jessie");
		vet2.setLastName("Porter");
		
		vetService.save(vet2);
		
		System.out.println("Loaded vets....");
	}  // end run()
	
}  // end DataLoader
