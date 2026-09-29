package com.odc.odcdevops.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.odc.odcdevops.entites.Grade;

@Repository
public interface GradeRepository extends JpaRepository<Grade, Long> {
    List<Grade> findByStudentMatricule(Long matricule);
    List<Grade> findByCourseId(Long courseId);
}
