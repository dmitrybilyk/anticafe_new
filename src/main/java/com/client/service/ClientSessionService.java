package com.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.shared.model.ClientSession;
import com.shared.model.DatePoint;
import com.shared.model.MoreLessUnlimModel;
import com.shared.model.SessionPseudoName;
import com.shared.model.User;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: dmitry
 * Date: 8/9/16
 * Time: 1:58 PM
 * To change this template use File | Settings | File Templates.
 */
@RemoteServiceRelativePath("clientSession")
public interface ClientSessionService extends RemoteService {
    public List<SessionPseudoName> getFreePseudoNames(Long userId);
    public void markNameAsFree(String name, Long userId);
    public SessionPseudoName markNameAsUsed(String name, Long userId);

    void addNames(List<SessionPseudoName> pseudoNamesList);

    void removeUser(String value);

    List<ClientSession> saveClientSession(DatePoint datePoint, ClientSession clientSession, boolean isShowRemoved, boolean showPayedOn);

    void addUser(String userName);

    List<ClientSession> removeClientSession(DatePoint datePoint, ClientSession clientSession, boolean isShowRemoved, boolean showPayedOn);

    List<ClientSession> getClientSessions(DatePoint datePoint, User currentUser, boolean isShowRemoved, boolean showPayedOn);

    void saveMoreLessModels(List<MoreLessUnlimModel> moreLessUnlimModels, Long userId);

    List<ClientSession> stopClientSession(DatePoint datePoint, ClientSession clientSession, boolean toShowRemoved, boolean toShowPayed);

    List<ClientSession> payClientSession(DatePoint datePoint, ClientSession clientSession, boolean toShowRemoved, boolean toShowPayed);

    List<SessionPseudoName> getAllPseudoNames(Long userId);

    void addName(SessionPseudoName namesTextBoxValue);

    List<SessionPseudoName> removeName(String sessionPseudoName, Long userId);

    User getCurrentUser(String userName, String userPassword);

    User saveUser(User user);

    User login(String userName, String passwordTextBoxValue);

    List<ClientSession> startClientSession(DatePoint datePoint, ClientSession clientSession, boolean toShowRemoved, boolean toShowPayed);

    List<ClientSession> unlimClientSession(DatePoint currentDatePointValue, ClientSession clientSession, boolean toShowRemoved, boolean toShowPayed);
}
