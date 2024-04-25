package edu.AF.UTMS;

import edu.AF.UTMS.models.User;
import edu.AF.UTMS.models.consts.UserRoles;
import edu.AF.UTMS.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;

@SpringBootApplication
@EnableScheduling
public class UniversityTimetableManagementApplication implements CommandLineRunner {
	@Autowired
	private UserRepository userRepository;

	public static void main(String[] args) {
		SpringApplication.run(UniversityTimetableManagementApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		List<User> adminAccount = userRepository.findAllByUserRole(UserRoles.ADMIN);
		if (adminAccount==null || adminAccount.isEmpty()) {
			User user = new User();

			user.setEmail("contact@admin.com");
			user.setFirstName("Lord");
			user.setLastName("Admin");
			user.setUserRole(UserRoles.ADMIN);
			user.setPassword(new BCryptPasswordEncoder().encode("admin"));
			userRepository.save(user);
		}
	}
}
