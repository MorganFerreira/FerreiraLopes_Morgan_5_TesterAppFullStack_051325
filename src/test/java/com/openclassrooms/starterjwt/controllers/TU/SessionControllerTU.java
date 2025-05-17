package com.openclassrooms.starterjwt.controllers.TU;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.openclassrooms.starterjwt.controllers.SessionController;
import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.mapper.SessionMapper;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.services.SessionService;

@SpringBootTest
public class SessionControllerTU {

	@Mock
	private SessionService sessionService;
	
	@Mock
	private SessionMapper sessionMapper;
	
	@InjectMocks
	private SessionController sessionController;
	
    private List<Session> sessionList;

    private List<SessionDto> sessionListDtos;
    
    private Session session;
    
    private SessionDto sessionDto;
    
    List<User> userList = new ArrayList<User>();
    
    List<Long> userIdList = new ArrayList<Long>();

	LocalDateTime rightNow = LocalDateTime.now();
	
	Date dateTest = new Date();
	
	@BeforeEach
	public void initTeacherAndSessions() {
        
		//ARRANGE : on créer un teacher, une session, une sessionDto, une liste de sessions et une liste de sessionDto
        Teacher teacher = new Teacher(1L, "test1", "test1", rightNow, rightNow);
		this.session = new Session(1L, "test session", dateTest, "test session", teacher, userList, rightNow, rightNow);
		this.sessionDto = new SessionDto(1L, "test session", dateTest, 1L, "test session", userIdList, rightNow, rightNow);
        this.sessionList = new ArrayList<>();
        this.sessionList.add(session);
        this.sessionList.add(new Session(2L, "test2 session", dateTest, "test2 session", teacher, userList, rightNow, rightNow));
        this.sessionListDtos = new ArrayList<>();
        this.sessionListDtos.add(sessionDto);
        this.sessionListDtos.add(new SessionDto(2L, "test2 session", dateTest, 2L, "test2 session", userIdList, rightNow, rightNow));
	}

	@Test
	@DisplayName("Cherche une session par son Id")
	public void givenSessionId_whenFindById_thenResponseIsOkAndContainSessionInfo() {

		// ARRANGE : Id Session, mock de sessionService et sessionMapper
		Long sessionId = 1L;
		when(sessionService.getById(sessionId)).thenReturn(session);
		when(sessionMapper.toDto(session)).thenReturn(sessionDto);

		// ACT : appel de findById de sessionController
		ResponseEntity<?> response = sessionController.findById(session.getId().toString());

		// ASSERT : on veux un status Ok, et les infos session sous forme de sessionDto
		SessionDto sessionDtoBody = (SessionDto) response.getBody();
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(session.getId(), sessionDtoBody.getId());
		assertEquals(session.getName(), sessionDtoBody.getName());
		assertEquals(session.getDescription(), sessionDtoBody.getDescription());
		assertEquals(session.getCreatedAt(), sessionDtoBody.getCreatedAt());
		assertEquals(session.getUpdatedAt(), sessionDtoBody.getUpdatedAt());
		assertEquals(session.getTeacher().getId(), sessionDtoBody.getTeacher_id());
		assertEquals(session.getDate(), sessionDtoBody.getDate());
	}
	
	@Test
	@DisplayName("Cherche une session par son Id mais ne le trouve pas")
	public void givenSessionIdNotFound_whenFindById_thenThrowNotFoundException() {

		// ARRANGE : mock de sessionService
		Long id = 1L;
		when(sessionService.getById(id)).thenReturn(null);

		// ACT : appel de findById de sessionController
		ResponseEntity<?> response = sessionController.findById(id.toString());

		// ASSERT : on veux un statut NOT_FOUND
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
	}
	
	@Test
	@DisplayName("Cherche une session par son Id mais son Id n'est pas un nombre")
	public void givenSessionIdWhereIdIsNotANumber_whenFindById_thenThrowBadRequestException() {

		// ACT : appel de findById de sessionController
		ResponseEntity<?> response = sessionController.findById("notANumber");

		// ASSERT : on veux un status BAD_REQUEST
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}
	
	@Test
	@DisplayName("Récupère la liste des sessions")
	public void givenSessionList_whenFindAll_thenResponseIsOkAndContainListOfSessionDto() {

		// ARRANGE : mock de sessionService et sessionMapper
		when(sessionService.findAll()).thenReturn(sessionList);
		when(sessionMapper.toDto(sessionList)).thenReturn(sessionListDtos);

		// ACT : appel de findAll de sessionController
		ResponseEntity<?> response = sessionController.findAll();

		// ASSERT : on veux un status Ok, et la liste des sessions sous forme de List<SessionDto>
		List<SessionDto> sessionDtoBody = (List<SessionDto>) response.getBody();
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(sessionList.get(0).getId(), sessionDtoBody.get(0).getId());
		assertEquals(sessionList.get(0).getName(), sessionDtoBody.get(0).getName());
		assertEquals(sessionList.get(0).getDescription(), sessionDtoBody.get(0).getDescription());
		assertEquals(sessionList.get(1).getId(), sessionDtoBody.get(1).getId());
		assertEquals(sessionList.get(1).getName(), sessionDtoBody.get(1).getName());
		assertEquals(sessionList.get(1).getDescription(), sessionDtoBody.get(1).getDescription());
	}
	
	@Test
	@DisplayName("Créer une session")
	public void givenSessionDto_whenCreate_thenResponseIsOkAndContainSessionDto() {

		// ARRANGE : mock sessionService et sessionMapper
		when(sessionService.create(session)).thenReturn(session);
		when(sessionMapper.toEntity(sessionDto)).thenReturn(session);
		when(sessionMapper.toDto(session)).thenReturn(sessionDto);

		// ACT : appel de create de sessionController
		ResponseEntity<?> response = sessionController.create(sessionDto);

		// ASSERT : on veux un status Ok, et les infos session sous forme de sessionDto
		SessionDto sessionDtoBody = (SessionDto) response.getBody();
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(session.getId(), sessionDtoBody.getId());
		assertEquals(session.getName(), sessionDtoBody.getName());
		assertEquals(session.getDescription(), sessionDtoBody.getDescription());
		assertEquals(session.getCreatedAt(), sessionDtoBody.getCreatedAt());
		assertEquals(session.getUpdatedAt(), sessionDtoBody.getUpdatedAt());
		assertEquals(session.getTeacher().getId(), sessionDtoBody.getTeacher_id());
		assertEquals(session.getDate(), sessionDtoBody.getDate());
	}
	
	@Test
	@DisplayName("Mettre à jour une session")
	public void givenSessionIdAndSessionDto_whenUpdate_thenResponseIsOkAndTheSameOfSessionDto() {

		// ARRANGE : mock sessionService et sessionMapper
		Long sessionId = 1L;
		when(sessionService.update(sessionId, session)).thenReturn(session);
		when(sessionMapper.toEntity(sessionDto)).thenReturn(session);
		when(sessionMapper.toDto(session)).thenReturn(sessionDto);

		// ACT : appel de update de sessionController
		ResponseEntity<?> response = sessionController.update(sessionDto.getId().toString(), sessionDto);

		// ASSERT : on veux un status Ok, et les infos session mise à jour
		SessionDto sessionDtoBody = (SessionDto) response.getBody();
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(sessionDto.getId(), sessionDtoBody.getId());
		assertEquals(sessionDto.getName(), sessionDtoBody.getName());
		assertEquals(sessionDto.getDescription(), sessionDtoBody.getDescription());
		assertEquals(sessionDto.getCreatedAt(), sessionDtoBody.getCreatedAt());
		assertEquals(sessionDto.getUpdatedAt(), sessionDtoBody.getUpdatedAt());
		assertEquals(sessionDto.getTeacher_id(), sessionDtoBody.getTeacher_id());
		assertEquals(sessionDto.getDate(), sessionDtoBody.getDate());
	}
	
	@Test
	@DisplayName("Mettre à jour une session mais l'Id n'est pas un nombre")
	public void givenSessionIdWhereIdIsNotANumber_whenUpdate_thenThrowBadRequestException() {

		// ACT : appel de update de sessionController
		ResponseEntity<?> response = sessionController.update("notANumber", sessionDto);

		// ASSERT : on veux un status BAD_REQUEST
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}
	
	@Test
	@DisplayName("Supprime une session")
	public void givenSessionId_whenDelete_thenResponseIsOk() {

		// ARRANGE : un sessionId et un mock de sessionService
		Long id = 1L;
		when(sessionService.getById(id)).thenReturn(session);
		sessionService.delete(id);

		// ACT : appel de delete de sessionController
		ResponseEntity<?> response = sessionController.delete(id.toString());

		// ASSERT : on veux un status Ok
		assertEquals(HttpStatus.OK, response.getStatusCode());
	}
	
	@Test
	@DisplayName("Supprime une session pqr son Id mais ne le trouve pas")
	public void givenSessionIdNotFound_whenDelete_thenThrowNotFoundException() {

		// ARRANGE : un sessionId et un mock de sessionService
		Long id = 1L;
		when(sessionService.getById(id)).thenReturn(null);

		// ACT : appel de delete de sessionController
		ResponseEntity<?> response = sessionController.delete(id.toString());

		// ASSERT : on veux un statut NOT_FOUND
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
	}
	
	@Test
	@DisplayName("Supprime une session par son Id mais son Id n'est pas un nombre")
	public void givenSessionIdWhereIdIsNotANumber_whenDelete_thenThrowBadRequestException() {

		// ACT : appel de delete de sessionController
		ResponseEntity<?> response = sessionController.delete("notANumber");

		// ASSERT : on veux un statut BAD_REQUEST
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}
	
	@Test
	@DisplayName("Inscrire un user à une session")
	public void givenSessionIdAndUserId_whenParticipate_thenResponseIsOk() {

		// ARRANGE : un sessionId, un userId
		Long sessionId = 1L;
		Long userId = 1L;
		sessionService.participate(sessionId, userId);

		// ACT : appel de participate de sessionController
		ResponseEntity<?> response = sessionController.participate(sessionId.toString(), userId.toString());

		// ASSERT : on veux un statut Ok
		assertEquals(HttpStatus.OK, response.getStatusCode());
	}
	
	@Test
	@DisplayName("Inscrire un user à une session mais l'Id de la session ou l'Id du user n'est pas un nombre")
	public void givenSessionIdOrUserIdWhereIdIsNotANumber_whenParticipate_thenThrowBadRequestException() {

		// ACT : appel de participate de sessionController
		ResponseEntity<?> response = sessionController.participate("notANumber", "notANumber");

		// ASSERT : on veux un statut BAD_REQUEST
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}
	
	@Test
	@DisplayName("Désinscrire un user à une session")
	public void givenSessionIdAndUserId_whenNoLongerParticipate_thenResponseIsOk() {

		// ARRANGE : un sessionId, un userId
		Long sessionId = 1L;
		Long userId = 1L;
		sessionService.noLongerParticipate(sessionId, userId);

		// ACT : appel de noLongerParticipate de sessionController
		ResponseEntity<?> response = sessionController.noLongerParticipate(sessionId.toString(), userId.toString());

		// ASSERT : on veux un statut Ok
		assertEquals(HttpStatus.OK, response.getStatusCode());
	}
	
	@Test
	@DisplayName("Désinscrire un user à une session mais l'Id de la session ou l'Id du user n'est pas un nombre")
	public void givenSessionIdOrUserIdWhereIdIsNotANumber_whenNoLongerParticipate_thenThrowBadRequestException() {

		// ACT : appel de noLongerParticipate de sessionController
		ResponseEntity<?> response = sessionController.noLongerParticipate("notANumber", "notANumber");

		// ASSERT : on veux un statut BAD_REQUEST
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}
}
