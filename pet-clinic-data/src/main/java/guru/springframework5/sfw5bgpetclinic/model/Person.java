// ***************************************************************************
// Class:    Person
// Extends:  BaseEntity
// 
// Provides base class for all model (entity) objects that require personal 
// (person related) information (i.e., first and last name).  This class 
// adheres to the rules of a JavaBean.
// 
// Initially implemented as a POJO (to store in a basic HashMap/other).
//
// Updated to a JPA entity object to be persisted into a database.  Therefore,  
// will require mapping [@MappedSuperclass, @Column, etc.] which is  
// what turns this POJO into a JPA entity.  @Id is in BaseEntity.  Even though 
// MappedSuperclass won't create a table, put column names to use for the derived
// classes that get the attribute. Also, Constructor is preferred injection method. 
//
// To be a true Java Bean, need setter/getter, default constructor, implement Serializable.
// *************************************************************************** 
package guru.springframework5.sfw5bgpetclinic.model;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass 		  
public class Person extends BaseEntity {
	
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
	private static final long serialVersionUID = 223761832424562593L;

	@Column(name = "first_name")	// By default, Hibernate makes camel case into _<lowercase>, so redundant but demos @Column
	private String 	firstName;
	
	@Column(name = "last_name")
	private String lastName;

	// -----------------------------------------------
	// Constructors  
	// -----------------------------------------------

	/**
	 * Default constructor (required of JPA entity objects)
	 */
	public Person() {
		super();

		firstName = null;
		lastName = null;
	}

	/**
	 * Constructor for Person class. (Used for constructor injection #4)
	 *
	 * Do NOT include "id" as parameter.  It is a generated value that 
	 * Hibernate will inject with setter. 
	 * 
	 * @param firstName Person's first name
	 * @param lastName Person's last name
	 *  
	 * @author Betty Jo Booth
	 * @version 1.0
	 * @since 1.0
	 */
	public Person (String firstName, String lastName) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
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

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
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
		
		// Works even if "o" is a derived class of Person. 
		if ( !(o instanceof Person) )
			return false;
		
		Person po = (Person)o;
		
		// Validate instance variables managed by base class.  If not equal, return false.  
		if (!(super.equals(o)))
			return false;

		// First name 
		if (this.firstName == null) 
		{
			// False if only this.firstName has null name.
			if (po.firstName != null)
				return false;
		} 
		else if (po.firstName == null)
		{
			// False if only po.firstName has null name.
			if (this.firstName != null)
				return false;
		} 
		else 
		{
			// Both have non-null names to compare!
			if (!this.firstName.equals(po.firstName))
				return false;
		}

		// Last Name
		if (this.lastName == null) 
		{
			// False if only this.lastName has null name.
			if (po.lastName != null)
				return false;
		} 
		else if (po.lastName == null)
		{
			// False if only ao.lastName has null name.
			if (this.lastName != null)
				return false;
		} 
		else 
		{
			// Both have non-null names to compare!
			if (!this.lastName.equals(po.lastName))
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
		
		// In this algorithm, based on Joshua Bloch's blog, if an attribute is an 
		// Object (i.e., String) return 0 if null or call hashCode() on it.
		// NOTE:  String.hashCode() returns the same int value for strings of the 
		// same value (i.e., "one" and "one") though they are different actual String objects).
		result = result * prime + ( (firstName == null) ? 0 : firstName.hashCode());
		result = result * prime + ( (lastName == null) ? 0 : lastName.hashCode());
	
		return result;
	}  // end hashCode()

	/**
	 * Provides a reader friendly, string representation of Person object.   
	 * 
	 * This method overrides the default functionality provided by the 
	 * java.lang.Object.toString() method.  Useful for logging and/or 
	 * debugging. 
	 * 
	 * @return String containing JSON style representation of Person object. 
	 */
	@Override
	public String toString() {
		return "Person{" +
	           super.toString() +
	           "firstName=" + firstName + 
			   ", lastName=" + lastName + '\'' + 
			   "}";
	}  // end toString()

}  // end class Person 
