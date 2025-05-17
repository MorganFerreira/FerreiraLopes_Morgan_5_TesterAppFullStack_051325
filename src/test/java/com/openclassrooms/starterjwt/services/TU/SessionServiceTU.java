package com.openclassrooms.starterjwt.services.TU;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import com.openclassrooms.starterjwt.exception.BadRequestException;
import com.openclassrooms.starterjwt.exception.NotFoundException;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import com.openclassrooms.starterjwt.repository.UserRepository;
import com.openclassrooms.starterjwt.services.SessionService;

@SpringBootTest
public class SessionServiceTU {

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SessionService sessionService;

    private List<Session> sessionList;

    private Session session;
    
    List<User> userList = new ArrayList<User>();
    
    LocalDateTime rightNow = LocalDateTime.now();
    
    Date dateTest = new Date();

    @BeforeEach
    public void initTeacherAndSessions() {

        //ARRANGE : on créer un teacher, une session et une liste de sessions
        Teacher teacher = new Teacher(1L, "test1", "test1", rightNow, rightNow);
		this.session = new Session(1L, "test session", dateTest, "test session", teacher, userList, rightNow, rightNow);
        this.sessionList = new ArrayList<>();
        this.sessionList.add(session);
        this.sessionList.add(new Session(2L, "test2 session", dateTest, "test2 session", teacher, userList, rightNow, rightNow));
    }

    @Test
    @DisplayName("Créer une session")
    public void givenSession_whenCreate_thenSaveSession() {

        //ARRANGE : mock de sessionsRepository
        when(sessionRepository.save(session)).thenReturn(session);

        //ACT : appel à create de sessionService
        Session sessionCreated = sessionService.create(session);

        // ASSERT : on veux retrouver les même objets et vérifier que sessionRepository a été appelé une fois
        assertEquals(session, sessionCreated);
        verify(sessionRepository, times(1)).save(session);

    }

    @Test
    @DisplayName("Supprimer une session")
    public void givenSession_whenDelete_thenSessionDeleted() {

        // ACT : appel à delete de sessionService
        sessionService.delete(1L);
        
		// ASSERT : on veux que sessionRepository.deleteById soit appelé une fois
        verify(sessionRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Récupére la liste des sessions")
    public void givenSessionList_whenFindAll_thenRetrieveAllSessions() {

        //ARRANGE : mock de SessionRepository
        when(sessionRepository.findAll()).thenReturn(sessionList);

        //ACT : appel à findAll de SessionService
        List<Session> resultListSession = sessionService.findAll();

        //ASSERT : on veux retrouver les même objets et vérifier que sessionRepository a été appelé une fois
        assertEquals(sessionList.get(0), resultListSession.get(0));
        assertEquals(sessionList.get(1), resultListSession.get(1));
        verify(sessionRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Récupére une session par son Id")
    public void givenId_whenGetById_thenSessionFindById() {

        //ARRANGE : mock de sessionRepository
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));

        //ACT : appel à getBydId de sessionService
        Session resultSession1 = sessionService.getById(1L);

        //ASSERT : on veux retrouver les même objets et vérifier que sessionRepository a été appelé une fois
        assertEquals(session, resultSession1);
        verify(sessionRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Mettre à jour une session par son Id")
    public void givenId_whenUpdate_thenSessionUpdateById() {

        //ARRANGE : mock de sessionRepository
        when(sessionRepository.save(session)).thenReturn(session);

        //ACT : appel à update de sessionService
        Session resultSessionToUpdate = sessionService.update(1L, session);

        //ASSERT : on veux retrouver les même objets et vérifier que sessionRepository à été appelé une fois
        assertEquals(session, resultSessionToUpdate);
        verify(sessionRepository, times(1)).save(session);
    }

    @Test
    @DisplayName("Inscrire un user à une session")
    public void givenIdSessionAndIdUser_whenParticipate_thenSessionContainUser() {

        // ARRANGE : un user, un mock de sessionRepository et un mock de userRepository
        User user1 = new User(1L, "test@gmail.com", "test", "test", "motdepass", false, rightNow, rightNow);
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));

        // ACT : appel à participate de sessionService
        sessionService.participate(1L, 1L);

        // ASSERT : on veux que la session contienne le user et vérifier que les repositorys soient appelés une fois chacun
        assertTrue(session.getUsers().contains(user1));
        verify(sessionRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Désinscrire un user à une session")
    public void givenIdSessionAndIdUser_whenNoLongerParticipate_thenSessionNoLongerContainUser() {

        // ARRANGE : un user et mock de sessionRepository
        User user1 = new User(1L, "test@gmail.com", "test", "test", "motdepass", false, rightNow, rightNow);
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
        session.getUsers().add(user1);

        // ACT : appel à noLongerParticipate de sessionService
        sessionService.noLongerParticipate(1L, 1L);

        // ASSERT : on veux avoir la session sans le user et vérifier que sessionRepository à été appelé une fois
        assertFalse(session.getUsers().contains(user1));
        verify(sessionRepository, times(1)).findById(1L);
    }
    
    @Test
    @DisplayName("Inscrire un user à une session inexistante")
    public void givenIdSessionAndIdUser_whenParticipateToInexistantSession_thenThrowSessionNotFoundException() {

        //ARRANGE : un user, un mock de userRepository et mock de sessionRepository sans session
        User user1 = new User(1L, "test@gmail.com", "test", "test", "motdepass", false, rightNow, rightNow);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(sessionRepository.findById(1L)).thenReturn(Optional.empty());

        //ASSERT : on veux une exception de type NotFoundException lors de l'appel à participate
        assertThrows(NotFoundException.class, () -> sessionService.participate(1L, 1L));
    }
    
    @Test
    @DisplayName("Inscrire un user à une session où il est déjà inscrit")
    public void givenIdSessionAndIdUser_whenParticipateToSessionWhereAlrdyParticipate_thenThrowBadRequestException() {

        // ARRANGE : un user et une session avec ce user
        User user1 = new User(1L, "test@gmail.com", "test", "test", "motdepass", false, rightNow, rightNow);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
        session.getUsers().add(user1);

        // ASSERT : on veux une exception de type BadRequestException lors de l'appel à participate
        assertThrows(BadRequestException.class, () -> sessionService.participate(1L, 1L));
    }
    
    @Test
    @DisplayName("Inscrire un user inexistant à une session")
    public void givenIdSession_whenParticipateWithInexistantUser_thenThrowNotFoundException() {

        //ARRANGE : une session et user inexistant
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        //ASSERT : on veux une exception de type NotFoundException lors de l'appel à participate
        assertThrows(NotFoundException.class, () -> sessionService.participate(1L, 1L));
    }
    
    @Test
    @DisplayName("Désinscrire un user à une session où il n'est pas inscrit")
    public void givenIdSessionAndIdUser_whenNoLongerParticipateWhereUserNotParticipate_thenThrowBadRequestException() {

    	//ARRANGE : une session sans user
         when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));

        //ASSERT : on veux une exception de type BadRequestException lors de l'appel à noLongerParticipate
        assertThrows(BadRequestException.class, () -> sessionService.noLongerParticipate(1L, 1L));        

    }
    
    @Test
    @DisplayName("Désinscrire un user à une session inexistante")
    public void givenIdUser_whenNoLongerParticipateWhereSessionNotFound_thenThrowNotFoundException() {

    	//ARRANGE : une session inexistante et un user
         when(sessionRepository.findById(1L)).thenReturn(Optional.empty());
         User user1 = new User(1L, "test@gmail.com", "test", "test", "motdepass", false, rightNow, rightNow);
         when(userRepository.findById(1L)).thenReturn(Optional.of(user1));

        //ASSERT : on veux une exception de type NotFoundException lors de l'appel à noLongerParticipate
        assertThrows(NotFoundException.class, () -> sessionService.noLongerParticipate(1L, 1L));        

    }
}