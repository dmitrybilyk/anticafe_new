package com.client;

import com.client.events.AddSessionEvent;
import com.client.events.UpdateNameEvent;
import com.client.gin.Injector;
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
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.inject.Inject;
import com.shared.model.ClientSession;
import com.shared.model.DatePoint;
import com.shared.model.SessionPseudoName;
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
        center();
        setModal(true);
        setText("Выбор имени");
        setSize("150px", "100px");
        VerticalPanel dialogContents = new VerticalPanel();
        dialogContents.setSpacing(5);
        dialogContents.setSize("200px", "150px");
        dialogContents.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
        dialogContents.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        this.setWidget(dialogContents);

        final ListBox namesListBox = new ListBox();
        namesListBox.setWidth("200px");
        clientSessionService.getFreePseudoNames(UserUtils.currentUser.getUserId(), new AsyncCallback<List<SessionPseudoName>>() {
            @Override
            public void onFailure(Throwable caught) {

            }

            @Override
            public void onSuccess(List<SessionPseudoName> result) {
                for (SessionPseudoName item : result) {
                    namesListBox.addItem(item.getName());
                }
            }
        });
        dialogContents.add(namesListBox);
        Button createButton = new Button("Создать");
        createButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                NameSelectWindow.this.hide();
                final AddSessionEvent event = new AddSessionEvent();
                event.setClientPseudoName(namesListBox.getSelectedValue());
                Injector.INSTANCE.getEventBus().fireEvent(event);
                UpdateNameEvent updateNameEvent = new UpdateNameEvent();
                Injector.INSTANCE.getEventBus().fireEvent(updateNameEvent);
            }
        });
        Button cancelButton = new Button("Отмена");
        cancelButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                NameSelectWindow.this.hide();
            }
        });
        HorizontalPanel buttonContainer = new HorizontalPanel();

        buttonContainer.add(createButton);
        buttonContainer.add(cancelButton);
        dialogContents.add(buttonContainer);
    }

}
