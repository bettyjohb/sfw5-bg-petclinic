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
// will require mapping [@Entity, possibly @Table, @Column, etc.] which is  
// what turns this POJO into a JPA entity.  @Id is in BaseEntity.  Also, 
// Constructor is preferred injection method. 
//
// To be a true Java Bean, need setter/getter, default constructor, implement Serializable.
// *************************************************************************** 
package guru.springframework5.sfw5bgpetclinic.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.springframework.util.StringUtils;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Setter;

@AllArgsConstructor  // Lombok
@NoArgsConstructor   // Lombok 
@Getter				 // Lombok
@Setter              // Lombok
@Entity 		// Identify as JPA entity to be mapped to DB
@Table(name = "owners")     // DB name to hold these objects
public class Owner extends Person {

	/**
	 * Identifier necessary for all serializable objects in order to 
	 * uniquely identify the class during serialization (output) and 
	 * deserialization (input).  If the id at output does not match 
	 * that at input, an exception is thrown due to different class,
	 * 
	 * Note:  ALthough the Serializable interface is inherited through 
	 * GMSData, the static final UID attribute is not and must be 
	 * defined in each subclass. 
	 */
	private static final long serialVersionUID = -4063886627796020648L;

	// Typically, I place these at Person level.  However, we are not managing HR here, so 
	// Vet is a Person (with first and last name), but does not need address information.
	
	@Column (name = "address")
	private String 	address;

	@Column (name = "city")
	private String 	city;
	
	@Column (name = "telephone")
	private String 	telephone;
	
	// pets attribute - requires a relationship mapping EVERY OWNER CAN HAVE MULTIPLE PETS; MANY PETS HAVE ONE OWNER
	// 		1 Owner has MANY Pet a/w it in table.  			(Owner has Hash<Pet> attribute)
	//      Many Pet can ref same 1 Owner in table.	        (Pet has single Owner attribute)
	// Therefore, Many Pet / One Owner; This is Owner class, so OneToMany; In Pet class ManyToOne
	@OneToMany (cascade = CascadeType.ALL, mappedBy = "owner")	// CascadeType ALL = if delete owner, delete their pets
																// mappedBy = says "owner" (attrib of Pet) is foreign key to get Pet back to Owner.
	 															// In turn, Pet will specify @ManyToOne and @JoinColumn as owner_id
	private Set<Pet> pets = new HashSet<>();    // HashSet does not have duplicates.  Based on equals().  Can have null id's until save().  
	                                            // Equal() says equal if id's same; ignore rest of values.

	// -----------------------------------------------
	// Constructors - LOMBOK generated @NoArgsConstructor
	//                LOMBOK generated @AllArgsConstructor
	// -----------------------------------------------

//	/**
//	 * Default constructor (required of JPA entity objects)
//	 */
//	public Owner() {
//		super();
//	}

	@Builder // ONLY PUT @BUILDER on CHILD class (not parent Person).  I don't pass ID - - generated. 
	public Owner (String firstName, String lastName, String address, String city, String telephone, Set<Pet> pets) {
		super(firstName, lastName);
		this.address = address;
		this.city = city;
		this.telephone = telephone;
		if (pets != null) this.pets = pets;
	}
	
	// -----------------------------------------------
	// Public Methods
	// -----------------------------------------------

	/**
	 * Add a pet to the owner's set of pets.  
	 * @param pet - Pet to be added.  Can have null id if not saved yet. 
	 * @return true if added; false otherwise. 
	 */
	public boolean add(Pet pet) {
		if (pet == null)
			return false;
		
		// Request to add the Pet.  If the pet already exists in the Set (equals() returns true)
		// pet will not be added (returns false).  Otherwise, set the pet's owner attribute; return true.
		if (this.pets.add(pet)) {
			pet.setOwner(this);
			return true;
		}
		
		// Pet was not added to Owner. 
		return false;
	}

	/**
	 * Retrieve Pet by name.    
	 * @param name - Name of Pet to retrieve.  
	 * @return Pet; null if not found. 
	 */
	public Pet getPet(String name, boolean ignoreNew) {
		if ( (name == null) || (!StringUtils.hasLength(name)) )
			return null;
		
		// Search for the pet with the same name. 
		name = name.toLowerCase();	
		for (Pet curPet : pets) {
			if (!ignoreNew || !curPet.isNew()) {
				String compName = curPet.getName().toLowerCase();
				if (compName.equals(name))  {
					System.out.println ("Found name " + compName);
					return curPet;
				}	
			}
		}
		
		System.out.println("Not found name " + name);
		return null;  // Pet with requested name was not found. 
	}

// -----------------------------------------------
// LOMBOK generates @Getters and @Setters 
// -----------------------------------------------
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
//	public String getAddress() {
//		return address;
//	}
//	
//	public void setAddress(String address) {
//		this.address = address;
//	}
//	
//	public String getCity() {
//		return city;
//	}
//	
//	public void setCity(String city) {
//		this.city = city;
//	}
//	
//	public String getTelephone() {
//		return telephone;
//	}
//	
//	public void setTelephone(String telephone) {
//		this.telephone = telephone;
//	}
//	
//	public Set<Pet> getPets() {
//		return pets;
//	}
//	
//	public void setPets(Set<Pet> pets) {
//		this.pets = pets;
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
		
		// Works even if "o" is a derived class of Owner. 
		if ( !(o instanceof Owner) )
			return false;
		
		Owner owner = (Owner)o;
		
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
		
		// If not new, base hashcode on id only.  For existing (id not null), equals() will return true if id's 
		// are the same; false otherwise.  Therefore, hashCode must return the same value.  However, updates can 
		// have same id for two visits (one with updated values, the other with values from DB).   
		// descriptions. 
		if (!isNew()) {
			return result;
		}
		
		// For new (id null), include other values. 
		
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
				"address=" + address + '\'' +
				", city=" + city + '\'' +
				", telephone=" + telephone + '\'' +
				", pets=" + pets + '\'' +
			   "}";
	}  // end toString()

}  // end class Owner
