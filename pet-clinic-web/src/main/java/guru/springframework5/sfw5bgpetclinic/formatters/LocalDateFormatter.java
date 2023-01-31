package guru.springframework5.sfw5bgpetclinic.formatters;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.text.ParseException;
import org.springframework.format.Formatter;
import org.springframework.stereotype.Component;

/** Instructs Spring MVC on how to parse and print strings elements of type LocalDate.  Starting from Spring 3.0, 
 *  formatters have come as an improvement in comparison to legacy PropertyEditors.
 *  As come in from Thymepage (ie createOrUpdatePet.html), passed "1998-12-12" as String (not LocalDate).
 *  Spring automagically invokes parse and gets the LocalDate for "1998-12-12" (or other date).  
 */

@Component
public class LocalDateFormatter implements Formatter<LocalDate> {
	
	@Override 
	public String print(LocalDate localDate, Locale locale) {
		return localDate.toString();
	}

	@Override
	public LocalDate parse(String date, Locale locale) throws ParseException {
		
		LocalDate localDate;
		try { 
			// ISO_LOCAL_DATE = international standard that represents date as format yyyy-mm-dd
			// This code was working w/o this, but added because in some places will be dd-mm-yyyy, etc.  
			localDate = LocalDate.parse(date, java.time.format.DateTimeFormatter.ISO_LOCAL_DATE);
		} catch (DateTimeParseException e) {
			throw new ParseException("Unable to parse date: " + date, 0);
		}
		return localDate;
	}

}

	
