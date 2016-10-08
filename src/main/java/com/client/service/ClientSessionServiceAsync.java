package com.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.shared.model.ClientSession;
import com.shared.model.DatePoint;
import com.shared.model.MoreLessUnlimModel;
import com.shared.model.SessionPseudoName;
import com.shared.model.SettingsHolder;
import com.shared.model.User;

import java.util.Collection;
import java.util.List;

public interface ClientSessionServiceAsync {
    void getFreePseudoNames(Long userId, AsyncCallback<List<SessionPseudoName>> async);

    void markNameAsFree(String name, Long userId, AsyncCallback<Void> async);

    void markNameAsUsed(String name, Long userId, AsyncCallback<SessionPseudoName> async);

    void addNames(List<SessionPseudoName> pseudoNamesList, AsyncCallback<Void> asyncCallback);

    void removeUser(String value, AsyncCallback<Void> asyncCallback);

    void saveClientSession(DatePoint datePoint, ClientSession clientSession, boolean isShowRemoved, boolean showPayedOn, AsyncCallback<List<ClientSession>> asyncCallback);

    void addUser(String userName, AsyncCallback<Void> asyncCallback);

    void removeClientSession(DatePoint datePoint, ClientSession clientSession, boolean isShowRemoved, boolean showPayedOn, AsyncCallback<List<ClientSession>> asyncCallback);

    void getClientSessions(DatePoint datePoint, User currentUser, boolean isShowRemoved, boolean showPayedOn, AsyncCallback<List<ClientSession>> asyncCallback);

    void stopClientSession(DatePoint datePoint, ClientSession clientSession, boolean toShowRemoved, boolean toShowPayed, AsyncCallback<List<ClientSession>> asyncCallback);

    void payClientSession(DatePoint datePoint, ClientSession clientSession, boolean toShowRemoved, boolean toShowPayed, AsyncCallback<List<ClientSession>> asyncCallback);

    void getAllPseudoNames(Long userId, AsyncCallback<List<SessionPseudoName>> asyncCallback);

    void addName(SessionPseudoName namesTextBoxValue, AsyncCallback<Void> asyncCallback);

    void removeName(String sessionPseudoName, Long userId, AsyncCallback<List<SessionPseudoName>> asyncCallback);

    void getCurrentUser(String userName, String userPassword, AsyncCallback<User> asyncCallback);

    void saveUser(User user, AsyncCallback<User> asyncCallback);

    void saveMoreLessModels(List<MoreLessUnlimModel> moreLessUnlimModels, Long userId, AsyncCallback<Void> asyncCallback);

    void login(String userName, String passwordTextBoxValue, AsyncCallback<User> asyncCallback);

    void startClientSession(DatePoint datePoint, ClientSession clientSession, boolean toShowRemoved, boolean toShowPayed, AsyncCallback<List<ClientSession>> asyncCallback);

    void unlimClientSession(DatePoint currentDatePointValue, ClientSession clientSession, boolean toShowRemoved, boolean toShowPayed, AsyncCallback<List<ClientSession>> asyncCallback);
}
