package com.odc.odcdevops.services.impl;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.odc.odcdevops.entites.Student;
import com.odc.odcdevops.exception.ResourceNotFoundException;
import com.odc.odcdevops.repository.StudentRepository;
import com.odc.odcdevops.services.StudentService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Student> getStudentById(Long matricule) {
        return studentRepository.findById(matricule);
    }

    @Override
    public Student saveStudent(Student student) {
        return studentRepository.save(student);
    }

    @Override
    public Student updateStudent(Long matricule, Student studentDetails) {
        Student existingStudent = studentRepository.findById(matricule)
                .orElseThrow(() -> new ResourceNotFoundException("Étudiant introuvable avec le matricule : " + matricule));

        existingStudent.setNom(studentDetails.getNom());
        existingStudent.setPrenom(studentDetails.getPrenom());
        existingStudent.setAddresse(studentDetails.getAddresse());
        existingStudent.setDateNaissance(studentDetails.getDateNaissance());

        return studentRepository.save(existingStudent);
    }

    @Override
    public void deleteStudent(Long matricule) {
        Student existingStudent = studentRepository.findById(matricule)
                .orElseThrow(() -> new ResourceNotFoundException("Étudiant introuvable avec le matricule : " + matricule));
        studentRepository.delete(existingStudent);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Student> searchStudents(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return studentRepository.findAll();
        }
        return studentRepository.searchStudents(keyword.trim());
    }

    @Override
    @Transactional(readOnly = true)
    public long countStudents() {
        return studentRepository.count();
    }
}
