package com.odc.odcdevops.services;

import java.util.List;
import java.util.Optional;
import com.odc.odcdevops.entites.Student;

public interface StudentService {

    List<Student> getAllStudents();

    Optional<Student> getStudentById(Long matricule);

    Student saveStudent(Student student);

    Student updateStudent(Long matricule, Student studentDetails);

    void deleteStudent(Long matricule);

    List<Student> searchStudents(String keyword);

    long countStudents();
}
