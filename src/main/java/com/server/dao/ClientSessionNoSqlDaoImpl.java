//package com.server.dao;
//
//import com.googlecode.objectify.Key;
//import com.googlecode.objectify.ObjectifyService;
//import com.googlecode.objectify.Result;
//import com.shared.model.ClientSession;
//import com.shared.model.DatePoint;
//import com.shared.model.MoreLessUnlimModel;
//import com.shared.model.SessionPseudoName;
//import com.shared.model.User;
//import com.shared.utils.UserUtils;
//
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.Date;
//import java.util.List;
//
///**
// * Created with IntelliJ IDEA.
// * User: dmitry
// * Date: 8/8/16
// * Time: 5:23 PM
// * To change this template use File | Settings | File Templates.
// */
//public class ClientSessionNoSqlDaoImpl implements ClientSessionDao{
////    Map<String,SessionPseudoName> pseudoNamesMap = new HashMap<>();
////    Map<Long, ClientSession> clientSessionMap = new HashMap<>();
//    @Override
//    public List<SessionPseudoName> getFreePseudoNames(Long userId) {
//        List<SessionPseudoName> freeNames = ObjectifyService.ofy().load().type(SessionPseudoName.class).
//                filter("isUsed == ", "false").list();//To change body of implemented methods use File | Settings | File Templates.
//        return freeNames;  //To change body of implemented methods use File | Settings | File Templates.
//    }
//
//    @Override
//    public void markNameAsFree(String name, Long userId) {
////        name.setIsUsed(false);
////        ObjectifyService.ofy().save().entity(name);
//    }
//
//    @Override
//    public SessionPseudoName markNameAsUsed(String name, Long userId) {
////        name.setIsUsed(true);
////        ObjectifyService.ofy().save().entity(name);
//        return null;
//    }
//
//  @Override
//  public SessionPseudoName markNameAsFreeById(Long nameId) {
//    return null;
//  }
//
//  @Override
//    public void addNames(List<SessionPseudoName> pseudoNamesList) {
//        for (SessionPseudoName name : pseudoNamesList) {
//            ObjectifyService.ofy().save().entity(name);
//        }
//    }
//
//  @Override
//  public void removeUser(String value) {
//
//  }
//
//  @Override
//    public List<ClientSession> saveClientSession(DatePoint datePoint, ClientSession clientSession, boolean isShowRemoved, boolean isShowPayed) {
//        Result<Key<ClientSession>> clientSessionResult = ObjectifyService.ofy().save().entity(clientSession);
//        return getClientSessionsList(datePoint, clientSession.getUserEntity(), isShowRemoved, isShowPayed);
//    }
//
//    @Override
//    public void addUser(String userName) {
//    }
//
//    @Override
//    public List<ClientSession> removeClientSession(DatePoint datePoint, ClientSession clientSession, boolean isShowRemoved, boolean showPayedOn) {
//        ObjectifyService.ofy().save().entity(clientSession);
//        return  getClientSessionsList(datePoint, clientSession.getUserEntity(), isShowRemoved, showPayedOn);
//    }
//
//    @Override
//    public List<ClientSession> getClientSessionsList(DatePoint datePoint, long currentUser, boolean isShowRemoved, boolean showPayedOn) {
//        Date comparedDate = new Date();
//        long comparedTime = 0;
//        Calendar c = Calendar.getInstance();
//        c.setTime(comparedDate);
//        c.add(Calendar.DATE, datePoint.getShiftValue());
//        c.set(Calendar.HOUR, 0);
//        c.set(Calendar.MINUTE, 0);
//        c.set(Calendar.SECOND, 0);
//        return ObjectifyService.ofy().load().type(ClientSession.class)
//                .filter("userId =", currentUser)
//                .filter("startTime > ", comparedTime).list();
//    }
//
//  @Override
//  public void saveMoreLessModels(List<MoreLessUnlimModel> moreLessUnlimModels, Long userId) {
//
//  }
//
//  @Override
//    public List<ClientSession> stopClientSession(DatePoint datePoint, ClientSession clientSession, boolean toShowRemoved, boolean toShowPayed) {
//        ObjectifyService.ofy().save().entity(clientSession);
//        return getClientSessionsList(datePoint, clientSession.getUserEntity(), toShowRemoved,
//                toShowPayed);
//    }
//
//    @Override
//    public List<ClientSession> payClientSession(DatePoint datePoint, ClientSession clientSession, boolean toShowRemoved, boolean toShowPayed) {
//        ObjectifyService.ofy().save().entity(clientSession);
//        return getClientSessionsList(datePoint, clientSession.getUserEntity(), toShowRemoved,
//                toShowPayed);
//    }
//
//    @Override
//    public List<SessionPseudoName> getAllPseudoNames(Long userId) {
//        return ObjectifyService.ofy().load().type(SessionPseudoName.class).filter("userId =", UserUtils.INSTANCE.getCurrentUser().getUserId())
//                .list();
//    }
//
//    @Override
//    public void addName(SessionPseudoName namesTextBoxValue) {
//        ObjectifyService.ofy().save().entity(namesTextBoxValue);
//    }
//
//    @Override
//    public List<SessionPseudoName> removeName(String sessionPseudoName, Long userId) {
////        ObjectifyService.ofy().delete().entity(sessionPseudoName);
//          return new ArrayList<>();
//    }
//
//    @Override
//    public User getCurrentUser(String userName, String userPassword) {
//        return ObjectifyService.ofy().load().type(User.class).filter("userName =", "userName").first().now();
//    }
//
//    @Override
//    public User saveUser(User user) {
//        ObjectifyService.ofy().save().entity(user);
//        return getCurrentUser(user.getUserName(), user.getPassword());
//    }
//
//    @Override
//    public User login(String userName, String userPassword) {
//        User user = ObjectifyService.ofy().load().type(User.class).filter("userName =", "userName").first().now();
//        if (user != null) {
////            SettingsHolder settingsHolder = ObjectifyService.ofy().load().type(SettingsHolder.class).filter("userId =", user.getUserEntity()).first().now();
////            UserUtils.init();
////            UserUtils.setSettings(settingsHolder);
////            UserUtils.INSTANCE.setCurrentUser(user);
//        }
//        return user;
//    }
//
//    @Override
//    public List<ClientSession> startClientSession(DatePoint datePoint, ClientSession clientSession, boolean toShowRemoved, boolean toShowPayed) {
//        ObjectifyService.ofy().save().entity(clientSession);
//        return getClientSessionsList(datePoint, clientSession.getUserEntity(), toShowRemoved,
//                toShowPayed);
//    }
//
//    @Override
//    public List<ClientSession> unlimClientSession(DatePoint currentDatePointValue, ClientSession clientSession, boolean toShowRemoved, boolean toShowPayed) {
//        ObjectifyService.ofy().save().entity(clientSession);
//        return getClientSessionsList(currentDatePointValue, clientSession.getUserEntity(), toShowRemoved,
//                toShowPayed);
//    }
//
//  @Override
//  public void updateName(String oldName, String newName, Long userId) {
//
//  }
//}
