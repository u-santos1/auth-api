package AuthAPI.AuthAPI;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AuthApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthApiApplication.class, args);
	}

}
