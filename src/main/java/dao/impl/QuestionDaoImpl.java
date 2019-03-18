package dao.impl;

import dao.QuestionDao;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import model.Question;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public class QuestionDaoImpl extends HibernateDaoSupport implements QuestionDao {
    @Transactional(value = "wrongSetTransactionManager",propagation = Propagation.REQUIRES_NEW)
    public Integer save(Question question){
        return (Integer) getHibernateTemplate().save(question);
    }

    @Transactional(value = "wrongSetTransactionManager",propagation = Propagation.REQUIRES_NEW)
    public void delete(Question question){
        getHibernateTemplate().delete(question);
    }

    @Transactional(value = "wrongSetTransactionManager",propagation = Propagation.REQUIRES_NEW)
    public void update(Question question){
        getHibernateTemplate().merge(question);
    }

    @Transactional(value = "wrongSetTransactionManager", propagation = Propagation.SUPPORTS, isolation = Isolation.READ_COMMITTED)
    public Question getQuestionById(int id){
        @SuppressWarnings("unchecked")
        List<Question> questions = (List<Question>) getHibernateTemplate().find(
                "from Question as q where q.id=?", id);
        Question question = questions.size() > 0 ? questions.get(0) : null;
        return question;
    }

    @Transactional(value = "wrongSetTransactionManager", propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public List<Question> getAllQuestions(){
        @SuppressWarnings("unchecked")
        List<Question> questions = (List<Question>) getHibernateTemplate().find(
                "from Question");
        return questions;
    }
}
