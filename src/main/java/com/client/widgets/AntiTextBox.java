package com.client.widgets;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.ui.TextBox;

import java.util.Arrays;
import java.util.List;

/**
 * Created by dmitry on 22.08.16.
 */
public class AntiTextBox extends TextBox {
  List<Integer> permittedChars = Arrays.asList(0, 9, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 190,  KeyCodes.KEY_BACKSPACE,
          KeyCodes.KEY_DELETE, KeyCodes.KEY_LEFT, KeyCodes.KEY_RIGHT, KeyCodes.KEY_NUM_PERIOD);
  public AntiTextBox() {
    addKeyDownHandler(new KeyDownHandler() {
      @Override
      public void onKeyDown(KeyDownEvent event) {
        int enteredChar = event.getNativeKeyCode();
        if (!permittedChars.contains(enteredChar)) {
          // show some error
          event.preventDefault();
        }
      }
    });
  }
}
