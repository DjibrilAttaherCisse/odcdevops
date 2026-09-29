package com.odc.odcdevops.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.odc.odcdevops.entites.Student;
import com.odc.odcdevops.exception.ResourceNotFoundException;
import com.odc.odcdevops.services.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/students")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Tag(name = "Gestion des Étudiants", description = "APIs CRUD complètes pour la gestion des étudiants")
public class StudentController {

    private final StudentService studentService;

    @GetMapping
    @Operation(summary = "Lister tous les étudiants ou rechercher par mot-clé")
    public ResponseEntity<List<Student>> getAllStudents(
            @RequestParam(required = false) String search) {
        if (search != null && !search.trim().isEmpty()) {
            return ResponseEntity.ok(studentService.searchStudents(search));
        }
        return ResponseEntity.ok(studentService.getAllStudents());
    }

    @GetMapping("/{matricule}")
    @Operation(summary = "Obtenir un étudiant par son matricule")
    public ResponseEntity<Student> getStudentById(@PathVariable Long matricule) {
        Student student = studentService.getStudentById(matricule)
                .orElseThrow(() -> new ResourceNotFoundException("Étudiant introuvable avec le matricule : " + matricule));
        return ResponseEntity.ok(student);
    }

    @PostMapping
    @Operation(summary = "Créer un nouvel étudiant")
    public ResponseEntity<Student> createStudent(@RequestBody Student student) {
        Student created = studentService.saveStudent(student);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{matricule}")
    @Operation(summary = "Mettre à jour un étudiant existant")
    public ResponseEntity<Student> updateStudent(
            @PathVariable Long matricule,
            @RequestBody Student studentDetails) {
        Student updated = studentService.updateStudent(matricule, studentDetails);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{matricule}")
    @Operation(summary = "Supprimer un étudiant par son matricule")
    public ResponseEntity<Map<String, String>> deleteStudent(@PathVariable Long matricule) {
        studentService.deleteStudent(matricule);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Étudiant avec le matricule " + matricule + " supprimé avec succès.");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/stats")
    @Operation(summary = "Statistiques sur les étudiants")
    public ResponseEntity<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalStudents", studentService.countStudents());
        return ResponseEntity.ok(stats);
    }
}
