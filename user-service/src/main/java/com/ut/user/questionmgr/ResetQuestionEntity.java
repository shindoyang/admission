package com.ut.user.questionmgr;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class ResetQuestionEntity {
    @Id
    @Column(unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String question;

    public ResetQuestionEntity() {
    }

    public ResetQuestionEntity(String question) {
        this.question = question;
    }
}
