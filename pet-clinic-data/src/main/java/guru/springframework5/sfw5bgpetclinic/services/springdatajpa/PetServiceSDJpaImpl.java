//***************************************************************************
//Layer:        Service - Generic interface / multiple IMPLs that hide persistence type.  
//Class:        PetServiceSDJpaImpl (SDJpa = Spring Data JPA IMPL) 
//              [Persistence using CrudRepository based repository interfaces (see repositories pkg) to work with Spring Data]
//              [IMPL has knowledge of type of persistence (Map, Spring Data JPA, JDBC, etc.).  Interface does not so limit code to update.]
//Implements:   PetService interface ([1] Defines common interface for PetService;
//                      It does so by extending interface BaseService which 
//                      defines the service method signatures common to all entity
//                      objects.  PetService then adds any methods specific to Pet. 
//                      [2] PetController "has a" PetService interface so can 
//                      inject any IMPL that implements it (i.e. PetServiceMapImpl,
//                      PetServiceJPAImpl, etc.) based on @Profile.  
//Stereotype:   @Service  Component Scan will pick up and instantiate to keep in Spring Context as Spring Bean.
//                      By default, Singleton scope (only 1 instance ever).  Okay - services don't have state.
//
//PetServiceSDJpaImpl is not the type referenced by using classes.  Reference PetService.   
//Storage is in DB (so don't need AbstractMapService type class which held the HashMap for Map version of persistence).    
//Methods here are defined with actual types <Pet, Long>, but mostly call up [super.somemethod()]
//***************************************************************************
//Summary:   
//Interface: 
//  BaseService     [common method signatures for all entities]
//  <entity>Service [i.e., PetService = = BaseService + Pet specific methods]
//
//	  Classes (impl are grouped into packages based on type (i.e., map, springdatajpa, jdbc, etc.): 
//   PetServiceSDJpaImpl implements PetService.  [Encapsulates use of repositories to invoke CRUD ops on the DB using Spring Data JPA.] 
//                                                       [Has entity specific CrudRepository interface at attribute (PetRepository) 
//  												   
//   PetServiceMapImpl, PetServiceJDBCImpl, etc. [Can swap IMPL at runtime.]
//
//Controllers have reference of entity specific service (PetService).  
//This will be an IMPL determined at runtime by Spring using @Profile and application.properties. 
//***************************************************************************
package guru.springframework5.sfw5bgpetclinic.services.springdatajpa;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import java.util.HashSet;
import java.util.Set;

import guru.springframework5.sfw5bgpetclinic.model.Pet;
import guru.springframework5.sfw5bgpetclinic.services.PetService;
import guru.springframework5.sfw5bgpetclinic.repositories.PetRepository;

@Service                     // Wire it up as a Spring Component
@Profile("springdatajpa")    // Only instantiated by Spring if active profile is springdatajpa.
public class PetServiceSDJpaImpl implements PetService {

	private final PetRepository petRepository;

	// -------------------------------------------------------
	// Constructor Injection - To initialize private final attributes above. 
	// -------------------------------------------------------
	public PetServiceSDJpaImpl (PetRepository petRepository)  {
		super();
		this.petRepository = petRepository;
	}

	// -------------------------------------------------------
	// MUST SPECIFY PUBLIC ON THESE METHODS.  CLASS DEFAULTS TO PACKAGE-PRIVATE.  
	// INTERACES DEFAULT TO PUBLIC.  THEREFORE, BASESERVICE DECLARATIONS DEFAULT TO
	// PUBLIC AND THESE METHODS THAT IMPLEMENT THEM CANNOT BE LESS THAN PUBLIC 
	// (I.E. THE DEFAULT PACKAGE-PRIVATE).  SO, PUT SPECIFY PUBLIC FOR EACH METHOD. 
	// -------------------------------------------------------

	// -------------------------------------------------------
	// Implementation of PetService 
	// These are non-standard CrudRepository methods so will 
	// need to update PetRepository to have this method.
	// -------------------------------------------------------
	// NA
	// -------------------------------------------------------

	// -------------------------------------------------------
	// Implementation of BaseService (extended by PetService)
	// -------------------------------------------------------

	/**
	 * Save a given entity.  Use the returned instance for further operations as the save operation 
	 * might have changed the entity instance completely.
	 * @param non-null object
	 * @return the saved entity (never null) 
	 */
	@Override
	public Pet save(Pet entity) {
		// Pet has only String member.  Hibernate takes care of savings Sets, 
		// etc. if needed if CascadeType specified to do so.
		return petRepository.save(entity);
	}  // end save

	/**
	 * Returns all instances of the type. 
	 * @return all entities 
	 */
	@Override
	public Set<Pet> findAll() {
		Set<Pet> pets = new HashSet<>();
		// CrudRepository.findAll returns Iterable<T>; So PetRepository returns Iterable<Pet>.  
		// Put results into a Set<Pet>
		petRepository.findAll().forEach(pets::add);
		return pets;
	}

	/**
	 * Returns instances of the type with the given Id.
	 * @param id
	 * @return entity of given type and id. 
	 */
	@Override
	public Pet findById(Long id) {
		
		// SHORT CODE
		return petRepository.findById(id).orElse(null);
		
		// LONG CODE 
		// Optional<Pet> optionalPet = petRepository.findAllById(id);
		// if (optionalPet.isPresent())   // Return pet if found 
		//	 return optionalPet.get();
		// return null;                   // pet not found    
	}

	/**
	 * Delete the given entity.
	 * @param entity  
	 */
	@Override
	public void delete(Pet entity) {
		petRepository.delete(entity);
	}

	/**
	 * Delete the entity with the given Id.
	 * @param id must not be null 
	 */
	@Override
	public void deleteById(Long id) {
		petRepository.deleteById(id);
	}

}  // end class PetServiceSDJpaImpl
