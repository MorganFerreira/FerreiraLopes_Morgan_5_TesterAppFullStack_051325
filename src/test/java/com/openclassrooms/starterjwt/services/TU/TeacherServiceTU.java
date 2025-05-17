package com.openclassrooms.starterjwt.services.TU;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
import com.openclassrooms.starterjwt.services.TeacherService;

@SpringBootTest
public class TeacherServiceTU {

    @Mock
    private TeacherRepository teacherRepository;

    @InjectMocks
    private TeacherService teacherService;

    LocalDateTime rightNow = LocalDateTime.now();

    @Test
    @DisplayName("Récupére la liste des teachers")
    public void givenTeacherList_whenFindAll_thenRetrieveAllTeachers() {
    
        // ARRANGE : on créer une liste de professeurs et mock de teacherRepository
        List<Teacher> teacherList = new ArrayList<Teacher>();
        teacherList.add(new Teacher(1L, "test1", "test1", rightNow, rightNow));
        teacherList.add(new Teacher(2L, "test2", "test2", rightNow, rightNow));
        when(teacherRepository.findAll()).thenReturn(teacherList);

        // ACT : appel à findAll de TeacherService
        List<Teacher> result = teacherService.findAll();

        // ASSERT : on veux retrouver les même objets et vérifier que teacherRepository a été appelé une fois
        assertEquals(teacherList.get(0), result.get(0));
        assertEquals(teacherList.get(1), result.get(1));
        verify(teacherRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Récupére un teacher par son Id")
    public void givenId_whenFindById_thenTeacherFindById() {
    
        // ARRANGE : on créer un teacher et mock de teacherRepository
        Teacher teacher1 = new Teacher(1L, "test1", "test1", rightNow, rightNow);
        when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher1));

        // ACT : appel à findBydId de TeacherService
        Teacher resultTeacher1 = teacherService.findById(1L);

        // ASSERT : on veux retrouver les même objets et vérifier que teacherRepository a été appelé une fois
        assertEquals(teacher1, resultTeacher1);
        verify(teacherRepository, times(1)).findById(1L);
    }
}
