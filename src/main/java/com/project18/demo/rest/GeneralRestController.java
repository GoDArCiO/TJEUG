package com.project18.demo.rest;

import com.project18.demo.model.Comment;
import com.project18.demo.model.dto.CommentResponse;
import com.project18.demo.model.dto.NewComment;
import com.project18.demo.model.dto.PostModel;
import com.project18.demo.model.dto.UserDTO;
import com.project18.demo.service.AuthorService;
import com.project18.demo.service.BlogService;
import com.project18.demo.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "/api")
public class GeneralRestController {

    private final AuthorService service;
    private final BlogService blogService;
    private final CommentService commentService;

    @Autowired
    public GeneralRestController(AuthorService service, BlogService blogService, CommentService commentService) {
        this.service = service;
        this.blogService = blogService;
        this.commentService = commentService;
    }

    /*
    curl --location --request POST 'localhost:8080/api/posts' \
--header 'Authorization: Basic YWRtaW5AZW1haWwucGw6cGFzc3dvcmQ=' \
--header 'Content-Type: application/json' \
--header 'Cookie: JSESSIONID=8227E73ABC40BF7E86C5B7E1EF0D16D8' \
--data-raw '{
    "id" : null,
    "authors" :[],
    "content" : "nowa tresc",
    "tag" : "nowy tag",
    "autor" : "admin",
    "comments" : [],
    "dropdownSelectedValue" : "value"
}'

     */
    @RequestMapping(value = "/posts", method = RequestMethod.POST)
    public ResponseEntity<Void> addPost( @RequestBody PostModel newPost) {
        blogService.savePost(newPost);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    /*
    curl --location --request GET 'localhost:8080/api/tags'
     */
    @RequestMapping(value = "/tags", method = RequestMethod.GET)
    public ResponseEntity<List<String>> getTags() {
        return ResponseEntity.ok(blogService.getTags());
    }
 /*
curl --location --request PUT 'localhost:8080/api/comments/1' \
--header 'Authorization: Basic YWRtaW5AZW1haWwucGw6cGFzc3dvcmQ=' \
--header 'Content-Type: application/json' \
--header 'Cookie: JSESSIONID=7CEAF79F1D2363E3BDDC0F849377C1DD' \
--data-raw '{
    "username" : "admin",
    "comment" : "nowa tresc"
}'
  */
    @RequestMapping(value = "/comments/{id}", method = RequestMethod.PUT)
    public ResponseEntity<Void> editCommentByUser(@PathVariable long id, @RequestBody NewComment comment, HttpServletRequest request) {
        String userEmail = request.getUserPrincipal().getName();
        blogService.editCommentByUser(id, comment, userEmail);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

 /*
curl --location --request PUT 'localhost:8080/api/admin/comments/1' \
--header 'Authorization: Basic YWRtaW5AZW1haWwucGw6cGFzc3dvcmQ=' \
--header 'Content-Type: application/json' \
--header 'Cookie: JSESSIONID=7CEAF79F1D2363E3BDDC0F849377C1DD' \
--data-raw '{
    "username" : "admin",
    "comment" : "nowa tresc"
}'
  */

    @RequestMapping(value = "/admin/comments/{id}", method = RequestMethod.PUT)
    public ResponseEntity<Void> editComment(@PathVariable long id, @RequestBody NewComment comment) {
        blogService.editComment(id, comment);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    /*
    curl --location --request PUT 'localhost:8080/api/posts/1' \
--header 'Authorization: Basic YWRtaW5AZW1haWwucGw6cGFzc3dvcmQ=' \
--header 'Content-Type: application/json' \
--header 'Cookie: JSESSIONID=4C90E03D2E88762EA639C76814F32FF8' \
--data-raw '{
    "id" : 1,
    "authors" :[],
    "content" : "nowa tresc",
    "tag" : "nowy tag",
    "autor" : "admin",
    "comments" : [],
    "dropdownSelectedValue" : "value"


}'
     */
    @RequestMapping(value = "/posts/{id}", method = RequestMethod.PUT)
    public ResponseEntity<Void> editPostByUser(@PathVariable long id, @RequestBody PostModel postModel, HttpServletRequest request) {
        String userEmail = request.getUserPrincipal().getName();
        blogService.editPost(id, postModel, userEmail);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    /*

    curl --location --request PUT 'localhost:8080/api/admin/posts/1' \
--header 'Authorization: Basic YWRtaW5AZW1haWwucGw6cGFzc3dvcmQ=' \
--header 'Content-Type: application/json' \
--header 'Cookie: JSESSIONID=4C90E03D2E88762EA639C76814F32FF8' \
--data-raw '{
    "id" : 1,
    "authors" :[],
    "content" : "nowa tresc",
    "tag" : "nowy tag",
    "autor" : "admin",
    "comments" : [],
    "dropdownSelectedValue" : "value"
}'
     */

    @RequestMapping(value = "/admin/posts/{id}", method = RequestMethod.PUT)
    public ResponseEntity<Void> editPost(@PathVariable long id, @RequestBody PostModel postModel) {
        blogService.editPost(id, postModel);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    /*
curl --location --request DELETE 'localhost:8080/api/posts/3' \
        --header 'Authorization: Basic YWRtaW5AZW1haWwucGw6cGFzc3dvcmQ='
 */
    @RequestMapping(value = "/posts/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> removePost(@PathVariable long id) {
        blogService.removePost(id);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    /*
    curl --location --request DELETE 'localhost:8080/api/comments/3' \
            --header 'Authorization: Basic YWRtaW5AZW1haWwucGw6cGFzc3dvcmQ='
     */
    @RequestMapping(value = "/comments/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> removeComment(@PathVariable long id) {
        blogService.removeComment(id);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    /*
      curl --location --request GET 'localhost:8080/api/comments/my' \
        --header 'Authorization: Basic YWRtaW5AZW1haWwucGw6cGFzc3dvcmQ='
     */
    @RequestMapping(value = "/comments/my", method = RequestMethod.GET)
    public ResponseEntity<List<CommentResponse>> getAllComments(HttpServletRequest request) {
        String userEmail = request.getUserPrincipal().getName();
        return ResponseEntity.ok(commentService.findByEmail(userEmail));
    }

    /*
    curl --location --request GET 'localhost:8080/api/posts/my' \
            --header 'Authorization: Basic YWRtaW5AZW1haWwucGw6cGFzc3dvcmQ='
     */
    @RequestMapping(value = "/posts/my", method = RequestMethod.GET)
    public ResponseEntity<List<PostModel>> getAllUserPost(HttpServletRequest request) {
        String userEmail = request.getUserPrincipal().getName();
        return ResponseEntity.ok(blogService.getPostByEmail(userEmail));
    }

    /* curl --location --request GET 'localhost:8080/api/posts?page=1&size=50&sort=id'  */

    @RequestMapping(value = "/posts", method = RequestMethod.GET)
    public ResponseEntity<List<PostModel>> getAllPostsData(@RequestParam("page") int page,
                                                           @RequestParam("size") int size,
                                                           @RequestParam("sort") String sort) {
        return ResponseEntity.ok(blogService.getAllPostsData(page, size, sort));
    }

    /*    curl --location --request POST 'localhost:8080/api/signup' \
                --header 'Content-Type: application/json' \
                --data-raw '{
                "email": "amail@mail.pl",
                "password": "Password12@",
                "re_password": "Password12@",
                "fullName": "myname@"
    }'*/
    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public ResponseEntity<Void> submitForm(@RequestBody @Valid UserDTO user) {
        service.register(user);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
