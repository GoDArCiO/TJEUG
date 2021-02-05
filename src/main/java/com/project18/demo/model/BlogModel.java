package com.project18.demo.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "POSTS")
//to jest nazwa tablicy zawierającej posty, która należy do bazy MyBlogDatabase na serwerze localhost
public class BlogModel {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column
    private Long id;

    @Column(name = "post_content", length=10485760)
    public String post_content;

    @Column(name = "post_tag", length=10485760)
    public String post_tag;

    @Column(name = "no_regirster")
    public boolean noRegister;

    @Column
    @OneToMany(mappedBy = "blogModel", fetch = FetchType.EAGER)
    private List<Comment> comments;

    @ManyToMany(mappedBy = "posts")
    private List<Authors> authors;
}
