//***************************************************************************
//Interface:    PetService
//Extends:      BaseService (common CRUD related services)
//
//Public interface for Owner related services (primarily CRUD related).  
//Inherits common methods of base class, and adds any entity specific method 
//signatures. 
//
//Focus will be on CRUD related services for Pet entities.  This layer is 
//also responsible for interacting with the PetRepository.  The type of 
//repository (JDBC, JPA, Collection, etc.) is determined at runtime depending 
//on the IMPL that is injected by Spring based on @Qualifier [in controller] / 
//@Profile and @Service [in service].  
//
//Therefore, using this interface looks like: 
//    private PetService petService; 
//            [don't need template type=Pet that's part of PetService defn]
//            [do constructor injection, etc.]
//
//Note:  These are simple interfaces.  The IMPLs have the @ and repositories injected. 
//***************************************************************************
//*** SEE BASESERVICE.JAVA FOR FULL EXPLANATION FOR WHY WE HAVE:
//***************************************************************************
//Why PetServiceMapIMpl class extends AbstractMapService class .... and 
//    PetService interface implements BaseService interface 
//
//    Interface: 
//        BaseService     [common method signatures for all entities]
//        <entity>Service [i.e., PetService = = BaseService + Pet specific methods]
//    
//	  Classes:
//         AbstractMapService [Not referenced by any class.  Has common methods implemented.] 
//         PetServiceMapImpl extends AbstractMapService and implements PetService.
//
//    Controllers have reference of entity specific service (PetService).  
//    This will be an IMPL determined at runtime by @Profile and application.properties. 
//***************************************************************************
package guru.springframework5.sfw5bgpetclinic.services;
import guru.springframework5.sfw5bgpetclinic.model.Pet;

public interface PetService extends BaseService<Pet, Long> {

}  // end interface PetService
