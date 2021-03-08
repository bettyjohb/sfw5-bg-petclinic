//***************************************************************************
//Interface:    VetService
//Extends:      BaseService (common CRUD related services)
//
//Public interface for Vet related services.
//
//Focus will be on CRUD related services for Vet entities.  This layer is 
//also responsible for interacting with the VetRepository.  The type of 
//repository (JDBC, JPA, Collection, etc.) is determined at runtime depending 
//on the IMPL that is injected by Spring based on @Qualifier [in controller] / 
//@Profile and @Service [in service].  
//
//Note:  These are simple interfaces.  The IMPLs have the @ and repositories injected. 
//***************************************************************************
package guru.springframework5.sfw5bgpetclinic.services;
import guru.springframework5.sfw5bgpetclinic.model.Vet;

public interface VetService extends BaseService<Vet, Long> {

	// Methods specific to VetService.
	Vet findByLastName(String lastName);
	
}  // end interface VetService
