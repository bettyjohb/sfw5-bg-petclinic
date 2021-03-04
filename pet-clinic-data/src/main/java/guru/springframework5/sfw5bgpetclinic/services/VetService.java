//***************************************************************************
//Interface:    VetService
//
//Public interface for Vet released services.
//
//Focus will be on CRUD related services for Vet entities.  This layer is 
//also responsible for interacting with the VetRepository.  The type of 
//repository (JDBC, JPA, Collection, etc.) is determined at runtime depending 
//on the IMPL that is injected by Spring based on @Qualifier / @Profile.
//
//Note:  These are simple interfaces.  The IMPLs have the @ and repositories injected. 
//***************************************************************************
package guru.springframework5.sfw5bgpetclinic.services;
import java.util.Set;
import guru.springframework5.sfw5bgpetclinic.model.Vet;

public interface VetService {

	// Methods provided are based largely on Spring's CrudRepostory.
	Vet save (Vet vet);
	
	Vet findByLastName(String lastName);
	Vet findById(Long id);
	Set<Vet> findAll();
	
}  // end interface VetService
