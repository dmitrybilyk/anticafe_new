package com.server.dao;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.server.hibernate.util.HibernateAnnotationUtil;
import com.shared.model.ClientSession;
import com.shared.model.DatePoint;
import com.shared.model.HourCostModel;
import com.shared.model.MoreLessUnlimModel;
import com.shared.model.SessionPseudoName;
import com.shared.model.SettingsHolder;
import com.shared.model.User;
import com.shared.utils.UserUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: dmitry
 * Date: 8/8/16
 * Time: 5:23 PM
 * To change this template use File | Settings | File Templates.
 */
public class ClientSessionMemoryDaoImpl implements ClientSessionDao{
    Map<String,SessionPseudoName> pseudoNamesMap = new HashMap<>();
    Map<Long, ClientSession> clientSessionMap = new HashMap<>();
    Map<Long, User> usersMap = new HashMap<>();
    Map<Long, SettingsHolder> settingsHolderMap = new HashMap<>();
    Map<Long, HourCostModel> hourCostModelMap = new HashMap<>();
    Map<Long, MoreLessUnlimModel> moreLessUnlimModelMap = new HashMap<>();

    public ClientSessionMemoryDaoImpl() {
        User testUser = new User();
        testUser.setUserId(0l);
        testUser.setUserName("a");
        testUser.setPassword("");
        usersMap.put(testUser.getUserId(), testUser);

        SettingsHolder testSettingsHolder = new SettingsHolder();
        testSettingsHolder.setFirstPartLength(20000l);
        testSettingsHolder.setFirstPartSumAmount(3500l);
        testSettingsHolder.setSettingsId(0l);
//        testSettingsHolder.setUserEntity(testUser.getUserEntity());
        testSettingsHolder.setUserEntity(testUser.getUserId());
        settingsHolderMap.put(testSettingsHolder.getSettingsId(), testSettingsHolder);

//        addTestClientSession(testUser, System.currentTimeMillis() - 150000, System.currentTimeMillis(), ClientSession.SESSION_STATUS.REMOVED);
        addTestClientSession(testUser, System.currentTimeMillis() - 50000, 0, ClientSession.SESSION_STATUS.CREATED, 0l);
        addTestClientSession(testUser, System.currentTimeMillis() - 150000, System.currentTimeMillis(), ClientSession.SESSION_STATUS.REMOVED, Long.valueOf("3508"));
//        addTestClientSession(testUser, System.currentTimeMillis() - 150000, System.currentTimeMillis(), ClientSession.SESSION_STATUS.REMOVED);
//        addTestClientSession(testUser, System.currentTimeMillis() - 150000, System.currentTimeMillis(), ClientSession.SESSION_STATUS.REMOVED);
        addTestClientSession(testUser, System.currentTimeMillis() - 150000, System.currentTimeMillis(), ClientSession.SESSION_STATUS.PAYED, Long.valueOf("3637"));
        Date yesterday = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(yesterday);
        c.add(Calendar.DATE, -1);
//        CalendarUtil.addDaysToDate(yesterday, -1);
        addTestClientSession(testUser, yesterday.getTime(), System.currentTimeMillis(), ClientSession.SESSION_STATUS.PAYED, Long.valueOf("5555"));
//        addTestClientSession(testUser, System.currentTimeMillis() - 150000, System.currentTimeMillis(), ClientSession.SESSION_STATUS.REMOVED);
//        addTestClientSession(testUser, System.currentTimeMillis() - 150000, System.currentTimeMillis(), ClientSession.SESSION_STATUS.REMOVED);
//        addTestClientSession(testUser, System.currentTimeMillis() - 150000, System.currentTimeMillis(), ClientSession.SESSION_STATUS.REMOVED);
//        addTestClientSession(testUser, System.currentTimeMillis() - 150000, System.currentTimeMillis(), ClientSession.SESSION_STATUS.REMOVED);
//        addTestClientSession(testUser, System.currentTimeMillis() - 150000, System.currentTimeMillis(), ClientSession.SESSION_STATUS.REMOVED);
//        addTestClientSession(testUser, System.currentTimeMillis() - 150000, System.currentTimeMillis(), ClientSession.SESSION_STATUS.REMOVED);
//        addTestClientSession(testUser, System.currentTimeMillis() - 150000, System.currentTimeMillis(), ClientSession.SESSION_STATUS.REMOVED);
        addTestClientSession(testUser, System.currentTimeMillis(), System.currentTimeMillis(), ClientSession.SESSION_STATUS.REMOVED, 0l);
//        addTestClientSession(testUser, System.currentTimeMillis() - 150000, System.currentTimeMillis(), ClientSession.SESSION_STATUS.REMOVED);
//        addTestClientSession(testUser, System.currentTimeMillis() - 150000, System.currentTimeMillis(), ClientSession.SESSION_STATUS.REMOVED);
//        addTestClientSession(testUser, System.currentTimeMillis() - 150000, System.currentTimeMillis(), ClientSession.SESSION_STATUS.REMOVED);
//        addTestClientSession(testUser, System.currentTimeMillis() - 150000, System.currentTimeMillis(), ClientSession.SESSION_STATUS.REMOVED);
    }

    private void addTestClientSession(User testUser, long startTime, long stopTime, ClientSession.SESSION_STATUS sessionStatus, Long finalSum) {
        ClientSession testClientSession = new ClientSession(startTime, stopTime, testUser.getUserId());
        testClientSession.setId(getMaxId() + 1);
        testClientSession.setCreationTime(startTime - 70000);
        testClientSession.setFinalSum(finalSum);
        SessionPseudoName removedTestSessionPseudoName12 = new SessionPseudoName("testName" + testClientSession.getId());
        addName(removedTestSessionPseudoName12);
        removedTestSessionPseudoName12.setIsUsed(true);
        testClientSession.setSessionPseudoName(removedTestSessionPseudoName12.getName());
        testClientSession.setStatus(sessionStatus);
        clientSessionMap.put(testClientSession.getId(), testClientSession);
    }

    @Override
    public List<SessionPseudoName> getFreePseudoNames(Long userId) {
        List<SessionPseudoName> freeNames = new ArrayList<>();
        for (String key : pseudoNamesMap.keySet()) {
            if (!pseudoNamesMap.get(key).isUsed()) {
                freeNames.add(pseudoNamesMap.get(key));
            }
        }
        return freeNames;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void markNameAsFree(String name, Long userId) {

    }

    @Override
    public SessionPseudoName markNameAsUsed(String name, Long userId) {
        this.pseudoNamesMap.get(name).setIsUsed(true);
        return pseudoNamesMap.get(name);
    }

    @Override
    public SessionPseudoName markNameAsFreeById(Long nameId) {
        return null;
    }

    @Override
    public void addNames(List<SessionPseudoName> pseudoNamesList) {
        for (SessionPseudoName name : pseudoNamesList) {
            this.pseudoNamesMap.put(name.getName(), name);
        }
    }

    @Override
    public void removeUser(String value) {

    }

    @Override
    public List<ClientSession> saveClientSession(DatePoint datePoint, ClientSession clientSession, boolean isShowRemoved,
                                                 boolean isShowPayed) {
        Session session = HibernateAnnotationUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        session.save(clientSession);
        tx.commit();
        session.close();

        //Loading the Student and the Course objects
        HibernateAnnotationUtil.shutdown();
        System.out.println("------Done------");
        return new ArrayList<>();

//        clientSession.setId(getMaxId() + 1);
//        clientSession.setUserEntity(UserUtils.INSTANCE.getCurrentUser().getUserEntity());
//        clientSession.setStartTime(System.currentTimeMillis());
//        markNameAsUsed(new SessionPseudoName(clientSession.getSessionPseudoName()));
//        this.clientSessionMap.put(clientSession.getId(), clientSession);
//        return getClientSessionsList(datePoint, UserUtils.INSTANCE.getCurrentUser(), isShowRemoved, isShowPayed);

    }

    @Override
    public void addUser(String userName) {

    }

    @Override
    public List<ClientSession> removeClientSession(DatePoint datePoint, ClientSession clientSession, boolean isShowRemoved, boolean showPayedOn) {
        ClientSession sessionToRemove = this.clientSessionMap.get(clientSession.getId());
        if (sessionToRemove != null) {
            sessionToRemove.setStatus(ClientSession.SESSION_STATUS.REMOVED);
            sessionToRemove.setFinalSum(0l);
            markNameAsFree(clientSession.getSessionPseudoName(), UserUtils.currentUser.getUserId());
        }
        return getClientSessionsList(datePoint, clientSession.getUserEntity(), isShowRemoved, showPayedOn);
    }

    @Override
    public List<ClientSession> getClientSessionsList(final DatePoint datePoint, long currentUser, final boolean isShowRemoved, final boolean showPayedOn) {
        List<ClientSession> clientSessions = new ArrayList<>();
        for (ClientSession clientSession : clientSessionMap.values()) {
            if (clientSession.getUserEntity() == currentUser) {
//                if (isShowRemoved || (clientSession.getSessionStatus() != ClientSession.SESSION_STATUS.REMOVED)) {
                    clientSessions.add(clientSession);
//                }
            }
        }
        Predicate<ClientSession> removedPredicate = new Predicate<ClientSession>() {
            @Override
            public boolean apply(ClientSession clientSession) {
                return isShowRemoved || ClientSession.SESSION_STATUS.REMOVED != clientSession.getSessionStatus();
            }
        };
        Predicate<ClientSession> payedPredicate = new Predicate<ClientSession>() {
            @Override
            public boolean apply(ClientSession clientSession) {
                return showPayedOn || ClientSession.SESSION_STATUS.PAYED != clientSession.getSessionStatus();
            }
        };
        Predicate<ClientSession> datePointPredicate = new Predicate<ClientSession>() {
            @Override
            public boolean apply(ClientSession clientSession) {
                Date comparedDate = new Date();
                long comparedTime;
                Calendar c = Calendar.getInstance();
                c.setTime(comparedDate);
                c.add(Calendar.DATE, datePoint.getShiftValue());
                c.set(Calendar.HOUR, 0);
                c.set(Calendar.MINUTE, 0);
                c.set(Calendar.SECOND, 0);
//                CalendarUtil.addDaysToDate(comparedDate, datePoint.getShiftValue());
//                CalendarUtil.resetTime(comparedDate);
                comparedTime = c.getTime().getTime();
                return clientSession.getStartTime() > comparedTime;
            }
        };
        Collection<ClientSession> filteredByRemoveList = Collections2.filter(clientSessions, removedPredicate);
        Collection<ClientSession> clientSessionCollections = Collections2.filter(filteredByRemoveList, payedPredicate);
        Collection<ClientSession> filteredByDateCollections = Collections2.filter(clientSessionCollections, datePointPredicate);
        ArrayList<ClientSession> filteredCollections = new ArrayList<>(filteredByDateCollections);
        Collections.sort(filteredCollections);
        return filteredCollections;
    }

    @Override
    public void saveMoreLessModels(List<MoreLessUnlimModel> moreLessUnlimModels, Long userId) {

    }

    @Override
    public List<ClientSession> stopClientSession(DatePoint datePoint, ClientSession clientSession, boolean toShowRemoved, boolean toShowPayed) {
        ClientSession session = this.clientSessionMap.get(clientSession.getId());
        session.setStatus(ClientSession.SESSION_STATUS.STOPPED);
        session.setStopTime(clientSession.getStopTime());
        session.setFinalSum(clientSession.getFinalSum());
        return getClientSessionsList(datePoint, clientSession.getUserEntity(), toShowRemoved, toShowPayed);
    }

    @Override
    public List<ClientSession> payClientSession(DatePoint datePoint, ClientSession clientSession, boolean toShowRemoved, boolean toShowPayed) {
        ClientSession session = this.clientSessionMap.get(clientSession.getId());
        session.setStatus(ClientSession.SESSION_STATUS.PAYED);
        markNameAsFree(session.getSessionPseudoName(), UserUtils.currentUser.getUserId());
        return getClientSessionsList(datePoint, clientSession.getUserEntity(), toShowRemoved, toShowPayed);
    }

    @Override
    public List<SessionPseudoName> getAllPseudoNames(Long userId) {
        List<SessionPseudoName> allNames = new ArrayList<>();
        for (String key : pseudoNamesMap.keySet()) {
            allNames.add(pseudoNamesMap.get(key));
        }
        return allNames;
    }

    @Override
    public void addName(SessionPseudoName namesTextBoxValue) {
        this.pseudoNamesMap.put(namesTextBoxValue.getName(), namesTextBoxValue);
    }

    @Override
    public List<SessionPseudoName> removeName(String sessionPseudoName, Long userId) {
//        return this.pseudoNamesMap.remove(sessionPseudoName);
        return new ArrayList<>();
    }

    @Override
    public User getCurrentUser(String userName, String userPassword) {
        for (Long key : usersMap.keySet()) {
            User userFromMap = usersMap.get(key);
            if (userFromMap != null && userFromMap.getUserName().equals(userName) &&
                    userFromMap.getPassword().equals(userPassword)) {
                for (Long settingsKey : settingsHolderMap.keySet()) {
                    SettingsHolder settingsHolder = settingsHolderMap.get(settingsKey);
                    if (settingsHolder.getUserEntity() == (userFromMap.getUserId())) {
                        UserUtils.setSettings(settingsHolder);
                        return userFromMap;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public User saveUser(User user) {
        User savedUser = usersMap.get(user.getUserId());
        SettingsHolder settingsHolder = UserUtils.getSettings();
//        settingsHolder.setFirstPartLength(user.getSettings().getFirstPartLength());
//        settingsHolder.setFirstPartSumAmount(user.getSettings().getFirstPartSumAmount());
//        UserUtils.INSTANCE.setHourCostModelMap(user.getSettings().getHourCostModelMap());
//        settingsHolder.setMoreLessUnlimModelMap(user.getSettings().getMoreLessUnlimModelMap());
        return savedUser;
    }

    @Override
    public User login(String userName, String userPassword) {
        for (Long key : usersMap.keySet()) {
            User userFromMap = usersMap.get(key);
            if (userFromMap != null && userFromMap.getUserName().equals(userName) &&
                    userFromMap.getPassword().equals(userPassword)) {
                for (Long settingsKey : settingsHolderMap.keySet()) {
                    SettingsHolder settingsHolder = settingsHolderMap.get(settingsKey);
                    if (settingsHolder.getUserEntity() == userFromMap.getUserId()) {
                        UserUtils.init();
                        UserUtils.setSettings(settingsHolder);
                        UserUtils.INSTANCE.setCurrentUser(userFromMap);
                        return userFromMap;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public List<ClientSession> startClientSession(DatePoint datePoint, ClientSession clientSession, boolean toShowRemoved, boolean toShowPayed) {
        ClientSession session = this.clientSessionMap.get(clientSession.getId());
        session.setStatus(ClientSession.SESSION_STATUS.STARTED);
        session.setStartTime(clientSession.getStartTime());
        return getClientSessionsList(datePoint, clientSession.getUserEntity(), toShowRemoved, toShowPayed);
    }

    @Override
    public List<ClientSession> unlimClientSession(DatePoint currentDatePointValue, ClientSession clientSession, boolean toShowRemoved, boolean toShowPayed) {
        ClientSession session = this.clientSessionMap.get(clientSession.getId());
        session.setStatus(ClientSession.SESSION_STATUS.STOPPED_UNLIMITED);
        session.setStopTime(clientSession.getStopTime());
        session.setFinalSum(clientSession.getFinalSum());
        return getClientSessionsList(currentDatePointValue, clientSession.getUserEntity(), toShowRemoved, toShowPayed);
    }

    @Override
    public void updateName(String oldName, String newName, Long userId) {

    }

    private long getMaxId() {
        long maxId = 0;
        for (Long key : clientSessionMap.keySet()) {
            if (key > maxId) {
                maxId = key;
            }
        }
        return maxId;
    }
//    @Override
//    public void saveClientSession(ClientSession clientSession) {
//        ObjectifyService.ofy().save().entity(clientSession);//To change body of implemented methods use File | Settings | File Templates.
//    }
}
