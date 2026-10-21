package com.hamsoo.bpm.comment;


import com.hamsoo.bpm.commentaudience.CommentAudience;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name="COMMENTS")
@Data
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="ID" , nullable = false,precision = 0)
    private long id;

    @Basic
    @Column(name="MESSAGE" , nullable = false,length = 2000)
     private String message;

    @Basic
    @Column(name="ADD_BY_ID" , nullable = false,length = 5)
    private String addBy;

    @Basic
    @Column(name="ADD_BY_NAME", nullable = false,length = 150)
    private String addByName;


    @Basic
    @Column(name="SOURCE_TYPE", nullable = false,length = 150)
    private String sourceType;


    @Basic
    @Column(name="SOURCE_ID", nullable = false,precision = 0)
    private long sourceId;

    @Basic
    @Column(name="SEEN" , nullable = false,length = 150)
    private boolean seen;


    @ManyToOne
    @JoinColumn( name="PRE_COMMENT_ID",referencedColumnName = "ID",nullable = true)
    private Comment preComment;


    @OneToMany(mappedBy = "preComment",targetEntity = Comment.class, fetch = FetchType.LAZY)
    private List<Comment> comments;

    @OneToMany(mappedBy = "comment",targetEntity = CommentAudience.class, fetch = FetchType.LAZY)
    private List<CommentAudience> commentAudiences;






}
