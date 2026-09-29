package com.odc.odcdevops.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.odc.odcdevops.entites.Student;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    List<Student> findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase(String nom, String prenom);

    @Query("SELECT s FROM Student s WHERE " +
           "LOWER(s.nom) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(s.prenom) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(s.addresse) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Student> searchStudents(@Param("query") String query);
}
