package com.project18.demo.controller;

import com.project18.demo.model.Authors;
import com.project18.demo.model.dto.UserDTO;
import com.project18.demo.service.AuthorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/signup")
public class SignUpController {

    @Autowired
    private AuthorService service;

    @GetMapping
    public String showForm(Model model) {
        model.addAttribute("user", new UserDTO());
        return "signup_form";
    }

    @PostMapping //funkcja 1/3 do rejestorwania przez mail
    public ModelAndView submitForm(@Valid @ModelAttribute("user") UserDTO user, BindingResult bindingResult) {
        ModelAndView modelAndView = new ModelAndView();
        if (bindingResult.hasErrors()) {
            modelAndView.addObject("user", user);
            modelAndView.setViewName("signup_form");
            return modelAndView;
        }
        service.register(user);
        modelAndView.setViewName("register_success");
        return modelAndView;
    }

    @GetMapping("/users")// funkcja 2/3 do rejestorwania przez mail
    public String listUsers(Model model) {
        List<Authors> listUsers = service.getAll();
        model.addAttribute("listUsers", listUsers);
        return "users";
    }

    @GetMapping("/verify")// funkcja 3/3 do rejestorwania przez mail
    public String verifyUser(@RequestParam("code") String code) {
        if (service.verify(code)) {
            return "verify_success";
        } else {
            return "verify_fail";
        }
    }

}
