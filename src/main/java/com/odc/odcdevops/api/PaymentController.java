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
import com.odc.odcdevops.entites.Payment;
import com.odc.odcdevops.exception.ResourceNotFoundException;
import com.odc.odcdevops.repository.PaymentRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Tag(name = "Gestion des Paiements", description = "APIs pour gérer la comptabilité des étudiants")
public class PaymentController {

    private final PaymentRepository paymentRepository;

    @GetMapping
    @Operation(summary = "Lister tous les paiements")
    public ResponseEntity<List<Payment>> getAllPayments() {
        return ResponseEntity.ok(paymentRepository.findAll());
    }

    @GetMapping("/student/{matricule}")
    @Operation(summary = "Obtenir les paiements d'un étudiant")
    public ResponseEntity<List<Payment>> getPaymentsByStudent(@PathVariable Long matricule) {
        return ResponseEntity.ok(paymentRepository.findByStudentMatricule(matricule));
    }

    @PostMapping
    @Operation(summary = "Enregistrer un paiement")
    public ResponseEntity<Payment> createPayment(@RequestBody Payment payment) {
        return new ResponseEntity<>(paymentRepository.save(payment), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Annuler un paiement")
    public ResponseEntity<Void> deletePayment(@PathVariable Long id) {
        if (!paymentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Paiement introuvable");
        }
        paymentRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
