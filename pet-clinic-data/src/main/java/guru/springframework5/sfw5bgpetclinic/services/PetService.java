//***************************************************************************
//Interface:    PetService
//
//Public interface for Pet released services.
//
//Focus will be on CRUD related services for Pet entities.  This layer is 
//also responsible for interacting with the PetRepository.  The type of 
//repository (JDBC, JPA, Collection, etc.) is determined at runtime depending 
//on the IMPL that is injected by Spring based on @Qualifier / @Profile.  
//
//Note:  These are simple interfaces.  The IMPLs have the @ and repositories injected. 
//***************************************************************************
package guru.springframework5.sfw5bgpetclinic.services;
import java.util.Set;
import guru.springframework5.sfw5bgpetclinic.model.Pet;

public interface PetService {

	// Methods provided are based largely on Spring's CrudRepostory.
	Pet save (Pet pet);
	
	Pet findByOwnerLastName(String ownerLastName);
	Pet findById(Long id);
	Set<Pet> findAll();

}  // end interface PetService
