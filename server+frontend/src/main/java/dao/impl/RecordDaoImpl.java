package dao.impl;

import dao.RecordDao;
import model.Record;
import model.URKey;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.sql.Blob;
import java.util.List;

public class RecordDaoImpl extends HibernateDaoSupport implements RecordDao {
    @Transactional(value = "wrongSetTransactionManager",propagation = Propagation.REQUIRES_NEW)
    public void save(Record record){
        getHibernateTemplate().save(record);
    }

    @Transactional(value = "wrongSetTransactionManager",propagation = Propagation.REQUIRES_NEW)
    public void delete(Record record){
        getHibernateTemplate().delete(record);
    }

    @Transactional(value = "wrongSetTransactionManager",propagation = Propagation.REQUIRES_NEW)
    public void update(Record record){
        getHibernateTemplate().merge(record);
    }

    @Transactional(value = "wrongSetTransactionManager", propagation = Propagation.SUPPORTS, isolation = Isolation.READ_COMMITTED)
    public Record getRecordByKey(URKey key){
        @SuppressWarnings("unchecked")
        List<Record> rs = (List<Record>) getHibernateTemplate().find(
                "from Record as r where r.urKey.userId=? " +
                        "and r.urKey.libraryId=?" +
                        "and r.urKey.recordId=?", key.getUserId(),key.getLibraryId(),key.getRecordId());
        Record r = rs.size() > 0 ? rs.get(0) : null;
        return r;
    }

    @Transactional(value = "wrongSetTransactionManager", propagation = Propagation.REQUIRED,isolation = Isolation.READ_COMMITTED)
    public List<Record> getAllRecords(int userId,int libraryId){
        @SuppressWarnings("unchecked")
        List<Record> rs = (List<Record>) getHibernateTemplate()
                .find("from Record as r where r.urKey.userId=? " +
                        "and r.urKey.libraryId=?",userId,libraryId);
        return rs;
    }

    public Blob convertBlob(String ans) throws UnsupportedEncodingException {
        byte[] bytes=ans.getBytes("utf-8");
        Blob blobContent= getSession().getLobHelper().createBlob(bytes);
        return blobContent;
    }
}
