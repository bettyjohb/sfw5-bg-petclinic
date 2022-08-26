//***************************************************************************
//Class:  PetType (model object)  
//Extends: BaseEntity
//
//Model (entity) object representing PetTypes in the Petclinic system.
//This class adheres to the rules of a JavaBean.
//
//Initially implemented as a POJO (to store in a basic HashMap/other).
//
//Updated to a JPA entity object to be persisted into a database.  Therefore,  
//will require mapping [@Entity, @Id, possibly @Table, @Column, etc.] which is  
//what turns this POJO into a JPA entity.  Also Constructor as preferred injection 
//method. Steps #1-4
//
//To be a true Java Bean, need setter/getter, default constructor, implement Serializable.
//*************************************************************************** 
package guru.springframework5.sfw5bgpetclinic.model;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor   // Lombok 
@Getter				 // Lombok
@Setter              // Lombok
@Entity 		// Identify as JPA entity for DB
@Table(name = "pet_types")
public class PetType extends BaseEntity {

	// -----------------------------------------------
	// Attributes  
	// -----------------------------------------------
	/**
	 * Identifier necessary for all serializable objects in order to 
	 * uniquely identify the class during serialization (output) and 
	 * deserialization (input).  If the id at output does not match 
	 * that at input, an exceptoin is thrown due to different class,
	 * 
	 * Note:  ALthough the Serializable interface is inherited through 
	 * GMSData, the static final UID attribute is not and must be 
	 * defined in each subclsas. 
	 */
	private static final long serialVersionUID = -1517377308231321732L;
	
	@Column(name = "name")
	private String name;

	// -----------------------------------------------
	// Constructors  - LOMBOK generated @NoArgsConstructor
	//				   LOMBOK generated @AllArgsConstructor
	// -----------------------------------------------

//	/**
//	 * Default constructor (required of JPA entity objects)
//	 */
//	public PetType() {
//		super();
//		this.name = null;
//	}
//
	/** 
	 * Constructor - Don't use Lombok generated so do @Builder. 
	 * Used for constructor injection.  
	 * Do NOT include "id" as parameter.  It is generated; Hibernate inject with setter.
     */	 
	@Builder // ONLY PUT @BUILDER on CHILD class (not parent Person).  I don't pass ID - - generated. 
	public PetType (String name) {
		super();
		this.name = name;
	}  

// -----------------------------------------------
// LOMBOK generates @Getters and @Setters 
// -----------------------------------------------
//
//	// -----------------------------------------------
//	// Getters / Setters
//	// Used by Spring JPA / Hibernate to do Dependency Injection (DI)
//	// if doing setter injection - though constructor injection preferred 
//	// -----------------------------------------------
//
//	public String getName() {
//		return name;
//	}
//
//	public void setName(String name) {
//		this.name = name;
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
		
		// Works even if "o" is a derived class of Pet. 
		if ( !(o instanceof PetType) )
			return false;
		
		PetType po = (PetType)o;

		// Determine if instance variables maintained by base class are equal.  If not, return false.  
		if (!(super.equals(o)))
			return false;

		// -----------------------------------------------------
		// At this point, both 'this' and 'vo' are new OR are existing with same id. 
		// -----------------------------------------------------

		// If same non-null id, equal regardless of remaining since could be update.  
		if (!this.isNew())
			return true;

		// Otherwise, both new (null id), compare remaining values. 

		// Name 
		if (this.name == null) 
		{
			// False if ONLY this.name is null.
			if (po.name != null)
				return false;
		} 
		else if (po.name == null)
		{
			// False if ONLY po.name is null. 
			if (this.name != null)
				return false;
		} 
		else 
		{
			// Both have non-null names to compare!
			if (!this.name.equals(po.name))
				return false;
		}

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
		result = result * prime + ( (name == null) ? 0 : name.hashCode());
		
		return result;
	}  // end hashCode()

	/**
	 * Provides a reader friendly, string representation of PetType object.   
	 * 
	 * This method overrides the default functionality provided by the 
	 * java.lang.Object.toString() method.  Useful for logging and/or 
	 * debugging. 
	 * 
	 * @return String containing JSON style representation of Pet object. 
	 */
	@Override
	public String toString() {
		return name;
	}  // end toString()

}  // end class PetType
