//***************************************************************************
//Class:        VisitServiceMapImpl [persistence using HashMap kept in AbstractMapService]
//Extends:      AbstractMapService 
//							 [Gives HashMap used to store all Visits; One instance created of this Service by 
//                            Spring by Default + Provide generic <T, ID>.] 
//                           ([1] Extends/implements nothing; [2] has same basic CRUD methods as BaseService
//                           interface but does not implement it; [3] all methods are fully implemented, but make
//                           it abstract so no one instantiates. [4] Contains the reference
//                           to storage (in this case HashMap<T, ID>).  Only one instance by default so one Hash.  
//                           The IMPL implementing AbstractMapService will specify type 
//                           (i.e. VisitServiceMapImpl<Visit, Long>
//Implements:    VisitService interface ([1] Defines common interface for VisitService;
//                            It does so by extending interface BaseService interface which 
//                            defines the service method signatures common to all entity
//                            objects.  VisitService then adds any methods specific to Visit. 
//                            [2] VisitController "has a" VisitService interface so can 
//                            inject any IMPL that implements it (i.e. VisitServiceMapImpl,
//                            VisitServiceJPAImpl, etc.) based on @Profile.  
//Stereotype:   @Service    Component Scan will pick up and instantiate to keep in Spring Context as Spring Bean.
//                            By default, Singleton scope (only 1 instance ever).  Okay - services don't have state.
//
// VisitServiceMapImpl is not the type referenced by using classes.  Reference VisitService.   
// Storage is in HashMap managed in BaseServiceMapImpl (that is our method of persistence).
// Methods here are defined with actual types <Visit, Long>, but mostly call up [super.somemethod()]
//***************************************************************************
//*** SEE BASESERVICE.JAVA FOR FULL EXPLANATION FOR WHY WE HAVE:
//***************************************************************************
//Why <Entity>ServiceMapIMpl class extends AbstractMapService class .... and 
//    <Entity>Service interface implements BaseService interface 
//
//    Interface: 
//        BaseService     [common method signatures for all entities]
//        <entity>Service [i.e., VisitService = = BaseService + Visit specific methods]
//    
//	  Classes:
//         AbstractMapService [Not referenced by any class.  Has common methods implemented.] 
//         VisitServiceMapImpl extends AbstractMapService and implements VisitService.
//
//    Controllers have reference of entity specific service (VisitService).  
//    This will be an IMPL determined at runtime by @Profile and application.properties. 
//***************************************************************************
package guru.springframework5.sfw5bgpetclinic.services.map;

import java.util.Set;

import org.springframework.stereotype.Service;

import guru.springframework5.sfw5bgpetclinic.model.Visit;
import guru.springframework5.sfw5bgpetclinic.services.VisitService;

// Recall, AbstractMapService 1) Provides HashMap acting as DB in Map Version.  (only one instant of). 
// 2) Provides generic implementation. 
@Service
public class VisitServiceMapImpl extends AbstractMapService<Visit, Long> implements VisitService {

	// -------------------------------------------------------
	// Constructor Injection - To initialize private final attributes above. 
	// -------------------------------------------------------
	public VisitServiceMapImpl ()  {
		super();
	}

	/**
	 * Save a given entity.  If id of object is null, generate and create new object; otherwise update existing. 
	 * Calling source should use the returned instance for further operations as the save operation might have 
	 * changed the entity instance completely.
	 *  
	 * @param non-null object (BaseEntity or extension so has getId()
	 * @return the saved entity (never null) 
	 */
	@Override
	public Visit save(Visit visit) {
		// Compound Visit has complex Pet object as attribute.  
		// Visits only save if the Pet is valid (existing).  Otherwise, throw exception.
		// Not saving Pets here. 
		// LocalDate and String description remain with the class (not complex).
		if (visit != null)  {
			Visit savedVisit = null;
			if ( (visit.getPet() != null) && (visit.getPet().getId() != null) ) {
				savedVisit = super.save(visit);  // AbstractMapService will generate id.
				// Add the visit to the Pet
				visit.getPet().add(visit);
			} else {
				throw new java.lang.RuntimeException("Visit must have a valid Pet already in system.");
			}
			
			return savedVisit; 
		} else {
			// Visit entity to be saved is null.
			return null;
		}
	}  // end save

	/**
	 * Returns all instances of the type in HashSet. 
	 * @return New HashSet containing all entities 
	 */
	@Override
	public Set<Visit> findAll() {
		return super.findAll();   // returns HashSet<T>; which here is Visit
	}

	/**
	 * Returns all instances of the type with the given Id.
	 * @param id
	 * @return entity of given type and id. 
	 */
	@Override
	public Visit findById(Long id) {
		return super.findById(id);
	}

	/**
	 * Delete the given entity.
	 * @param entity  
	 */
	@Override
	public void delete(Visit entity) {
		super.delete(entity);
	}

	/**
	 * Delete the entity with the given Id.
	 * @param id must not be null 
	 */
	@Override
	public void deleteById(Long id) {
		super.deleteById(id);
	}
}  // end class VisitServiceMapImpl
