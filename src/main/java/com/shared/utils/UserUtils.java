package com.shared.utils;

import com.shared.model.DatePoint;
import com.shared.model.HourCostModel;
import com.shared.model.MoreLessUnlimModel;
import com.shared.model.SettingsHolder;
import com.shared.model.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Created by dmitry on 14.08.16.
 */
public class UserUtils {
//  static
//  {
//    INSTANCE = new UserUtils();
//  }
  public static UserUtils INSTANCE;
  public static User currentUser;
  private static SettingsHolder settings;
  private static Map<Long, HourCostModel> hourCostModelMap;
  private static Map<Long, MoreLessUnlimModel> moreLessUnlimModelMap;

  public static User getCurrentUser() {
    return currentUser;
  }

  public static void setCurrentUser(User currentUser) {
    currentUser = currentUser;
  }

  public static SettingsHolder getSettings() {
    return UserUtils.settings;
  }

  public static void setSettings(SettingsHolder settings) {
    UserUtils.settings = settings;
  }

  public static void init() {
    INSTANCE = new UserUtils();
    User testUser = new User();
    testUser.setUserId(0l);
    testUser.setUserName("a");
    testUser.setPassword("");
//    usersMap.put(testUser.getUserEntity(), testUser);

    UserUtils.settings = new SettingsHolder();
//    settings.setFirstPartLength(20000l);
//    settings.setFirstPartSumAmount(3500l);
//    settings.setSettingsId(0l);
//    settings.setUserEntity(testUser.getUserEntity());
//    settings.setUserEntity(testUser);
////    settingsHolderMap.put(testSettingsHolder.getSettingsId(), testSettingsHolder);
//    SettingsHolder settingsHolder = new SettingsHolder();
//    settingsHolder.setSettingsId(1l);
////    settingsHolder.
//    UserUtils.hourCostModelMap = new HashMap<>();
//    moreLessUnlimModelMap = new HashMap<>();
//    HourCostModel hourCostModel = new HourCostModel();
//    hourCostModel.setHourOrder(1);
//    hourCostModel.setCostPerHour(250);
//    hourCostModel.setCostPerMinute(5);
//    hourCostModel.setSettingsHolderId(1);
//    hourCostModelMap.put(hourCostModel.getHourOrder(), hourCostModel);
//    HourCostModel hourCostModel2 = new HourCostModel();
//    hourCostModel2.setHourOrder(2);
//    hourCostModel2.setCostPerHour(200);
//    hourCostModel2.setCostPerMinute(4);
//    hourCostModel2.setSettingsHolderId(1);
//    hourCostModelMap.put(hourCostModel2.getHourOrder(), hourCostModel2);
//    MoreLessUnlimModel moreLessUnlimModel = new MoreLessUnlimModel(1, 2, 5, 2, 250, 600);
//    moreLessUnlimModelMap.put(moreLessUnlimModel.getOrder(), moreLessUnlimModel);
//    MoreLessUnlimModel moreLessUnlimModel2 = new MoreLessUnlimModel(1, 3, 5, 3, 300, 600);
//    moreLessUnlimModelMap.put(moreLessUnlimModel2.getOrder(), moreLessUnlimModel2);
//    MoreLessUnlimModel moreLessUnlimModel3 = new MoreLessUnlimModel(1, 4, 5, 4, 350, 600);
//    moreLessUnlimModelMap.put(moreLessUnlimModel3.getOrder(), moreLessUnlimModel3);
//    MoreLessUnlimModel moreLessUnlimModel4 = new MoreLessUnlimModel(1, 5, 5, 5, 450, 600);
//    moreLessUnlimModelMap.put(moreLessUnlimModel4.getOrder(), moreLessUnlimModel4);
  }

  public void setHourCostModelMap(Map<Long, HourCostModel> hourCostModelMap) {
    UserUtils.hourCostModelMap = hourCostModelMap;
  }

  public Map<Long, HourCostModel> getHourCostModelMap() {
    return hourCostModelMap;
  }

  public static List<HourCostModel> getOrderedHourCostModels() {
    List<HourCostModel> hourCostModels = new ArrayList<>();
    for (Long key : hourCostModelMap.keySet()) {
      hourCostModels.add(hourCostModelMap.get(key));
    }
    Collections.sort(hourCostModels, new Comparator<HourCostModel>() {
      @Override
      public int compare(HourCostModel o1, HourCostModel o2) {
        return o1.getHourOrder() > o2.getHourOrder() ? 1 : -1;
      }
    });
    return hourCostModels;
  }


    public static List<MoreLessUnlimModel> getOrderedMoreLessUnlimModels() {
    List<MoreLessUnlimModel> moreLessUnlimModels = new ArrayList<>();
    for (Long key : moreLessUnlimModelMap.keySet()) {
      moreLessUnlimModels.add(moreLessUnlimModelMap.get(key));
    }
    Collections.sort(moreLessUnlimModels, new Comparator<MoreLessUnlimModel>() {
      @Override
      public int compare(MoreLessUnlimModel o1, MoreLessUnlimModel o2) {
        return o1.getModelOrder() > o2.getModelOrder() ? 1 : -1;
      }
    });
    return moreLessUnlimModels;
  }

  public Map<Long, MoreLessUnlimModel> getMoreLessUnlimModelMap() {
    return moreLessUnlimModelMap;
  }

  public void setMoreLessUnlimModelMap(Map<Long, MoreLessUnlimModel> moreLessUnlimModelMap) {
    UserUtils.moreLessUnlimModelMap = moreLessUnlimModelMap;
  }

}
