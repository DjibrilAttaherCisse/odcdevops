package com.odc.odcdevops.entites;

import java.sql.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "students")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long matricule;

    private String nom;
    private String prenom;
    private String addresse;
    private Date dateNaissance;
    private Date createdDate;
    private Date updateDate;

    @PrePersist
    public void onPrePersist() {
        Date now = new Date(System.currentTimeMillis());
        if (this.createdDate == null) {
            this.createdDate = now;
        }
        this.updateDate = now;
    }

    @PreUpdate
    public void onPreUpdate() {
        this.updateDate = new Date(System.currentTimeMillis());
    }
}