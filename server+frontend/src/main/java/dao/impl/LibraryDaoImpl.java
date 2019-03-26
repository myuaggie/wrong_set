package dao.impl;

import dao.LibraryDao;
import model.ULKey;
import model.UQ_Library;

import org.hibernate.SessionFactory;

import org.hibernate.query.Query;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class LibraryDaoImpl implements LibraryDao {

    private SessionFactory sessionFactory;

    private RedisTemplate<String, Object> redisTemplate;

    public void setSessionFactory(SessionFactory sessionFactory){this.sessionFactory=sessionFactory;}
    public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Transactional(value = "wrongSetTransactionManager",propagation = Propagation.REQUIRES_NEW)
    public void save(UQ_Library library){
        sessionFactory.getCurrentSession().save(library);
    }

    @Transactional(value = "wrongSetTransactionManager",propagation = Propagation.REQUIRES_NEW)
    public void delete(UQ_Library library){
        sessionFactory.getCurrentSession().delete(library);
    }

    @Transactional(value = "wrongSetTransactionManager",propagation = Propagation.REQUIRES_NEW)
    public void update(UQ_Library library){
        sessionFactory.getCurrentSession().merge(library);
    }

    @Transactional(value = "wrongSetTransactionManager", propagation = Propagation.SUPPORTS, isolation = Isolation.READ_COMMITTED)
    public UQ_Library getLibraryByKey(ULKey key){
        @SuppressWarnings("unchecked")
        Query<UQ_Library> query = sessionFactory.getCurrentSession().createQuery(
                "from UQ_Library as l where l.ulKey.userId=? and l.ulKey.libraryId=?",
                UQ_Library.class);
        query.setParameter(0,key.getUserId()).setParameter(1,key.getLibraryId());
        List<UQ_Library> ls = query.getResultList();
        UQ_Library l = ls.size() > 0 ? ls.get(0) : null;
        return l;
    }

    @Transactional(value = "wrongSetTransactionManager", propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public List<UQ_Library> getAllLibrariesById(int id){
        if (!redisTemplate.hasKey(String.valueOf(id))) {
            @SuppressWarnings("unchecked")
            Query<UQ_Library> query = sessionFactory.getCurrentSession().createQuery(
                    "from UQ_Library as l where l.ulKey.userId=?",
                    UQ_Library.class);
            query.setParameter(0,id);
            List<UQ_Library> ls = query.getResultList();
            redisTemplate.opsForList().rightPushAll(String.valueOf(id),ls);
            redisTemplate.expire(String.valueOf(id), 300, TimeUnit.SECONDS);
            return ls;
        }
        List<Object> s=redisTemplate.opsForList().range(String.valueOf(id),0,-1);
        if (s.size()>0) {
            List<UQ_Library> ls = (List<UQ_Library>) s.get(0);
            return ls;
        }
        else {
            List<UQ_Library> ls=new ArrayList<>();
            return ls;
        }
    }

    @Transactional(value = "wrongSetTransactionManager", propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public List<UQ_Library> getAllLibrariesByQuestion(int questionId){
        @SuppressWarnings("unchecked")
        Query<UQ_Library> query = sessionFactory.getCurrentSession().createQuery(
                "from UQ_Library as l where l.question.id=?",
                UQ_Library.class);
        query.setParameter(0,questionId);
        List<UQ_Library> ls = query.getResultList();
        return ls;
    }

    @Transactional(value = "wrongSetTransactionManager", propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public List<UQ_Library> getAllLibraries(){
        @SuppressWarnings("unchecked")
        Query<UQ_Library> query = sessionFactory.getCurrentSession().createQuery(
                "from UQ_Library as l where l.ulKey.userId<>111111",
                UQ_Library.class);
        List<UQ_Library> ls = query.getResultList();
        return ls;
    }
}
