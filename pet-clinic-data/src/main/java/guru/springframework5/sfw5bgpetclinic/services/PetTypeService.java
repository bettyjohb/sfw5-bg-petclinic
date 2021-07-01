//***************************************************************************
//Interface:    PetTypeService
//Extends:      BaseService (common CRUD related services)
//
//Public interface for PetType related services (primarily CRUD related).  
//Inherits common methods of base class, and adds any entity specific method 
//signatures. 
//
//Focus will be on CRUD related services for PetType entities.  This layer is 
//also responsible for interacting with the PetTypeRepository.  The type of 
//repository (JDBC, JPA, Collection, etc.) is determined at runtime depending 
//on the IMPL that is injected by Spring based on @Qualifier [in controller] / 
//@Profile and @Service [in service].  
//
//Therefore, using this interface looks like: 
//    private PetTypeService petTypeService; 
//            [don't need template type=PetType since in PetTypeService defn]
//            [do constructor injection, etc.]
//
//Note:  These are simple interfaces.  The IMPLs have the @ and repositories injected. 
//***************************************************************************
//*** SEE BASESERVICE.JAVA FOR FULL EXPLANATION FOR WHY WE HAVE:
//***************************************************************************
//Why PetTypeServiceMapImpl class extends AbstractMapService class .... and 
//    PetTypeService interface implements BaseService interface 
//
//    Interface: 
//        BaseService     [common method signatures for all entities]
//        <entity>Service [i.e., PetTypeService = = BaseService + PetType specific methods]
//    
//	  Classes:
//         AbstractMapService [Not referenced by any class.  Has common methods implemented.] 
//         PetTypeServiceMapImpl extends AbstractMapService and implements PetTypeService.
//
//    Controllers have reference of entity specific service (PetTypeService).  
//    This will be an IMPL determined at runtime by @Profile and application.properties. 
//***************************************************************************
package guru.springframework5.sfw5bgpetclinic.services;
import guru.springframework5.sfw5bgpetclinic.model.PetType;

public interface PetTypeService extends BaseService<PetType, Long> {

	// Methods specific to PetTypeService.  

}  // end interface PetTypeService



