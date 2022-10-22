//***************************************************************************
//Class:  Pet (model object)  
//Extends: BaseEntity
//
// Model (entity) object representing Pets in the Petclinic system.
// This class adheres to the rules of a JavaBean.
//
//Initially implemented as a POJO (to store in a basic HashMap/other).
//
// Updated to a JPA entity object to be persisted into a database.  Therefore,  
// will require mapping [@Entity, possibly @Table, @Column, etc.] which is  
// what turns this POJO into a JPA entity.  @Id is in BaseEntity.  Also, 
// Constructor is preferred injection method. 
//
//To be a true Java Bean, need setter/getter, default constructor, implement Serializable.
//*************************************************************************** 
package guru.springframework5.sfw5bgpetclinic.model;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.Attributes;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
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
@Table(name = "pets")
public class Pet extends BaseEntity {

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
	private static final long serialVersionUID = 1986885758403978000L;

	@Column(name = "name")
	private String name;
	
	// PetType attribute
	// 		1 PetType has MANY Pet a/w it in table.  (PetType has NO Hash<Pet> attrib; but logically PetType has lots of Pet of its type)
	//      Many Pet ref same 1 PetType in table.    (Pet has single PetType attribute)
	// Therefore, Many Pet / One PetType; This is Pet class, so ManyToOne; In PetType class OneToMany
	@ManyToOne						   // Tells JPA join based on pet_type_id
	@JoinColumn(name = "pet_type_id")  
	private PetType petType;
	
	// Owner attribute
	// 		1 Owner has MANY Pet a/w it in table.  		(Owner has Hash<Pet> attribute)
	//      Many Pet can have ref same 1 Owner in table.	(Pet has single Owner attribute)
	// Therefore, Many Pet / One Owner; This is Pet class, so ManyToOne; In Owner class OneToMany
	@ManyToOne
	@JoinColumn(name = "owner_id")    // Tells JPA join based on Owner id.  
	private Owner owner; 			  // Recall Owner's Hash<Pet> is @OneToMany, "mappedBy" this "owner" attrib (foreign key)
	
	@Column(name = "birth_date")
	private LocalDate birthDate;
	
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "pet")     // 1 Pet / Many Visits (CascadeType.ALL = If delete Pet, delete its visits)   
	private Set<Visit> visits = new HashSet<>();                //                     (mappedBy "pet" attribute of Visit class) 
	
	// -----------------------------------------------
	// Constructors - LOMBOK generated @NoArgsConstructor
	//                LOMBOK generated @AllArgsConstructor
	//                Uncommented constructor has 4 of the 5 attributes. 
	// -----------------------------------------------

	
	/**
	 * Removed default constructor.  Putting @Build on default and parameterized constructor 
	 * always called default constructor so never reached code that set the parameters and 
	 * constructed object would have null values.  Could not even leave default contructor 
	 * without @Builder and other with @Builder.     
	 */
//	public Pet() {
//		super();
//	}

	/** 
	 * Constructor   
	 * 
	 * #4 - Used for constructor injection and @Builder.  
	 * 
	 * Do NOT include "id" as parameter.  It is a generated value that 
	 * Hibernate will inject with setter.
	 *
	 * @param name Name of pet
	 * @param petType Pet type / breed 
	 * @param owner Owner of pet
	 * @param birthDate Pet data of birth
  */	 
	@Builder  // builder().a1("x").a2("y").build() calls constructor once w/ a1 and a2 at same time. 
			  // Don't include id.  It is generated.  Set with setter.  If Spring, will inject with setter. 
	          // Don't allow visits because bidirectional and need "pet.add(visit)" to make sure pet gets set as add each visit. 
	public Pet (String name, PetType petType, Owner owner, LocalDate birthDate) {
		super();
		this.name = name;
		this.petType = petType;
		this.birthDate = birthDate;
		if (owner != null) 
			owner.add(this);
	}  

	// -----------------------------------------------
	// Public Methods
	// -----------------------------------------------

	/**
	 * Add a visit to the pet's set of visits.  
	 * @param visit - Visit to be added.  Can have null id if not saved yet. 
	 * @return true if added; false otherwise. 
	 */
	public boolean add(Visit visit) {

		System.out.println("All visits for pet ");
		for (Visit v : this.visits) {
			if 	(v.isNew()) 
				System.out.println("New null id");
			else  
				System.out.println ("Visit id = " + v.getId());
			
			System.out.println("Visit desc " + v.getDescription());
			System.out.println("Visit date " + v.getDate());

		}
		
		if (this.visits.contains(visit))
			System.out.println("In Pet:addVisit - - Contains visit already!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		else 
			System.out.println("In Pet:addVisit - - DOES NOT contain visit yet !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

		System.out.println("Does contains call equals?????????????????????????????????");
		if (this.visits.contains(visit))
			System.out.println("CONTAINS");
		else 
			System.out.println("NOPE");
			
		if ((visit == null) || (this.visits.contains(visit)) )
			return false;
		// Visit to be added
		System.out.println("Visit id " + visit.getId());
		System.out.println("Visit desc " + visit.getDescription());
		System.out.println("Visit date " + visit.getDate());

		
		// Request to add the Visit.  If the visit already exists in the Set (equals() returns true)
		// the existing visit will be removed and the current one added.  This handles updates where the 
		// id's are the same (eqaual returns true), but some values are different. 
		if (this.visits.contains(visit)) 
			this.visits.remove(visit);  

		if (this.visits.add(visit)) {
			System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! ADDING VISIT to PETS SET OF VISITS");
			visit.setPet(this);
			return true;
		}
		
		// Visit was not added to Pet. 
		return false;
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
//
//	public PetType getPetType() {
//		return petType;
//	}
//	
//	public void setPetType(PetType petType) {
//		this.petType = petType;
//	}
//	
//	public Owner getOwner() {
//		return owner;
//	}
//	
//	public void setOwner(Owner owner) {
//		this.owner = owner;
//	}
//	
//	public LocalDate getBirthDate() {
//		return birthDate;
//	}
//	
//	public void setBirthDate(LocalDate birthDate) {
//		this.birthDate = birthDate;
//	}
//
//	public Set<Visit> getVisits() {
//		return visits;
//	}
//	
//	public void setVisits(Set<Visit> visits) {
//		this.visits = visits;
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
		if ( !(o instanceof Pet) )
			return false;
		
		Pet po = (Pet)o;

		// Determine if instance variables maintained by base class are equal.  If not, return false.  
		if (!(super.equals(o)))
			return false;

		// -----------------------------------------------------
		// At this point, both 'this' and 'o' are new OR are existing with same id. 
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
			// False if ONLY po.name has null name.
			if (this.name != null)
				return false;
		} 
		else 
		{
			// Both have non-null names to compare!
			if (!this.name.equals(po.name))
				return false;
		}

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

		// Ignore Visits for now - as did at Regis. 
				
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

		// Build String of visit descriptions.  If use toString() will be recursive since Pet has Visits and Visit has a Pet. 
		int numVisits = visits.size();
		int count = 1;
		String strVisits = "";
		for (Visit visit : visits) {
			if (count < numVisits)	
				strVisits = strVisits.concat(visit.getDescription() + " | ");
			else 
				strVisits = strVisits.concat(visit.getDescription());
		}
	
		return "Pet{" +
	           super.toString() + 
	           "name=" + name +
	           ", petType=" + petType + '\'' +  
	           // Do not use owner.toString or recursive since owner has a Pet that in turn has them as Owner.  
			   ", owner=" + ( (owner == null) ? "null" : owner.getFirstName() + " " + owner.getLastName() ) + '\'' +
			   ", birthDate=" + ( (birthDate == null) ? "unspecified" : birthDate ) + '\'' +
	           // Do not use visit.toString or recursive since visit has a Pet that in turn has them as Visit.
			   ", visits=" + ( strVisits.isEmpty() ? "none" : strVisits) + '\'' +
			   "}";
	}  // end toString()

}  // end class Pet
