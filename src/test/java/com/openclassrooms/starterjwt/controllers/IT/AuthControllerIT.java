package com.openclassrooms.starterjwt.controllers.IT;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
public class AuthControllerIT {

	@Autowired
    MockMvc mockMvc;

	@Test
	@DisplayName("Authentifie un user Admin")
	public void givenLoginRequestWithAdminUser_whenAuthenticateUser_thenResponseContainAdminTrue() throws Exception {

		String requestBodyAdmin = "{\"email\":\"yoga@studio.com\",\"password\":\"test!1234\"}";
		mockMvc.perform(post("/api/auth/login")
			   .contentType(MediaType.APPLICATION_JSON)
			   .content(requestBodyAdmin))
			   .andExpect(status().isOk())
			   .andExpect(jsonPath("$.admin").value(true));
	}

	@Test
	@DisplayName("Authentifie un user non Admin")
	public void givenLoginRequest_whenAuthenticateUser_thenResponseContainAdminFalse() throws Exception {
		
		String requestBodyUser = "{\"email\":\"test1@gmail.com\",\"password\":\"test1test1\"}";
		mockMvc.perform(post("/api/auth/login")
			   .contentType(MediaType.APPLICATION_JSON)
			   .content(requestBodyUser))
			   .andExpect(status().isOk())
			   .andExpect(jsonPath("$.admin").value(false));
	}

	@Test
	@DisplayName("Inscrire un user")
	public void givenSignupRequest_whenRegisterUser_thenResponseContainUserRegistered() throws Exception {
		
		String requestBody = "{\"lastName\": \"test\",\"firstName\": \"test\",\"email\":" +
							 "\"test@gmail.com\",\"password\": \"testtest\"}";
		mockMvc.perform(post("/api/auth/register")
			   .contentType(MediaType.APPLICATION_JSON)
			   .content(requestBody))
			   .andExpect(status().isOk())
			   .andExpect(jsonPath("$.message").value("User registered successfully!"));
	}

    @Test
    @DisplayName("Inscrire un user avec un email déjà pris")
    public void givenSignupRequestWithEmailAlrdyTaken_whenRegisterUser_thenResponseContainError() throws Exception {
    	
    	String requestBodyEmailAlrdyTaken = "{\"lastName\":\"test1\",\"firstName\":\"test1\",\"email\":" +
    										"\"test1@gmail.com\",\"password\":\"test1test1\"}";
    	mockMvc.perform(post("/api/auth/register")
    		   .contentType(MediaType.APPLICATION_JSON)
    		   .content(requestBodyEmailAlrdyTaken))
    		   .andExpect(status().isBadRequest())
    		   .andExpect(jsonPath("$.message").value("Error: Email is already taken!"));
    }
}
