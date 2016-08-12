package com.server.dao;

import com.shared.model.ClientSession;
import com.shared.model.SessionPseudoName;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: dmitry
 * Date: 8/8/16
 * Time: 5:22 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ClientSessionDao {

    List<SessionPseudoName> getFreePseudoNames();

    void markNameAsFree(SessionPseudoName name);

    void markNameAsUsed(SessionPseudoName name);

    void addNames(List<SessionPseudoName> pseudoNamesList);

    void saveClientSession(ClientSession clientSession);
}
