package com.openclassrooms.starterjwt.controllers.TU;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.openclassrooms.starterjwt.controllers.TeacherController;
import com.openclassrooms.starterjwt.dto.TeacherDto;
import com.openclassrooms.starterjwt.mapper.TeacherMapper;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.services.TeacherService;

@SpringBootTest
public class TeacherControllerTU {

	@Mock
	private TeacherMapper teacherMapper;
	
	@Mock
	private TeacherService teacherService;
	
	@InjectMocks
	private TeacherController teacherController;
	
	LocalDateTime rightNow = LocalDateTime.now();
	
	@Test
	@DisplayName("Cherche un teacher par son Id")
	public void givenTeacherId_whenFindById_thenResponseIsOkAndContainTeacherInfo() {
		
		// ARRANGE : un teacher, un teacherDto, mock de teacherService et teacherMapper
		Teacher teacher = new Teacher(1L, "teacher1", "teacher1", rightNow, rightNow);
		TeacherDto teacherDto = new TeacherDto(1L, "teacher1", "teacher1", rightNow, rightNow);
		when(teacherService.findById(1L)).thenReturn(teacher);
		when(teacherMapper.toDto(teacher)).thenReturn(teacherDto);
		
		// ACT : appel de findById de teacherController
		ResponseEntity<?> response = teacherController.findById(teacher.getId().toString());
		
		// ASSERT : on veux un status Ok, et les infos teacher sous forme de teacherDto
		TeacherDto teacherDtoBody = (TeacherDto) response.getBody();
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(teacher.getId(), teacherDtoBody.getId());
		assertEquals(teacher.getFirstName(), teacherDtoBody.getFirstName());
		assertEquals(teacher.getLastName(), teacherDtoBody.getLastName());
		assertEquals(teacher.getCreatedAt(), teacherDtoBody.getCreatedAt());
		assertEquals(teacher.getUpdatedAt(), teacherDtoBody.getUpdatedAt());
	}
	
	@Test
	@DisplayName("Cherche un teacher par son Id mais ne le trouve pas")
	public void givenTeacherIdNotFound_whenFindById_thenThrowNotFoundException() {

		// ARRANGE : mock de teacherService
        Long id = 1L;
		when(teacherService.findById(id)).thenReturn(null);

		// ACT : appel de findById de teacherController
		ResponseEntity<?> response = teacherController.findById(id.toString());

		// ASSERT : on veux un status NOT_FOUND
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
	}
	
	@Test
	@DisplayName("Cherche un teacher par son Id mais son Id n'est pas un nombre")
	public void givenTeacherWhereIdIsNotANumber_whenFindById_thenThrowBadRequestException() {

		// ACT : appel de findById de teacherController
		ResponseEntity<?> response = teacherController.findById("notANumber");

		// ASSERT : on veux un status BAD_REQUEST
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}
	
	@Test
	@DisplayName("Récupère la liste des teachers")
	public void givenTeachers_whenFindAll_thenResponseIsOkAndContainListOfTeacherInfo() {

		// ARRANGE : une liste de teacher, une liste de teacherDto, mock de teacherService et teacherMapper
		Teacher teacher1 = new Teacher(1L, "teacher1", "teacher1", rightNow, rightNow);
		Teacher teacher2 = new Teacher(2L, "teacher2", "teacher2", rightNow, rightNow);
		List<Teacher> teachers = new ArrayList<>();
		teachers.add(teacher1);
		teachers.add(teacher2);

		TeacherDto teacherDto1 = new TeacherDto(1L, "teacher1", "teacher1", rightNow, rightNow);
		TeacherDto teacherDto2 = new TeacherDto(2L, "teacher2", "teacher2", rightNow, rightNow);
		List<TeacherDto> teacherDtos = new ArrayList<>();
		teacherDtos.add(teacherDto1);
		teacherDtos.add(teacherDto2);

		when(teacherService.findAll()).thenReturn(teachers);
		when(teacherMapper.toDto(teachers)).thenReturn(teacherDtos);

		// ACT : appel de findAll de teacherController
		ResponseEntity<?> response = teacherController.findAll();

		// ASSERT : on veux un status Ok et la liste des teachers sous forme de teacherDtos
		List<TeacherDto> resultList = (List<TeacherDto>) response.getBody();
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(teachers.get(0).getId(), resultList.get(0).getId());
		assertEquals(teachers.get(1).getFirstName(), resultList.get(1).getFirstName());
	}
	
}
