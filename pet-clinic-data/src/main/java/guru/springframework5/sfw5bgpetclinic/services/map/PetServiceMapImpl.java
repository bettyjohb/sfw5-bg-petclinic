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
//Stereotype:   @Service    Component Scan will pick up and instantiate to keep in Spring Context as Spring Bean.
//                          By default, Singleton scope (only 1 instance ever).  Okay - services don't have state.
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

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import guru.springframework5.sfw5bgpetclinic.model.Pet;
import guru.springframework5.sfw5bgpetclinic.services.PetService;
import guru.springframework5.sfw5bgpetclinic.services.PetTypeService;
import guru.springframework5.sfw5bgpetclinic.services.VisitService;

@Service
@Profile({"default", "map"})    // Use MapImpl's by default or if specify "map"; any other Profile do not instantiate MapImpl
public class PetServiceMapImpl extends AbstractMapService<Pet, Long> implements PetService {

	// When saving a Pet, need to mimic JPA / Hibernate saving compound attributes.  
	// Pets have a PetType that needs to be saved so must call save on the PetType
	// service (and if new, id == null, AbstractMapService will generate an ID).
	// Do this here and not in model so don't alter when run against real DB.  Just needed
	// for HashMap. 
	private final PetTypeService petTypeService;
	private final VisitService visitService;
	
	// -------------------------------------------------------
	// Constructor Injection - To initialize private final attributes. 
	// -------------------------------------------------------
	public PetServiceMapImpl (PetTypeService petTypeService, VisitService visitService)  {
		super();
		this.petTypeService = petTypeService;
		this.visitService = visitService;
	}

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
	 * Save a given entity.  If Pet's id is null, generates id and adds as new Pet.  Otherwise, 
	 * treats as an updated.  Use the returned instance for further operations as the save operation 
	 * might have changed the entity instance completely (particularly newly generated id).
	 * @param non-null object
	 * @return the saved entity (never null) 
	 */
	@Override
	public Pet save(Pet pet) {
		// Pet is a composite object ("has a" PetType).  Need to behave like JPA / Hibernate and make 
		// sure when save the Pet, also save the PetType.  This behavior is kept here because we don't  
		// want it in the entity / object model so behaves normally when use Hibernate.  Only this 
		// Map implementation needs to do this management of IDs.  Note:  When save() any of our entity objects, 
		// AbstractMapService.save() actually generates the next ID if the object does not have one.  Therefore, 
		// this method is only concerned with calling save for composite objects (not generating the id itself).  
		if (pet != null) {
			// If there is a PetType, save it.  Otherwise, exception.  
			if (pet.getPetType() != null) {
				// If new PetType, save it. 
				// PetTypeServiceMapImpl will take care of generating an ID for PetType if new.
				if (pet.getPetType().getId() == null)
					pet.setPetType( petTypeService.save(pet.getPetType()) );
				
				// If new Owner, throw exception.  Only existing Owner can save a Pet.  
				if (pet.getOwner().getId() == null)
					throw new java.lang.RuntimeException("Pet must have an exising Owner with valid ID.");
			} else {
				throw new java.lang.RuntimeException("Pet must have a PetType.");
			}
			
			return super.save(pet);
		} else {
			// Pet was null.  Nothing saved. 
			return null;
		}
		
	}  // end save

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
		if (pet != null) {
			// Remove all Visits for this Pet.  Done automatically if SD JPA / Hibernate.  
			pet.getVisits().forEach(visit -> {
				visitService.deleteById(visit.getId());
			});
			
			// Now delete the Pet itself. 
			super.delete(pet);
		}
	}

	/**
	 * Delete the entity with the given Id.
	 * @param id must not be null 
	 */
	@Override
	public void deleteById(Long id) {
		Pet pet = findById(id);
		delete(pet);
	}

}  // end PetServiceMapImpl