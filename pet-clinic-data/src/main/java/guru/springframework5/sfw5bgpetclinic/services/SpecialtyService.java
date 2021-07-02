//***************************************************************************
//Interface:    SpecialtyService
//Extends:      BaseService (common CRUD related services)
//
//Public interface for Specialty related services (primarily CRUD related).  
//Inherits common methods of base class, and adds any entity specific method 
//signatures. 
//
//Focus will be on CRUD related services for Specialty entities.  This layer is 
//also responsible for interacting with the Specialty Repository.  The type of 
//repository (JDBC, JPA, Collection, etc.) is determined at runtime depending 
//on the IMPL that is injected by Spring based on @Qualifier [in controller] / 
//@Profile and @Service [in service].  
//
//
//Therefore, using this interface looks like: 
//  private SpecialtyService specialtyService; 
//          [don't need template type=Specialty since in SpecialtyService defn]
//          [do constructor injection, etc.]
//
//Note:  These are simple interfaces.  The IMPLs have the @ and repositories injected. 
//***************************************************************************
//*** SEE BASESERVICE.JAVA FOR FULL EXPLANATION FOR WHY WE HAVE:
//***************************************************************************
//Why SpecialtyServiceMapIMpl class extends AbstractMapService class .... and 
//  SpecialtyService interface implements BaseService interface 
//
//  Interface: 
//      BaseService     [common method signatures for all entities]
//      <entity>Service [i.e., SpecialtyService = = BaseService + Specialty specific methods]
//  
//	  Classes:
//       AbstractMapService [Not referenced by any class.  Has common methods implemented.] 
//       SpecialtyServiceMapImpl extends AbstractMapService and implements SpecialtyService.
//
//  Controllers have reference of entity specific service (SpecialtyService).  
//  This will be an IMPL determined at runtime by @Profile and application.properties. 
//***************************************************************************
package guru.springframework5.sfw5bgpetclinic.services;

import guru.springframework5.sfw5bgpetclinic.model.Specialty;

public interface SpecialtyService extends BaseService<Specialty, Long> {

	// Methods specific to OwnerService.  

}  // end interface SpecialtyService
