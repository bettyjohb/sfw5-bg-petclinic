//***************************************************************************
//Class:  Pet (model object)  
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

import java.time.LocalDate;

//@Entity 		// #1 - Annotate with @Entity to identify as JPA entity for DB  
public class Pet {

	// -----------------------------------------------
	// Attributes  
	// -----------------------------------------------
	// IF ADD ID, CHECK AUTHOR CLASS TO ADD ID BACK IN 
	//@Id             // #2 - Annotate with @Id to identify as key for Author class
	//@GeneratedValue(strategy = GenerationType.AUTO)  // #3 - DB will generate key 
	//private Long id;		

	private PetType petType;
	private Owner owner;
	private LocalDate birthDate;
	
	// -----------------------------------------------
	// Constructors  
	// -----------------------------------------------

	/**
	 * Default constructor (required of JPA entity objects)
	 */
	public Pet() {
		super();
	}

	/** 
	 * Constructor   
	 * 
	 * #4 - Used for constructor injection.  
	 * 
	 * Do NOT include "id" as parameter.  It is a generated value that 
	 * Hibernate will inject with setter.
  */	 
	public Pet (PetType petType, Owner owner, LocalDate birthDate) {
		super();
		this.petType = petType;
		this.owner = owner;
		this.birthDate = birthDate;
	}  

	// -----------------------------------------------
	// Getters / Setters
	// Used by Spring JPA / Hibernate to do Dependency Injection (DI)
	// if doing setter injection - though constructor injection preferred 
	// -----------------------------------------------

	public PetType getPetType() {
		return petType;
	}
	public void setPetType(PetType petType) {
		this.petType = petType;
	}
	public Owner getOwner() {
		return owner;
	}
	public void setOwner(Owner owner) {
		this.owner = owner;
	}
	public LocalDate getBirthDate() {
		return birthDate;
	}
	public void setBirthDate(LocalDate birthDate) {
		this.birthDate = birthDate;
	}
	
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
		if ( !(o instanceof Pet) )
			return false;
		
		Pet po = (Pet)o;

		// Id (type Long)  
		// This is the primary key, so most important to determine if same object.
//		if (this.id == null) 
//		{
//			// False if only this.id is null. Not equal.
//			if (ao.id != null)
//				return false;
//		} 
//		else if (ao.id == null)
//		{
//			// False if only ao.id is null.
//			if (this.id != null)
//				return false;
//		} 
//		else 
//		{
//			// Both have non-null id to compare!
//			if (!this.id.equals(ao.id))
//				return false;
//		}


		// PetType 
		if (this.petType == null) 
		{
			// False if ONLY this.petType is null.
			if (po.petType != null)
				return false;
		} 
		else if (po.petType == null)
		{
			// False if ONLY po.petType has null name.
			if (this.petType != null)
				return false;
		} 
		else 
		{
			// Both have non-null names to compare!
			if (!this.petType.equals(po.petType))
				return false;
		}

		// Owner
		if (this.owner == null) 
		{
			// False if only this.owner has null name.
			if (po.owner != null)
				return false;
		} 
		else if (po.owner == null)
		{
			// False if only po.owner has null name.
			if (this.owner != null)
				return false;
		} 
		else 
		{
			// Both have non-null names to compare!
			if (!this.owner.equals(po.owner))
				return false;
		}

		// Birth Date
		if (this.birthDate == null) 
		{
			// False if only this.birthDate has null name.
			if (po.birthDate != null)
				return false;
		} 
		else if (po.birthDate == null)
		{
			// False if only po.birthDate has null name.
			if (this.birthDate != null)
				return false;
		} 
		else 
		{
			// Both have non-null names to compare!
			if (!this.birthDate.isEqual(po.birthDate))
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
		int result = 1;
		
		// In this algorithm, based on Joshua Bloch's blog, if an attribute is an 
		// Object (i.e., String) return 0 if null or call hashCode() on it.
		// NOTE:  String.hashCode() returns the same int value for strings of the 
		// same value (i.e., "one" and "one") though they are different actual String objects).
		result = result * prime + ( (petType == null) ? 0 : petType.hashCode());
		result = result * prime + ( (owner == null) ? 0 : owner.hashCode());
		result = result * prime + ( (birthDate == null) ? 0 : birthDate.hashCode());
		
		return result;
	}  // end hashCode()

	/**
	 * Provides a reader friendly, string representation of Person object.   
	 * 
	 * This method overrides the default functionality provided by the 
	 * java.lang.Object.toString() method.  Useful for logging and/or 
	 * debugging. 
	 * 
	 * @return String containing JSON style representation of Pet object. 
	 */
	@Override
	public String toString() {
		return "Pet{" +
	           "petType=" + petType + 
			   ", owner=" + owner + '\'' +
			   ", birthDate=" + birthDate + '\'' +
			   "}";
	}  // end toString()

}  // end class Pet
 

