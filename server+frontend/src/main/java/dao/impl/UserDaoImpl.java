package dao.impl;

import dao.UserDao;
import model.User;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public class UserDaoImpl implements UserDao {

    private SessionFactory sessionFactory;

    public void setSessionFactory(SessionFactory sessionFactory){this.sessionFactory=sessionFactory;}

    @Transactional(value = "wrongSetTransactionManager",propagation = Propagation.REQUIRES_NEW)
    public Integer save(User user) {
        return (Integer) sessionFactory.getCurrentSession().save(user);
    }

    @Transactional(value = "wrongSetTransactionManager",propagation = Propagation.REQUIRES_NEW)
    public void delete(User user) {
        sessionFactory.getCurrentSession().delete(user);
    }

    @Transactional(value = "wrongSetTransactionManager",propagation = Propagation.REQUIRES_NEW)
    public void update(User user) {
        sessionFactory.getCurrentSession().merge(user);
    }

    @Transactional(value = "wrongSetTransactionManager", propagation = Propagation.SUPPORTS, isolation = Isolation.READ_COMMITTED)
    public User getUserById(int id) {
        @SuppressWarnings("unchecked")
        Query<User> query=sessionFactory.getCurrentSession().createQuery(
                "from User as u where u.id=?",User.class);
        query.setParameter(0,id);
        List<User> users = query.getResultList();
        User user = users.size() > 0 ? users.get(0) : null;
        return user;
    }

    @Transactional(value = "wrongSetTransactionManager", propagation = Propagation.SUPPORTS,isolation = Isolation.READ_COMMITTED)
    public User getUserByPhone(String phone){
        @SuppressWarnings("unchecked")
        Query<User> query=sessionFactory.getCurrentSession().createQuery(
                "from User as u where u.phone=?",User.class);
        query.setParameter(0,phone);
        List<User> users = query.getResultList();
        User user = users.size() > 0 ? users.get(0) : null;
        return user;
    }

    @Transactional(value = "wrongSetTransactionManager", propagation = Propagation.REQUIRED,isolation = Isolation.READ_COMMITTED)
    public List<User> getAllUsers() {
        @SuppressWarnings("unchecked")
        Query<User> query=sessionFactory.getCurrentSession().createQuery(
                        "from User", User.class);
        List<User> users = query.getResultList();
        return users;
    }
}
