package com.client.events;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Created with IntelliJ IDEA.
 * User: dmitry
 * Date: 8/8/16
 * Time: 6:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class UpdateNameEvent extends GwtEvent<UpdateNameEventHandler> {
    public static Type<UpdateNameEventHandler> TYPE = new Type<UpdateNameEventHandler>();

    private long sum;

    public long getSum() {
        return sum;
    }

    public void setSum(long sum) {
        this.sum = sum;
    }

    @Override
    public Type<UpdateNameEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(UpdateNameEventHandler handler) {
        handler.updateSum(this);
    }
}
