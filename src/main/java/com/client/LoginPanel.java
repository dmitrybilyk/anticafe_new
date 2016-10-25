package com.client;

import com.client.events.UserLoggedInEvent;
import com.client.gin.Injector;
import com.client.service.ClientSessionService;
import com.client.service.ClientSessionServiceAsync;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DecoratedPopupPanel;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
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

    final TextBox nameTextBox = new TextBox();
    final TextBox passwordTextBox = new TextBox();
    final Button loginButton = new Button("ВХОД");
    private ClientSessionServiceAsync clientSessionService = GWT.create(ClientSessionService.class);

    @Inject
    public LoginPanel() {
        this.setWidth("100%");
        this.setHeight("100%");
        this.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        this.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
        passwordTextBox.setReadOnly(true);
        loginButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (nameTextBox.getValue() != null && !nameTextBox.getValue().isEmpty()) {
                    login(nameTextBox.getValue(), passwordTextBox.getValue());
                }
            }
        });
        this.add(getLoginPanel());
    }

    private Widget getLoginPanel() {

        VerticalPanel loginPanel = new VerticalPanel();
//        loginPanel.setBorderWidth(1);

        loginPanel.setWidth("100%");
        loginPanel.setHeight("100%");
        loginPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        loginPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);

        loginPanel.setSpacing(5);
        loginPanel.setSize("270px", "170px");
        HorizontalPanel namePanel = new HorizontalPanel();
        Label nameLabel = new Label("Имя");
        nameLabel.setWidth("70px");
        namePanel.add(nameLabel);
        namePanel.add(nameTextBox);
        HorizontalPanel passwordPanel = new HorizontalPanel();
        Label passwordLabel = new Label("Пароль");
        passwordLabel.setWidth("70px");
        passwordPanel.add(passwordLabel);
        passwordPanel.add(passwordTextBox);
        loginPanel.add(namePanel);
        loginPanel.add(passwordPanel);
        loginButton.setWidth("100px");
        loginPanel.add(loginButton);

        nameTextBox.addKeyUpHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(KeyUpEvent event) {
                if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
                    login(nameTextBox.getValue(), passwordTextBox.getValue());
                }
            }
        });

//        // Create a table to layout the form options
//        FlexTable layout = new FlexTable();
//        layout.setCellSpacing(6);
//        FlexTable.FlexCellFormatter cellFormatter = layout.getFlexCellFormatter();
//
//        // Add a title to the form
//        layout.setHTML(0, 0, "Вход в систему");
//        cellFormatter.setColSpan(0, 0, 2);
//        cellFormatter.setHorizontalAlignment(
//                0, 0, HasHorizontalAlignment.ALIGN_CENTER);
//
//        // Add some standard form options
//        layout.setHTML(1, 0, "Имя");
//        layout.setWidget(1, 1, nameTextBox);
//        layout.setHTML(2, 0, "Пароль");
//        layout.setWidget(2, 1, passwordTextBox);
//        nameTextBox.addKeyUpHandler(new KeyUpHandler() {
//            @Override
//            public void onKeyUp(KeyUpEvent event) {
//                if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
//                    login(nameTextBox.getValue(), passwordTextBox.getValue());
//                } else {
//                    DecoratedPopupPanel decoratedPopupPanel = new DecoratedPopupPanel();
//                    decoratedPopupPanel.center();
//                    decoratedPopupPanel.setAutoHideEnabled(true);
//                    decoratedPopupPanel.setWidget(new HTML("Введите имя"));
//                    decoratedPopupPanel.show();
//                }
//            }
//        });
//        // Wrap the content in a DecoratorPanel
        DecoratorPanel decPanel = new DecoratorPanel();
        decPanel.getElement().getStyle().setBorderStyle(Style.BorderStyle.SOLID);
        decPanel.getElement().getStyle().setBorderWidth(0.3, Style.Unit.PX);
        decPanel.setWidget(loginPanel);
        return decPanel;
    }

    private void login(String name, String password) {
        clientSessionService.login(name, password,
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
//                        RootLayoutPanel.get().add(Injector.INSTANCE.getMainPanel());
                        Injector.INSTANCE.getEventBus().fireEvent(userLoggedInEvent);
                    }
                });
    }

//    public Widget asWidget() {
//        return super.asWidget();
//    }
}
