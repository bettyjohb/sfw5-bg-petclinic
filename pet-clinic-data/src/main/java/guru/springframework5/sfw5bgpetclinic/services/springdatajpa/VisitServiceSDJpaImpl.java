//***************************************************************************
//Layer:        Service - Generic interface / multiple IMPLs that hide persistence type.  
//Class:        VisitServiceSDJpaImpl (SDJpa = Spring Data JPA IMPL) 
//              [Persistence using CrudRepository based repository interfaces (see repositories pkg) to work with Spring Data]
//              [IMPL has knowledge of type of persistence (Map, Spring Data JPA, JDBC, etc.).  Interface does not so limit code to update.]
//Implements:   VisitService interface ([1] Defines common interface for VisitService;
//                    It does so by extending interface BaseService which 
//                    defines the service method signatures common to all entity
//                    objects.  VisitService then adds any methods specific to Visit. 
//                    [2] VisitController "has a" VisitService interface so can 
//                    inject any IMPL that implements it (i.e. VisitServiceMapImpl,
//                    VisitServiceJPAImpl, etc.) based on @Profile.  
//Stereotype:   @Service  Component Scan will pick up and instantiate to keep in Spring Context as Spring Bean.
//                    By default, Singleton scope (only 1 instance ever).  Okay - services don't have state.
//
//VisitServiceSDJpaImpl is not the type referenced by using classes.  Reference VisitService.   
//Storage is in DB (so don't need AbstractMapService type class which held the HashMap for Map version of persistence).    
//Methods here are defined with actual types <Visit, Long>, but mostly call up [super.somemethod()]
//***************************************************************************
//Summary:   
//Interface: 
//BaseService     [common method signatures for all entities]
//<entity>Service [i.e., VisitService = = BaseService + Visit specific methods]
//
//	  Classes (impl are grouped into packages based on type (i.e., map, springdatajpa, jdbc, etc.): 
// VisitServiceSDJpaImpl implements VisitService.  [Encapsulates use of repositories to invoke CRUD ops on the DB using Spring Data JPA.] 
//                                                 [Has entity specific CrudRepository interface at attribute (VisitRepository) 
//												   
// VisitServiceMapImpl, VisitServiceJDBCImpl, etc. [Can swap IMPL at runtime.]
//
//Controllers have reference of entity specific service (VisitService).  
//This will be an IMPL determined at runtime by Spring using @Profile and application.properties. 
//***************************************************************************
package guru.springframework5.sfw5bgpetclinic.services.springdatajpa;

import java.util.HashSet;
import java.util.Set;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import guru.springframework5.sfw5bgpetclinic.model.Visit;
import guru.springframework5.sfw5bgpetclinic.services.VisitService;
import guru.springframework5.sfw5bgpetclinic.repositories.VisitRepository;

@Service                     // Wire it up as a Spring Component
@Profile("springdatajpa")    // Only instantiated by Spring if active profile is springdatajpa.
public class VisitServiceSDJpaImpl implements VisitService {

	private final VisitRepository visitRepository;

	// -------------------------------------------------------
	// Constructor Injection - To initialize private final attributes above. 
	// -------------------------------------------------------
	public VisitServiceSDJpaImpl (VisitRepository visitRepository)  {
		super();
		this.visitRepository = visitRepository;
	}

	// -------------------------------------------------------
	// MUST SPECIFY PUBLIC ON THESE METHODS.  CLASS DEFAULTS TO PACKAGE-PRIVATE.  
	// INTERACES DEFAULT TO PUBLIC.  THEREFORE, BASESERVICE DECLARATIONS DEFAULT TO
	// PUBLIC AND THESE METHODS THAT IMPLEMENT THEM CANNOT BE LESS THAN PUBLIC 
	// (I.E. THE DEFAULT PACKAGE-PRIVATE).  SO, PUT SPECIFY PUBLIC FOR EACH METHOD. 
	// -------------------------------------------------------

	// -------------------------------------------------------
	// Implementation of VisitService 
	// These are non-standard CrudRepository methods so will 
	// need to update VisitRepository to have this method.
	// -------------------------------------------------------
	// NA
	// -------------------------------------------------------

	// -------------------------------------------------------
	// Implementation of BaseService (extended by VisitService)
	// -------------------------------------------------------

	/**
	 * Save a given entity.  Use the returned instance for further operations as the save operation 
	 * might have changed the entity instance completely.
	 * @param non-null object
	 * @return the saved entity (never null) 
	 */
	@Override
	public Visit save(Visit visit) {
		// Visit has complex Pet and non-complex String description and LocalDate. 
		// Hibernate takes care of savings Pet if CascadeType specified to do so.
		return visitRepository.save(visit);
	}  // end save

	/**
	 * Returns all instances of the type. 
	 * @return all entities 
	 */
	@Override
	public Set<Visit> findAll() {
		Set<Visit> visits = new HashSet<>();
		// CrudRepository.findAll returns Iterable<T>; So VisitRepository returns Iterable<Visit>.  
		// Put results into a Set<Visit>
		visitRepository.findAll().forEach(visits::add);
		return visits;
	}

	/**
	 * Returns instances of the type with the given Id.
	 * @param id
	 * @return entity of given type and id. 
	 */
	@Override
	public Visit findById(Long id) {
		
		// SHORT CODE
		return visitRepository.findById(id).orElse(null);
		
		// LONG CODE 
		// Optional<Visit> optionalVisit = visitRepository.findAllById(id);
		// if (optionalVisit.isPresent())   // Return visit if found 
		//	 return optionalVisit.get();
		// return null;                     // Visit not found    
	}

	/**
	 * Delete the given entity.
	 * @param entity  
	 */
	@Override
	public void delete(Visit entity) {
		visitRepository.delete(entity);
	}

	/**
	 * Delete the entity with the given Id.
	 * @param id must not be null 
	 */
	@Override
	public void deleteById(Long id) {
		visitRepository.deleteById(id);
	}

}  // end class VisitServiceSDJpaImpl

