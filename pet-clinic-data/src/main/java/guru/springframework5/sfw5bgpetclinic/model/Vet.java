// ***************************************************************************
// Class:    Vet	
// Extends:  Person 
// 
//Model (entity) object representing Vets in the Petclinic system.
//Each Vet can have a set of specialties a/w them that they provide as services for the clinic. 
//This class adheres to the rules of a JavaBean.
// 
// Initially implemented as a POJO (to store in a basic HashMap/other).
//
// Updated to a JPA entity object to be persisted into a database.  Therefore,  
// will require mapping [@Entity, @Id, possibly @Table, @Column, etc.] which is  
// what turns this POJO into a JPA entity.  Also Constructor as preferred injection 
// method. Steps #1-4
//
// To be a true Java Bean, need setter/getter, default constructor, implement Serializable.
//*************************************************************************** 
package guru.springframework5.sfw5bgpetclinic.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinTable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor   // Lombok 
@AllArgsConstructor	 // Lombok
@Getter				 // Lombok
@Setter              // Lombok
@Entity 		       // Identify as JPA entity for DB
@Table(name = "vets")  // optional - Can use Hibernate default
public class Vet extends Person {

	/**
	 * Identifier necessary for all serializable objects in order to 
	 * uniquely identify the class during serialization (output) and 
	 * deserialization (input).  If the id at output does not match 
	 * that at input, an exceptoin is thrown due to different class,
	 * 
	 * Note:  ALthough the Serializable interface is inherited through 
	 * GMSData, the static final UID attribute is not and must be 
	 * defined in each subclass. 
	 */
	private static final long serialVersionUID = 4158981079483919923L;

	/**
	 * Each vet can have associated with them one or more areas of focus.
	 */
	// EVERY VET CAN HAVE MANY SPECIALTY TYPES; AND (BECAUSE ONLY 1 INSTANCE OF EACH SPECIALTY TYPE - like Constant), EACH SPECIALTY BELONG TO MANY VET 
	// 		1 Vet has MANY Specialty a/w it in table.  		(Vet has Hash<Specialty> attribute)
	//      "Single-instance" Specialty can be a/w Many Vet in table.	(BUT Specialty does not have a Vet attrib; just logically related back)
	// Therefore, Many Specialty / Many Vet; This is Vet class, so MamyToMany
	@ManyToMany (fetch = FetchType.EAGER)	// No cascade type; If delete Vet, don't delete the Specialty (shared by all of that specialty)
	@JoinTable(name = "vet_specialties", joinColumns = @JoinColumn(name = "vet_id"), inverseJoinColumns = @JoinColumn(name = "specialty_id"))
	private Set<Specialty> specialties = new HashSet<>();   // HashSet does not allow dups.  Based on equals().  Can have null id's till Save().
															// Equals will allow compare two new specialties with null id. 
															// Null id's considered equal, but then compare rest of vals. 
	
	// -----------------------------------------------
	// Constructors  - LOMBOK generated @NoArgsConstructor
	//				   LOMBOK generated @AllArgsConstructor
	// -----------------------------------------------

//	/**
//	 * Default constructor (required of JPA entity objects) - Lombok generated 
//	 */
//	public Vet() {
//		super();
//	}

	// -----------------------------------------------
	// Public Methods
	// -----------------------------------------------

	/**
	 * Add a specialty to the vet's set of specialties.  
	 * @param Specialty - Specialty to be added.  Can have null id if not saved yet. 
	 * @return true if added; false otherwise. 
	 */
	public boolean add(Specialty specialty) {
		if (specialty == null)
			return false;
		
		// Request to add the Specialty.  If the specialty already exists in the Set (equals() returns true)
		// specialty will not be added (returns false).  
		return this.specialties.add(specialty);
	}

// -----------------------------------------------
// LOMBOK generates @Getters and @Setters 
// -----------------------------------------------
//
//	// -----------------------------------------------
//	// Getters / Setters
//	// Used by Spring JPA / Hibernate to do Dependency Injection (DI)
//	// if doing setter injection - though constructor injection preferred
//	//
//	// Validation is done within validate(), not in setters to adhere to 
//	// JavaBean restrictions (expect setters that return void and cannot 
//	// throw exceptions.  
//	// -----------------------------------------------
//
//	public Set<Specialty> getSpecialties() {
//		return specialties;
//	}
//	
//	public void setSpecialties(Set<Specialty> specialties) {
//		this.specialties = specialties;
//	}
	
	// -----------------------------------------------
	// #5 Methods that override Java default functionality.
	//    Required for JPA / Hibernate and by Sets. 
	// -----------------------------------------------

	/**
	 * Determines if two objects are logically equal.
	 * 
	 * Logically equal means that they have the same "state" (instance 
	 * variables have the same values).       
	 * 
	 * This is needed by Hibernate and Collections (like Sets) to determine equality. 
	 * 
	 * This method overrides that default functionality provided by the 
	 * java.lang.Object.equals(Object) method that determines if the 
	 * object reference variables are the same (i.e. if they reference the
	 * same object in memory).   
	 * 
	 * @return boolean true if logically equal; false otherwise. 
	 */
	public boolean equals (Object o)
	{
		// Check if both object reference variables reference the same object in memory.
		if (o == this)
			return true;
		
		if (o == null)
			return false;
		
		// Works even if "o" is a derived class of Vet. 
		if ( !(o instanceof Vet) )
			return false;
		
		Vet vo = (Vet)o;
		
		// Determine if instance variables maintained by base class are equal.  If not, return false.  
		if (!(super.equals(o)))
			return false;
		
		// -----------------------------------------------------
		// At this point, both 'this' and 'vo' are new OR are existing with same id. 
		// -----------------------------------------------------

		// If same non-null id, equal regardless of remaining since could be update.  
		if (!this.isNew())
			return true;

		// Otherwise, both new (null id), compare any other remaining values of interest. 

		// We made it!!!  Objects are equal!!
		return true;
		
	}  // end equals(Object)

	/**
	 * Calculates the object's hash code.
	 * 
	 * The hash code need not be a specific value.  However, the following must be true:
	 * a) IMPORTANT - If two objects are equal per the equals(Object) method, then calling 
	 * hashCode() on each object MUST return the same int.  
	 * b) While it is NOT required that if two objects are unequal per equals(Object) calling 
	 * hashCode() produces distinct values, doing so will increase performance on 
	 * collections (i.e., a hash table).       
	 * 
	 * This method overrides the default functionality provided by the 
	 * java.lang.Object.hashCode() method.  This method must be overridden any 
	 * time the equals method is overridden.
	 *  
	 * @return int hash code value 
	 */
	public int hashCode()
	{
		final int prime = 17;
		int result = super.hashCode();
		
		// If not new, base hashcode on id only.  For existing (id not null), equals() will return true if id's 
		// are the same; false otherwise.  Therefore, hashCode must return the same value.  However, updates can 
		// have same id for two visits (one with updated values, the other with values from DB).   
		// descriptions. 
		if (!isNew()) {
			return result;  // id belongs to base entity - handled by call to super
		}
		
		// For new (id null), include other values. 

		// In this algorithm, based on Joshua Bloch's blog, if an attribute is an 
		// Object (i.e., String) return 0 if null or call hashCode() on it.
		// NOTE:  String.hashCode() returns the same int value for strings of the 
		// same value (i.e., "one" and "one") though they are different actual String objects).
		result = result * prime + ( (specialties == null) ? 0 : specialties.hashCode());

		return result;
	}  // end hashCode()

	/**
	 * Provides a reader friendly, string representation of Owner object.   
	 * 
	 * This method overrides the default functionality provided by the 
	 * java.lang.Object.toString() method.  Useful for logging and/or 
	 * debugging. 
	 * 
	 * @return String containing JSON style representation of Person object. 
	 */
	@Override
	public String toString() {
		
		return "Vet{" +
				super.toString() +
				"specialties=" + specialties + '\'' +
			   "}";
	}  // end toString()

}  // end class Vet 
