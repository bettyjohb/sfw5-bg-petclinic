//***************************************************************************
//Interface:    VisitService
//Extends:      BaseService (common CRUD related services)
//
//Public interface for Visit related services (primarily CRUD related).  
//Inherits common methods of base class, and adds any entity specific method 
//signatures. 
//
//Focus will be on CRUD related services for Visit entities.  This layer is 
//also responsible for interacting with the Visit Repository.  The type of 
//repository (JDBC, JPA, Collection, etc.) is determined at runtime depending 
//on the IMPL that is injected by Spring based on @Qualifier [in controller] / 
//@Profile and @Service [in service].  
//
//Therefore, using this interface looks like: 
//private VisitService visitService; 
//        [don't need template type=Visit since in VisitService defn]
//        [do constructor injection, etc.]
//
//Note:  These are simple interfaces.  The IMPLs have the @ and repositories injected. 
//***************************************************************************
//*** SEE BASESERVICE.JAVA FOR FULL EXPLANATION FOR WHY WE HAVE:
//***************************************************************************
//Why VisitServiceMapIMpl class extends AbstractMapService class .... and 
//VisitService interface implements BaseService interface 
//
//Interface: 
//    BaseService     [common method signatures for all entities]
//    <entity>Service [i.e., VisitService = = BaseService + Visit specific methods]
//
//	  Classes:
//     AbstractMapService [Not referenced by any class.  Has common methods implemented.] 
//     VisitServiceMapImpl extends AbstractMapService and implements VisitService.
//
//Controllers have reference of entity specific service (VisitService).  
//This will be an IMPL determined at runtime by @Profile and application.properties. 
//***************************************************************************
package guru.springframework5.sfw5bgpetclinic.services;
import guru.springframework5.sfw5bgpetclinic.model.Visit;

public interface VisitService extends BaseService<Visit, Long> {

	// Methods specific to VisitService.  

}  // end interface VisitService
