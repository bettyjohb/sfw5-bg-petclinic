//***************************************************************************
//Class:        PetServiceMapImpl [persistence using HashMap kept in AbstractMapService]
//Extends:      AbstractMapService ([1] Extends/implements nothing;
//                         [2] has same basic CRUD methods as BaseService interface but does 
//                         not implement it; [3] all methods are fully implemented, but make
//                         it abstract so no one instantiates. [4] Contains the reference
//                         to storage (in this case HashMap<T, ID>).  
//                         The IMPL implementing AbstractMapService will specify type 
//                         (i.e. PetServiceMapImpl<Pet, Long>
//Implements:    PetService interface ([1] Defines common interface for PetService;
//                          It does so by extending interface BaseService interface which 
//                          defines the service method signatures common to all entity
//                          objects.  PetService can then add any methods specific to Pet. 
//                          [2] PetController "has a" PetService interface so can 
//                          inject any IMPL that implements it (i.e. PetServiceMapImpl,
//                          PetServiceJPAImpl, etc.) based on @Profile.  
//
//PetServiceMapImpl is not the type referenced by using classes.  Reference OrderService.   
//Storage is in HashMap managed in BaseServiceMapImpl (that is our method of persistence).
//Methods here are defined with actual types <Pet, Long>, but mostly call up [super.somemethod()]
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
import guru.springframework5.sfw5bgpetclinic.services.PetService;

public class PetServiceMapImpl extends AbstractMapService<Pet, Long> implements PetService {

	// -------------------------------------------------------
	// MUST SPECIFY PUBLIC ON THESE METHODS.  CLASS DEFAULTS TO PACKAGE-PRIVATE.  
	// INTERACES DEFAULT TO PUBLIC.  THEREFORE, BASESERVICE DECLARATIONS DEFAULT TO
	// PUBLIC AND THESE METHODS THAT IMPLEMENT THEM CANNOT BE LESS THAN PUBLIC 
	// (I.E. THE DEFAULT PACKAGE-PRIVATE).  SO, PUT SPECIFY PUBLIC FOR EACH METHOD. 
	// -------------------------------------------------------

	// -------------------------------------------------------
	// Implementation of PetService
	// -------------------------------------------------------
	// NONE!   There are no additional methods in PetService that
	//         aren't in BaseService (implemented by AbstractMapService).
	// -------------------------------------------------------
  
	// -------------------------------------------------------
	// Implementation of BaseService (extended by PetService)
	// -------------------------------------------------------
    // AbstractMapService provided generic implementation of BaseService.
    // Now override those AbstractMapService methods with specific <Pet, Long> types.
    // These methods will call super.sameMethod(...)
	// -------------------------------------------------------
	
	/**
	 * Save a given entity.  Use the returned instance for further operations as the save operation 
	 * might have changed the entity instance completely.
	 * @param non-null object
	 * @return the saved entity (never null) 
	 */
	@Override
	public Pet save(Pet pet) {
		return super.save(pet.getId(), pet);
	}

	/**
	 * Returns all instances of the type. 
	 * @return all entities 
	 */
	@Override
	public Set<Pet> findAll() {
		return super.findAll();	
	}

	/**
	 * Returns all instances of the type with the given Id.
	 * @param id
	 * @return entity of given type and id. 
	 */
	@Override
	public Pet findById(Long id) {
		return super.findById(id);
	}

	/**
	 * Delete the given entity.
	 * @param entity  
	 */
	@Override
	public void delete(Pet pet) {
		super.delete(pet);
	}

	/**
	 * Delete the entity with the given Id.
	 * @param id must not be null 
	 */
	@Override
	public void deleteById(Long id) {
		super.deleteById(id);
	}

}  // end PetServiceMapImpl