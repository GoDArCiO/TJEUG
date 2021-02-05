package com.project18.demo.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PostEditRequest {
    private Long id;

    @Size(min = 4, max = 32, message = "Name is invalid")
    @Pattern(regexp = "^[A-Za-z0-9 ']*$", message = "Name is invalid")
    private String name;

    @Size(min = 2, max = 1000, message = "Post content is invalid")
    private String post_content;

    @Size(min = 2, max = 1000, message = "Tag is invalid")
    private String tag;

    private long postId;

    private String autors;
}
