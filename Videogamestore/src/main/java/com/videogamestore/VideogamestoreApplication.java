package com.videogamestore;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.videogamestore.domain.User;
import com.videogamestore.domain.security.Role;
import com.videogamestore.domain.security.UserRole;
import com.videogamestore.service.UserService;
import com.videogamestore.utility.SecurityUtility;



@SpringBootApplication
@EnableAutoConfiguration
public class VideogamestoreApplication implements CommandLineRunner{

	
	@Autowired
	private UserService userService;
	
	public static void main(String[] args) {
		SpringApplication.run(VideogamestoreApplication.class, args);
	}

	@Override
	public void run(String... arg0) throws Exception {
		// uncomment the code bellow and add values to auto define  user entry
		
//		User user1 = new User();
//		user1.setFirstName("");
//		user1.setLastName("");
//		user1.setUsername("");
//		user1.setPassword(SecurityUtility.passwordEncoder().encode(""));
//		user1.setEmail("");
//		Set<UserRole> userRoles = new HashSet<>();
//		Role role1= new Role();
//		role1.setRoleId(1);
//		role1.setName("ROLE_USER");
//		userRoles.add(new UserRole(user1, role1));		
//		userService.createUser(user1, userRoles);
		
	}
	
	
}
