
//***************************************************************************
//Class:        SpecialtyServiceMapImpl [persistence using HashMap kept in AbstractMapService]
//Extends:      AbstractMapService ([1] Extends/implements nothing;
//                           [2] has same basic CRUD methods as BaseService interface but does 
//                           not implement it; [3] all methods are fully implemented, but make
//                           it abstract so no one instantiates. [4] Contains the reference
//                           to storage (in this case HashMap<T, ID>).  
//                           The IMPL implementing AbstractMapService will specify type 
//                           (i.e. SpecialtyServiceMapImpl<Specialty, Long>
//Implements:    SpecialtyService interface ([1] Defines common interface for SpecialtyService;
//                            It does so by extending interface BaseService interface which 
//                            defines the service method signatures common to all entity
//                            objects.  SpecialtyService then adds any methods specific to Specialty. 
//                            [2] SpecialtyController "has a" SpecialtyService interface so can 
//                            inject any IMPL that implements it (i.e. SpecialtyServiceMapImpl,
//                            SpecialtyServiceJPAImpl, etc.) based on @Profile.  
//Stereotype:   @Service    Component Scan will pick up and instantiate to keep in Spring Context as Spring Bean.
//                            By default, Singleton scope (only 1 instance ever).  Okay - services don't have state.
//
// SpecialtyServiceMapImpl is not the type referenced by using classes.  Reference SpecialtyService.   
// Storage is in HashMap managed in BaseServiceMapImpl (that is our method of persistence).
// Methods here are defined with actual types <Specialty, Long>, but mostly call up [super.somemethod()]
//***************************************************************************
//*** SEE BASESERVICE.JAVA FOR FULL EXPLANATION FOR WHY WE HAVE:
//***************************************************************************
//Why <Entity>ServiceMapIMpl class extends AbstractMapService class .... and 
//    <Entity>Service interface implements BaseService interface 
//
//    Interface: 
//        BaseService     [common method signatures for all entities]
//        <entity>Service [i.e., SpecialtyService = = BaseService + Specialty specific methods]
//    
//	  Classes:
//         AbstractMapService [Not referenced by any class.  Has common methods implemented.] 
//         SpecialtyServiceMapImpl extends AbstractMapService and implements SpecialtyService.
//
//    Controllers have reference of entity specific service (SpecialtyService).  
//    This will be an IMPL determined at runtime by @Profile and application.properties. 
//***************************************************************************
package guru.springframework5.sfw5bgpetclinic.services.map;

import java.util.Set;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import guru.springframework5.sfw5bgpetclinic.model.Specialty;
import guru.springframework5.sfw5bgpetclinic.services.SpecialtyService;

@Service  // Must be a @Service to get picked up by Spring Context. 
@Profile({"default", "map"})    // Use MapImpl's by default or if specify "map"; any other Profile do not instantiate MapImpl
public class SpecialtyServiceMapImpl extends AbstractMapService<Specialty, Long> implements SpecialtyService {

	// -------------------------------------------------------
	// MUST SPECIFY PUBLIC ON THESE METHODS.  CLASS DEFAULTS TO PACKAGE-PRIVATE.  
	// INTERACES DEFAULT TO PUBLIC.  THEREFORE, BASESERVICE DECLARATIONS DEFAULT TO
	// PUBLIC AND THESE METHODS THAT IMPLEMENT THEM CANNOT BE LESS THAN PUBLIC 
	// (I.E. THE DEFAULT PACKAGE-PRIVATE).  SO, PUT SPECIFY PUBLIC FOR EACH METHOD. 
	// -------------------------------------------------------

	// -------------------------------------------------------
	// Implementation of SpecialtyService 
	// This is not in AbstractMapService/BaseService, it is in SpecialtyService.
	// Therefore, unlike overriding BaseService methods <T, ID>,
	// that just call super.someMethod, we implement it here
	// since its a SpecialtyService interface w/o an implementation. 
	// -------------------------------------------------------
	
	// NO SPECIALTY SPECIFIC METHODS!
	
	
	// -------------------------------------------------------
	// Implementation of BaseService (extended by SpecialtyService)
	// -------------------------------------------------------
    // AbstractMapService provided generic implementation of BaseService.
    // Now override those AbstractMapService methods with specific <Specialty, Long> types.
    // These methods will call super.sameMethod(...)
	// -------------------------------------------------------
	
	/**
	 * Save a given entity.  Use the returned instance for further operations as the save operation 
	 * might have changed the entity instance completely.
	 * @param non-null object
	 * @return the saved entity (never null) 
	 */
	@Override
	public Specialty save(Specialty specialty) {
		return super.save(specialty);
	}

	/**
	 * Returns all instances of the type. 
	 * @return all entities 
	 */
	@Override
	public Set<Specialty> findAll() {
		return super.findAll();
	}

	/**
	 * Returns all instances of the type with the given Id.
	 * @param id
	 * @return entity of given type and id. 
	 */
	@Override
	public Specialty findById(Long id) {
		return super.findById(id);
	}

	/**
	 * Delete the given entity.
	 * @param entity  
	 */
	@Override
	public void delete(Specialty specialty) {
		super.delete(specialty);
	}

	/**
	 * Delete the entity with the given Id.
	 * @param id must not be null 
	 */
	@Override
	public void deleteById(Long id) {
		super.deleteById(id);
	}

}  // end class SpecialtyServiceMapImpl
