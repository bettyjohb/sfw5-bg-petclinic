//***************************************************************************
//Interface:    OwnerService
//Extends:      BaseService (common CRUD related services)
//
//Public interface for Owner related services (primarily CRUD related).  
//Inherits common methods of base class, and adds any entity specific method 
//signatures. 
//
//Focus will be on CRUD related services for Owner entities.  This layer is 
//also responsible for interacting with the OwnerRepository.  The type of 
//repository (JDBC, JPA, Collection, etc.) is determined at runtime depending 
//on the IMPL that is injected by Spring based on @Qualifier [in controller] / 
//@Profile and @Service [in service].  
//
// 
//Therefore, using this interface looks like: 
//    private OwnerService ownerService; 
//            [don't need template type=Owner since in OwnderService defn]
//            [do constructor injection, etc.]
//
//Note:  These are simple interfaces.  The IMPLs have the @ and repositories injected. 
//***************************************************************************
//*** SEE BASESERVICE.JAVA FOR FULL EXPLANATION FOR WHY WE HAVE:
//***************************************************************************
//Why OrderServiceMapIMpl class extends AbstractMapService class .... and 
//    OwnerService interface implements BaseService interface 
//
//    Interface: 
//        BaseService     [common method signatures for all entities]
//        <entity>Service [i.e., OrderService = = BaseService + Order specific methods]
//    
//	  Classes:
//         AbstractMapService [Not referenced by any class.  Has common methods implemented.] 
//         OrderServiceMapImpl extends AbstractMapService and implements OrderService.
//
//    Controllers have reference of entity specific service (OrderService).  
//    This will be an IMPL determined at runtime by @Profile and application.properties. 
//***************************************************************************
package guru.springframework5.sfw5bgpetclinic.services;
import guru.springframework5.sfw5bgpetclinic.model.Owner;

public interface OwnerService extends BaseService<Owner, Long> {

	// Methods specific to OwnerService.  
	Owner findByLastName(String lastName);
	
}  // end interface OwnerService
