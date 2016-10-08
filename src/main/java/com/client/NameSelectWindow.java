package com.client;

import com.client.events.AddSessionEvent;
import com.client.service.ClientSessionService;
import com.client.service.ClientSessionServiceAsync;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.inject.Inject;
import com.shared.model.ClientSession;
import com.shared.model.DatePoint;
import com.shared.utils.UserUtils;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: dmitry
 * Date: 8/8/16
 * Time: 5:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class NameSelectWindow extends DialogBox {
//    @Inject
//    private SimpleEventBus eventBus;
    private VerticalPanel verticalPanel;
    private final ClientSessionServiceAsync clientSessionService = GWT.create(ClientSessionService.class);

    @Inject
    public NameSelectWindow(final EventBus eventBus) {
        verticalPanel = new VerticalPanel();
        setHeight("350px");
        setWidth("700px");
//        setSize("400", "400");
//        verticalPanel.setSize("300", "300");
        verticalPanel.setHeight("300px");
        verticalPanel.setWidth("600px");
        final ListBox namesListBox = new ListBox();
        namesListBox.addItem("GREEN");
        namesListBox.addItem("YELLOW");
        namesListBox.addItem("BLACK");
        verticalPanel.add(namesListBox);
        Button button = new Button("Создать");
        button.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                AddSessionEvent event = new AddSessionEvent();
                event.setClientPseudoName(namesListBox.getSelectedValue());
                eventBus.fireEvent(event);//To change body of implemented methods use File | Settings | File Templates.
            }
        });
        verticalPanel.add(button);
        Button addEntityButton = new Button("Создать client");
        button.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                 final ClientSession clientSession = new ClientSession(System.currentTimeMillis(),
                         0, UserUtils.INSTANCE.getCurrentUser().getUserId());
                 clientSessionService.saveClientSession(DatePoint.TODAY, clientSession, UserUtils.getSettings().isToShowRemoved(),
                         UserUtils.getSettings().isToShowRemoved(), new AsyncCallback<List<ClientSession>>() {
                     @Override
                     public void onFailure(Throwable throwable) {
                         //To change body of implemented methods use File | Settings | File Templates.
                     }

                     @Override
                     public void onSuccess(List<ClientSession> result) {
//                         clientSession.setId(id);//To change body of implemented methods use File | Settings | File Templates.
                     }
                 });

            }
        });
        verticalPanel.add(addEntityButton);
    }

}
