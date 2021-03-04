// ***************************************************************************
// Class:    Owner	
// Extends:  Person 
// 
// Model (entity) object representing Owners in the Petclinic system.
// This class adheres to the rules of a JavaBean.
// 
// Initially implemented as a POJO (to store in a basic HashMap/other).
//
// Updated to a JPA entity object to be persisted into a database.  Therefore,  
// will require mapping [@Entity, @Id, possibly @Table, @Column, etc.] which is  
// what turns this POJO into a JPA entity.  Also Constructor as preferred injection 
// method. Steps #1-4
//
// To be a true Java Bean, need setter/getter, default constructor, implement Serializable.
// *************************************************************************** 
package guru.springframework5.sfw5bgpetclinic.model;

//@Entity 		// #1 - Annotate with @Entity to identify as JPA entity for DB  
public class Owner extends Person {

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
	private static final long serialVersionUID = -4063886627796020648L;

	// -----------------------------------------------
	// Constructors  
	// -----------------------------------------------

	/**
	 * Default constructor (required of JPA entity objects)
	 */
	public Owner() {
		super();
	}

	// -----------------------------------------------
	// Getters / Setters
	// Used by Spring JPA / Hibernate to do Dependency Injection (DI)
	// if doing setter injection - though constructor injection preferred
	//
	// Validation is done within validate(), not in setters to adhere to 
	// JavaBean restrictions (expect setters that return void and cannot 
	// throw exceptions.  
	// -----------------------------------------------

	
	
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
		
		// Works even if "o" is a derived class of Owner. 
		if ( !(o instanceof Owner) )
			return false;
		
		Owner owner = (Owner)o;
		
		// Validate instance variables managed by base class.  If not equal, return false.  
		if (!(super.equals(o)))
			return false;
		
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
		
		return "Owner{" +
				super.toString() +
			   "}";
	}  // end toString()

}  // end class Owner
