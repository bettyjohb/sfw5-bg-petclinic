/**
 * Interface:  VisitRepository
 * Extends:    CrudRepository 
 *                 - Has basic create, read, update, delete operation methods
 *                 - To use CrudRepository, will need a DB (SQL, MySQL, MongoDB, H2).
 *                 - Through its CrudRepository implementation, Spring Framework is able to determine 
 *                   from your @ annotations how to map objects to your DB. 
 *                  
 * Provides the interface for performing CRUD ops on the DB for Category entity objects.   
 */
package guru.springframework5.sfw5bgpetclinic.repositories;
import org.springframework.data.repository.CrudRepository;
import guru.springframework5.sfw5bgpetclinic.model.Visit;

public interface VisitRepository extends CrudRepository<Visit, Long> {    // Long is the id type in BaseEntity
	// At this point, no need for JPA Query methods with special find capabilities, etc.  Just stay with default CrudRepository.
}  // end interface VisitRepository 
