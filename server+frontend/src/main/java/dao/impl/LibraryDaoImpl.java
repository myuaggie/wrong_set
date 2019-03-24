package dao.impl;

import dao.LibraryDao;
import model.ULKey;
import model.UQ_Library;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public class LibraryDaoImpl extends HibernateDaoSupport implements LibraryDao {
    @Transactional(value = "wrongSetTransactionManager",propagation = Propagation.REQUIRES_NEW)
    public void save(UQ_Library library){
        getHibernateTemplate().save(library);
    }

    @Transactional(value = "wrongSetTransactionManager",propagation = Propagation.REQUIRES_NEW)
    public void delete(UQ_Library library){
        getHibernateTemplate().delete(library);
    }

    @Transactional(value = "wrongSetTransactionManager",propagation = Propagation.REQUIRES_NEW)
    public void update(UQ_Library library){
        getHibernateTemplate().merge(library);
    }

    @Transactional(value = "wrongSetTransactionManager", propagation = Propagation.SUPPORTS, isolation = Isolation.READ_COMMITTED)
    public UQ_Library getLibraryByKey(ULKey key){
        @SuppressWarnings("unchecked")
        List<UQ_Library> ls = (List<UQ_Library>) getHibernateTemplate().find(
                "from UQ_Library as l where l.ulKey.userId=? and l.ulKey.libraryId=?", key.getUserId(),key.getLibraryId());
        UQ_Library l = ls.size() > 0 ? ls.get(0) : null;
        return l;
    }

    @Transactional(value = "wrongSetTransactionManager", propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public List<UQ_Library> getAllLibrariesById(int id){
        @SuppressWarnings("unchecked")
        List<UQ_Library> ls = (List<UQ_Library>) getHibernateTemplate()
                .find("from UQ_Library as l where l.ulKey.userId=?",id);
        return ls;
    }

    @Transactional(value = "wrongSetTransactionManager", propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public List<UQ_Library> getAllLibrariesByQuestion(int questionId){
        @SuppressWarnings("unchecked")
        List<UQ_Library> ls = (List<UQ_Library>) getHibernateTemplate()
                .find("from UQ_Library as l where l.question.id=?",questionId);
        return ls;
    }

    @Transactional(value = "wrongSetTransactionManager", propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public List<UQ_Library> getAllLibraries(){
        @SuppressWarnings("unchecked")
        List<UQ_Library> ls = (List<UQ_Library>) getHibernateTemplate()
                .find("from UQ_Library as l where l.ulKey.userId<>111111");
        return ls;
    }
}
