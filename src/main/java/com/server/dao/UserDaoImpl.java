package com.server.dao;

import com.server.hibernate.util.HibernateAnnotationUtil;
import com.shared.model.SettingsHolder;
import com.shared.model.User;
import com.shared.utils.UserUtils;
import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 * Created by dmitry on 16.10.16.
 */
public class UserDaoImpl implements UserDao {
  @Override
  public User login(String userName, char[] password) {
    Session hibernateSession = HibernateAnnotationUtil.getSessionFactory().openSession();
    Transaction transaction = null;
    try {
      transaction = hibernateSession.beginTransaction();
//            populateDB(hibernateSession);
//            hibernateSession.flush();

      Query query = hibernateSession.createQuery("from com.shared.model.User as u where u.userName=:userName");
      query.setParameter("userName", userName);
      User user = (User) query.uniqueResult();
      if (user != null) {
        Query settingsHolderQuery = hibernateSession.createQuery("from com.shared.model.SettingsHolder sh " +
                "where sh.userEntity.id = :userId");
        settingsHolderQuery.setParameter("userId", user.getUserId());
        SettingsHolder settingsHolder = (SettingsHolder) settingsHolderQuery.uniqueResult();
        UserUtils.init();
        UserUtils.setSettings(settingsHolder);
        UserUtils.currentUser = user;
      }
      transaction.commit();
      Mapper mapper = new DozerBeanMapper();

      User destObject = mapper.map(user, User.class);
      return destObject;
    } catch (HibernateException e) {
      if (transaction != null) {
        transaction.rollback();
      }
      e.printStackTrace();
    } finally {
      hibernateSession.close();
    }
    return null;
  }
}
