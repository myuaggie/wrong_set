package dao.impl;

import java.util.List;

import model.User;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import dao.UserDao;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class UserDaoImpl extends HibernateDaoSupport implements UserDao{
    @Transactional(value = "wrongSetTransactionManager",propagation = Propagation.REQUIRES_NEW)
    public Integer save(User user) {
        return (Integer) getHibernateTemplate().save(user);
    }

    @Transactional(value = "wrongSetTransactionManager",propagation = Propagation.REQUIRES_NEW)
    public void delete(User user) {
        getHibernateTemplate().delete(user);
    }

    @Transactional(value = "wrongSetTransactionManager",propagation = Propagation.REQUIRES_NEW)
    public void update(User user) {
        getHibernateTemplate().merge(user);
    }

    @Transactional(value = "wrongSetTransactionManager", propagation = Propagation.SUPPORTS, isolation = Isolation.READ_COMMITTED)
    public User getUserById(int id) {
        @SuppressWarnings("unchecked")
        List<User> users = (List<User>) getHibernateTemplate().find(
                "from User as u where u.id=?", id);
        User user = users.size() > 0 ? users.get(0) : null;
        return user;
    }

    @Transactional(value = "wrongSetTransactionManager", propagation = Propagation.SUPPORTS,isolation = Isolation.READ_COMMITTED)
    public User getUserByPhone(String phone){
        @SuppressWarnings("unchecked")
        List<User> users = (List<User>) getHibernateTemplate().find(
                "from User as u where u.phone=?", phone);
        User user = users.size() > 0 ? users.get(0) : null;
        return user;
    }

    @Transactional(value = "wrongSetTransactionManager", propagation = Propagation.REQUIRED,isolation = Isolation.READ_COMMITTED)
    public List<User> getAllUsers() {
        @SuppressWarnings("unchecked")
        List<User> users = (List<User>) getHibernateTemplate()
                .find("from User");
        return users;
    }
}
