//***************************************************************************
//Interface:    BaseService
//
//Base public interface defining the common methods required by all 
//service interfaces in the Petclinic app.  The focus will be on CRUD 
//related services required by all entities.  Entity specific service  
//interfaces (i.e. OwnerService) will extend this and can include their
//own custom methods (i.e., findByLastName(String name)).    
//
//Because this interface must handle all Entity types it utilizes templates.  
//<T, ID> where T will ultimately be Owner, Pet, etc., and ID the id type (Long). 
//
//Therefore, using this class looks like: 
//    public interface OwnerService extends BaseService<Owner, Long>
//
//The classes that implement these interfaces (IMPLs) are responsible for interacting 
//with the repositories (the specific type, i.e., OwnerRepostory) will depend 
//on the service impl.  More than one impl can exist for each entity type  
//to handle different persistence (i.e., JDBC, JPA, Collection, etc.).  The IMPL
//that is instantiated and injected is determined at runtime depending on the 
//@Qualifier [in the controller being injected] / @Profile [in the service]
//and the applicaction.properities.  (See IMPLs for more detail)    
//***************************************************************************
package guru.springframework5.sfw5bgpetclinic.services;

import java.util.Set;

// Type <T> [i.e. placeholder for Owner, Pet, Vet, etc.].  
// ID [i.e. placeholder for id type - typeically Long in Petclinic app]
public interface BaseService<T, ID> {

	// Create / Update
	
	/**
	 * Save a given entity.  Use the returned instance for further operations as the save operation 
	 * might have changed the entity instance completely.
	 *  
	 * @param non-null object
	 * @return the saved entity (never null) 
	 */
	T save (T object);   

	// Retrieve
	/**
	 * Returns all instances of the type. 
	 * 
	 * @return all entities 
	 */
	Set<T> findAll();
	
	/**
	 * Returns all instances of the type with the given Id.
	 * 
	 * @param id
	 * @return entity of given type and id. 
	 */
	T findById(ID id);

	// Delete 

	/**
	 * Delete the given entity.
	 * 
	 * @param entity  
	 */
	void delete(T object);
	
	/**
	 * Delete the entity with the given Id.
	 * 
	 * @param id must not be null 
	 */
    void deleteById(ID id);
    
}  // end interface BaseService

