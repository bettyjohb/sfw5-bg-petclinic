package guru.springframework5.sfw5bgpetclinic;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

//@ExtendWith(SpringExtension.class)  // Replaces JUnit4 @RunWith(SpringRunner.class) to bring in Context.
									   // Included within @SpringBootTest
@SpringBootTest
class Sfw5BgPetclinicApplicationTests {

	// This just brings up Spring Context for tests because of ExtendWith(SpringExtension.class). 
	@Test
	void contextLoads() {
	}

}
