//***************************************************************************
//Interface:    OwnerService
//Extends:      BaseService (common CRUD related services)
//
//Public interface for Owner related services.
//
//Focus will be on CRUD related services for Owner entities.  This layer is 
//also responsible for interacting with the OwnerRepository.  The type of 
//repository (JDBC, JPA, Collection, etc.) is determined at runtime depending 
//on the IMPL that is injected by Spring based on @Qualifier [in controller] / 
//@Profile and @Service [in service].  
//
//Note:  These are simple interfaces.  The IMPLs have the @ and repositories injected. 
//***************************************************************************
package guru.springframework5.sfw5bgpetclinic.services;
import guru.springframework5.sfw5bgpetclinic.model.Owner;

public interface OwnerService extends BaseService<Owner, Long> {

	// Methods specific to OwnerService.  
	Owner findByLastName(String lastName);
	
}  // end interface OwnerService
