package guru.springframework5.sfw5bgpetclinic.formatters;

import java.text.ParseException;
import java.util.Collection;
import java.util.Locale;
import org.springframework.format.Formatter;
import org.springframework.stereotype.Component;
import guru.springframework5.sfw5bgpetclinic.model.PetType;
import guru.springframework5.sfw5bgpetclinic.services.PetTypeService;

/** Instructs SPring MVC on how to parse and print elemtns of type PetType.  Starting from Spring 3.0, 
 *  formatters have come as an improvement in comparison to legacy PropertyEditors.
 *  As come in from Thymepage (ie createOrUpdatePet.html), passed "dog" (not PetType for "dog").
 *  Spring automagically invokes parse and gets the PetType for "dog" (or "cat" or whatever).  
 */
@Component
public class PetTypeFormatter implements Formatter<PetType> {
	private final PetTypeService petTypeService;
	
	// Dependency injection 
	public PetTypeFormatter (PetTypeService petTypeService) {
		this.petTypeService = petTypeService;
	}
	
	@Override 
	public String print(PetType petType, Locale locale) {
		return petType.getName();
	}
	
	@Override
	public PetType parse(String text, Locale locale) throws ParseException {
		
		// Retrieve all PetTypes.  Iterate the Collection and find the PetType with the given name "text". 
		Collection<PetType> allPetTypes = this.petTypeService.findAll();
		for (PetType type : allPetTypes) {
			if (type.getName().equals(text)) {
				return type;
			}
		}
		throw new ParseException("Type not found: " + text, 0);
	}
}

