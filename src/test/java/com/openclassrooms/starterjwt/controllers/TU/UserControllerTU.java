package com.openclassrooms.starterjwt.controllers.TU;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.openclassrooms.starterjwt.controllers.UserController;
import com.openclassrooms.starterjwt.dto.UserDto;
import com.openclassrooms.starterjwt.mapper.UserMapper;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;
import com.openclassrooms.starterjwt.services.UserService;

@SpringBootTest
public class UserControllerTU {

	@Mock
    private UserMapper userMapper;
    
	@Mock
    private UserService userService;
    
	@Mock
    private SecurityContext securityContext;
    
	@InjectMocks
    private UserController userController;

    LocalDateTime rightNow = LocalDateTime.now();

    @Test
    @DisplayName("Cherche un user par son Id")
    public void givenUserId_whenFindById_thenResponseIsOkAndContainUserInfo() {
            
    	// ARRANGE : un user, un userDto, mock de userService et userMapper
		User user = new User(1L, "yoga@studio.com", "Admin", "Admin", "test!1234", true, rightNow, rightNow);
    	UserDto userDto = new UserDto(1L, "yoga@studio.com", "Admin", "Admin", true, "test!1234", rightNow, rightNow);
    	when(userService.findById(1L)).thenReturn(user);
    	when(userMapper.toDto(user)).thenReturn(userDto);
    	
    	// ACT : appel de findById de userController
    	ResponseEntity<?> response = userController.findById(user.getId().toString());

    	// ASSERT : on veux un status Ok, et les infos user sous forme de userDto
    	UserDto userDtoBody = (UserDto) response.getBody();
    	assertEquals(HttpStatus.OK, response.getStatusCode());
    	assertEquals(user.getId(), userDtoBody.getId());
    	assertEquals(user.getEmail(), userDtoBody.getEmail());
    	assertEquals(user.getPassword(), userDtoBody.getPassword());
    	assertEquals(user.getLastName(), userDtoBody.getLastName());
    	assertEquals(user.getFirstName(), userDtoBody.getFirstName());
    	assertEquals(user.getCreatedAt(), userDtoBody.getCreatedAt());
    	assertEquals(user.getUpdatedAt(), userDtoBody.getUpdatedAt());
    }

    @Test
    @DisplayName("Cherche un user par son Id mais ne le trouve pas")
    public void givenUserIdNotFound_whenFindById_thenThrowNotFoundException() {
    	
    	// ARRANGE : mock de userService
    	Long id = 1L;
    	when(userService.findById(id)).thenReturn(null);

    	// ACT : appel de findById de userController
    	ResponseEntity<?> response = userController.findById(id.toString());

    	// ASSERT : on veux un statut NOT_FOUND
    	assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("Cherche un user par son Id mais son Id n'est pas un nombre")
    public void givenUserIdWhereIdIsNotANumber_whenFindById_thenThrowBadRequestException() {
    	
    	// ACT : appel de findById de userController
    	ResponseEntity<?> response = userController.findById("notANumber");

    	// ASSERT : on veux un statut BAD REQUEST
    	assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("Supprime un user")
    public void givenUserId_whenDeleteById_thenResponseIsOk() {
    	
    	// ARRANGE : un user, un UserDetailsImpl, un mock de authentication et userService
    	Long id = 1L;
		User user = new User(1L, "yoga@studio.com", "Admin", "Admin", "test!1234", true, rightNow, rightNow);
    	UserDetailsImpl userDetailsImpl = new UserDetailsImpl(1L, "yoga@studio.com", "Admin", "Admin", true, "test!1234");
    	Authentication authentication = new UsernamePasswordAuthenticationToken(userDetailsImpl, null);
    	SecurityContextHolder.setContext(securityContext);

    	when(securityContext.getAuthentication()).thenReturn(authentication);
    	when(userService.findById(id)).thenReturn(user);

    	// ACT : appel de deleteById de userController
    	ResponseEntity<?> response = userController.deleteById(id.toString());

    	// ASSERT : on veux un statut Ok
    	assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("Supprime un user inexistant")
    public void givenUserIdNotFound_whenDeleteById_thenThrowNotFoundException() {
    	
    	// ARRANGE : mock de userService
    	Long id = 1L;
    	when(userService.findById(id)).thenReturn(null);

    	// ACT : appel de deleteById de userController
    	ResponseEntity<?> response = userController.deleteById(id.toString());

    	// ASSERT : on veux un statut NOT_FOUND
    	assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("Supprime un user mais userDetailsImpl.username != user.email")
    public void givenEmailUserAndUsernameOfUserDetailsImplDifferent_whenDeleteById_thenThrowUnauthorizedException() {
    	
    	// ARRANGE : un user, un UserDetailsImpl, un mock de authentication et userService
    	Long id = 1L;
		User user = new User(1L, "yoga@studio.com", "Admin", "Admin", "test!1234", true, rightNow, rightNow);
    	UserDetailsImpl userDetailsImpl = new UserDetailsImpl(1L, "email@different.com", "Admin", "Admin", true, "test!1234");
    	Authentication authentication = new UsernamePasswordAuthenticationToken(userDetailsImpl, null);
    	SecurityContextHolder.setContext(securityContext);

    	when(securityContext.getAuthentication()).thenReturn(authentication);
    	when(userService.findById(id)).thenReturn(user);

    	// ACT : appel de deleteById de userController
    	ResponseEntity<?> response = userController.deleteById(id.toString());

    	// ASSERT : on veux un statut UNAUTHORIZED
    	assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    @DisplayName("Delete un user par son Id mais son Id n'est pas un nombre")
    public void givenUserIdWhereIdIsNotANumber_whenDeleteById_thenThrowBadRequestException() {
    	
    	// ACT : appel de deleteById de userController
    	ResponseEntity<?> response = userController.deleteById("NotANumber");

    	// ASSERT : on veux un statut BAD REQUEST
    	assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

}
