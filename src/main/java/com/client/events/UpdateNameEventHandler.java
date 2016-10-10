package com.client.events;

import com.google.gwt.event.shared.EventHandler;

/**
 * Created with IntelliJ IDEA.
 * User: dmitry
 * Date: 8/8/16
 * Time: 5:59 PM
 * To change this template use File | Settings | File Templates.
 */
public interface UpdateNameEventHandler extends EventHandler{
    void updateSum(UpdateNameEvent updateNameEvent);
}
