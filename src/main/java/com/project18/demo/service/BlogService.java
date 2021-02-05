package com.project18.demo.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.project18.demo.exception.AuthorsNotExistsException;
import com.project18.demo.exception.CommentNotExistsException;
import com.project18.demo.exception.PostNotExistsException;
import com.project18.demo.exception.YouCantEditPostException;
import com.project18.demo.exception.YouCantRemovePostException;
import com.project18.demo.model.Authors;
import com.project18.demo.model.BlogModel;
import com.project18.demo.model.Comment;
import com.project18.demo.model.Role;
import com.project18.demo.model.dto.AuthorName;
import com.project18.demo.model.dto.NewComment;
import com.project18.demo.model.dto.PostModel;
import com.project18.demo.model.dto.StatisticsResponse;
import com.project18.demo.repository.AuthorsRepository;
import com.project18.demo.repository.BlogRepository;
import com.project18.demo.repository.CommentRepository;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import javax.transaction.Transactional;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BlogService {

    private final BlogRepository blogRepository;
    private final AuthorsRepository authorsRepository;
    private final CommentRepository commentRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public BlogService(BlogRepository blogRepository, AuthorsRepository authorsRepository, CommentRepository commentRepository, PasswordEncoder passwordEncoder) {
        this.blogRepository = blogRepository;
        this.authorsRepository = authorsRepository;
        this.commentRepository = commentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<PostModel> getAllPosts() {
        List<PostModel> result = new ArrayList<>();
        for (BlogModel it : blogRepository.findAll()) {
            PostModel build = PostModel.builder()
                    .authors(authorsRepository.findAllByPosts(it))
                    .comments(it.getComments())
                    .id(it.getId())
                    .content(it.getPost_content())
                    .tag(it.getPost_tag())
                    .build();
            result.add(build);
        }
        return result;
    }

    @Transactional
    public List<PostModel> getAllPostsData(int page, int size, String sort) {
        List<PostModel> result = new ArrayList<>();
        if (sort.equals("post_content")) {
            Pageable sortedByName =
                    PageRequest.of(page, size, Sort.Direction.ASC, "id" );
            var data = blogRepository.findAll(sortedByName).stream().sorted(Comparator.comparing(BlogModel::getPost_content)).collect(Collectors.toList());
            for (BlogModel it : data) {
                PostModel build = PostModel.builder()
                        .autor(it.getAuthors().stream().map(Authors::getFullName).collect(Collectors.joining(";")))
                        .comments(it.getComments())
                        .id(it.getId())
                        .authors(new ArrayList<>())
                        .content(it.getPost_content())
                        .tag(it.getPost_tag())
                        .build();
                result.add(build);

            }
            return result;
        }

        if (sort.equals("post_tag")) {
            Pageable sortedByName =
                    PageRequest.of(page, size, Sort.Direction.ASC, "id" );
            var data = blogRepository.findAll(sortedByName).stream().sorted(Comparator.comparing(BlogModel::getPost_tag)).collect(Collectors.toList());
            for (BlogModel it : data) {
                PostModel build = PostModel.builder()
                        .autor(it.getAuthors().stream().map(Authors::getFullName).collect(Collectors.joining(";")))
                        .comments(it.getComments())
                        .id(it.getId())
                        .authors(new ArrayList<>())
                        .content(it.getPost_content())
                        .tag(it.getPost_tag())
                        .build();
                result.add(build);

            }
            return result;
        }

        Pageable sortedByName =
                PageRequest.of(page, size, Sort.Direction.ASC, sort);
        for (BlogModel it : blogRepository.findAll(sortedByName)) {
            PostModel build = PostModel.builder()
                    .autor(it.getAuthors().stream().map(Authors::getFullName).collect(Collectors.joining(";")))
                    .comments(it.getComments())
                    .id(it.getId())
                    .authors(new ArrayList<>())
                    .content(it.getPost_content())
                    .tag(it.getPost_tag())
                    .build();
            result.add(build);
        }
        return result;
    }

    @Transactional
    public List<PostModel> getAllPostsWithoutLogin() {
        List<PostModel> result = new ArrayList<>();
        List<BlogModel> collect = blogRepository.findAll().stream().filter(BlogModel::isNoRegister).collect(Collectors.toList());
        for (BlogModel it : collect) {
            PostModel build = PostModel.builder()
                    .authors(authorsRepository.findAllByPosts(it))
                    .comments(it.getComments())
                    .id(it.getId())
                    .content(it.getPost_content())
                    .tag(it.getPost_tag())
                    .build();
            result.add(build);
        }
        return result;
    }

    public BlogModel findById(long id) {
        return blogRepository.findById(id).get();
    }

    public void savePost(PostModel newPost) {
        String[] autors = newPost.getAutor().split(";");
        List<Authors> authorsList = new ArrayList<>();
        Arrays.stream(autors).forEach(it -> {
            Optional<Authors> autor = authorsRepository.findByFullName(it);
            authorsList.add(autor.orElseThrow(() -> new AuthorsNotExistsException(it)));
        });

        var id = newPost.getId() == 0 ? null : newPost.getId();
        BlogModel post = BlogModel.builder()
                .id(id)
                .authors(authorsList)
                .comments(newPost.getComments())
                .post_tag(newPost.getTag())
                .post_content(newPost.getContent())
                .noRegister(false)
                .build();
        blogRepository.save(post);
        authorsList.stream().forEach(it -> {
            it.getPosts().add(post);
            authorsRepository.save(it);
        });
        log.debug("Post {} saved successfully", post);
    }

    public List<BlogModel> findActuallyLoggedUserPosts(String email) {
        Authors authors = authorsRepository.findByEmail(email).get();
        return authors.getPosts();
    }

    public PostModel findByIdAndMapToEdit(long id, String userEmail) {
        var result = blogRepository.findById(id).get();
        String autors = authorsRepository.findAllByPosts(result).stream().map(Authors::getFullName).collect(Collectors.joining(";"));
        if (authorsRepository.findByEmail(userEmail).get().getRole().equals(Role.ADMIN) ||
                autors.contains(authorsRepository.findByEmail(userEmail).get().getFullName())) {
            return PostModel.builder()
                    .comments(result.getComments())
                    .id(result.getId())
                    .autor(autors)
                    .content(result.getPost_content())
                    .tag(result.getPost_tag())
                    .build();
        }
        throw new YouCantEditPostException("Nie masz uprawnień do edycji posta", "code");
    }

    //Usuwamy komentarze, czyscimy powiązanie z autorem i usuwamy wpis
    @Transactional
    public void deleteById(long id, String userEmail) {
        Authors authors = authorsRepository.findByEmail(userEmail).get();
        if (authors.getRole().equals(Role.ADMIN)) {
            commentRepository.deleteByBlogModelId(id);
            BlogModel blogModel = blogRepository.findById(id).get();
            blogModel.getAuthors().stream().forEach(it -> {
                it.getPosts().remove(blogModel);
                authorsRepository.save(it);
            });
            blogRepository.deleteById(id);
            log.debug("BlogModel with id: {} was removed", id);
        } else {
            BlogModel blogModel = blogRepository.findById(id).get();
            if (blogModel.getAuthors().stream().filter(it -> it.getEmail().equals(userEmail)).findAny().isEmpty()) {
                throw new YouCantRemovePostException("Nie masz uprawnień do usunięcia tego posta!", "code");
            }
            commentRepository.deleteByBlogModelId(id);
            blogModel.getAuthors().stream().forEach(it -> {
                it.getPosts().remove(blogModel);
                authorsRepository.save(it);
            });
            blogRepository.deleteById(id);
            log.debug("BlogModel with id: {} was removed", id);
        }
    }

    @Transactional
    public String getAutorsById(long id) {
        BlogModel blogModel = blogRepository.findById(id).get();
        return blogModel.getAuthors().stream().map(Authors::getFullName).collect(Collectors.joining(";"));
    }

    public List<StatisticsResponse> prepareStats() {
        return authorsRepository.findAll().stream()
                .map(it -> new StatisticsResponse(it.getFullName(), it.getPosts().size())).sorted(Comparator.comparingInt(StatisticsResponse::getCount)
                        .reversed())
                .collect(Collectors.toList());
    }

    public BlogModel findByTag(String tagWybrany) {
        return blogRepository.findAll().stream().filter(it -> it.getPost_tag().equals(tagWybrany)).findAny().get();
    }

    @Transactional
    public String getAllPostsAsCsv() {
        String NEW_LINE_SEPARATOR = "\n";
        String COLUMN_SEPARATOR = ";";
        String HEADER = "Id; Authors; Content";
        StringBuilder result = new StringBuilder(HEADER).append(NEW_LINE_SEPARATOR);
        blogRepository.findAll().forEach(it -> {
            result.append(it.getId())
                    .append(COLUMN_SEPARATOR)
                    .append("authors: ")
                    .append(it.getAuthors().stream().map(Authors::getFullName).collect(Collectors.joining(",")))
                    .append(COLUMN_SEPARATOR)
                    .append(it.getPost_content())
                    .append(NEW_LINE_SEPARATOR);
        });
        return result.toString();
    }

    public void importData(Reader reader) throws JsonProcessingException {
        List<List<String>> records = new ArrayList<>();
        try (CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build()) {
            String[] values;
            while ((values = csvReader.readNext()) != null) {
                records.add(Arrays.asList(values));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        importUser(records);
        importPost(records);
        log.info("Method importData was called");
    }

    @Transactional
    public void importPost(List<List<String>> records) {
        log.info("Method importPost was called");
        records.stream().parallel().forEach(it -> {
            try {
                List<AuthorName> authors = new ObjectMapper().readValue(it.get(1), new TypeReference<List<AuthorName>>() {
                });
                List<Authors> authorsList = new ArrayList<>();
                authors.forEach(a -> authorsList.add(authorsRepository.findByFullName(a.getAuthors()).get()));
                var post = blogRepository.save(
                        BlogModel.builder()
                                .authors(authorsList)
                                .noRegister(true)
                                .post_content(it.get(2))
                                .post_tag(it.get(3))
                                .comments(new ArrayList<>())
                                .build());
                authorsList.forEach(d -> {
                    Authors authors1 = authorsRepository.findById(d.getId()).get();
                    authors1.getPosts().add(post);
                    authorsRepository.save(authors1);
                });

            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        });

        log.info("Method importPost was finished");
    }

    private void importUser(List<List<String>> records) {
        log.info("Method importUser was called");
        List<Authors> toSave = new ArrayList<>();
        records.forEach(it -> {
            try {
                List<AuthorName> authors = new ObjectMapper().readValue(it.get(1), new TypeReference<List<AuthorName>>() {
                });
                authors.forEach(author -> {
                    if (authorsRepository.findByFullName(author.getAuthors()).isEmpty() && toSave.stream().filter(ty -> ty.getFullName().equals(author.getAuthors())).findAny().isEmpty()) {
                        toSave.add(Authors.builder()
                                .email(RandomString.make(6) + "test@email.pl")
                                .fullName(author.getAuthors())
                                .enabled(true)
                                .role(Role.USER)
                                .build());
                    }
                });

            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        });
        log.info("Saving");
        authorsRepository.saveAll(toSave);
        log.info("Method importUser was finished");
    }

    public List<Comment> getCommentsByPostId(long id) {
        return commentRepository.findAllByBlogModelId(id);
    }

    @Transactional
    public ArrayList<PostModel> getPostByEmail(String userEmail) {
        var result = new ArrayList<PostModel>();
        var posts = authorsRepository.findByEmail(userEmail).get().getPosts();
        for (BlogModel it : posts) {
            PostModel build = PostModel.builder()
                    .autor(it.getAuthors().stream().map(Authors::getFullName).collect(Collectors.joining(";")))
                    .comments(it.getComments())
                    .id(it.getId())
                    .authors(new ArrayList<>())
                    .content(it.getPost_content())
                    .tag(it.getPost_tag())
                    .build();
            result.add(build);
        }
        return result;
    }

    public void removeComment(long id) {
        commentRepository.deleteById(id);
        log.debug("Comment with id: {} has been removed", id);
    }

    @Transactional
    public void removePost(long id) {
        BlogModel blogModel = blogRepository.findById(id).get();
        List<Authors> authors = blogRepository.findById(id).get().getAuthors();
        authors.forEach(it ->{
            it.getPosts().remove(blogModel);
            authorsRepository.save(it);
        });

        blogRepository.deleteById(id);
        log.debug("Post with id: {} has been removed", id);
    }

    public void editPost(long id, PostModel postModel) {
        Optional<BlogModel> optionalBlogModel = blogRepository.findById(id);
        BlogModel blogModel = optionalBlogModel.orElseThrow(() -> new PostNotExistsException(id));
        blogModel.setPost_tag(postModel.getTag());
        blogModel.setPost_content(postModel.getContent());
        blogRepository.save(blogModel);
    }

    public void editComment(long id, @RequestBody NewComment comment) {
        Optional<Comment> optionalComment = commentRepository.findById(id);
        Comment commentdb = optionalComment.orElseThrow(() -> new CommentNotExistsException(id));
        commentdb.setComment(comment.getComment());
        commentRepository.save(commentdb);
    }

    public void editPost(long id, PostModel postModel, String userEmail) {
        Authors authors = authorsRepository.findByEmail(userEmail).get();
        Optional<BlogModel> optionalBlogModel = blogRepository.findById(id);
        BlogModel blogModel = optionalBlogModel.orElseThrow(() -> new PostNotExistsException(id));
        if (blogModel.getAuthors().contains(authors)) {
            editPost(id, postModel);
        } else {
            throw new YouCantEditPostException("Nie masz uprawnień do edycji postu", "code");
        }
    }

    public void editCommentByUser(long id, NewComment comment, String userEmail) {
        Authors authors = authorsRepository.findByEmail(userEmail).get();
        Comment comment1 = commentRepository.findById(id).orElseThrow(() -> new CommentNotExistsException(id));
        if (comment1.getUsername().contains(authors.getFullName())) {
            editComment(id, comment);
        } else {
            throw new YouCantEditPostException("Nie masz uprawnień do edycji postu", "code");
        }
    }

    public List<String> getTags() {
        return blogRepository.findAll().stream().map(BlogModel::getPost_tag).collect(Collectors.toList());
    }
}
