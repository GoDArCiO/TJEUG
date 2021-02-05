package com.project18.demo.controller;

import com.project18.demo.model.Authors;
import com.project18.demo.model.BlogModel;
import com.project18.demo.model.dto.Greeting;
import com.project18.demo.model.dto.PostModel;
import com.project18.demo.model.dto.UserDTO;
import com.project18.demo.service.BlogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class BlogController {

    private final BlogService blogService;

    @Autowired
    public BlogController(BlogService blogService) {
        this.blogService = blogService;
    }

    @GetMapping("/posts")
    public String readAllAuthors(Model model) {
        model.addAttribute("postList", blogService.getAllPosts());
        return "posts";
    }

    // Formularz dodawania postów - wywołanie formularza
    @GetMapping("/posts/new")
    public String postForm(Model model) {
        PostModel postModel = new PostModel();
        model.addAttribute("newPost", postModel);
        return "postForm";
    }

    // Obsługa formularza dodawania postów (zapis posta do bazy)
    @PostMapping("/posts/new")
    public String add(@ModelAttribute("newPost") PostModel newPost, BindingResult bindingResult, BlogModel blogModel, Model model, RedirectAttributes ra) {
        ra.addFlashAttribute("newPost", newPost);//int id, List<Authors> authors, String post_content,String post_tag,  Boolean bezrej)
        blogService.savePost(newPost);
        return "redirect:/posts";
    }

    @GetMapping("/bezloginu")
    public String readAllAuthorsWithoutLogin(Model model) {
        model.addAttribute("postList", blogService.getAllPostsWithoutLogin());
        return "postsnologin";
    }

    // Pobieranie postów zalogowanego uzytkownika na stronę z postami
    @GetMapping("/posts/tylkomojeposty")
    public String readMyPosts(@RequestParam(required = false) String blogId, Model model) {
        List<BlogModel> postList = new ArrayList<BlogModel>();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String emailZalogowanegoUsera = auth.getName();//np. cr@gmail.com jest aktualnie zalogowany
        postList.addAll(blogService.findActuallyLoggedUserPosts(emailZalogowanegoUsera));

        model.addAttribute("postList", postList);
        return "mojeposty";
    }

    // Formularz edycji posta o numerze id - wywołanie z danymi posta
    @GetMapping("/posts/edit/{id}")
    public String postFormEdit(@PathVariable("id") long id, Model model, HttpServletRequest request) {
        String userEmail = request.getUserPrincipal().getName();
        PostModel postModel = blogService.findByIdAndMapToEdit(id, userEmail);
        model.addAttribute("oldPost", postModel);
        return "postFormEdit";
    }

    // Obsługa formularza edycji posta o numerze id - (zapis poprawionego rekordu do bazy))
    @PostMapping("/posts/edit/{id}")
    public String postFormEditSubmit(@PathVariable("id") long id, @Valid @ModelAttribute("oldPost") PostModel postModel, BindingResult bindingResult, Authors model) throws InterruptedException {
        postModel.setId(id);
        blogService.savePost(postModel);
        return "redirect:/posts";
    }

    // usuwanie z bazy posta o numerze id
    @GetMapping("/posts/remove/{id}")
    public String postRemove(@PathVariable("id") long id, HttpServletRequest request) {
        String userEmail = request.getUserPrincipal().getName();
        blogService.deleteById(id, userEmail);
        return "redirect:/posts";
    }

    @GetMapping("/greeting")
    public String greetingForm(Model model) {
        Greeting greeting = new Greeting();
        greeting.setId(108);
        greeting.setContent("Welcome");
        model.addAttribute("greeting", greeting);
        return "greeting";
    }

    @PostMapping("/greeting")
    public String greetingSubmit(@ModelAttribute Greeting greeting) {
        greeting.setId(greeting.getId() + 1);
        return "greeting";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new UserDTO());
        return "signup_form";
    }

    @GetMapping("/")
    public String viewHomePage() {
        return "index";
    }

    @GetMapping("/statistics")
    public String getStats(Model model) {
        model.addAttribute("statistics", blogService.prepareStats());
        return "postStatistics";
    }

    @GetMapping("/posts/search")
    public String searchPost(Model model) {
        model.addAttribute("search", new PostModel());
        List<String> tagList = new ArrayList<>();
        var tags = blogService.getAllPosts().stream().map(PostModel::getTag).collect(Collectors.toList());
        model.addAttribute("taglist", tags);
        return "postSearch";
    }

    // Obsługa wyszukiwarki
    @PostMapping("/posts/search")
    public String searchPostResult(Model model, @ModelAttribute PostModel postModel) {
        String tagWybrany = postModel.getDropdownSelectedValue();
        var result = blogService.findByTag(tagWybrany);
        model.addAttribute("postList", result.getPost_content());
        return "postSearchResult";
    }

    // Ustawienia
    @GetMapping("/settings")
    public String settings(Model model) {
        return "settings";
    }

    // Pobieranie postów z pliku .csv i zapis do bazy
    @PostMapping("/settings/sendCsv")
    public String settingsSendCsv(@RequestParam("file") MultipartFile file) throws IOException {
        Reader reader = new InputStreamReader(file.getInputStream());
        blogService.importData(reader);
        return "redirect:/posts";
    }

    // Pobieranie postów z bazy i eksport do pliku .csv
    @GetMapping("/settings/downloadCsv")
    public String settingsExportToCsv(Model model) throws IOException {
        model.addAttribute("postywyeksportowane", blogService.getAllPostsAsCsv());
        return "exported";
    }

    @GetMapping("/settings/downloadCsv/exported.csv")
    public ResponseEntity<String> getCsvReport() {
        return ResponseEntity
                .ok()
                .contentType(MediaType.valueOf("text/csv"))
                .body(blogService.getAllPostsAsCsv());
    }
}
