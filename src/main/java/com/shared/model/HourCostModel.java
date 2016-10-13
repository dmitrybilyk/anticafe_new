package com.shared.model;

import com.google.gwt.user.client.rpc.IsSerializable;
//import com.googlecode.objectify.annotation.Entity;
//import com.googlecode.objectify.annotation.Id;

import java.io.Serializable;

/**
 * Created by dmitry on 22.08.16.
 */
//@Entity
public class HourCostModel implements Serializable, IsSerializable {
//  @Id
  private long id;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  private long settingsHolderId;
  private long hourOrder;
  private long costPerMinute;
  private long costPerHour;

  public HourCostModel(long settingsHolderId, long hourOrder, long costPerMinute, long costPerHour) {
    this.settingsHolderId = settingsHolderId;
    this.hourOrder = hourOrder;
    this.costPerMinute = costPerMinute;
    this.costPerHour = costPerHour;
  }

  public HourCostModel() {
  }

  public long getCostPerMinute() {
    return costPerMinute;
  }

  public void setCostPerMinute(long costPerMinute) {
    this.costPerMinute = costPerMinute;
  }

  public long getCostPerHour() {
    return costPerHour;
  }

  public void setCostPerHour(long costPerHour) {
    this.costPerHour = costPerHour;
  }

  public long getHourOrder() {
    return hourOrder;
  }

  public void setHourOrder(long hourOrder) {
    this.hourOrder = hourOrder;
  }

  public long getSettingsHolderId() {
    return settingsHolderId;
  }

  public void setSettingsHolderId(long settingsHolderId) {
    this.settingsHolderId = settingsHolderId;
  }
}
