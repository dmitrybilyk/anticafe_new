package com.server.dao;

import com.shared.model.User;

/**
 * Created by dmitry on 16.10.16.
 */
public interface UserDao {
  public User login(String userName, char[] password);
}
