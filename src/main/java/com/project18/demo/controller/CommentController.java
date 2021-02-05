package com.project18.demo.controller;

import com.project18.demo.model.BlogModel;
import com.project18.demo.model.dto.NewComment;
import com.project18.demo.model.dto.PostEditRequest;
import com.project18.demo.service.BlogService;
import com.project18.demo.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class CommentController {

    private final CommentService commentService;
    private final BlogService blogService;

    @Autowired
    public CommentController(CommentService commentService, BlogService blogService) {
        this.commentService = commentService;
        this.blogService = blogService;
    }

    @GetMapping("/komentarz/{id}")
    public String commentsView(@PathVariable("id") int id, Model model) {
        NewComment comment = new NewComment();
        BlogModel blogModel = blogService.findById(id);
        PostEditRequest postEditRequest = new PostEditRequest(null, "", blogModel.getPost_content()
                , blogModel.getPost_tag(), blogModel.getId(), blogService.getAutorsById(id));
        model.addAttribute("autors1", blogService.getAutorsById(id));
        model.addAttribute("oldPost", postEditRequest);
        model.addAttribute("komentarz", comment);
        return "komentarz";
    }

    @GetMapping("/komentarz/bezloginu/{id}")
    public String commentAddNoLogin(@PathVariable("id") int id, Model model) {
        NewComment comment = new NewComment();
        BlogModel blogModel = blogService.findById(id);
        model.addAttribute("oldPost", id);
        model.addAttribute("autors1", blogService.getAutorsById(id));
        model.addAttribute("komentarz", comment);
        return "komentarz_bez_loginu";
    }

    @GetMapping("/komentarze/{id}")
    public String commentsPreView(@PathVariable("id") int id, Model model) {
        model.addAttribute("post_id", id);
        model.addAttribute("comments", blogService.getCommentsByPostId(id));
        return "komenatrze_widok";
    }

    @GetMapping("/komentarze/bezloginu/{id}")
    public String commentsPreViewNoLo(@PathVariable("id") int id, Model model) {
        model.addAttribute("post_id", id);
        model.addAttribute("comments", blogService.getCommentsByPostId(id));
        return "komenatrze_widok_no_login";
    }

    @GetMapping("/comments/{post_id}/remove/{id}")
    public String remove(@PathVariable("post_id") long post_id, @PathVariable("id") long id, Model model, HttpServletRequest request) {
        String userEmail = request.getUserPrincipal().getName();
        commentService.remove(userEmail,id);
        return "redirect:/komentarze/" + post_id;
    }

    @PostMapping("/komentarz/{id}")
    public String commentSubmit(@PathVariable("id") Long id, @ModelAttribute NewComment comment,  HttpServletRequest request) {
        String userEmail = request.getUserPrincipal().getName();
        commentService.saveLogin(comment, id, userEmail);
        return "redirect:/posts";
    }

    @PostMapping("/komentarz/bezloginu/{id}")
    public String commentNoLoginPost(@PathVariable("id") Long id, @ModelAttribute NewComment comment) {
        commentService.save(comment, id);
        return "redirect:/bezloginu";
    }
}
