package com.odc.odcdevops.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.odc.odcdevops.entites.Course;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
}
