package course.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import course.entity.Student;
import course.entity.Course;
import course.service.StudentServiceImpl;
import course.service.CourseServiceImpl;
import javax.validation.Valid;
import org.springframework.validation.BindingResult;

@Controller
public class StudentController {

    @Autowired
    private StudentServiceImpl studentService;

    @Autowired
    private CourseServiceImpl courseService;

    @GetMapping("students")
    public String index(Model model) {
        List<Student> students = (List<Student>) studentService.findAllByOrderByFirstNameAsc();
        model.addAttribute("students", students);
        return "students";
    }

    @GetMapping(value = "student/new")
    public String newStudent(Model model) {
        model.addAttribute("student", new Student());
        model.addAttribute("title", "Add Student");
        return "studentForm";
    }

    @GetMapping(value = "student/edit/{id}")
    public String editStudent(@PathVariable("id") Long studentId, Model model) {
        model.addAttribute("student", studentService.findStudentById(studentId));
        model.addAttribute("title", "Edit Student");
        return "studentForm";
    }

    @GetMapping("student/{id}")
    public String showStudent(@PathVariable("id") Long studentId, Model model) {
        model.addAttribute("student", studentService.findStudentById(studentId));
        model.addAttribute("title", "Show Student");
        return "studentShow";
    }

    @PostMapping(value = "saveStudent")
    public String saveStudent(@Valid @ModelAttribute("student") Student student, BindingResult bindingResult, @RequestParam("id") Long studentId, Model model) {
        if (!bindingResult.hasErrors()) {
            studentService.saveStudent(student);
        } else {
            String title = (studentId == null) ? "Add Student" : "Edit Student";
            model.addAttribute("title", title);
            return "studentForm";
        }
        return "redirect:/students";
    }


    @PostMapping(value = "/add_student")
    public String addStudent(Student student){
        studentService.saveStudent(student);
        return "redirect:/students";
    }

    @GetMapping(value = "deleteStudentCourse/{id}")
    public String deleteStudentCourse(@PathVariable("id") Long studentId, Model model) {
        model.addAttribute("student", studentService.findStudentById(studentId));
        model.addAttribute("courses", courseService.findByStudentsId(studentId));
        return "deleteStudentCourse";
    }
    @PostMapping(value = "student/{id}/deletecourses")
    public String deleteStudentCourse(@PathVariable Long id, @RequestParam Long courseId) {
        Student student = studentService.findStudentById(id);
        Course course = courseService.findCourseById(courseId);
        student.getCourses().removeIf(c -> c.getName().equals(course.getName()));
        studentService.saveStudent(student);
        return "redirect:/students";
    }



    @GetMapping(value = "/add_student")
    public String addStudent(){
        return "add_student_page";
    }

    @RequestMapping(value = "student/delete/{id}", method = RequestMethod.GET)
    public String deleteStudent(@PathVariable("id") Long studentId, Model model) {
        studentService.deleteStudentById(studentId);
        return "redirect:/students";
    }

    @GetMapping(value = "addStudentCourse/{id}")
    public String addStudentCourse(@PathVariable("id") Long studentId, Model model) {
        model.addAttribute("student", studentService.findStudentById(studentId));
        model.addAttribute("courses", courseService.findAllCourses());
        return "addStudentCourse";
    }

    @GetMapping(value = "student/{id}/courses")
    public String studentsAddCourse(@PathVariable Long id, @RequestParam Long courseId, Model model) {
        Student student = studentService.findStudentById(id);
        Course course = courseService.findCourseById(courseId);

        if (!student.hasCourse(course)) {
            student.getCourses().add(course);
        }
        studentService.saveStudent(student);
        model.addAttribute("student", studentService.findStudentById(id));
        model.addAttribute("courses", courseService.findAllCourses());
        return "redirect:/students";
    }

    @RequestMapping(value = "getstudents", method = RequestMethod.GET)
    public @ResponseBody
    List<Student> getStudents() {
        return (List<Student>) studentService.findAllStudents();
    }
}
