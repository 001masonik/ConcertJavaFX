package org.example.dao;

import org.example.HibernateUtil;
import org.example.entity.Config;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class ConfigDAO {

    public Config getOrCreate() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Config config = session.get(Config.class, 1L);
            if (config == null) {
                Transaction tx = session.beginTransaction();
                config = new Config();
                session.save(config);
                tx.commit();
            }
            return config;
        }
    }

    public void update(Config config) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.update(config);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }
}