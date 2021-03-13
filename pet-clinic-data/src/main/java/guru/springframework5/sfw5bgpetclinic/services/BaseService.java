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
//    public interface OwnerService extends BaseService<Owner, Long> {
//
//    public class OwnerServiceMapImpl extends AbstractMapService<Owner, Long> 
//                                       implements OwnerService {
//***************************************************************************
//Why OrderServiceMapIMpl class extends AbstractMapService class .... and 
//    OwnerService interface implements BaseService interface 
//
//  - BaseService defines the contract of the methods that will be available.  
//    BaseService has the common methods for all entities (so we don't duplicate them for 
//    each entity type - i.e., OwnerService, PetService, etc. all define save(object) is BAD).
//    OrderService adds any additional methods specific to Owner.  
//    Therefore, the main goal of the base and entity level interfaces is the 
//    contract of methods.  If you want to add a new "find" method, can add to 
//    BaseService then all entity service interfaces (OrderService) inherit automagically. 
//    Then, any IMPLs would haev to write.
//    
//    The entity service interfaces are the ones the controllers have.  For example, 
//    OrderController has a OrderService interface type.  It has all method in BaseService
//    plus additional method found in OrderService.  Then the correct IMPL is injected based 
//    on @Profile (where each entity has an IMPL for the type of persistence (JPA, MAP, etc.).
//    That is determined at runtime by looking at @Profile and application.properties. 
//
//  - AbstractMapService is not referenced by other classes ("has a"). What it does is 
//    provides the primary persistence type (i.e., HashMap<T, ID>) and generic functionality 
//    implemented in its methods. All of its methods are implemented.  The class is abstract
//    only to prevent it being instantiated since it does not have a specific entity type. 
//
//  - Entity specific service impls come in here...
//    OrderServiceMapImpl extends AbstractMapService and implements OrderService.
//    This class' method bodies call super().someMethod to utilize AbstractMapService
//    to do work. [Note:  If call this.someMethod will recursively call at same level.  
//    Need to call up so does not loop (call on itself, in turn calls on itself, etc.]   
//    Note:
//    When this class extends AbstractMapService it provides <entity, id type> [i.e.,
//    <Order, Long> if OrderServiceMapImpl.
//    This class implements methods of OwnerService interface (and its BaseService interface).
//    It specifies <Order, Long> to define concrete types to itself and AbstractMapService 
//    when an instance is created.  Therefore, AbstractMapService's HashMap will only ever 
//    hold one entity type - - the one specified in <entity type, ID> like <Owner, Long>.  
//       
// Therefore, Controllers have their respective interfaces (PetService, etc.) which 
// implement BaseService just so define common interface in one place (not in each 
// entity) so stays consistent.  Use @Qualifier to specify exact service (if one IMPL)
// or family of service IMPL (if many - i.e., JDBCImpl, JPAImpl, etc.).  
// 
// Service has @Service which can be name for single service or more generic group name, 
// Service also has @Profile which gives specific IMPL to pull in if matches 
// application.properies.  To change DB type, make other single AbstractJPAService 
// and corresponding IMPLs (i.e., OwnerServiceJPAImpl, etc.).  Then change with @Profile 
// w/o compile. 
//
// No other class references the Abstract or IMPL classes.  They only reference the 
// interfaces so can define IMPL at Runtime with hte purpose of changing DB type 
// (map to jpa etc.).
//***************************************************************************
package guru.springframework5.sfw5bgpetclinic.services;
import java.util.Set;

// Type <T> [i.e. placeholder for Owner, Pet, Vet, etc.].  
// ID [i.e. placeholder for id type - typically Long in Petclinic app]
public interface BaseService<T, ID> {

	// ------------------------------------------------
	// NOTE:  BY DEFAULT, INTERFACE METHODS ARE PUBLIC!  
	// ------------------------------------------------
	
	/**
	 * Save a given entity.  Use the returned instance for further operations as the save operation 
	 * might have changed the entity instance completely.
	 * @param non-null object
	 * @return the saved entity (never null) 
	 */
	T save (T object);   

	/**
	 * Returns all instances of the type. 
	 * @return all entities 
	 */
	Set<T> findAll();
	
	/**
	 * Returns all instances of the type with the given Id.
	 * @param id
	 * @return entity of given type and id. 
	 */
	T findById(ID id);

   /**
	 * Delete the given entity.
	 * @param entity  
	 */
	void delete(T object);
	
	/**
	 * Delete the entity with the given Id.
	 * @param id must not be null 
	 */
    void deleteById(ID id);
    
}  // end interface BaseService

