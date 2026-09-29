package com.odc.odcdevops.entites;

import java.sql.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "payments")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_matricule", nullable = false)
    private Student student;

    private double amount;
    private Date paymentDate;
    
    // Status: PAID, PENDING, OVERDUE
    private String status;

    @PrePersist
    public void onPrePersist() {
        if (this.paymentDate == null) {
            this.paymentDate = new Date(System.currentTimeMillis());
        }
    }
}
