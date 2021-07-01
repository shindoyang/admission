package com.ut.user.questionmgr;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;


@Service
public class ResetQuestionService {
    @Autowired
    private ResetQuestionDao resetQuestionDao;

    public ResetQuestionService(){
    }

    void initQuestion(Long id, String question){
        ResetQuestionEntity rq = new ResetQuestionEntity(question);
        rq.setId(id);
        resetQuestionDao.save(rq);
    }

    @PostConstruct
    public void init(){
        initQuestion(1L, "你的全名叫什么?");
        initQuestion(2L, "你母亲的全名叫什么?");
        initQuestion(3L, "你的大学是什么?");
    }

}
