// ***************************************************************************
// Class:    Visit
// Extends:  BaseEntity
// 
// Model (entity) object representing a scheduled Visit in the PetClinic system.
// Manages information such as LocalDate visit instantiated, description of Visit, and Id of pet seen.
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

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//@NoArgsConstructor   // Don't use Lombok - Custom gets current date / time    
//@AllArgsConstructor  // Don't use Lombok - Sets bi-directional ref (Pet arg set to ref back to this visit)
@Getter				 // Lombok
@Setter              // Lombok - Override setPet since custom.
@Entity 		// Identify as JPA entity for DB (gets a table)
@Table(name = "visits")
public class Visit extends BaseEntity {

	// -----------------------------------------------
	// Attributes  
	// -----------------------------------------------
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
	private static final long serialVersionUID = 7396511996642980652L;
	
	// Date/time of visit - Set to the data Visit instance was created.
	@Column (name ="date") 
	private LocalDate date;

	// Reason for visit.
	@Column (name = "description")
	private String description;

	// Set up mapping between Visit and Pet (Visit can have 1 pet; Pet can have many Visits (Pet has Set<Visit>)
	// [NOTE:  If did Integer petId, could just use @Column(name="pet_Id") insteadd.] 
	@ManyToOne                           // Many Visits can be a/w 1 Pet in DB
	@JoinColumn(name = "pet_id")         // Tells Visit to join (find correct Pet a/w visit) based on Pet's Pet Id.    
	private Pet pet;
		
	// -----------------------------------------------
	// Constructors  
	// -----------------------------------------------

	public Visit() {
		super();

		date = LocalDate.now();  // obtain current date from system clock.
	}

	/**
	 * Constructor for Visit class. (Used for constructor injection).
	 *
	 * If has Visit class "id" field, do NOT include "id" as parameter.  
	 * It would be generated value that Hibernate will inject with setter.
	 * Currently not an attribute so moot point.  
	 * 
	 * @param date Date Visit created/instantiated
	 * @param description Description of visit
	 * @param pet Pet object being seen
	 *  
	 * @author Betty Jo Booth
	 * @version 1.0
	 * @since 1.0
	 */
	public Visit (LocalDate date, String description, Pet pet) {
		super();
		this.date = date;
		this.description = description;
		this.pet = pet;
		this.pet.add(this);
	}

	// -----------------------------------------------
	// LOMBOK generates @Getters and @Setters
	// 
	// - Provide custom "setPet" to manually reference pet back to this Visit. 
	// -----------------------------------------------

	// -----------------------------------------------
	// Getters / Setters   
	// Used by Spring JPA / Hibernate to do Dependency Injection (DI)
	// if doing setter injection - though constructor injection preferred
	//
	// Validation is done within validate(), not in setters to adhere to 
	// JavaBean restrictions (expect setters that return void and cannot 
	// throw exceptions.  
	// -----------------------------------------------
	
//	public LocalDate getDate() {
//		return date;
//	}
//	
//	public void setDate(LocalDate date) {
//		this.date = date;
//	}
//	
//	public String getDescription() {
//		return description;
//	}
//	
//	public void setDescription(String description) {
//		this.description = description;
//	}
//	
//	public Pet getPet() {
//		return pet;
//	}
//	
	public void setPet(Pet pet) {
		this.pet = pet;
		this.pet.add(this);
	}
	
	// -----------------------------------------------
	// Methods that override Java default functionality.
	// Required for JPA / Hibernate and by Sets. 
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
	@Override
	public boolean equals (Object o)
	{
		// Check if both object reference variables reference the same object in memory.
		if (o == this)
			return true;
		
		if (o == null)
			return false; 
		
		// Works even if "o" is a derived class of Visit. 
		if ( !(o instanceof Visit) )
			return false;
		
		Visit vo = (Visit)o;
		
		// Determine if instance variables maintained by base class are equal.  If not, return false.  
		if (!(super.equals(o)))
			return false;
	
		// Date  
		if (this.date == null) 
		{
			// False if only this.date has null date.
			if (vo.date != null)
				return false;
		} 
		else if (vo.date == null)
		{
			// False if only vo.date has null date.
			if (this.date != null)
				return false;
		} 
		else 
		{
			// Both have non-null dates to compare!  LocalDate.equals is logical (state based) comparison.
			if (!this.date.equals(vo.date))  
				return false;
		}
	
		// Description 
		if (this.description == null) 
		{
			// False if only this.description has null description.
			if (vo.description != null)
				return false;
		} 
		else if (vo.description == null)
		{
			// False if only vo.description has null description.
			if (this.description != null)
				return false;
		} 
		else 
		{
			// Both have non-null descriptions to compare!
			if (!this.description.equals(vo.description))    // String.equals compares the two Strings (not references)
				return false;
		}

		// Pet  
		if (this.pet == null) 
		{
			// False if only this.pet has null pet.
			if (vo.pet != null)
				return false;
		} 
		else if (vo.pet == null)
		{
			// False if only vo.ped has null pet.
			if (this.pet != null)
				return false;
		} 
		else 
		{
			// Both have non-null pet to compare!
			if (!this.pet.equals(vo.pet))   // Compares if the values represented are the same. 
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
	@Override
	public int hashCode()
	{
		final int prime = 17;
		int result = super.hashCode();
		
		// In this algorithm, based on Joshua Bloch's blog, if an attribute is an 
		// Object (i.e., String) return 0 if null or call hashCode() on it.
		// NOTE:  String.hashCode() returns the same int value for strings of the 
		// same value (i.e., "one" and "one") though they are different actual String objects).
		result = result * prime + ( (date == null) ? 0 : date.hashCode());
		result = result * prime + ( (description == null) ? 0 : description.hashCode());
		result = result * prime + ( (pet == null) ? 0 : pet.hashCode());
	
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
		return "Visit{" +
	           super.toString() +
	           "date=" + date +
	           ", pet=" + pet +
			   ", description=" + description + '\'' + 
			   "}";
	}  // end toString()

}  // end class Visit 