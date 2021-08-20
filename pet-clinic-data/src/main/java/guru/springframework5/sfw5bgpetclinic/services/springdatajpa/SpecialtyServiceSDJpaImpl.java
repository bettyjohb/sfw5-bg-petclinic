//***************************************************************************
//Layer:        Service - Generic interface / multiple IMPLs that hide persistence type.  
//Class:        SpecialtyServiceSDJpaImpl (SDJpa = Spring Data JPA IMPL) 
//            [Persistence using CrudRepository based repository interfaces (see repositories pkg) to work with Spring Data]
//            [IMPL has knowledge of type of persistence (Map, Spring Data JPA, JDBC, etc.).  Interface does not so limit code to update.]
//Implements:   SpecialtyService interface ([1] Defines common interface for SpecialtyService;
//                      It does so by extending interface BaseService which 
//                      defines the service method signatures common to all entity
//                      objects.  SpecialtyService then adds any methods specific to Specialty. 
//                      [2] SpecialtyController "has a" SpecialtyService interface so can 
//                      inject any IMPL that implements it (i.e. SpecialtyServiceMapImpl,
//                      SpecialtyServiceJPAImpl, etc.) based on @Profile.  
//Stereotype:   @Service  Component Scan will pick up and instantiate to keep in Spring Context as Spring Bean.
//                      By default, Singleton scope (only 1 instance ever).  Okay - services don't have state.
//
//SpecialtyServiceSDJpaImpl is not the type referenced by using classes.  Reference SpecialtyService.   
//Storage is in DB (so don't need AbstractMapService type class which held the HashMap for Map version of persistence).    
//Methods here are defined with actual types <Specialty, Long>, but mostly call up [super.somemethod()]
//***************************************************************************
//Summary:   
//Interface: 
//  BaseService     [common method signatures for all entities]
//  <entity>Service [i.e., SpecialtyService = = BaseService + Specialty specific methods]
//
//	  Classes (impl are grouped into packages based on type (i.e., map, springdatajpa, jdbc, etc.): 
//   SpecialtyServiceSDJpaImpl implements SpecialtyService.  [Encapsulates use of repositories to invoke CRUD ops on the DB using Spring Data JPA.] 
//                                                           [Has entity specific CrudRepository interface at attribute (SpecialtyRepository) 
//  												   
//   SpecialtyServiceMapImpl, SpecialtyServiceJDBCImpl, etc. [Can swap IMPL at runtime.]
//
//Controllers have reference of entity specific service (SpecialtyService).  
//This will be an IMPL determined at runtime by Spring using @Profile and application.properties. 
//***************************************************************************
package guru.springframework5.sfw5bgpetclinic.services.springdatajpa;

import java.util.HashSet;
import java.util.Set;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import guru.springframework5.sfw5bgpetclinic.model.Specialty;
import guru.springframework5.sfw5bgpetclinic.services.SpecialtyService;
import guru.springframework5.sfw5bgpetclinic.repositories.SpecialtyRepository;

@Service                     // Wire it up as a Spring Component
@Profile("springdatajpa")    // Only instantiated by Spring if active profile is springdatajpa.
public class SpecialtyServiceSDJpaImpl implements SpecialtyService {

	private final SpecialtyRepository specialtyRepository;

	// -------------------------------------------------------
	// Constructor Injection - To initialize private final attributes above. 
	// -------------------------------------------------------
	public SpecialtyServiceSDJpaImpl (SpecialtyRepository specialtyRepository)  {
		super();
		this.specialtyRepository = specialtyRepository;
	}

	// -------------------------------------------------------
	// MUST SPECIFY PUBLIC ON THESE METHODS.  CLASS DEFAULTS TO PACKAGE-PRIVATE.  
	// INTERACES DEFAULT TO PUBLIC.  THEREFORE, BASESERVICE DECLARATIONS DEFAULT TO
	// PUBLIC AND THESE METHODS THAT IMPLEMENT THEM CANNOT BE LESS THAN PUBLIC 
	// (I.E. THE DEFAULT PACKAGE-PRIVATE).  SO, PUT SPECIFY PUBLIC FOR EACH METHOD. 
	// -------------------------------------------------------

	// -------------------------------------------------------
	// Implementation of SpecialtyService 
	// These are non-standard CrudRepository methods so will 
	// need to update SpecialtyRepository to have this method.
	// -------------------------------------------------------
	// NA
	// -------------------------------------------------------

	// -------------------------------------------------------
	// Implementation of BaseService (extended by SpecialtyService)
	// -------------------------------------------------------

	/**
	 * Save a given entity.  Use the returned instance for further operations as the save operation 
	 * might have changed the entity instance completely.
	 * @param non-null object
	 * @return the saved entity (never null) 
	 */
	@Override
	public Specialty save(Specialty entity) {
		// Specialty has only String member.  Hibernate takes care of savings Sets, 
		// etc. if needed if CascadeType specified to do so.
		return specialtyRepository.save(entity);
	}  // end save

	/**
	 * Returns all instances of the type. 
	 * @return all entities 
	 */
	@Override
	public Set<Specialty> findAll() {
		Set<Specialty> specialties = new HashSet<>();
		// CrudRepository.findAll returns Iterable<T>; So SpecialtyRepository returns Iterable<Specialty>.  
		// Put results into a Set<Specialty>
		specialtyRepository.findAll().forEach(specialties::add);
		return specialties;
	}

	/**
	 * Returns instances of the type with the given Id.
	 * @param id
	 * @return entity of given type and id. 
	 */
	@Override
	public Specialty findById(Long id) {
		
		// SHORT CODE
		return specialtyRepository.findById(id).orElse(null);
		
		// LONG CODE 
		// Optional<Specialty> optionalSpecialty = specialtyRepository.findAllById(id);
		// if (optionalSpecialty.isPresent())   // Return specialty if found 
		//	 return optionalSpecialty.get();
		// return null;                       // Specialty not found    
	}

	/**
	 * Delete the given entity.
	 * @param entity  
	 */
	@Override
	public void delete(Specialty entity) {
		specialtyRepository.delete(entity);
	}

	/**
	 * Delete the entity with the given Id.
	 * @param id must not be null 
	 */
	@Override
	public void deleteById(Long id) {
		specialtyRepository.deleteById(id);
	}

}  // end class SpecialtyServiceSDJpaImpl
