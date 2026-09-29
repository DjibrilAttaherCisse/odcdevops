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
import com.odc.odcdevops.entites.Grade;
import com.odc.odcdevops.exception.ResourceNotFoundException;
import com.odc.odcdevops.repository.GradeRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/grades")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Tag(name = "Gestion des Notes", description = "APIs pour attribuer et consulter des notes")
public class GradeController {

    private final GradeRepository gradeRepository;

    @GetMapping("/student/{matricule}")
    @Operation(summary = "Obtenir toutes les notes d'un étudiant")
    public ResponseEntity<List<Grade>> getGradesByStudent(@PathVariable Long matricule) {
        return ResponseEntity.ok(gradeRepository.findByStudentMatricule(matricule));
    }

    @PostMapping
    @Operation(summary = "Ajouter une note à un étudiant")
    public ResponseEntity<Grade> addGrade(@RequestBody Grade grade) {
        return new ResponseEntity<>(gradeRepository.save(grade), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une note")
    public ResponseEntity<Void> deleteGrade(@PathVariable Long id) {
        if (!gradeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Note introuvable");
        }
        gradeRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
