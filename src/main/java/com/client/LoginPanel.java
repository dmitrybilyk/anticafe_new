package com.client;

import com.client.events.UserLoggedInEvent;
import com.client.gin.Injector;
import com.client.service.ClientSessionService;
import com.client.service.ClientSessionServiceAsync;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.shared.model.User;
import com.shared.utils.UserUtils;

import javax.inject.Inject;

/**
 * Created by Dimon on 19.07.2016.
 */
public class LoginPanel extends VerticalPanel {
//    @Inject
//    private SimpleEventBus eventBus;
    private ClientSessionServiceAsync clientSessionService = GWT.create(ClientSessionService.class);

    @Inject
    public LoginPanel(final EventBus eventBus) {
//        setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
//        setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        getElement().setAttribute("align", "center");
        getElement().setAttribute("top", "100px");
        setSpacing(150);
        getElement().getStyle().setBackgroundColor("dark-grey");
        add(getLoginPanel());
    }

    private Widget getLoginPanel() {
        // Create a table to layout the form options
        FlexTable layout = new FlexTable();
        layout.setCellSpacing(6);
        FlexTable.FlexCellFormatter cellFormatter = layout.getFlexCellFormatter();

        // Add a title to the form
        layout.setHTML(0, 0, "Вход в систему");
        cellFormatter.setColSpan(0, 0, 2);
        cellFormatter.setHorizontalAlignment(
                0, 0, HasHorizontalAlignment.ALIGN_CENTER);

        // Add some standard form options
        layout.setHTML(1, 0, "Имя");
        final TextBox nameTextBox = new TextBox();
        layout.setWidget(1, 1, nameTextBox);
        layout.setHTML(2, 0, "Пароль");
        final TextBox passwordTextBox = new TextBox();
        layout.setWidget(2, 1, passwordTextBox);
        nameTextBox.addKeyUpHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(KeyUpEvent event) {
                if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
                    clientSessionService.login(nameTextBox.getValue(), passwordTextBox.getValue(),
                            new AsyncCallback<User>() {
                                @Override
                                public void onFailure(Throwable caught) {

                                }

                                @Override
                                public void onSuccess(User result) {
                                    UserLoggedInEvent userLoggedInEvent = new UserLoggedInEvent();
                                    userLoggedInEvent.setUserName(result.getUserName());
                                    userLoggedInEvent.setUserPassword(result.getPassword());
                                    UserUtils.init();
                                    UserUtils.currentUser = result;
//                                    UserUtils.INSTANCE.getCurrentUser().setSettings(result.getSettings());
                                    RootLayoutPanel.get().clear();
                                    RootLayoutPanel.get().add(Injector.INSTANCE.getMainTabPanel());
                                    Injector.INSTANCE.getEventBus().fireEvent(userLoggedInEvent);
                                }
                            });
//                            clientSessionService.getCurrentUser(nameTextBox.getValue(), passwordTextBox.getValue(),
//                                    new AsyncCallback<User>() {
//                                        @Override
//                                        public void onFailure(Throwable caught) {
//
//                                        }
//
//                                        @Override
//                                        public void onSuccess(User result) {
//                                            UserLoggedInEvent userLoggedInEvent = new UserLoggedInEvent();
//                                            userLoggedInEvent.setUserName(result.getUserName());
//                                            userLoggedInEvent.setUserPassword(result.getPassword());
//                                            simpleEventBus.fireEvent(userLoggedInEvent);
//                                            UserUtils.init();
//                                            UserUtils.INSTANCE.setCurrentUser(result);
//                                            UserUtils.INSTANCE.getCurrentUser().setSettings(result.getSettings());
//                                            RootPanel.get().clear();
//                                            RootPanel.get().add(new MainTabPanel(2.5, Style.Unit.EM, simpleEventBus));
//                                        }
//                                    });
                    ;
//                    handlerManager.fireEvent();
                }
            }
        });

        // Wrap the content in a DecoratorPanel
        DecoratorPanel decPanel = new DecoratorPanel();
        decPanel.getElement().getStyle().setBorderColor("black");
        decPanel.setWidget(layout);
        return decPanel;
    }

    public Widget asWidget() {
        return super.asWidget();
    }
}
