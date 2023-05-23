package course.controller;

import java.util.List;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.BindingResult;

import course.entity.Course;
import course.service.CourseServiceImpl;

@Controller
public class CourseController {

    @Autowired
    private CourseServiceImpl courseService;
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("courses")
    public String index(Model model) {
        List<Course> courses = (List<Course>) courseService.findAllByOrderByNameAsc();
        model.addAttribute("courses", courses);
        return "courses";
    }

    @GetMapping(value = "course/new")
    public String newCourse(Model model) {
        model.addAttribute("course", new Course());
        model.addAttribute("title", "Add Course");
        return "courseForm";
    }

    @GetMapping(value = "course/edit/{id}")
    public String editCourse(@PathVariable("id") Long courseId, Model model) {
        model.addAttribute("course", courseService.findCourseById(courseId));
        model.addAttribute("title", "Edit Course");
        return "courseForm";
    }

    @GetMapping("course/{id}")
    public String showCourse(@PathVariable("id") Long courseId, Model model) {
        model.addAttribute("course", courseService.findCourseById(courseId));
        model.addAttribute("title", "Show Course");
        return "courseShow";
    }
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping(value = "saveCourse")
    public String saveCourse(@Valid @ModelAttribute("course") Course course, BindingResult bindingResult, @RequestParam("courseid") Long courseId, Model model) {
        if (!bindingResult.hasErrors()) {
            courseService.saveCourse(course);
        } else {
            String title = (courseId == null) ? "Add Course" : "Edit Course";
            model.addAttribute("title", title);
            model.addAttribute("courseid", courseId); // Добавить эту строку
            return "courseForm";
        }
        return "redirect:/courses";
    }
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping(value = "/add_course")
    public String addCourse(Course course){
        courseService.saveCourse(course);
        return "redirect:/courses";
    }
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping(value = "/add_course")
    public String addCourse(){
        return "add_course_page";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "course/delete/{id}", method = RequestMethod.GET)
    public String deleteCourse(@PathVariable("id") Long courseId, Model model) {
        courseService.deleteCourseById(courseId);
        return "redirect:/courses";
    }


}
