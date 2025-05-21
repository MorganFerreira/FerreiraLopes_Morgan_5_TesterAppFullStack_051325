package com.openclassrooms.starterjwt.controllers.IT;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeAll;
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

import com.jayway.jsonpath.JsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
public class TeacherControllerIT {

	@Autowired
	MockMvc mockMvc;

	String token;

	@BeforeAll
	public void initToken() throws Exception {
    	
		// ARRANGE : on récupère le token depuis une loginRequest
    	String requestBodyLoginUser = "{\"email\":\"test1@gmail.com\",\"password\":\"test1test1\"}";
    	MvcResult resultLogin = mockMvc.perform(post("/api/auth/login")
    								   .contentType(MediaType.APPLICATION_JSON)
    								   .content(requestBodyLoginUser))
    								   .andReturn();
    	token = "Bearer " + JsonPath.read(resultLogin.getResponse().getContentAsString(), "$.token");
    }

	@Test
	@DisplayName("Cherche un teacher par son Id")
	public void givenTeacherId_whenFindById_thenResponseIsOkAndContainTeacherInfo() throws Exception {
		
		mockMvc.perform(get("/api/teacher/1")
			   .header("Authorization", token))
			   .andExpect(status().isOk())
			   .andExpect(jsonPath("lastName").value("DELAHAYE"));
	}

	@Test
	@DisplayName("Cherche un teacher par son Id mais ne le trouve pas")
	public void givenTeacherIdNotFound_whenFindById_thenThrowNotFoundException() throws Exception {
		
		mockMvc.perform(get("/api/teacher/64864156")
			   .header("Authorization", token))
			   .andExpect(status().isNotFound());
	}

	@Test
	@DisplayName("Cherche un teacher par son Id mais son Id n'est pas un nombre")
	public void givenTeacherWhereIdIsNotANumber_whenFindById_thenThrowBadRequestException() throws Exception {
		
		mockMvc.perform(get("/api/teacher/notANumber")
			   .header("Authorization", token))
			   .andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("Récupère la liste des teachers")
	public void givenTeachers_whenFindAll_thenResponseIsOkAndContainListOfTeacherInfo() throws Exception {
		
		mockMvc.perform(get("/api/teacher/")
			   .header("Authorization", token))
			   .andExpect(status().isOk())
			   .andExpect(jsonPath("$[0].id").value(1))
			   .andExpect(jsonPath("$[0].lastName").value("DELAHAYE"))
			   .andExpect(jsonPath("$[1].id").value(2))
			   .andExpect(jsonPath("$[1].lastName").value("THIERCELIN"));
	}

}
