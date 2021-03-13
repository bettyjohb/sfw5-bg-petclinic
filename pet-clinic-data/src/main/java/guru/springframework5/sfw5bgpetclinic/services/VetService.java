//***************************************************************************
//Interface:    VetService
//Extends:      BaseService (common CRUD related services)
//
//Public interface for Vet related services (primarily CRUD related).  
//Inherits common methods of base class, and adds any entity specific method 
//signatures. 
//
//Focus will be on CRUD related services for Vet entities.  This layer is 
//also responsible for interacting with the VetRepository.  The type of 
//repository (JDBC, JPA, Collection, etc.) is determined at runtime depending 
//on the IMPL that is injected by Spring based on @Qualifier [in controller] / 
//@Profile and @Service [in service].  
//
//Therefore, using this interface looks like: 
//    private VetService vetService; 
//            [don't need template type=Vet since that part of VetService defn]
//            [do constructor injection, etc.]
//
//Note:  These are simple interfaces.  The IMPLs have the @ and repositories injected. 
//***************************************************************************
//*** SEE BASESERVICE.JAVA FOR FULL EXPLANATION FOR WHY WE HAVE:
//***************************************************************************
//Why VetServiceMapIMpl class extends AbstractMapService class .... and 
//    VetService interface implements BaseService interface 
//
//    Interface: 
//        BaseService     [common method signatures for all entities]
//        <entity>Service [i.e., VetService = = BaseService + Vet specific methods]
//    
//	  Classes:
//         AbstractMapService [Not referenced by any class.  Has common methods implemented.] 
//         VetServiceMapImpl extends AbstractMapService and implements VetService.
//
//    Controllers have reference of entity specific service (VetService).  
//    This will be an IMPL determined at runtime by @Profile and application.properties. 
//***************************************************************************
package guru.springframework5.sfw5bgpetclinic.services;
import guru.springframework5.sfw5bgpetclinic.model.Vet;

public interface VetService extends BaseService<Vet, Long> {

	// Methods specific to VetService.
	Vet findByLastName(String lastName);
	
}  // end interface VetService
