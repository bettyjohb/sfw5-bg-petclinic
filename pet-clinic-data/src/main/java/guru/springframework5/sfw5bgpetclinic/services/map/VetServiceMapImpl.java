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
//Stereotype:   @Service    Component Scan will pick up and instantiate to keep in Spring Context as Spring Bean.
//                          By default, Singleton scope (only 1 instance ever).  Okay - services don't have state.
//
//VetServiceMapImpl is not the type referenced by using classes.  Reference VetService.   
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
//      <entity>Service [i.e., VetService = = BaseService + Vet specific methods]
//  
//	  Classes:
//       AbstractMapService [Not referenced by any class.  Has common methods implemented.] 
//       VetServiceMapImpl extends AbstractMapService and implements VetService.
//
//  Controllers have reference of entity specific service (VetService).  
//  This will be an IMPL determined at runtime by @Profile and application.properties. 
//***************************************************************************
package guru.springframework5.sfw5bgpetclinic.services.map;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import guru.springframework5.sfw5bgpetclinic.model.Vet;
import guru.springframework5.sfw5bgpetclinic.services.VetService;
import guru.springframework5.sfw5bgpetclinic.services.SpecialtyService;

@Service
@Profile({"default", "map"})    // Use MapImpl's by default or if specify "map"; any other Profile do not instantiate MapImpl
public class VetServiceMapImpl extends AbstractMapService<Vet, Long> implements VetService {

	// Used on save() to account for composition (need to save Specialties).  
	// Hibernate impl won't need to do this since Hibernate handles this for us (id gen and saving composite objects).
	private final SpecialtyService specialtyService;

	// -------------------------------------------------------
	// Constructor Injection - To initialize private final attributes above. 
	// -------------------------------------------------------
	public VetServiceMapImpl (SpecialtyService specialtyService)  {
		super();
		this.specialtyService = specialtyService;
	}

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
	}  // end findByLastName 

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
		// Vet is a composite object ("has a" set of Specialties).  Need to behave like JPA / Hibernate and make 
		// sure when save the Vet, also save the Vet's Specialties.  This behavior is kept here because we don't  
		// want it in the entity / object model so behaves normally when use Hibernate.  Only this 
		// Map implementation needs to do this management of composite objects and also generating new object IDs.
		// Note:  When save() any of our entity objects, AbstractMapService.save() actually generates the next ID if the object does not have one.  Therefore, 
		// this method is only concerned with calling save for composite objects (not generating the id itself).  

		if (vet != null) {
			// If there are 1+ Specialties, save them in case there were changes.
			// Otherwise, if no Specialties, just go on to save the Vet.
			if (vet.getSpecialties() != null) {
				vet.getSpecialties().forEach(specialty->{
					// SpecialtyServiceMapImpl will take care of generating an ID for Specialty if new.
					// If existing Specialty, will swap and return reference to this Specialty with updates.
					specialtyService.save(specialty);
				});
			}  // end if
			
			// AbstractMapService level of VetServiceMapImpl owns HashMap of Vets and adds/updates Vet objects.
			// Managed by ID.  If replaces, HashMap returns prior, so our Abstract class returns current Vet object.
			return super.save(vet);  
		} else {
			// Vet was null.  Nothing saved. 
			return null;
		}
		
	}  // end save

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
