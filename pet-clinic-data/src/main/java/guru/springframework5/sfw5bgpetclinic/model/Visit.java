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
	// Constructors - LOMBOK Don't use @NoArgsConstructor - Want this to provide default date. 
	//                LOMBOK Don't use @AllArgsConstructor - Don't want id to be specified. 
	//                This constructor has 4 of the 5 attributes. 
	// -----------------------------------------------

	/**
	 * Default constructor - NOT Lombok so can provide default date/time.  
	 * 
	 * REMOVED - - Putting @Builder on both a default and parameterized constructor 
	 * always called default constructor so it never reached code that set the parameters so 
	 * constructed object that resulted would have null values.  Could not even leave default 
	 * constructor without @Builder and other with @Builder.     
	 */
//	@Builder
//	public Visit() {
//		super();
//		date = LocalDate.now();  // obtain current date from system clock.
//	}

	/**
	 * Constructor for Visit class. (Used for constructor injection).
	 *
	 * If has Visit class "id" field, do NOT include "id" as parameter.  
	 * It would be generated value that Hibernate will inject with setter.
	 * Currently not an attribute so moot point.  
	 * 
	 * @param date Date Visit created/instantiated (defaults to current if null)
	 * @param description Description of visit
	 * @param pet Pet object being seen
	 *  
	 * @author Betty Jo Booth
	 * @version 1.0
	 * @since 1.0
	 */
	@Builder  // builder().a1("x").a2("y").build() calls constructor once w/ a1 and a2 at same time. 
		      // Don't allow id as a param.  It is generated.  Set with setter.  If Spring, will inject with setter. 
              // Don't allow visits because bidirectional and need "pet.add(visit)" to make sure pet gets set as add each visit. 
	public Visit (LocalDate date, String description, Pet pet) {
		super();
		System.out.println("In Visit constructor");

		// If date not provided, default to current date.
		if (date != null)
			this.date = date;
		else
			this.date = LocalDate.now();
		
		this.description = description;
		
		this.setPet(pet);
//		if (pet != null) {
	//		this.pet = pet;
		//	this.pet.add(this);
		//}
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
		if ( (pet != null) && ((this.pet == null) || !(this.pet.equals(pet))) ) {
			System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! SETTING VISITS PET ATTRIBUTE TO PET");
			this.pet = pet;
			this.pet.add(this);
		}
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
		System.out.println("Invoked visit:equals");
		
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

		// -----------------------------------------------------
		// At this point, both 'this' and 'vo' are new OR are existing with same id. 
		// -----------------------------------------------------
		
		// If same non-null id, equal regardless of remaining since could be update.  
		if (!this.isNew())
			return true;
		
		// Otherwise, both new (null id), compare remaining values. 
			
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
		System.out.println("invoked Hashcode");
		
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