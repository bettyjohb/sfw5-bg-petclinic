//***************************************************************************
//Layer:        Service - Generic interface / multiple IMPLs that hide persistence type.  
//Class:        VetServiceSDJpaImpl (SDJpa = Spring Data JPA IMPL) 
//              [Persistence using CrudRepository based repository interfaces (see repositories pkg) to work with Spring Data]
//              [IMPL has knowledge of type of persistence (Map, Spring Data JPA, JDBC, etc.).  Interface does not so limit code to update.]
//Implements:   VetService interface ([1] Defines common interface for VetService;
//                          It does so by extending interface BaseService which 
//                          defines the service method signatures common to all entity
//                          objects.  VetService then adds any methods specific to Vet. 
//                          [2] VetController "has a" VetService interface so can 
//                          inject any IMPL that implements it (i.e. VetServiceMapImpl,
//                          VetServiceJPAImpl, etc.) based on @Profile.  
//Stereotype:   @Service    Component Scan will pick up and instantiate to keep in Spring Context as Spring Bean.
//                          By default, Singleton scope (only 1 instance ever).  Okay - services don't have state.
//
//VetServiceSDJpaImpl is not the type referenced by using classes.  Reference VetService.   
//Storage is in DB (so don't need AbstractMapService type class which held the HashMap for Map version of persistence).    
//Methods here are defined with actual types <Vet, Long>, but mostly call up [super.somemethod()]
//***************************************************************************
//Summary:   
//  Interface: 
//      BaseService     [common method signatures for all entities]
//      <entity>Service [i.e., VetService = = BaseService + Vet specific methods]
//  
//	  Classes (impl are grouped into packages based on type (i.e., map, springdatajpa, jdbc, etc.): 
//       VetServiceSDJpaImpl implements VetService.  [Encapsulates use of repositories to invoke CRUD ops on the DB using Spring Data JPA.] 
//                                                   [Has entity specific CrudRepository interface at attribute (VetRepository) 
//      												   
//       VetServiceMapImpl, VetServiceJDBCImpl, etc. [Can swap IMPL at runtime.]
//
//  Controllers have reference of entity specific service (VetService).  
//  This will be an IMPL determined at runtime by Spring using @Profile and application.properties. 
//***************************************************************************
package guru.springframework5.sfw5bgpetclinic.services.springdatajpa;

import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Profile;
import guru.springframework5.sfw5bgpetclinic.model.Vet;
import guru.springframework5.sfw5bgpetclinic.repositories.VetRepository;
import guru.springframework5.sfw5bgpetclinic.services.VetService;
import java.util.HashSet;
import java.util.Set;

@Service      // Wire it up as Spring component.
@Profile("springdatajpa")      // Only instantiated by Spring if active profile is springdatajpa.  
public class VetServiceSDJpaImpl implements VetService{

	private final VetRepository vetRepository;

	// -------------------------------------------------------
	// Constructor Injection - To initialize private final attributes above. 
	// -------------------------------------------------------
	public VetServiceSDJpaImpl (VetRepository vetRepository)  {
		super();
		this.vetRepository = vetRepository;
	}

	// -------------------------------------------------------
	// MUST SPECIFY PUBLIC ON THESE METHODS.  CLASS DEFAULTS TO PACKAGE-PRIVATE.  
	// INTERACES DEFAULT TO PUBLIC.  THEREFORE, BASESERVICE DECLARATIONS DEFAULT TO
	// PUBLIC AND THESE METHODS THAT IMPLEMENT THEM CANNOT BE LESS THAN PUBLIC 
	// (I.E. THE DEFAULT PACKAGE-PRIVATE).  SO, PUT SPECIFY PUBLIC FOR EACH METHOD. 
	// -------------------------------------------------------

	// -------------------------------------------------------
	// Implementation of VetService 
	// These are non-standard CrudRepository methods so will 
	// need to update VetRepository to have this method.
	// -------------------------------------------------------

	@Override
	public Vet findByLastName(String lastName) {
		return vetRepository.findByLastName(lastName);
    }

	// -------------------------------------------------------
	// Implementation of BaseService (extended by VetService)
	// -------------------------------------------------------

	/**
	 * Save a given entity.  Use the returned instance for further operations as the save operation 
	 * might have changed the entity instance completely.
	 * @param non-null object
	 * @return the saved entity (never null) 
	 */
	@Override
	public Vet save(Vet entity) {
		// Vet is a composite object ("has a" set of Specialties).  JPA / Hibernate handles saving of Specialties. 
		// Map Impl had to do manually. vetRepository.save returns Vet so no translation. 
		return vetRepository.save(entity);
	}  // end save

	/**
	 * Returns all instances of the type. 
	 * @return all entities 
	 */
	@Override
	public Set<Vet> findAll()  {
		Set<Vet> vets = new HashSet<>();
		// CrudRepository.findAll returns Iterable<T>; So VetRepository returns Iterable<Vet>.  
		// Put results into a Set<Vet>
		vetRepository.findAll().forEach(vets::add);
		return vets;
	}

	/**
	 * Returns instances of the type with the given Id.
	 * @param id
	 * @return entity of given type and id. 
	 */
	@Override
	public Vet findById(Long id) {
		
		// SHORT CODE
		return vetRepository.findById(id).orElse(null);
		
		// LONG CODE 
		// Optional<Vet> optionalVet = vetRepository.findAllById(id);
		// if (optionalVet.isPresent())   // Return vet if found 
		//	 return optionalVet.get();
		// return null;                   // vet not found    
	}

	/**
	 * Delete the given entity.
	 * @param entity  
	 */
	@Override
	public void delete(Vet vet) {
		vetRepository.delete(vet);
	}

	/**
	 * Delete the entity with the given Id.
	 * @param id must not be null 
	 */
	@Override
	public void deleteById(Long id) {
		vetRepository.deleteById(id);
	}

}  // end class VetServiceSDJpaImpl
