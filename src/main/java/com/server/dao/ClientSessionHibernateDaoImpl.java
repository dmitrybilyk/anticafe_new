package com.server.dao;

import com.server.hibernate.util.HibernateAnnotationUtil;
import com.shared.model.ClientSession;
import com.shared.model.DatePoint;
import com.shared.model.MoreLessUnlimModel;
import com.shared.model.SessionPseudoName;
import com.shared.model.SettingsHolder;
import com.shared.model.User;
import com.shared.utils.UserUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.BadPaddingException;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: dmitry
 * Date: 8/8/16
 * Time: 5:23 PM
 * To change this template use File | Settings | File Templates.
 */
@Repository
public class ClientSessionHibernateDaoImpl implements ClientSessionDao{
//    Map<String,SessionPseudoName> pseudoNamesMap = new HashMap<>();
//    Map<Long, ClientSession> clientSessionMap = new HashMap<>();
//    Map<Long, User> usersMap = new HashMap<>();
//    Map<Long, SettingsHolder> settingsHolderMap = new HashMap<>();
//    Map<Long, HourCostModel> hourCostModelMap = new HashMap<>();
//    Map<Long, MoreLessUnlimModel> moreLessUnlimModelMap = new HashMap<>();
//    @Autowired
//    private SessionFactory sessionFactory;

//    public void setSessionFactory(SessionFactory sessionFactory) {
//        this.sessionFactory = sessionFactory;
//    }

    public ClientSessionHibernateDaoImpl() {

    }

    private void addTestClientSession(User testUser, long startTime, long stopTime, ClientSession.SESSION_STATUS sessionStatus, Long finalSum, Session session) {
        ClientSession testClientSession = new ClientSession(startTime, stopTime, testUser.getUserId());
//        testClientSession.setId(getMaxId() + 1);
        testClientSession.setCreationTime(startTime - 70000);
        testClientSession.setFinalSum(finalSum);
        SessionPseudoName removedTestSessionPseudoName12 = new SessionPseudoName();
        if (testClientSession.getSessionStatus() != ClientSession.SESSION_STATUS.REMOVED) {
            removedTestSessionPseudoName12.setIsUsed(true);
        }
        removedTestSessionPseudoName12.setUserEntity(testUser.getUserId());
        session.save(removedTestSessionPseudoName12);
        testClientSession.setStatus(sessionStatus);
        testClientSession.setUserEntity(testUser.getUserId());
        session.save(testClientSession);
        removedTestSessionPseudoName12.setName("testName" + testClientSession.getId());
        testClientSession.setSessionPseudoName(removedTestSessionPseudoName12.getName());
    }

    @Override
    public List<SessionPseudoName> getFreePseudoNames(Long userId) {
        Session session = HibernateAnnotationUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            Query query = session.createQuery("from com.shared.model.SessionPseudoName where userEntity = :userId and isUsed = false " +
                    "order by name ASC");
            query.setParameter("userId", userId);
            List<SessionPseudoName> sessionPseudoNames = query.list();
            transaction.commit();
            return sessionPseudoNames;
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
        return null;
    }

    @Override
    public void logout(String userName) {
        Subject subject = SecurityUtils.getSubject();
        subject.logout();
    }

    @Override
    public void markNameAsFree(String name, Long userId) {
        Session session = HibernateAnnotationUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            Query nameQuery = session.createQuery("from SessionPseudoName spn where name =:name and userEntity =:userId");
            nameQuery.setParameter("name", name);
            nameQuery.setParameter("userId", userId);
            SessionPseudoName sessionPseudoName = (SessionPseudoName) nameQuery.uniqueResult();
            if (sessionPseudoName != null) {
                sessionPseudoName.setIsUsed(false);
            }
            session.saveOrUpdate(sessionPseudoName);
            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    @Override
    public SessionPseudoName markNameAsFreeById(Long nameId) {
        Session session = HibernateAnnotationUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            Query nameQuery = session.createQuery("from SessionPseudoName spn where id =:nameId");
            nameQuery.setParameter("nameId", nameId);
            SessionPseudoName sessionPseudoName = (SessionPseudoName) nameQuery.uniqueResult();
            if (sessionPseudoName != null) {
                sessionPseudoName.setIsUsed(false);
            }
            session.saveOrUpdate(sessionPseudoName);
            transaction.commit();
            return sessionPseudoName;
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
        return null;
    }

    @Override
    public SessionPseudoName markNameAsUsed(String name, Long userId) {
        Session session = HibernateAnnotationUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            Query nameQuery = session.createQuery("from SessionPseudoName spn where name =:name and userEntity =:userId");
            nameQuery.setParameter("name", name);
            nameQuery.setParameter("userId", userId);
            SessionPseudoName sessionPseudoName = (SessionPseudoName) nameQuery.uniqueResult();
            if (sessionPseudoName != null) {
                sessionPseudoName.setIsUsed(true);
            }
            session.update(sessionPseudoName);
            transaction.commit();
            return sessionPseudoName;
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
        return null;
    }

    @Override
    public void addNames(List<SessionPseudoName> pseudoNamesList) {
        Session session = HibernateAnnotationUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            for (SessionPseudoName name : pseudoNamesList) {
                session.save(name);
            }
            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    @Override
    public void removeUser(String userName) {
        Session session = HibernateAnnotationUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            Query userQuery = session.createQuery("from com.shared.model.User where userName =:userName");
            userQuery.setParameter("userName", userName);

            User testUser = (User) userQuery.uniqueResult();
            session.delete(testUser);
            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    @Override
    public List<ClientSession> saveClientSession(DatePoint datePoint, ClientSession clientSession, boolean isShowRemoved,
                                                 boolean isShowPayed) {
        Session session = HibernateAnnotationUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
                    User user = (User) session.get(User.class, clientSession.getUserEntity());
            user.getClientSessions().add(clientSession);
            clientSession.setUserEntity(clientSession.getUserEntity());
//            clientSession.setStartTime(System.currentTimeMillis());
//
//            Query nameQuery = session.createQuery("from SessionPseudoName spn where name =:name and user =:userId");
//            nameQuery.setParameter("name", clientSession.getSessionPseudoName().getName());
//            nameQuery.setParameter("userId", clientSession.getUserEntity());
//            SessionPseudoName sessionPseudoName = (SessionPseudoName) nameQuery.uniqueResult();
//            if (sessionPseudoName != null) {
//                sessionPseudoName.setIsUsed(true);
//            }
//            clientSession.setSessionPseudoName(se);
//            session.merge(sessionPseudoName);
//            markNameAsUsed(clientSession.getSessionPseudoName().getName(), UserUtils.currentUser.getUserEntity());
//            clientSession.getSessionPseudoName().setIsUsed(true);
            session.save(clientSession);
            transaction.commit();
            return getClientSessionsList(datePoint, clientSession.getUserEntity(), isShowRemoved, isShowPayed);
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
        return null;
    }

    @Override
    public void addUser(String userName) {
        addUser(HibernateAnnotationUtil.getSessionFactory().openSession(), userName);
    }

    @Override
    public List<ClientSession> removeClientSession(DatePoint datePoint, ClientSession clientSession, boolean isShowRemoved, boolean showPayedOn) {
        Session session = HibernateAnnotationUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            ClientSession clientSessionFromDb = (ClientSession) session.get(clientSession.getClass(), clientSession.getId());
            clientSessionFromDb.setStatus(ClientSession.SESSION_STATUS.REMOVED);
//            clientSessionFromDb.setFinalSum(0l);
            clientSessionFromDb.setStopTime(clientSession.getStopTime());
            markNameAsFree(clientSession.getSessionPseudoName(), clientSession.getUserEntity());
            transaction.commit();
            return getClientSessionsList(datePoint, clientSession.getUserEntity(), isShowRemoved, showPayedOn);
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
        return null;
    }

    @Override
    public List<ClientSession> getClientSessionsList(final DatePoint datePoint, long currentUserId, final boolean isShowRemoved, final boolean showPayedOn) {
        Session session = HibernateAnnotationUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            StringBuilder queryString = new StringBuilder();
            String mainQuery = "from com.shared.model.ClientSession where userEntity =:currentUserId";
            queryString.append(mainQuery);
            if (!isShowRemoved) {
                String removedQuery = " and status !=:removedStatus";
                queryString.append(removedQuery);
            }
            if (!showPayedOn) {
                String payedQuery = " and status !=:payedStatus";
                queryString.append(payedQuery);
            }
            Date comparedDate = new Date();
            Calendar c = Calendar.getInstance();
            c.setTime(comparedDate);
            c.add(Calendar.DATE, datePoint.getShiftValue());
            Calendar cal = DateUtils.truncate(c, Calendar.DATE);
            Date comparedTime = cal.getTime();

            String dateQuery = " and creationTime >:comparedTime ";
            queryString.append(dateQuery);
            String orderQuery = " order by creationTime DESC";
            queryString.append(orderQuery);
            Query query = session.createQuery(queryString.toString());
            query.setParameter("currentUserId", currentUserId);
            if (!isShowRemoved) {
                query.setParameter("removedStatus", ClientSession.SESSION_STATUS.REMOVED);
            }
            if (!showPayedOn) {
                query.setParameter("payedStatus", ClientSession.SESSION_STATUS.PAYED);
            }
            query.setParameter("comparedTime", comparedTime.getTime());
            List<ClientSession> clientSessions = query.list();
//            Predicate<ClientSession> removedPredicate = new Predicate<ClientSession>() {
//                @Override
//                public boolean apply(ClientSession clientSession) {
//                    return isShowRemoved || ClientSession.SESSION_STATUS.REMOVED != clientSession.getSessionStatus();
//                }
//            };
//            Predicate<ClientSession> payedPredicate = new Predicate<ClientSession>() {
//                @Override
//                public boolean apply(ClientSession clientSession) {
//                    return showPayedOn || ClientSession.SESSION_STATUS.PAYED != clientSession.getSessionStatus();
//                }
//            };
//            Predicate<ClientSession> datePointPredicate = new Predicate<ClientSession>() {
//                @Override
//                public boolean apply(ClientSession clientSession) {
//                    Date comparedDate = new Date();
//                    Calendar c = Calendar.getInstance();
//                    c.setTime(comparedDate);
//                    c.add(Calendar.DATE, datePoint.getShiftValue());
//                    Calendar cal = DateUtils.truncate(c, Calendar.DATE);
//                    Date comparedTime = cal.getTime();
//                    return clientSession.getStartTime() > comparedTime.getTime();
//                }
//            };
//            Collection<ClientSession> filteredByRemoveList = Collections2.filter(clientSessions, removedPredicate);
//            Collection<ClientSession> clientSessionCollections = Collections2.filter(filteredByRemoveList, payedPredicate);
//            Collection<ClientSession> filteredByDateCollections = Collections2.filter(clientSessionCollections, datePointPredicate);
//            ArrayList<ClientSession> filteredCollections = new ArrayList<>(filteredByDateCollections);
//            Collections.sort(filteredCollections);
            transaction.commit();
//            List<ClientSession> result = new ArrayList<>();
//            Mapper mapper = new DozerBeanMapper();
//            for (ClientSession clientSession : clientSessions) {
//                result.add(mapper.map(clientSession, ClientSession.class));
//            }
            return clientSessions;
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
        return null;
    }

    @Override
    public void saveMoreLessModels(List<MoreLessUnlimModel> moreLessUnlimModels, Long userId) {
        Session session = HibernateAnnotationUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            Query query = session.createQuery("delete from MoreLessUnlimModel where userEntity =:userId");
            query.setParameter("userId", userId);
            query.executeUpdate();
            for (MoreLessUnlimModel moreLessUnlimModel : moreLessUnlimModels) {
                moreLessUnlimModel.setUserEntity(userId);
                session.save(moreLessUnlimModel);
            }
            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    @Override
    public List<ClientSession> stopClientSession(DatePoint datePoint, ClientSession clientSession, boolean toShowRemoved, boolean toShowPayed) {
        Session session = HibernateAnnotationUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            ClientSession clientSessionFromDb = (ClientSession) session.get(clientSession.getClass(), clientSession.getId());
            clientSessionFromDb.setStatus(ClientSession.SESSION_STATUS.STOPPED);
            clientSessionFromDb.setStopTime(clientSession.getStopTime());
            clientSessionFromDb.setFinalTime(clientSession.getFinalTime());
            clientSessionFromDb.setFinalSum(clientSession.getFinalSum());
            transaction.commit();
            return getClientSessionsList(datePoint, clientSession.getUserEntity(), toShowRemoved, toShowPayed);
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
        return null;
    }

    @Override
    public List<ClientSession> payClientSession(DatePoint datePoint, ClientSession clientSession, boolean toShowRemoved, boolean toShowPayed) {
        Session session = HibernateAnnotationUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            ClientSession clientSessionFromDb = (ClientSession) session.get(clientSession.getClass(), clientSession.getId());
            clientSessionFromDb.setStatus(ClientSession.SESSION_STATUS.PAYED);
            clientSessionFromDb.setFinalSum(clientSession.getFinalSum());
//            clientSessionFromDb.setStopTime(clientSession.getStopTime());
            markNameAsFree(clientSessionFromDb.getSessionPseudoName(), clientSession.getUserEntity());
            transaction.commit();
            return getClientSessionsList(datePoint, clientSession.getUserEntity(), toShowRemoved, toShowPayed);
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
        return null;
    }

    @Override
    public List<SessionPseudoName> getAllPseudoNames(Long userId) {
        Session session = HibernateAnnotationUtil.getSessionFactory().openSession();

        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            Query query = session.createQuery("from com.shared.model.SessionPseudoName where userEntity =:userId");
            query.setParameter("userId", userId);
            List<SessionPseudoName> sessionPseudoNames = query.list();
            transaction.commit();
            return sessionPseudoNames;
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
        return null;
    }

    @Override
    public void addName(SessionPseudoName namesTextBoxValue) {
        Session session = HibernateAnnotationUtil.getSessionFactory().openSession();

        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            session.save(namesTextBoxValue);
            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    @Override
    public List<SessionPseudoName> removeName(String sessionPseudoName, Long userId) {
        Session session = HibernateAnnotationUtil.getSessionFactory().openSession();

        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            Query deleteQuery = session.createQuery("delete from SessionPseudoName where name=:name and " +
                    "userEntity =:userId");
            deleteQuery.setParameter("name", sessionPseudoName);
            deleteQuery.setParameter("userId", userId);
            deleteQuery.executeUpdate();
            transaction.commit();
            return getAllPseudoNames(userId);
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
        return null;
    }

    @Override
    public User getCurrentUser(String userName, String userPassword) {
        Session session = HibernateAnnotationUtil.getSessionFactory().openSession();

        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();

            Query query = session.createQuery("from com.shared.model.User u");
            List<User> users = query.list();
            User loggedUser = null;
            if (users != null && !users.isEmpty()) {
                loggedUser = users.get(0);
            }
            if (loggedUser != null) {
                Query settingsHolderQuery = session.createQuery("from com.shared.model.SettingsHolder");
//                settingsHolderQuery.setParameter("loggedUserId", loggedUser.getUserEntity());
                List<SettingsHolder> settingsHolders = settingsHolderQuery.list();
                SettingsHolder settingsHolder = null;
                if (settingsHolders != null && !settingsHolders.isEmpty()) {
                    settingsHolder = settingsHolders.get(0);
                }
                UserUtils.init();
                Query moreLessModel = session.createQuery("from com.shared.model.MoreLessUnlimModel");
                List<MoreLessUnlimModel>  moreLessUnlimModels = moreLessModel.list();
                Collections.sort(moreLessUnlimModels, new Comparator<MoreLessUnlimModel>() {
                    @Override
                    public int compare(MoreLessUnlimModel o1, MoreLessUnlimModel o2) {
                        return o1.getModelOrder() > o2.getModelOrder() ? 1 : -1;
                    }
                });
//                .setMoreLessUnlimModelList(moreLessUnlimModels);
                UserUtils.setSettings(settingsHolder);
//                loggedUser.setSettingsHolder(settingsHolder.getSettingsId());
                UserUtils.currentUser = loggedUser;
//                loggedUser.setSettingsHolder(settingsHolder.getSettingsId());
            }
            transaction.commit();
            return loggedUser;
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
        return null;
    }

    @Override
    public User saveUser(User user) {
//        User savedUser = usersMap.get(user.getUserEntity());
//        SettingsHolder settingsHolder = UserUtils.getSettings();
//        settingsHolder.setFirstPartLength(user.getSettings().getFirstPartLength());
//        settingsHolder.setFirstPartSumAmount(user.getSettings().getFirstPartSumAmount());
//        UserUtils.INSTANCE.setHourCostModelMap(user.getSettings().getHourCostModelMap());
//        settingsHolder.setMoreLessUnlimModelMap(user.getSettings().getMoreLessUnlimModelMap());
        Session session = HibernateAnnotationUtil.getSessionFactory().openSession();

        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            User dbUser = (User) session.get(User.class, user.getUserId());
            dbUser.setMoreLessUnlimModelList(user.getMoreLessUnlimModelList());
            dbUser.setUserName(user.getUserName());
            dbUser.setPassword(user.getPassword());
            dbUser.setClientSessions(user.getClientSessions());
            dbUser.setSessionPseudoNames(user.getSessionPseudoNames());
            dbUser.setSettingsHolder(user.getSettingsHolder());
            UserUtils.currentUser = dbUser;
            transaction.commit();
            return user;
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
        return null;
    }

    @Override
    @Transactional
    public User login(String userName, String userPassword) {
//        populateDB();
            Subject currentUser = SecurityUtils.getSubject();

            org.apache.shiro.session.Session session = currentUser.getSession();
            session.setAttribute("sessionId", session.getId());

            if (!currentUser.isAuthenticated()) {
                //collect user principals and credentials in a gui specific manner
                //such as username/password html form, X509 certificate, OpenID, etc.
                //We'll use the username/password example here since it is the most common.
                //(do you know what movie this is from? ;)
                UsernamePasswordToken token = new UsernamePasswordToken(userName, userPassword);
                //this is all you have to do to support 'remember me' (no config - built in!):
                token.setRememberMe(true);
                currentUser.login(token);
            }
        Session hibernateSession = HibernateAnnotationUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        try {
            transaction = hibernateSession.beginTransaction();
//            hibernateSession.flush();

            Query query = hibernateSession.createQuery("from com.shared.model.User as u where u.userName=:userName");
            query.setParameter("userName", userName);
            User user = (User) query.uniqueResult();
            if (user != null) {
                Query settingsHolderQuery = hibernateSession.createQuery("from com.shared.model.SettingsHolder sh " +
                        "where sh.userEntity.id = :userId");
                settingsHolderQuery.setParameter("userId", user.getUserId());
//                settingsHolderQuery.setParameter("loggedUserId", loggedUser.getUserEntity());
                SettingsHolder settingsHolder = (SettingsHolder) settingsHolderQuery.uniqueResult();
//                Query moreLessModelQuery = hibernateSession.createQuery("from com.shared.model.MoreLessUnlimModel ml " +
//                        "where ml.setting = :settingId");
//                moreLessModelQuery.setParameter("settingId", settingsHolder.getSettingsId());
//
//                if (moreLessModelQuery != null) {
//                    settingsHolder.setMoreLessUnlimModelList(moreLessModelQuery.list());
//                }
                UserUtils.init();
                UserUtils.setSettings(settingsHolder);
//                user.setSettingsHolder(settingsHolder.getSettingsId());
                UserUtils.currentUser = user;
            }
            transaction.commit();
            Mapper mapper = new DozerBeanMapper();

            User destObject =
                    mapper.map(user, User.class);
//            User destObject = new User();
//            mapper.map(user, destObject);
            return destObject;
        } catch (HibernateException e) {
            if (transaction != null) {
                try {
                    transaction.rollback();
                } catch(Exception re) {
                    System.err.println("Error when trying to rollback transaction:"); // use logging framework here
                    re.printStackTrace();
                }
            }
            System.err.println("Original error when executing query:"); // // use logging framework here

            e.printStackTrace();
        } finally {
            hibernateSession.close();
        }
        return null;
    }

    private void populateDB() {
        Session session = HibernateAnnotationUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
        User testUser = new User();
//        testUser.setUserEntity(0l);
        testUser.setUserName("dik81");
        testUser.setPassword("");


        SettingsHolder testSettingsHolder = new SettingsHolder();
        testSettingsHolder.setFirstPartLength(20000l);
        testSettingsHolder.setFirstPartSumAmount(3500l);
        session.save(testUser);
        session.save(testSettingsHolder);
        testUser.setSettingsHolder(testSettingsHolder.getSettingsId());
        testSettingsHolder.setUserEntity(testUser.getUserId());

        MoreLessUnlimModel moreLessUnlimModel = new MoreLessUnlimModel();
        moreLessUnlimModel.setNumberOfHours(1);
        moreLessUnlimModel.setCostForHours(150);
        moreLessUnlimModel.setCostPerMinute(5);
        moreLessUnlimModel.setModelOrder(1);
        moreLessUnlimModel.setUserEntity(testUser.getUserId());
        moreLessUnlimModel.setUnlimCost(500);
        session.save(moreLessUnlimModel);

        MoreLessUnlimModel moreLessUnlimModel2 = new MoreLessUnlimModel();
        moreLessUnlimModel2.setNumberOfHours(2);
        moreLessUnlimModel2.setCostForHours(250);
        moreLessUnlimModel2.setCostPerMinute(5);
        moreLessUnlimModel2.setModelOrder(2);
        moreLessUnlimModel2.setUserEntity(testUser.getUserId());
        moreLessUnlimModel2.setUnlimCost(500);
        session.save(moreLessUnlimModel2);

        MoreLessUnlimModel moreLessUnlimModel3 = new MoreLessUnlimModel();
        moreLessUnlimModel3.setNumberOfHours(3);
        moreLessUnlimModel3.setCostForHours(300);
        moreLessUnlimModel3.setCostPerMinute(5);
        moreLessUnlimModel3.setModelOrder(3);
        moreLessUnlimModel3.setUserEntity(testUser.getUserId());
        moreLessUnlimModel3.setUnlimCost(500);
        session.save(moreLessUnlimModel3);

        testUser.getMoreLessUnlimModelList().add(moreLessUnlimModel);
        testUser.getMoreLessUnlimModelList().add(moreLessUnlimModel2);
        testUser.getMoreLessUnlimModelList().add(moreLessUnlimModel3);

        addTestClientSession(testUser, System.currentTimeMillis() - 50000, 0, ClientSession.SESSION_STATUS.CREATED, 0l, session);
        addTestClientSession(testUser, System.currentTimeMillis() - 50000, 0, ClientSession.SESSION_STATUS.STARTED, 0l, session);
        addTestClientSession(testUser, System.currentTimeMillis() - 150000, System.currentTimeMillis(), ClientSession.SESSION_STATUS.PAYED, Long.valueOf("3637"), session);
        Date yesterday = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(yesterday);
        c.add(Calendar.DATE, -1);
        addTestClientSession(testUser, c.getTime().getTime(), System.currentTimeMillis(), ClientSession.SESSION_STATUS.PAYED, Long.valueOf("5555"), session);
        addTestClientSession(testUser, System.currentTimeMillis(), System.currentTimeMillis(), ClientSession.SESSION_STATUS.REMOVED, 0l, session);
        } catch (HibernateException e) {
            if (transaction != null) {
                try {
                    transaction.rollback();
                } catch(Exception re) {
                    System.err.println("Error when trying to rollback transaction:"); // use logging framework here
                    re.printStackTrace();
                }
            }
            System.err.println("Original error when executing query:"); // // use logging framework here

            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    private void addUser(Session session, String userName) {
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            User testUser = new User();
//        testUser.setUserEntity(0l);
            testUser.setUserName(userName);
            testUser.setPassword("");


            SettingsHolder testSettingsHolder = new SettingsHolder();
            testSettingsHolder.setFirstPartLength(20000l);
            testSettingsHolder.setFirstPartSumAmount(3500l);
            session.save(testUser);
            session.save(testSettingsHolder);
            testUser.setSettingsHolder(testSettingsHolder.getSettingsId());
            testSettingsHolder.setUserEntity(testUser.getUserId());

            MoreLessUnlimModel moreLessUnlimModel = new MoreLessUnlimModel();
            moreLessUnlimModel.setNumberOfHours(1);
            moreLessUnlimModel.setCostForHours(150);
            moreLessUnlimModel.setCostPerMinute(5);
            moreLessUnlimModel.setModelOrder(1);
            moreLessUnlimModel.setUserEntity(testUser.getUserId());
            moreLessUnlimModel.setUnlimCost(500);
            session.save(moreLessUnlimModel);

            MoreLessUnlimModel moreLessUnlimModel2 = new MoreLessUnlimModel();
            moreLessUnlimModel2.setNumberOfHours(2);
            moreLessUnlimModel2.setCostForHours(250);
            moreLessUnlimModel2.setCostPerMinute(4);
            moreLessUnlimModel2.setModelOrder(2);
            moreLessUnlimModel2.setUserEntity(testUser.getUserId());
            moreLessUnlimModel2.setUnlimCost(500);
            session.save(moreLessUnlimModel2);

            MoreLessUnlimModel moreLessUnlimModel3 = new MoreLessUnlimModel();
            moreLessUnlimModel3.setNumberOfHours(3);
            moreLessUnlimModel3.setCostForHours(300);
            moreLessUnlimModel3.setCostPerMinute(3);
            moreLessUnlimModel3.setModelOrder(3);
            moreLessUnlimModel3.setUserEntity(testUser.getUserId());
            moreLessUnlimModel3.setUnlimCost(500);
            session.save(moreLessUnlimModel3);

            testUser.getMoreLessUnlimModelList().add(moreLessUnlimModel);
            testUser.getMoreLessUnlimModelList().add(moreLessUnlimModel2);
            testUser.getMoreLessUnlimModelList().add(moreLessUnlimModel3);
            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
    }


    @Override
    public List<ClientSession> startClientSession(DatePoint datePoint, ClientSession clientSession, boolean toShowRemoved, boolean toShowPayed) {
        Session session = HibernateAnnotationUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            ClientSession clientSessionFromDb = (ClientSession) session.get(clientSession.getClass(), clientSession.getId());
            clientSessionFromDb.setStatus(ClientSession.SESSION_STATUS.STARTED);
            clientSessionFromDb.setStartTime(clientSession.getStartTime());
            clientSessionFromDb.setStopTime(clientSession.getStopTime());
            clientSessionFromDb.setPausedTimeSum(clientSession.getPausedTimeSum());
            session.update(clientSessionFromDb);
            transaction.commit();
            return getClientSessionsList(datePoint, clientSession.getUserEntity(), toShowRemoved, toShowPayed);
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
        return null;
    }

    @Override
    public List<ClientSession> unlimClientSession(DatePoint currentDatePointValue, ClientSession clientSession, boolean toShowRemoved, boolean toShowPayed) {
        Session session = HibernateAnnotationUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            ClientSession clientSessionFromDb = (ClientSession) session.get(clientSession.getClass(), clientSession.getId());
            clientSessionFromDb.setStatus(ClientSession.SESSION_STATUS.STOPPED_UNLIMITED);
            clientSessionFromDb.setStopTime(clientSession.getStopTime());
            clientSessionFromDb.setFinalTime(clientSession.getFinalTime());
            clientSessionFromDb.setFinalSum(clientSession.getFinalSum());
            transaction.commit();
            return getClientSessionsList(currentDatePointValue, clientSession.getUserEntity(), toShowRemoved, toShowPayed);
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
        return null;
    }

    @Override
    public void updateName(String oldName, String newName, Long userId) {
        Session session = HibernateAnnotationUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            Query nameQuery = session.createQuery("from SessionPseudoName spn where name =:name and userEntity =:userId");
            nameQuery.setParameter("name", oldName);
            nameQuery.setParameter("userId", userId);
            SessionPseudoName sessionPseudoName = (SessionPseudoName) nameQuery.uniqueResult();
            sessionPseudoName.setName(newName);
            session.saveOrUpdate(sessionPseudoName);

            Query clientSessionsQuery = session.createQuery("from ClientSession where sessionPseudoName =:name");
            clientSessionsQuery.setParameter("name", oldName);
            List<ClientSession> clientSessions = clientSessionsQuery.list();
            for (ClientSession clientSession: clientSessions) {
                if (clientSession.getSessionPseudoName().equals(oldName)) {
                    clientSession.setSessionPseudoName(newName);
                }
            session.update(clientSession);
            }
            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

//    private long getMaxId() {
//        long maxId = 0;
//        for (Long key : clientSessionMap.keySet()) {
//            if (key > maxId) {
//                maxId = key;
//            }
//        }
//        return maxId;
//    }
//    @Override
//    public void saveClientSession(ClientSession clientSession) {
//        ObjectifyService.ofy().save().entity(clientSession);//To change body of implemented methods use File | Settings | File Templates.
//    }
}
