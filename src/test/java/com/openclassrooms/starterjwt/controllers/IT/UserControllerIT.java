package com.openclassrooms.starterjwt.controllers.IT;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
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

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
public class UserControllerIT {

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
    @DisplayName("Cherche un user par son Id")
    public void givenUserId_whenFindById_thenResponseIsOk() throws Exception {
    	
    	mockMvc.perform(get("/api/user/2")
    		   .header("Authorization", token))
    		   .andExpect(status().isOk())
    		   .andExpect(jsonPath("lastName").value("test1"));
    }

    @Test
    @DisplayName("Cherche un user par son Id mais ne le trouve pas")
    public void givenUserIdNotFound_whenFindById_thenThrowNotFoundException() throws Exception {
    	
    	mockMvc.perform(get("/api/user/7981645")
    		   .header("Authorization", token))
    		   .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Cherche un user par son Id mais son Id n'est pas un nombre")
    public void givenUserIdWhereIdIsNotANumber_whenFindById_thenThrowBadRequestException() throws Exception {
    	
    	mockMvc.perform(get("/api/user/notANumber")
    		   .header("Authorization", token))
    		   .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Supprime un user")
    public void givenUserId_whenDeleteById_thenResponseIsOk() throws Exception {
    	
    	String requestBodyLoginUser = "{\"email\":\"test2@gmail.com\",\"password\":\"test2test2\"}";
    	MvcResult resultLogin = mockMvc.perform(post("/api/auth/login")
    			.contentType(MediaType.APPLICATION_JSON)
    			.content(requestBodyLoginUser))
    			.andReturn();
    	String tokenForUserToDelete = "Bearer "	+ JsonPath.read(resultLogin.getResponse().getContentAsString(), "$.token");
    	mockMvc.perform(delete("/api/user/3")
    		   .header("Authorization", tokenForUserToDelete))
    		   .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Supprime un user inexistant")
    public void givenUserIdNotFound_whenDeleteById_thenThrowNotFoundException() throws Exception {
    	
    	mockMvc.perform(delete("/api/user/54574846")
    		   .header("Authorization", token))
    		   .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Supprime un user par son Id mais son Id n'est pas un nombre")
    public void givenUserIdWhereIdIsNotANumber_whenDeleteById_thenThrowBadRequestException() throws Exception {
    	
    	mockMvc.perform(delete("/api/user/notANumber")
    		   .header("Authorization", token))
    		   .andExpect(status().isBadRequest());
    }

}
