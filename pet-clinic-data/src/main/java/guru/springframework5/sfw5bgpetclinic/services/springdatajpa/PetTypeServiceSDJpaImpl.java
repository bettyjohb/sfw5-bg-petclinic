//***************************************************************************
//Layer:        Service - Generic interface / multiple IMPLs that hide persistence type.  
//Class:        PetTypeServiceSDJpaImpl (SDJpa = Spring Data JPA IMPL) 
//              [Persistence using CrudRepository based repository interfaces (see repositories pkg) to work with Spring Data]
//              [IMPL has knowledge of type of persistence (Map, Spring Data JPA, JDBC, etc.).  Interface does not so limit code to update.]
//Implements:   PetTypeService interface ([1] Defines common interface for PetTypeService;
//                        It does so by extending interface BaseService which 
//                        defines the service method signatures common to all entity
//                        objects.  PetTypeService then adds any methods specific to PetType. 
//                        [2] PetTypeController "has a" PetTypeService interface so can 
//                        inject any IMPL that implements it (i.e. PetTypeServiceMapImpl,
//                        PetTypeServiceJPAImpl, etc.) based on @Profile.  
//Stereotype:   @Service  Component Scan will pick up and instantiate to keep in Spring Context as Spring Bean.
//                        By default, Singleton scope (only 1 instance ever).  Okay - services don't have state.
//
//PetTypeServiceSDJpaImpl is not the type referenced by using classes.  Reference PetTypeService.   
//Storage is in DB (so don't need AbstractMapService type class which held the HashMap for Map version of persistence).    
//Methods here are defined with actual types <PetType, Long>, but mostly call up [super.somemethod()]
//***************************************************************************
//Summary:   
//Interface: 
//    BaseService     [common method signatures for all entities]
//    <entity>Service [i.e., PetTypeService = = BaseService + PetType specific methods]
//
//	  Classes (impl are grouped into packages based on type (i.e., map, springdatajpa, jdbc, etc.): 
//     PetTypeServiceSDJpaImpl implements PetTypeService.  [Encapsulates use of repositories to invoke CRUD ops on the DB using Spring Data JPA.] 
//                                                         [Has entity specific CrudRepository interface at attribute (PetTypeRepository) 
//    												   
//     PetTypeServiceMapImpl, PetTypeServiceJDBCImpl, etc. [Can swap IMPL at runtime.]
//
//Controllers have reference of entity specific service (PetTypeService).  
//This will be an IMPL determined at runtime by Spring using @Profile and application.properties. 
//***************************************************************************
package guru.springframework5.sfw5bgpetclinic.services.springdatajpa;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import java.util.HashSet;
import java.util.Set;
import guru.springframework5.sfw5bgpetclinic.model.PetType;
import guru.springframework5.sfw5bgpetclinic.repositories.PetTypeRepository;
import guru.springframework5.sfw5bgpetclinic.services.PetTypeService;

@Service                     // Wire it up as a Spring Component
@Profile("springdatajpa")    // Only instantiated by Spring if active profile is springdatajpa.
public class PetTypeServiceSDJpaImpl implements PetTypeService {

	private final PetTypeRepository petTypeRepository;

	// -------------------------------------------------------
	// Constructor Injection - To initialize private final attributes above. 
	// -------------------------------------------------------
	public PetTypeServiceSDJpaImpl (PetTypeRepository petTypeRepository)  {
		super();
		this.petTypeRepository = petTypeRepository;
	}

	// -------------------------------------------------------
	// MUST SPECIFY PUBLIC ON THESE METHODS.  CLASS DEFAULTS TO PACKAGE-PRIVATE.  
	// INTERACES DEFAULT TO PUBLIC.  THEREFORE, BASESERVICE DECLARATIONS DEFAULT TO
	// PUBLIC AND THESE METHODS THAT IMPLEMENT THEM CANNOT BE LESS THAN PUBLIC 
	// (I.E. THE DEFAULT PACKAGE-PRIVATE).  SO, PUT SPECIFY PUBLIC FOR EACH METHOD. 
	// -------------------------------------------------------

	// -------------------------------------------------------
	// Implementation of PetTypeService 
	// These are non-standard CrudRepository methods so will 
	// need to update PetTypeRepository to have this method.
	// -------------------------------------------------------
	// NA
	// -------------------------------------------------------

	// -------------------------------------------------------
	// Implementation of BaseService (extended by PetTypeService)
	// -------------------------------------------------------

	/**
	 * Save a given entity.  Use the returned instance for further operations as the save operation 
	 * might have changed the entity instance completely.
	 * @param non-null object
	 * @return the saved entity (never null) 
	 */
	@Override
	public PetType save(PetType entity) {
		// PetType has only String member.  Hibernate takes care of savings Sets, 
		// etc. if needed if CascadeType specified to do so.
		return petTypeRepository.save(entity);
	}  // end save

	/**
	 * Returns all instances of the type. 
	 * @return all entities 
	 */
	@Override
	public Set<PetType> findAll() {
		Set<PetType> petTypes = new HashSet<>();
		// CrudRepository.findAll returns Iterable<T>; So PetTypeRepository returns Iterable<PetType>.  
		// Put results into a Set<PetType>
		petTypeRepository.findAll().forEach(petTypes::add);
		return petTypes;
	}

	/**
	 * Returns instances of the type with the given Id.
	 * @param id
	 * @return entity of given type and id. 
	 */
	@Override
	public PetType findById(Long id) {
		
		// SHORT CODE
		return petTypeRepository.findById(id).orElse(null);
		
		// LONG CODE 
		// Optional<PetType> optionalPetType = petTypeRepository.findAllById(id);
		// if (optionalPetType.isPresent())   // Return petType if found 
		//	 return optionalPetType.get();
		// return null;                       // petType not found    
	}

	/**
	 * Delete the given entity.
	 * @param entity  
	 */
	@Override
	public void delete(PetType entity) {
		petTypeRepository.delete(entity);
	}

	/**
	 * Delete the entity with the given Id.
	 * @param id must not be null 
	 */
	@Override
	public void deleteById(Long id) {
		petTypeRepository.deleteById(id);
	}

}  // end class PetTypeServiceSDJpaImpl
