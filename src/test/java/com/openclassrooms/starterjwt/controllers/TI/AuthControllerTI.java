package com.openclassrooms.starterjwt.controllers.TI;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
public class AuthControllerTI {

	@Autowired
    MockMvc mockMvc;

	@Test
	@DisplayName("Authentifie un user Admin")
	public void givenLoginRequestWithAdminUser_whenAuthenticateUser_thenResponseContainAdminTrue() throws Exception {

		// GIVEN : Une LoginRequest avec un user admin
		String requestBodyAdmin = "{\"email\":\"yoga@studio.com\",\"password\":\"test!1234\"}";
		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBodyAdmin))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andReturn();
		
		// ASSERT : on veux "admin": true dans la réponse
		assertTrue(result.getResponse().getContentAsString().contains("\"admin\":true"));
	}

	@Test
	@DisplayName("Inscrire un user")
	public void givenSignupRequest_whenRegisterUser_thenResponseContainUserRegistered() throws Exception {
		
		// GIVEN : Une RegisterRequest
		String requestBodyRegisterUser = "{\"lastName\": \"test\",\"firstName\": \"test\",\"email\":" +
										 "\"test@gmail.com\",\"password\": \"testtest\"}";
		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBodyRegisterUser))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andReturn();
		
		// ASSERT : on veux "User registered" dans la réponse
		assertTrue(result.getResponse().getContentAsString().contains("User registered"));
	}

}
