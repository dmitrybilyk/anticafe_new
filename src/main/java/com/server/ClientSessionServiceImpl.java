package com.server;

import com.client.service.ClientSessionService;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.server.dao.ClientSessionDao;
import com.server.dao.ClientSessionHibernateDaoImpl;
import com.shared.model.ClientSession;
import com.shared.model.DatePoint;
import com.shared.model.MoreLessUnlimModel;
import com.shared.model.SessionPseudoName;
import com.shared.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: dmitry
 * Date: 8/9/16
 * Time: 2:01 PM
 * To change this template use File | Settings | File Templates.
 */
@Service
public class ClientSessionServiceImpl extends AutowiringRemoteServiceServlet implements ClientSessionService {
    @Autowired
//    @Qualifier(value = "ClientSessionHibernateDaoImpl")
    private ClientSessionDao clientSessionDao;

//    public void setClientSessionDao(ClientSessionDao clientSessionDao) {
//        this.clientSessionDao = clientSessionDao;
//    }

    //    private ClientSessionDao clientSessionDao = new ClientSessionHibernateDaoImpl();
//    private ClientSessionDao clientSessionDao = new ClientSessionNoSqlDaoImpl();
    @Override
    public List<SessionPseudoName> getFreePseudoNames(Long userId) {
        return clientSessionDao.getFreePseudoNames(userId);  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void markNameAsFree(String name, Long userId) {
        clientSessionDao.markNameAsFree(name, userId);
    }

    @Override
    public SessionPseudoName markNameAsUsed(String name, Long userId) {
        return clientSessionDao.markNameAsUsed(name, userId);
    }

    @Override
    public void addNames(List<SessionPseudoName> pseudoNamesList) {
        clientSessionDao.addNames(pseudoNamesList);
    }

    @Override
    public void removeUser(String value) {
        clientSessionDao.removeUser(value);
    }

    @Override
    public List<ClientSession> saveClientSession(DatePoint datePoint, ClientSession clientSession, boolean isShowRemoved, boolean showPayedOn) {
        List<ClientSession> clientSessions =  clientSessionDao.saveClientSession(datePoint, clientSession, isShowRemoved, showPayedOn);//To change body of implemented methods use File | Settings | File Templates.
//        clientSessionDao.markNameAsUsed(clientSession.getSessionPseudoName().getName(), UserUtils.currentUser.getUserEntity());
        return clientSessions;
    }

    @Override
    public void addUser(String userName) {
        clientSessionDao.addUser(userName);
    }

    @Override
    public List<ClientSession> removeClientSession(DatePoint datePoint, ClientSession clientSession, boolean isShowRemoved, boolean showPayedOn) {
        return clientSessionDao.removeClientSession(datePoint, clientSession, isShowRemoved, showPayedOn);
    }

    @Override
    public List<ClientSession> getClientSessions(DatePoint datePoint, long currentUser, boolean isShowRemoved, boolean showPayedOn) {
        return clientSessionDao.getClientSessionsList(datePoint, currentUser, isShowRemoved, showPayedOn);
    }

    @Override
    public void saveMoreLessModels(List<MoreLessUnlimModel> moreLessUnlimModels, Long userId) {
        clientSessionDao.saveMoreLessModels(moreLessUnlimModels, userId);
    }

    @Override
    public List<ClientSession> stopClientSession(DatePoint datePoint, ClientSession clientSession, boolean toShowRemoved, boolean toShowPayed) {
        return clientSessionDao.stopClientSession(datePoint, clientSession, toShowRemoved, toShowPayed);
    }

    @Override
    public List<ClientSession> payClientSession(DatePoint datePoint, ClientSession clientSession, boolean toShowRemoved, boolean toShowPayed) {
        return clientSessionDao.payClientSession(datePoint, clientSession, toShowRemoved, toShowPayed);
    }

    @Override
    public List<SessionPseudoName> getAllPseudoNames(Long userId) {
        return clientSessionDao.getAllPseudoNames(userId);
    }

    @Override
    public void addName(SessionPseudoName namesTextBoxValue) {
        clientSessionDao.addName(namesTextBoxValue);
    }

    @Override
    public List<SessionPseudoName> removeName(String sessionPseudoName, Long userId) {
        return clientSessionDao.removeName(sessionPseudoName, userId);
    }

    @Override
    public User getCurrentUser(String userName, String userPassword) {
        return clientSessionDao.getCurrentUser(userName, userPassword);
    }

    @Override
    public User saveUser(User user) {
        return clientSessionDao.saveUser(user);
    }

    @Override
    public User login(String userName, String userPassword) {
        return clientSessionDao.login(userName, userPassword);
    }

    @Override
    public List<ClientSession> startClientSession(DatePoint datePoint, ClientSession clientSession, boolean toShowRemoved, boolean toShowPayed) {
        return clientSessionDao.startClientSession(datePoint, clientSession, toShowRemoved, toShowPayed);
    }

    @Override
    public List<ClientSession> unlimClientSession(DatePoint currentDatePointValue, ClientSession clientSession, boolean toShowRemoved, boolean toShowPayed) {
        return clientSessionDao.unlimClientSession(currentDatePointValue, clientSession, toShowRemoved, toShowPayed);
    }

    @Override
    public void updateName(String oldName, String newName, Long userId) {
        clientSessionDao.updateName(oldName, newName, userId);
    }
}
