package com.openclassrooms.starterjwt.controllers.TU;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.openclassrooms.starterjwt.payload.request.LoginRequest;
import com.openclassrooms.starterjwt.payload.request.SignupRequest;
import com.openclassrooms.starterjwt.payload.response.JwtResponse;
import com.openclassrooms.starterjwt.payload.response.MessageResponse;
import com.openclassrooms.starterjwt.repository.UserRepository;
import com.openclassrooms.starterjwt.security.jwt.JwtUtils;
import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;
import com.openclassrooms.starterjwt.controllers.AuthController;
import com.openclassrooms.starterjwt.models.User;

@SpringBootTest
public class AuthControllerTU {

	@Mock
	private AuthenticationManager authenticationManager;
	
	@Mock
	private JwtUtils jwtUtils;
	
	@Mock
	private PasswordEncoder passwordEncoder;
	
	@Mock
	private UserRepository userRepository;
	
	@InjectMocks
	private AuthController authController;

    LocalDateTime rightNow = LocalDateTime.now();
	
    @Test
	@DisplayName("Authentifier un user")
	public void givenUserAndLoginRequest_whenAuthenticateUser_thenResponseIsOkAndResponseContainUserInfo() {
		
		// ARRANGE : user, loginRequest, mock de authenticationManager, jwtUtils et userRepository
		UserDetailsImpl userDetailsImpl = UserDetailsImpl
				.builder()
				.username("yoga@studio.com")
				.firstName("Admin")
				.lastName("Admin")
				.id(1L)
				.password("test!1234")
				.admin(true)
				.build();

		LoginRequest loginRequest = new LoginRequest();
		loginRequest.setEmail("yoga@studio.com");
		loginRequest.setPassword("test!1234");

		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
				userDetailsImpl, null);
		when(authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())))
				.thenReturn(authentication);

		String jwt = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ5b2dhQHN0dWRpby5jb20iLCJpYXQiOjE3NDczMTMyODEsImV4cCI6MTc0NzM5OTY4MX0.-APMHJRJiQWR8NqEmaOiGczuZ0EsSev45-nKHmK4wpAeHbsCasrSf4ushDdqVuZjAzAwK0lP52JZ4Y7D9s0opg";
		when(jwtUtils.generateJwtToken(authentication)).thenReturn(jwt);

		User user = new User(1L, "yoga@studio.com", "Admin", "Admin", "test!1234", true, rightNow, rightNow);
		when(userRepository.findByEmail(userDetailsImpl.getUsername())).thenReturn(Optional.of(user));

		// ACT : appel à authenticateUser de authController 
		ResponseEntity<?> responseEntity = authController.authenticateUser(loginRequest);

		// ASSERT : on veux un status OK et les infos user sous forme de jwtResponse
		JwtResponse jwtResponse = (JwtResponse) responseEntity.getBody();
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
		assertEquals(jwt, jwtResponse.getToken());
		assertEquals(1L, jwtResponse.getId());
		assertEquals("yoga@studio.com", jwtResponse.getUsername());
		assertEquals("Admin",jwtResponse.getFirstName());
		assertEquals("Admin", jwtResponse.getLastName());
		assertEquals(true, jwtResponse.getAdmin());
	}

	@Test
	@DisplayName("Inscrire un user")
	public void givenSignupRequest_whenRegisterUser_thenResponseIsOkAndMessageResponse() {
		
		// ARRANGE : signupRequest, mock de userRepository et passwordEncoder
		SignupRequest signupRequest = new SignupRequest();
		signupRequest.setEmail("test2@gmail.com");
		signupRequest.setPassword("test2!1234");
		signupRequest.setLastName("test2");
		signupRequest.setFirstName("test2");

		when(userRepository.existsByEmail("test2@gmail.com")).thenReturn(false);
		when(passwordEncoder.encode("test2!1234")).thenReturn("test2!1234Encodé");
		User user = new User(2L, "test2@gmail.com", "test2", "test2", "test2!1234", false, rightNow, rightNow);
		when(userRepository.save(user)).thenReturn(new User());

		// ACT : appel à registerUser de authController
		ResponseEntity<?> response = authController.registerUser(signupRequest);

		// ASSERT : on veux un status OK et le message User registered successfully
		MessageResponse messageResponse = (MessageResponse) response.getBody();
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals("User registered successfully!", messageResponse.getMessage());
	}

	@Test
	@DisplayName("Inscrire un user avec un email déjà pris")
	public void givenEmailAlrdyTaken_whenRegisterUser_thenThrowBadRequestAndMessageResponse() {
		
		// ARRANGE : signupRequest, mock de userRepository
		SignupRequest signupRequest = new SignupRequest();
		signupRequest.setEmail("test2@gmail.com");
		signupRequest.setPassword("test2!1234");
		signupRequest.setLastName("test2");
		signupRequest.setFirstName("test2");
		when(userRepository.existsByEmail("test2@gmail.com")).thenReturn(true);

		// ACT : appel à registerUser de authController
		ResponseEntity<?> response = authController.registerUser(signupRequest);

		// ASSERT : on veux un status BAD REQUEST et le message Email is already taken!
		MessageResponse messageResponse = (MessageResponse) response.getBody();
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertEquals("Error: Email is already taken!", messageResponse.getMessage());
	}

}
