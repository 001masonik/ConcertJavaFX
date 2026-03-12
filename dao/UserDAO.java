package org.example.dao;

import org.example.HibernateUtil;
import org.example.entity.User;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class UserDAO {

    public void save(User user) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.saveOrUpdate(user);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }

    public User findByLogin(String login) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM User WHERE login = :login", User.class)
                    .setParameter("login", login)
                    .uniqueResult();
        }
    }

    public User findByCredentials(String login, String password) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM User WHERE login = :login AND password = :pwd", User.class)
                    .setParameter("login", login)
                    .setParameter("pwd", password)
                    .uniqueResult();
        }
    }
}