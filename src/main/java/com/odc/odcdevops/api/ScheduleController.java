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
import com.odc.odcdevops.entites.Schedule;
import com.odc.odcdevops.exception.ResourceNotFoundException;
import com.odc.odcdevops.repository.ScheduleRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/schedules")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Tag(name = "Gestion des Emplois du temps", description = "APIs pour gérer les cours planifiés")
public class ScheduleController {

    private final ScheduleRepository scheduleRepository;

    @GetMapping
    @Operation(summary = "Lister tous les emplois du temps")
    public ResponseEntity<List<Schedule>> getAllSchedules() {
        return ResponseEntity.ok(scheduleRepository.findAll());
    }

    @GetMapping("/teacher/{teacherId}")
    @Operation(summary = "Obtenir l'emploi du temps d'un professeur")
    public ResponseEntity<List<Schedule>> getSchedulesByTeacher(@PathVariable Long teacherId) {
        return ResponseEntity.ok(scheduleRepository.findByTeacherId(teacherId));
    }

    @PostMapping
    @Operation(summary = "Ajouter un emploi du temps")
    public ResponseEntity<Schedule> createSchedule(@RequestBody Schedule schedule) {
        return new ResponseEntity<>(scheduleRepository.save(schedule), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une ligne d'emploi du temps")
    public ResponseEntity<Void> deleteSchedule(@PathVariable Long id) {
        if (!scheduleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Emploi du temps introuvable");
        }
        scheduleRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
