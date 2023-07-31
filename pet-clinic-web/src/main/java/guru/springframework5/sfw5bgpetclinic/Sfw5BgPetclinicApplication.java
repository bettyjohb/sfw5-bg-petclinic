package guru.springframework5.sfw5bgpetclinic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// To Run:  pet-clinic-web:  Sfw5BgPetclinicApplication.java -> run as -> Java Application. 
@SpringBootApplication
public class Sfw5BgPetclinicApplication {

	public static void main(String[] args) {
		SpringApplication.run(Sfw5BgPetclinicApplication.class, args);
		System.out.println("Petclinic is now up and running!");
	}

}
