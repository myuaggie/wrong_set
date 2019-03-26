package dao.impl;

import dao.QuestionDao;
import model.Question;
import org.hibernate.SessionFactory;

import org.hibernate.query.Query;
import org.springframework.data.redis.core.RedisTemplate;

import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public class QuestionDaoImpl implements QuestionDao {

    private SessionFactory sessionFactory;

    public void setSessionFactory(SessionFactory sessionFactory){this.sessionFactory=sessionFactory;}

    @Transactional(value = "wrongSetTransactionManager",propagation = Propagation.REQUIRES_NEW)
    public Integer save(Question question){
        return (Integer) sessionFactory.getCurrentSession().save(question);
    }

    @Transactional(value = "wrongSetTransactionManager",propagation = Propagation.REQUIRES_NEW)
    public void delete(Question question){
        sessionFactory.getCurrentSession().delete(question);
    }

    @Transactional(value = "wrongSetTransactionManager",propagation = Propagation.REQUIRES_NEW)
    public void update(Question question){
        sessionFactory.getCurrentSession().merge(question);
    }

    @Transactional(value = "wrongSetTransactionManager", propagation = Propagation.SUPPORTS, isolation = Isolation.READ_COMMITTED)
    public Question getQuestionById(int id){
        @SuppressWarnings("unchecked")
        Query<Question> query=sessionFactory.getCurrentSession().createQuery(
                        "from Question as q where q.id=?",Question.class);
        query.setParameter(0,id);
        List<Question> questions = query.getResultList();
        Question question = questions.size() > 0 ? questions.get(0) : null;
        return question;
    }

    @Transactional(value = "wrongSetTransactionManager", propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public List<Question> getAllQuestions(){
        @SuppressWarnings("unchecked")
        Query<Question> query=sessionFactory.getCurrentSession().createQuery(
                "from Question",Question.class);
        List<Question> questions = query.getResultList();
        return questions;
    }
}
