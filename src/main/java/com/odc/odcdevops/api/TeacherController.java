package com.odc.odcdevops.api;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.odc.odcdevops.entites.Teacher;
import com.odc.odcdevops.exception.ResourceNotFoundException;
import com.odc.odcdevops.repository.TeacherRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/teachers")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Tag(name = "Gestion des Professeurs", description = "APIs CRUD pour les professeurs")
public class TeacherController {

    private final TeacherRepository teacherRepository;

    @GetMapping
    @Operation(summary = "Lister tous les professeurs")
    public ResponseEntity<List<Teacher>> getAllTeachers() {
        return ResponseEntity.ok(teacherRepository.findAll());
    }

    @PostMapping
    @Operation(summary = "Ajouter un professeur")
    public ResponseEntity<Teacher> createTeacher(@RequestBody Teacher teacher) {
        return new ResponseEntity<>(teacherRepository.save(teacher), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un professeur")
    public ResponseEntity<Void> deleteTeacher(@PathVariable Long id) {
        if (!teacherRepository.existsById(id)) {
            throw new ResourceNotFoundException("Professeur introuvable");
        }
        teacherRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
