//***************************************************************************
//Class:        AbstractMapService
//Extends:      None
//Implements:   None
//
//This is not the type referenced by using classes.  It just provides the common 
//code shared by all Map IMPLs that extend it. [The methods provided essentially 
//match those in BaseService interface - though interface not implemented here.]
//The using classes keep a reference to <Entity>Service interface (i.e., OrderService)
//and IMPL is injected at runtime due to @Profile. 
//
//This class also keeps the generic HashMap<T,ID> for storing entities by Key, Value.
//NOTE:  Once a class extends (i.e., OrderServiceMapImpl), when instantiated
//       only entities of that type will ever be in that instance's HashMap. 
//       (Note:  Derived classes override methods with 
//               someMethod(Order, Long) that calls up to super.someMethod(T,ID).
//
//Focus will be on CRUD related services for entities.  This layer is 
//also responsible for interacting with the Repository.  The type of 
//repository (JDBC, JPA, Collection, etc.) is determined at runtime depending 
//on the IMPL that is injected by Spring based on @Qualifier [in controller] / 
//@Profile and @Service [in service].
//
// Although it implements its methods, they are generic template <T, ID>
// so make abstract to force IMPLs like OwnerServiceMapImpl <Owner, Long>
//***************************************************************************
//*** SEE BASESERVICE.JAVA FOR FULL EXPLANATION FOR WHY WE HAVE:
//***************************************************************************
//Why <Entity>ServiceMapIMpl class extends AbstractMapService class .... and 
//    <Entity>Service interface implements BaseService interface 
//
//    Interface: 
//        BaseService     [common method signatures for all entities]
//        <entity>Service [i.e., OrderService = = BaseService + Order specific methods]
//    
//	  Classes:
//         AbstractMapService [Not referenced by any class.  Has common methods implemented.] 
//         OrderServiceMapImpl extends AbstractMapService and implements OrderService.
//
//    Controllers have reference of entity specific service (OrderService).  
//    This will be an IMPL determined at runtime by @Profile and application.properties. 
//***************************************************************************
package guru.springframework5.sfw5bgpetclinic.services.map;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import guru.springframework5.sfw5bgpetclinic.model.BaseEntity;

//T can be BaseEntity or anything that extends it - Important for save() so be able to use getId(). 
//ID can be Long or anything that extends it for any Map IMPLs - Important for the private method getNextId().
public abstract class AbstractMapService<T extends BaseEntity, ID extends Long> {    
	
	// Note: When each entity service implements (i.e., OwnerServiceMapImpl), that IMPL 
	// will work with only that entity (i.e., Owner), so HashMap will only contain that 
	// entity (i.e, Owners) since declare class as MyEntityServiceMapImpl <MyEntityType, MyKeyType> 
	// [i.e., OwnerServiceMapImpl<Owner, Long>.
	protected Map<Long, T> map = new HashMap<>();  // ID is made Long for save() call to getId(). T is Pet, Vet, Owner, etc. 

	// -------------------------------------------
	// BY DEFAULT, THESE METHODS ARE PACKAGE-PRIVATE.
	// -------------------------------------------
	
	// Create / Update

	/**
	 * Save a given entity.  If id of object is null, generate and create new object; otherwise update existing. 
	 * Calling source should use the returned instance for further operations as the save operation might have 
	 * changed the entity instance completely.
	 *  
	 * @param non-null object (BaseEntity or extension so has getId()
	 * @return the saved entity (never null) 
	 */
	T save (T object) {
		// Check for an id.  If none, generate. 
		if (object != null) {               
			if (object.getId() == null)  {   
				object.setId(getNextId());  // No ID, so generate ID - creating new object
			}
			// If no object with that key yet, will add.  If existing key (id), will replace with new object.  Returns ref to prev obj a/w key so ignore.
			map.put(object.getId(), object);
		} else {
			throw new RuntimeException("Object cannot be null");
		}
		
		// map.put(id, object) returns the last object a/w a given id.  Therefore, don't return 
		// the result of map.put.  Return the object passed in. 
		return object;
	}

	// Retrieve
	/**
	 * Returns all instances of the type in the HashSet.
	 * @return New HashSet containing all entities 
	 */
	Set <T> findAll() {
		return new HashSet<>(map.values());  // HashMap is key based; HashSet is NOT key based
	}

	/**
	 * Returns all instances of the type with the given Id.
	 * 
	 * @param id
	 * @return entity of given type and id. 
	 */
	T findById(ID id) {
		return map.get(id);  // gets object with key matching given id
	}

	// Delete 

	/**
	 * Delete the given entity.
	 * 
	 * @param entity  
	 */
	void delete(T object) {
		// HashMap.entrySet() returns a Set of Map.Entry type (which holds key and value). 
		// So, on a Map.entry you can call getKey() and getValue().
		// Note that the entry set refers back to the orig HashMap when "remove" 
		// so will removeIf removes from actual HashMap. 
		// [Also recall, we provided "logical" equals() for our entity objects.]  
		// Therefore, removesIf removes "entry" if entry.getValue().equals(object)
		// [meaning is logically equal - - has same values (not reference)].
		// It looks at each entry in entrySet to see if matches.  
		// Note:  -> is lambda expression [ (0+ params) -> function ]
		//        In this form, may cause to look through all values - (still learning).  
		//        Requires entities to have proper equals method. 
		map.entrySet().removeIf(entry -> entry.getValue().equals(object));
	}

	/**
	 * Delete the entity with the given Id.
	 * 
	 * @param id must not be null 
	 */
	void deleteById(ID id)  {
		map.remove(id);
	}

	private Long getNextId() {
		Long nextId = null;
		try {
			nextId = Collections.max(map.keySet()) + 1;   // If 1+ entities exist, take greatest Id and add 1 for next id
		} catch (NoSuchElementException e) {
			nextId = 1L;                                  // If no entities, exception.  Return 1L as first id. 
		}
		
		return nextId;
	}
}  // end AbstractMapService

