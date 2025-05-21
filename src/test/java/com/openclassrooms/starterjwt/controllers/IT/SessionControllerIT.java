package com.openclassrooms.starterjwt.controllers.IT;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
public class SessionControllerIT {

	@Autowired
	MockMvc mockMvc;

	String token;

	int id;

	@BeforeAll
	public void initTokenAndId() throws Exception {

		// ARRANGE : on récupère le token et l'Id depuis un login
		String requestBodyLoginUser = "{\"email\":\"test1@gmail.com\",\"password\":\"test1test1\"}";
		MvcResult resultLogin = mockMvc.perform(post("/api/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBodyLoginUser))
				.andReturn();
		token = "Bearer " + JsonPath.read(resultLogin.getResponse().getContentAsString(), "$.token");
		id = JsonPath.read(resultLogin.getResponse().getContentAsString(), "$.id");
	}

	@Test
	@DisplayName("Cherche une session par son Id")
	public void givenSessionId_whenFindById_thenResponseIsOkAndContainSessionInfo() throws Exception {
		
		mockMvc.perform(get("/api/session/1")
			   .contentType(MediaType.APPLICATION_JSON)
			   .header("Authorization", token))
			   .andExpect(status().isOk())
			   .andExpect(jsonPath("name").value("test1 session"));
	}

	@Test
	@DisplayName("Cherche une session par son Id mais ne le trouve pas")
	public void givenSessionIdNotFound_whenFindById_thenThrowNotFoundException() throws Exception {
		
		mockMvc.perform(get("/api/session/68465416")
			   .header("Authorization", token))
			   .andExpect(status().isNotFound());
	}

	@Test
	@DisplayName("Cherche une session par son Id mais son Id n'est pas un nombre")
	public void givenSessionIdWhereIdIsNotANumber_whenFindById_thenThrowBadRequestException() throws Exception {
		
		mockMvc.perform(get("/api/session/notANumber")
			   .header("Authorization", token))
			   .andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("Récupère la liste des sessions")
	public void givenSessionList_whenFindAll_thenResponseIsOkAndContainListOfSessionDto() throws Exception {
		
		mockMvc.perform(get("/api/session/")
			   .header("Authorization", token))
			   .andExpect(status().isOk())
			   .andExpect(jsonPath("$[0].name").value("test1 session"))
			   .andExpect(jsonPath("$[1].name").value("test2 session"));
	}

	@Test
	@DisplayName("Créer une session")
	public void givenSession_whenCreate_thenResponseIsOkAndContainSessionInfo() throws Exception {
		
		String requestBodySession = "{\"name\":\"test1 session\",\"date\":\"2025-05-19\"," +
				    				"\"teacher_id\":1,\"users\":null,\"description\":\"test1 session\"}";
		mockMvc.perform(post("/api/session")
			   .contentType(MediaType.APPLICATION_JSON)
			   .header("Authorization", token)
			   .content(requestBodySession))
			   .andExpect(status().isOk())
			   .andExpect(jsonPath("name").value("test1 session"));
	}

	@Test
	@DisplayName("Mettre à jour une session")
	public void givenSessionIdAndSessionDto_whenUpdate_thenResponseIsOkAndTheSameOfSessionDto() throws Exception {
		
		String requestBodySessionUpdate = "{\"name\":\"Session updated\",\"date\":\"2025-05-19\"," +
										  "\"teacher_id\":1,\"users\":null,\"description\":\"test2 session\"}";
		mockMvc.perform(put("/api/session/3")
			   .contentType(MediaType.APPLICATION_JSON)
			   .header("Authorization", token)
			   .content(requestBodySessionUpdate))
			   .andExpect(status().isOk())
			   .andExpect(jsonPath("name").value("Session updated"));
	}

	@Test
	@DisplayName("Mettre à jour une session mais l'Id n'est pas un nombre")
	public void givenSessionIdWhereIdIsNotANumber_whenUpdate_thenThrowBadRequestException() throws Exception {
		
		mockMvc.perform(put("/api/session/notANumber")
			   .contentType(MediaType.APPLICATION_JSON)
			   .header("Authorization", token)
			   .content(""))
			   .andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("Supprime une session")
	public void givenSessionId_whenDelete_thenResponseIsOk() throws Exception {

		mockMvc.perform(delete("/api/session/4")
			   .contentType(MediaType.APPLICATION_JSON)
			   .header("Authorization", token))
			   .andExpect(status().isOk());
	}
	
	@Test
	@DisplayName("Supprime une session par son Id mais ne le trouve pas")
	public void givenSessionIdNotFound_whenDelete_thenThrowNotFoundException() throws Exception {
		
		mockMvc.perform(delete("/api/session/864963513")
			   .contentType(MediaType.APPLICATION_JSON)
			   .header("Authorization", token))
			   .andExpect(status().isNotFound());
	}
	
	@Test
	@DisplayName("Supprime une session par son Id mais son Id n'est pas un nombre")
	public void givenSessionIdWhereIdIsNotANumber_whenDelete_thenThrowBadRequestException() throws Exception {
		
		mockMvc.perform(delete("/api/session/notANumber")
			   .contentType(MediaType.APPLICATION_JSON)
			   .header("Authorization", token))
			   .andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("Inscrire un user à une session")
	public void givenSessionIdAndUserId_whenParticipate_thenResponseIsOk() throws Exception {

		mockMvc.perform(post("/api/session/1/participate/" + id)
			   .contentType(MediaType.APPLICATION_JSON)
			   .header("Authorization", token))
			   .andExpect(status().isOk());
	}

	@Test
	@DisplayName("Inscrire un user à une session mais l'Id de la session ou l'Id du user n'est pas un nombre")
	public void givenSessionIdOrUserIdWhereIdIsNotANumber_whenParticipate_thenThrowBadRequestException() throws Exception {
		
		mockMvc.perform(post("/api/session/notANumber/participate/notANumber")
			   .contentType(MediaType.APPLICATION_JSON)
			   .header("Authorization", token))
			   .andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("Désinscrire un user à une session")
	public void givenSessionIdAndUserId_whenNoLongerParticipate_thenResponseIsOk() throws Exception {

		mockMvc.perform(delete("/api/session/1/participate/" + id)
			   .contentType(MediaType.APPLICATION_JSON)
			   .header("Authorization", token))
			   .andExpect(status().isOk());
	}
	
	@Test
	@DisplayName("Désinscrire un user à une session mais l'Id de la session ou l'Id du user n'est pas un nombre")
	public void givenSessionIdOrUserIdWhereIdIsNotANumber_whenNoLongerParticipate_thenThrowBadRequestException() throws Exception {
		
		mockMvc.perform(delete("/api/session/notANumber/participate/notANumber")
			   .contentType(MediaType.APPLICATION_JSON)
			   .header("Authorization", token))
			   .andExpect(status().isBadRequest());
	}

}
