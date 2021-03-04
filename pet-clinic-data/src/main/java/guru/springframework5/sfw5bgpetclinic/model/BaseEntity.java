//***************************************************************************
//Class:    BaseEntity 	
//Implements:  Serializable (as part of JavaBean / persisting in DB with Hibernate)
//
//Single abstract base model (entity) class from which all entity objects 
//are derived, either directly or indirectly through levels of inheritance. 
//
//BaseEntity provides the following: 
//    - Used by base-level classes providing common functionality that need 
//      only know about BaseEntity objects (not each derived entity).  
//    - Extended by all entity objects persisted to a DB (or even a "key"
//      based collection).  The Id attribute offers a unique identifier to 
//      be used as a primary key. [Can't do it only at base level or same for all 
//      and it relates to identifying a particular object type.] 
// 
//To be a true Java Bean
//  - Implement Serializable (marker base interface w/o methods.  As part of 
//    serializable, all derived class types that will be persisted will provide
//    its own serialVersionUID. 
//  - Have getters / setters 
//  - Default constructor 
//*************************************************************************** 
package guru.springframework5.sfw5bgpetclinic.model;
import java.io.Serializable;

public class BaseEntity implements Serializable {

	/**
	 * Identifier necessary for all serializable objects in order to 
	 * uniquely identify the class during serialization (output) and 
	 * deserialization (input).  If the id at output does not match 
	 * that at input, an exception is thrown due to different class,
	 * 
	 * Note:  Although the Serializable interface is inherited through 
	 * GMSData, the static final UID attribute is not and must be 
	 * defined in each subclass. 
	 */
	private static final long serialVersionUID = 6624281712368887444L;

	// IF ADD ID, CHECK AUTHOR CLASS TO ADD ID BACK IN 
	//@Id             // #2 - Annotate with @Id to identify as key for Author class
	//@GeneratedValue(strategy = GenerationType.AUTO)  // #3 - DB will generate key 

	private Long id;    // Use Long (not primitive long) in case of Hibernate since can be null   

	// -----------------------------------------------
	// Constructors
	// -----------------------------------------------

	/* 
	 * Default constructor to adhere to Java Bean requirements. 
	 */
	public BaseEntity() {
		super();
	}
	
	// -----------------------------------------------
	// Getters / Setters
	// -----------------------------------------------

	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
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
		
		// Works even if "o" is a derived class of Person. 
		if ( !(o instanceof BaseEntity) )
			return false;
		
		BaseEntity bo = (BaseEntity)o;
		
		// Id 
		if (this.id == null) 
		{
			// False if this.id is null, but bo.id is not.
			if (bo.id != null)
				return false;
		} 
		else if (bo.id == null)
		{
			// False if bo.id is null, but this.id is not.
			if (this.id != null)
				return false;
		} 
		else 
		{
			// Both have non-null names to compare!  [0 if equal]
			if (this.id.compareTo(bo.id) != 0)
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
		result = result * prime + ( (id == null) ? 0 : id.intValue());
	
		return result;
	}  // end hashCode()

	/**
	 * Provides a reader friendly, string representation of BaseEntity object.   
	 * 
	 * This method overrides the default functionality provided by the 
	 * java.lang.Object.toString() method.  Useful for logging and/or 
	 * debugging. 
	 * 
	 * @return String containing JSON style representation of BaseEntity object. 
	 */
	@Override
	public String toString() {
		
		return "BaseEntity{" +
		       "id=" + id.toString() + 
			   "}";
	}  // end toString()

}  // end BaseEntity 
