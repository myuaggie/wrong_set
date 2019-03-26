package dao.impl;

import dao.RecordDao;
import model.Record;
import model.URKey;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.sql.Blob;
import java.util.List;

public class RecordDaoImpl implements RecordDao {

    private SessionFactory sessionFactory;

    public void setSessionFactory(SessionFactory sessionFactory){this.sessionFactory=sessionFactory;}

    @Transactional(value = "wrongSetTransactionManager",propagation = Propagation.REQUIRES_NEW)
    public void save(Record record){
        sessionFactory.getCurrentSession().save(record);
    }

    @Transactional(value = "wrongSetTransactionManager",propagation = Propagation.REQUIRES_NEW)
    public void delete(Record record){
        sessionFactory.getCurrentSession().delete(record);
    }

    @Transactional(value = "wrongSetTransactionManager",propagation = Propagation.REQUIRES_NEW)
    public void update(Record record){
        sessionFactory.getCurrentSession().merge(record);
    }

    @Transactional(value = "wrongSetTransactionManager", propagation = Propagation.SUPPORTS, isolation = Isolation.READ_COMMITTED)
    public Record getRecordByKey(URKey key){
        @SuppressWarnings("unchecked")
        Query<Record> query=sessionFactory.getCurrentSession().createQuery( "from Record as r where r.urKey.userId=? " +
                        "and r.urKey.libraryId=?" +
                        "and r.urKey.recordId=?",Record.class);
        query.setParameter(0,key.getUserId())
                .setParameter(1,key.getLibraryId())
                .setParameter(2,key.getRecordId());
        List<Record> rs = query.getResultList();
        Record r = rs.size() > 0 ? rs.get(0) : null;
        return r;
    }

    @Transactional(value = "wrongSetTransactionManager", propagation = Propagation.REQUIRED,isolation = Isolation.READ_COMMITTED)
    public List<Record> getAllRecords(int userId, int libraryId){
        @SuppressWarnings("unchecked")
        Query<Record> query=sessionFactory.getCurrentSession().createQuery("from Record as r where r.urKey.userId=? " +
                        "and r.urKey.libraryId=?",Record.class);
        query.setParameter(0,userId).setParameter(1,libraryId);
        List<Record> rs = query.getResultList();
        return rs;
    }

    public Blob convertBlob(String ans) throws UnsupportedEncodingException {
        byte[] bytes=ans.getBytes("utf-8");
        Blob blobContent= sessionFactory.getCurrentSession().getLobHelper().createBlob(bytes);
        return blobContent;
    }
}
