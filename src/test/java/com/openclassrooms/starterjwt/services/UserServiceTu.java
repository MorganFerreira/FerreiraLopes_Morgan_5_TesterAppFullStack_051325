package com.openclassrooms.starterjwt.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;

@SpringBootTest
public class UserServiceTu {

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private UserService userService;
	
	LocalDateTime rightNow = LocalDateTime.now();

	@Test
	@DisplayName("Supprimer un utilisateur")
	public void givenUser_whenDelete_thenUserDeleted() {
		
		// ACT
		userService.delete(1L);
		
		// ASSERT : on veux que userRepository.deleteById soit appelé une fois
		verify(userRepository, times(1)).deleteById(1L);
	}

	@Test
	@DisplayName("Récupérer un user par son Id")
	public void givenId_whenFindById_thenUserFindById() {
	
		// ARRANGE : on créer un user et mock de userRepository
		User user1 = new User(1L, "test1@gmail.com", "test1", "test1", "motdepass", false, rightNow, rightNow);
		when(userRepository.findById(1L)).thenReturn(Optional.of(user1));

		// ACT : appel à findBydId de UserService
		User resultUser1 = userService.findById(1L);

		// ASSERT : on veux retrouver les même objets et vérifier que userRepository à été appelé une fois
		assertEquals(user1, resultUser1);
		verify(userRepository, times(1)).findById(1L);
	}
}