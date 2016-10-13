package com.client.widgets;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.ui.TextBox;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

/**
 * Created by dmitry on 22.08.16.
 */
public class AntiMoneyTextBox extends AntiTextBox {
  public AntiMoneyTextBox() {
    setWidth("30px");
  }
  @Override
  public void setValue(String value) {
    super.setValue(new BigDecimal(value).divide(new BigDecimal("100")).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
  }

  @Override
  public String getValue() {
    String superValue = super.getValue();
    long value = 0;
    if (superValue != null && !superValue.isEmpty()) {
      value = new BigDecimal(superValue).multiply(new BigDecimal("100")).longValue();
    }
    return String.valueOf(value);
  }
}
