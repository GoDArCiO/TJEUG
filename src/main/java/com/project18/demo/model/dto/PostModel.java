package com.project18.demo.model.dto;

import com.project18.demo.model.Authors;
import com.project18.demo.model.Comment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostModel {
    private long id;
    private List<Authors> authors;
    private String content;
    private String tag;
    private String autor;
    private List<Comment> comments;
    private String dropdownSelectedValue;
}
