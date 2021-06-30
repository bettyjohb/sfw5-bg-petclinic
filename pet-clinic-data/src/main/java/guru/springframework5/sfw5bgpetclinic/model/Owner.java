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

import java.util.Set;

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


	// Typically, I place these at Person level.  However, we are not managing HR here, so 
	// Vet is a Person (with first and last name), but does not need address information.
	private String 	address,
					city,
					telephone;
	
	private Set<Pet> pets;    // Owners can have 1 or more pets. 

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

	public String getAddress() {
		return address;
	}
	
	public void setAddress(String address) {
		this.address = address;
	}
	
	public String getCity() {
		return city;
	}
	
	public void setCity(String city) {
		this.city = city;
	}
	
	public String getTelephone() {
		return telephone;
	}
	
	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}
	
	public Set<Pet> getPets() {
		return pets;
	}
	
	public void setPets(Set<Pet> pets) {
		this.pets = pets;
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
		
		// Works even if "o" is a derived class of Owner. 
		if ( !(o instanceof Owner) )
			return false;
		
		Owner owner = (Owner)o;
		
		// Validate instance variables managed by base class.  If not equal, return false.  
		if (!(super.equals(o)))
			return false;
		
		// Address
		if (this.address == null) 
		{
			// False if ONLY this.address is null.
			if (owner.address != null)
				return false;
		} 
		else if (owner.address == null)
		{
			// False if ONLY owner.address has null name.
			if (this.address != null)
				return false;
		} 
		else 
		{
			// Both have non-null addresses to compare!
			if (!this.address.equals(owner.address))
				return false;
		}
		
		// City 
		if (this.city == null) 
		{
			// False if ONLY this.city is null.
			if (owner.city != null)
				return false;
		} 
		else if (owner.city == null)
		{
			// False if ONLY owner.city has null name.
			if (this.city != null)
				return false;
		} 
		else 
		{
			// Both have non-null cities to compare!
			if (!this.city.equals(owner.city))
				return false;
		}
		
		// Telephone 
		if (this.telephone == null) 
		{
			// False if ONLY this.telephone is null.
			if (owner.telephone != null)
				return false;
		} 
		else if (owner.telephone == null)
		{
			// False if ONLY owner.telephone has null name.
			if (this.telephone != null)
				return false;
		} 
		else 
		{
			// Both have non-null telephones to compare!
			if (!this.telephone.equals(owner.telephone))
				return false;
		}
		
		// Ignore Pets for now!  Did this in Regis. 
		
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
		result = result * prime + ( (address == null) ? 0 : address.hashCode());
		result = result * prime + ( (city == null) ? 0 : city.hashCode());
		result = result * prime + ( (telephone == null) ? 0 : telephone.hashCode());
		
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
				"address=" + address + 
				", city=" + city +
				", telephone=" + telephone + '\'' +
			   "}";
	}  // end toString()


}  // end class Owner
