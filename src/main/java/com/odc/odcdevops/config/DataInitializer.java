package com.odc.odcdevops.config;

import java.sql.Date;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.odc.odcdevops.entites.Course;
import com.odc.odcdevops.entites.Grade;
import com.odc.odcdevops.entites.Student;
import com.odc.odcdevops.entites.Teacher;
import com.odc.odcdevops.entites.Schedule;
import com.odc.odcdevops.entites.Payment;
import com.odc.odcdevops.repository.CourseRepository;
import com.odc.odcdevops.repository.GradeRepository;
import com.odc.odcdevops.repository.StudentRepository;
import com.odc.odcdevops.repository.TeacherRepository;
import com.odc.odcdevops.repository.ScheduleRepository;
import com.odc.odcdevops.repository.PaymentRepository;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initDatabase(
        StudentRepository studentRepository, 
        CourseRepository courseRepository, 
        GradeRepository gradeRepository,
        TeacherRepository teacherRepository,
        ScheduleRepository scheduleRepository,
        PaymentRepository paymentRepository
    ) {
        return args -> {
            if (studentRepository.count() == 0) {
                // Seed Students
                List<Student> students = studentRepository.saveAll(Arrays.asList(
                    Student.builder().nom("Diallo").prenom("Fatoumata").addresse("Dakar").dateNaissance(Date.valueOf("2001-05-14")).build(),
                    Student.builder().nom("Traoré").prenom("Amadou").addresse("Bamako").dateNaissance(Date.valueOf("2000-11-23")).build(),
                    Student.builder().nom("Kouassi").prenom("Yao Jean").addresse("Abidjan").dateNaissance(Date.valueOf("2002-02-18")).build()
                ));

                // Seed Courses
                List<Course> courses = courseRepository.saveAll(Arrays.asList(
                    Course.builder().name("Mathématiques").credits(4).description("Algèbre et Analyse").build(),
                    Course.builder().name("Développement Web").credits(3).description("Spring Boot & React").build(),
                    Course.builder().name("Bases de Données").credits(3).description("SQL & PostgreSQL").build()
                ));

                // Seed Grades
                gradeRepository.saveAll(Arrays.asList(
                    Grade.builder().student(students.get(0)).course(courses.get(0)).score(15.5).comments("Très bon travail").build(),
                    Grade.builder().student(students.get(0)).course(courses.get(1)).score(18.0).comments("Excellent").build(),
                    Grade.builder().student(students.get(1)).course(courses.get(0)).score(12.0).comments("Peut mieux faire").build(),
                    Grade.builder().student(students.get(2)).course(courses.get(2)).score(14.5).comments("Bon niveau").build()
                ));

                // Seed Teachers
                List<Teacher> teachers = teacherRepository.saveAll(Arrays.asList(
                    Teacher.builder().nom("Sow").prenom("Ousmane").email("ousmane.sow@ecole.com").specialty("Mathématiques").build(),
                    Teacher.builder().nom("Bamba").prenom("Aline").email("aline.bamba@ecole.com").specialty("Informatique").build()
                ));

                // Seed Schedules
                scheduleRepository.saveAll(Arrays.asList(
                    Schedule.builder().course(courses.get(0)).teacher(teachers.get(0)).dayOfWeek("Lundi").startTime(LocalTime.of(8, 0)).endTime(LocalTime.of(10, 0)).room("Salle 101").build(),
                    Schedule.builder().course(courses.get(1)).teacher(teachers.get(1)).dayOfWeek("Mardi").startTime(LocalTime.of(14, 0)).endTime(LocalTime.of(16, 0)).room("Labo Informatique").build()
                ));

                // Seed Payments
                paymentRepository.saveAll(Arrays.asList(
                    Payment.builder().student(students.get(0)).amount(50000.0).status("PAID").build(),
                    Payment.builder().student(students.get(1)).amount(25000.0).status("PENDING").build()
                ));
            }
        };
    }
}
