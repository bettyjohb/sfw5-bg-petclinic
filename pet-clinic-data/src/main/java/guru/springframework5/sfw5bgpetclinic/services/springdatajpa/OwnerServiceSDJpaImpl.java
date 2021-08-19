//***************************************************************************
//Layer:        Service - Generic interface / multiple IMPLs that hide persistence type.  
//Class:        OwnerServiceSDJpaImpl (SDJpa = Spring Data JPA IMPL) 
//              [Persistence using CrudRepository based repository interfaces (see repositories pkg) to work with Spring Data]
//              [IMPL has knowledge of type of persistence (Map, Spring Data JPA, JDBC, etc.).  Interface does not so limit code to update.]
//Implements:   OwnerService interface ([1] Defines common interface for OwnerService;
//                            It does so by extending interface BaseService which 
//                            defines the service method signatures common to all entity
//                            objects.  OwnerService then adds any methods specific to Owner. 
//                            [2] OwnerController "has a" OwnerService interface so can 
//                            inject any IMPL that implements it (i.e. OwnerServiceMapImpl,
//                            OwnerServiceJPAImpl, etc.) based on @Profile.  
//Stereotype:   @Service    Component Scan will pick up and instantiate to keep in Spring Context as Spring Bean.
//                            By default, Singleton scope (only 1 instance ever).  Okay - services don't have state.
// 
// OwnerServiceSDJpaImpl is not the type referenced by using classes.  Reference OwnerService.   
// Storage is in DB (so don't need AbstractMapService type class which held the HashMap for Map version of persistence).    
// Methods here are defined with actual types <Owner, Long>, but mostly call up [super.somemethod()]
//***************************************************************************
//Summary:   
//    Interface: 
//        BaseService     [common method signatures for all entities]
//        <entity>Service [i.e., OwnerService = = BaseService + Owner specific methods]
//    
//	  Classes (impl are grouped into packages based on type (i.e., map, springdatajpa, jdbc, etc.): 
//         OwnerServiceSDJpaImpl implements OwnerService.  [Encapsulates use of repositories to invoke CRUD ops on the DB using Spring Data JPA.] 
//                                                         [Has entity specific CrudRepository interface at attribute (OwnerRepository) 
//        												   
//         OwnerServiceMapImpl, OwnerServiceJDBCImpl, etc. [Can swap IMPL at runtime.]
//
//    Controllers have reference of entity specific service (OwnerService).  
//    This will be an IMPL determined at runtime by Spring using @Profile and application.properties. 
//***************************************************************************
package guru.springframework5.sfw5bgpetclinic.services.springdatajpa;

import java.util.HashSet;
import java.util.Set;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import guru.springframework5.sfw5bgpetclinic.model.Owner;
import guru.springframework5.sfw5bgpetclinic.repositories.OwnerRepository;
import guru.springframework5.sfw5bgpetclinic.services.OwnerService;

@Service      // Wire it up as Spring component.
@Profile("springdatajpa")      // If not specified as active profile, will skip over.  
public class OwnerServiceSDJpaImpl implements OwnerService {

	private final OwnerRepository ownerRepository;
	// He added Pet and PetType repositories
	
	// -------------------------------------------------------
	// Constructor Injection - To initialize private final attributes above. 
	// -------------------------------------------------------
	public OwnerServiceSDJpaImpl (OwnerRepository ownerRepository)  {
		super();
		this.ownerRepository = ownerRepository;
	}

	// -------------------------------------------------------
	// MUST SPECIFY PUBLIC ON THESE METHODS.  CLASS DEFAULTS TO PACKAGE-PRIVATE.  
	// INTERACES DEFAULT TO PUBLIC.  THEREFORE, BASESERVICE DECLARATIONS DEFAULT TO
	// PUBLIC AND THESE METHODS THAT IMPLEMENT THEM CANNOT BE LESS THAN PUBLIC 
	// (I.E. THE DEFAULT PACKAGE-PRIVATE).  SO, PUT SPECIFY PUBLIC FOR EACH METHOD. 
	// -------------------------------------------------------

	// -------------------------------------------------------
	// Implementation of OwnerService 
	// These are non-standard CrudRepository methods so will 
	// need to update OwnerRepository to have this method. 
	// -------------------------------------------------------
	@Override
	public Owner findByLastName(String lastName) {
		return ownerRepository.findByLastName(lastName);
    }

	// -------------------------------------------------------
	// Implementation of BaseService (extended by OwnerService)
	// -------------------------------------------------------

	/**
	 * Save a given entity.  Use the returned instance for further operations as the save operation 
	 * might have changed the entity instance completely.
	 * @param non-null object
	 * @return the saved entity (never null) 
	 */
	@Override
	public Owner save(Owner entity) {
		// Owner is a composite object ("has a" set of Pets).  JPA / Hibernate handles saving of Pets. 
		// Map Impl had to do manually. ownerRepository.save returns Owner so no translation. 
		return ownerRepository.save(entity);
	}  // end save

	/**
	 * Returns all instances of the type. 
	 * @return all entities 
	 */
	@Override
	public Set<Owner> findAll()  {
		Set<Owner> owners = new HashSet<>();
		// CrudRepository.findAll returns Iterable<T>; So OwnerRepository returns Iterable<Owner>.  
		// Put results into a Set<Owner>
		ownerRepository.findAll().forEach(owners::add);
		return owners;
	}

	/**
	 * Returns instances of the type with the given Id.
	 * @param id
	 * @return entity of given type and id. 
	 */
	@Override
	public Owner findById(Long id) {
		
		// SHORT CODE
		return ownerRepository.findById(id).orElse(null);
		
		// LONG CODE 
		// Optional<Owner> optionalOwner = ownerRepository.findAllById(id);
		// if (optionalOwner.isPresent())   // Return owner if found 
		//	 return optionalOwner.get();
		// return null;                     // owner not found    
	}

	/**
	 * Delete the given entity.
	 * @param entity  
	 */
	@Override
	public void delete(Owner owner) {
		ownerRepository.delete(owner);
	}

	/**
	 * Delete the entity with the given Id.
	 * @param id must not be null 
	 */
	@Override
	public void deleteById(Long id) {
		ownerRepository.deleteById(id);
	}

}  // end OwnerServiceSDJpaImpl
