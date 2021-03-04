//***************************************************************************
//Interface:    OwnerService
//
//Public interface for Owner released services.
//
//Focus will be on CRUD related services for Owner entities.  This layer is 
//also responsible for interacting with the OwnerRepository.  The type of 
//repository (JDBC, JPA, Collection, etc.) is determined at runtime depending 
//on the IMPL that is injected by Spring based on @Qualifier / @Profile.  
//
//Note:  These are simple interfaces.  The IMPLs have the @ and repositories injected. 
//***************************************************************************
package guru.springframework5.sfw5bgpetclinic.services;
import java.util.Set;
import guru.springframework5.sfw5bgpetclinic.model.Owner;

public interface OwnerService {

	// Methods provided are based largely on Spring's CrudRepostory.
	Owner save (Owner owner);
	
	Owner findByLastName(String lastName);
	Owner findById(Long id);
	Set<Owner> findAll();
	
}  // end interface OwnerService
