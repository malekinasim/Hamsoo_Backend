package com.hamsoo.bpm.commentaudience;

import lombok.Data;
import com.hamsoo.bpm.comment.Comment;
import javax.persistence.*;

@Entity
@Table(name="COMMENTS")
@Data
public class CommentAudience {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="ID" , nullable = false,precision = 0)
    private long id;

    @Basic
    @Column(name="USER_ID" , nullable = false,length = 5)
    private String userId;

    @Basic
    @Column(name="USER_NAME", nullable = false,length = 150)
    private String userName;

    @ManyToOne
    @JoinColumn( name="COMMENT_ID",referencedColumnName = "ID",nullable = false,unique = true)
    private Comment comment;



}

