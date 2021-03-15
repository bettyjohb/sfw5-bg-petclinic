//***************************************************************************
//Class:        VetServiceMapImpl [persistence using HashMap kept in AbstractMapService]
//Extends:      AbstractMapService ([1] Extends/implements nothing;
//                         [2] has same basic CRUD methods as BaseService interface but does 
//                         not implement it; [3] all methods are fully implemented, but make
//                         it abstract so no one instantiates. [4] Contains the reference
//                         to storage (in this case HashMap<T, ID>).  
//                         The IMPL implementing AbstractMapService will specify type 
//                         (i.e. VetServiceMapImpl<Vet, Long>
//Implements:    VetService interface ([1] Defines common interface for VetService;
//                          It does so by extending interface BaseService interface which 
//                          defines the service method signatures common to all entity
//                          objects.  VetService then adds any methods specific to Vet. 
//                          [2] VetController "has a" VetService interface so can 
//                          inject any IMPL that implements it (i.e. VetServiceMapImpl,
//                          VetServiceJPAImpl, etc.) based on @Profile.  
//
//VetServiceMapImpl is not the type referenced by using classes.  Reference OrderService.   
//Storage is in HashMap managed in BaseServiceMapImpl (that is our method of persistence).
//Methods here are defined with actual types <Vet, Long>, but mostly call up [super.somemethod()]
//***************************************************************************
//*** SEE BASESERVICE.JAVA FOR FULL EXPLANATION FOR WHY WE HAVE:
//***************************************************************************
//Why <Entity>ServiceMapIMpl class extends AbstractMapService class .... and 
//  <Entity>Service interface implements BaseService interface 
//
//  Interface: 
//      BaseService     [common method signatures for all entities]
//      <entity>Service [i.e., OrderService = = BaseService + Order specific methods]
//  
//	  Classes:
//       AbstractMapService [Not referenced by any class.  Has common methods implemented.] 
//       OrderServiceMapImpl extends AbstractMapService and implements OrderService.
//
//  Controllers have reference of entity specific service (OrderService).  
//  This will be an IMPL determined at runtime by @Profile and application.properties. 
//***************************************************************************
package guru.springframework5.sfw5bgpetclinic.services.map;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import guru.springframework5.sfw5bgpetclinic.model.Pet;
import guru.springframework5.sfw5bgpetclinic.model.Vet;
import guru.springframework5.sfw5bgpetclinic.services.VetService;

public class VetServiceMapImpl extends AbstractMapService<Vet, Long> implements VetService {

	// -------------------------------------------------------
	// MUST SPECIFY PUBLIC ON THESE METHODS.  CLASS DEFAULTS TO PACKAGE-PRIVATE.  
	// INTERACES DEFAULT TO PUBLIC.  THEREFORE, BASESERVICE DECLARATIONS DEFAULT TO
	// PUBLIC AND THESE METHODS THAT IMPLEMENT THEM CANNOT BE LESS THAN PUBLIC 
	// (I.E. THE DEFAULT PACKAGE-PRIVATE).  SO, PUT SPECIFY PUBLIC FOR EACH METHOD. 
	// -------------------------------------------------------
	
	// -------------------------------------------------------
	// Implementation of VetService
	// This is not in AbstractMapService\BaseService, it is in VetService.
	// Therefore, unlike overriding BaseService methods <T, ID>,
	// that just call super.someMethod, we implement it here
	// since its a VetService interface w/o an implementation. 
	// -------------------------------------------------------
	public Vet findByLastName(String lastName) {
    	Iterator it = map.entrySet().iterator();
    	while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            Vet v = (Vet)pair.getValue();
            if (v.getLastName().equals(lastName))
            	return v; 
        }
        // Found nothing
        return null;
	}

	// -------------------------------------------------------
	// Implementation of BaseService (extended by VetService)
	// -------------------------------------------------------
    // AbstractMapService provided generic implementation of BaseService.
    // Now override those AbstractMapService methods with specific <Vet, Long> types.
    // These methods will call super.sameMethod(...)
	// -------------------------------------------------------
	
	/**
	 * Save a given entity.  Use the returned instance for further operations as the save operation 
	 * might have changed the entity instance completely.
	 * @param non-null object
	 * @return the saved entity (never null) 
	 */
	@Override
	public Vet save(Vet vet) {
		return super.save(vet.getId(), vet);
	}

	/**
	 * Returns all instances of the type. 
	 * @return all entities 
	 */
	@Override
	public Set<Vet> findAll() {
		return super.findAll();
	}

	/**
	 * Returns all instances of the type with the given Id.
	 * @param id
	 * @return entity of given type and id. 
	 */
	@Override
	public Vet findById(Long id) {
		return super.findById(id);
	}

	/**
	 * Delete the given entity.
	 * @param entity  
	 */
	@Override
	public void delete(Vet vet) {
		super.delete(vet);
	}

	/**
	 * Delete the entity with the given Id.
	 * @param id must not be null 
	 */
	@Override
	public void deleteById(Long id) {
		super.deleteById(id);
	}
	
}  // end class VetServiceMapImpl

  





