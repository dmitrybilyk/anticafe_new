package com.server.security;

import com.server.dao.UserDao;
import com.server.dao.UserDaoImpl;
import com.shared.model.User;
import org.apache.shiro.authc.AbstractAuthenticator;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAccount;
import org.apache.shiro.subject.SimplePrincipalCollection;

/**
 * Authenticator implementation for Scorecard, delegates authentication to <code>AuthenticationManager</code>
 *
 * Created by IntelliJ IDEA.
 * User: jelen
 * Date: 21.8.2009
 * Time: 12:03:25
 */
public class AntiAuthenticator extends AbstractAuthenticator {

//    private AuthenticationManager authenticationManager;

    public AntiAuthenticator() {
        super();
    }

    @Override
    protected AuthenticationInfo doAuthenticate(AuthenticationToken token)
            throws AuthenticationException {
//        try {
          UserDao userDao = new UserDaoImpl();

            User user = userDao.login(token.getPrincipal().toString(), (char[])token.getCredentials());

            if( user != null ){
              SimpleAccount simpleAccount =  new SimpleAccount(new SimplePrincipalCollection(token.getPrincipal(), this.getClass().toString()), token.getCredentials());
//              simpleAccount.setCredentialsExpired( user.isCredentialsExpired() );
//              simpleAccount.setLocked( user.isAccountLocked() );
              return simpleAccount;
            }
            return null;
          }
//  catch (LoginFailedException e) {
//            throw new AuthenticationException(e);
//          }
//    }
}
