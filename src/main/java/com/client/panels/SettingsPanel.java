package com.client.panels;

import com.client.events.UpdateNameEvent;
import com.client.events.UserLoggedInEvent;
import com.client.events.UserLoggedInHandler;
import com.client.gin.Injector;
import com.client.service.ClientSessionService;
import com.client.service.ClientSessionServiceAsync;
import com.client.widgets.HourSettingsWidget;
import com.client.widgets.MoreLessUnlimWidget;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import com.shared.model.HourCostModel;
import com.shared.model.MoreLessUnlimModel;
import com.shared.model.SessionPseudoName;
import com.shared.model.SettingsHolder;
import com.shared.model.User;
import com.shared.utils.UserUtils;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
//import com.google.appengine.tools.remoteapi.RemoteApiInstaller;
//import com.google.appengine.tools.remoteapi.RemoteApiOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: dmitry
 * Date: 8/9/16
 * Time: 3:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class SettingsPanel extends SplitLayoutPanel {
//    @Inject
//    private SimpleEventBus eventBus;
    private int selectedName = -1;
    private ClientSessionServiceAsync clientSessionService = GWT.create(ClientSessionService.class);
    HourSettingsWidget hourSettingsWidget;
    MoreLessUnlimWidget moreLessUnlimWidget;
//    private FormPanel formPanel = new FormPanel();
//    VerticalPanel mainPanel = new VerticalPanel();
    @Inject
    public SettingsPanel(final SimpleEventBus eventBus) {
        super();
//        super(Style.Unit.PX);
        VerticalPanel costSettingsVerticalPanel = new VerticalPanel();
        costSettingsVerticalPanel.setSize("300px", "300px");
        FlowPanel radioBoxesPanel = new FlowPanel();
        radioBoxesPanel.setHeight("30px");
        radioBoxesPanel.setWidth("400px");
        final DeckLayoutPanel deckLayoutPanel = new DeckLayoutPanel();
        deckLayoutPanel.setSize("400px", "500px");
        moreLessUnlimWidget = new MoreLessUnlimWidget();
        deckLayoutPanel.add(moreLessUnlimWidget);
        for (SettingsHolder.countStrategy countStrategy : SettingsHolder.countStrategy.values()) {
            if (SettingsHolder.countStrategy.HOUR_MINUTES == countStrategy) {

            } else if (SettingsHolder.countStrategy.MULTI_HOURS == countStrategy) {
                String countStrategyText = countStrategy.getText();
                RadioButton radioButton = new RadioButton("count", countStrategyText);
                radioButton.setValue(true);
                radioButton.setFormValue(countStrategy.name());
                radioButton.ensureDebugId(
                        "cwRadioButton-sport-" + countStrategyText.replaceAll(" ", ""));
                radioButton.addClickHandler(new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {

                    }
                });
                radioBoxesPanel.add(radioButton);
                deckLayoutPanel.showWidget(0);
            }
        }
        costSettingsVerticalPanel.add(radioBoxesPanel);

        costSettingsVerticalPanel.add(deckLayoutPanel);
        setWidth("100%");
        setHeight("500px");
//        add(new CheckBox("Some check"));

        VerticalPanel pseudoNamesSettingsPanel = new VerticalPanel();
        pseudoNamesSettingsPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
        pseudoNamesSettingsPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
        Label namesLabel = new Label("Настройки псевдонимов:");
        pseudoNamesSettingsPanel.add(namesLabel);
        HorizontalPanel addNamePanel = new HorizontalPanel();
        Label newNameLabel = new Label("Новое имя: ");
        final TextBox namesTextBox = new TextBox();
        final Button saveNameButton = new Button("Сохранить");

        addNamePanel.add(newNameLabel);
        addNamePanel.add(namesTextBox);
        addNamePanel.add(saveNameButton);
        saveNameButton.setVisible(false);
        pseudoNamesSettingsPanel.add(addNamePanel);
        HorizontalPanel existingNamesPanel = new HorizontalPanel();
        Label existingNamesLabel = new Label("Существующие имена: ");
        final ListBox namesBox = new ListBox();
        namesBox.setWidth("200px");
        existingNamesPanel.add(existingNamesLabel);
        existingNamesPanel.add(namesBox);
        pseudoNamesSettingsPanel.add(existingNamesPanel);
        clientSessionService.getAllPseudoNames(UserUtils.currentUser.getUserId(), new AsyncCallback<List<SessionPseudoName>>() {
            @Override
            public void onFailure(Throwable throwable) {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public void onSuccess(List<SessionPseudoName> strings) {
                for (SessionPseudoName name : strings) {
                    namesBox.addItem(name.getName());
                }//To change body of implemented methods use File | Settings | File Templates.
            }
        });
        Button addNameButton = new Button("Добавить");
        addNameButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                final String namesTextBoxValue = namesTextBox.getValue();
                if (namesTextBoxValue == null || namesTextBoxValue.isEmpty()) {
                    Window.alert("Нельзя добавить пустое имя");
                    return;
                }
                if (isNameAlreadyPresent()) {
                    Window.alert("Такое имя уже есть в базе");
                    return;
                }
                clientSessionService.addName(new SessionPseudoName(namesTextBoxValue, UserUtils.currentUser.getUserId()),
                        new AsyncCallback<Void>() {
                    @Override
                    public void onFailure(Throwable caught) {

                    }

                    @Override
                    public void onSuccess(Void result) {
                        namesTextBox.setText("");
                        namesBox.addItem(namesTextBoxValue);
                    }
                });
            }

            private boolean isNameAlreadyPresent() {
                for (int i = 0; i < namesBox.getItemCount(); i++) {
                    if (namesTextBox.getValue().equalsIgnoreCase(namesBox.getItemText(i))) {
                        return true;
                    }
                }
                return false;
            }

        });

        Button editNameButton = new Button("Редактировать");
        editNameButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                selectedName = namesBox.getSelectedIndex();
                namesTextBox.setValue(namesBox.getSelectedValue());
                saveNameButton.setVisible(true);
            }
        });

        saveNameButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                String oldName = namesBox.getItemText(selectedName);
                final String namesTextBoxValue = namesTextBox.getValue();
                namesTextBox.setValue(null);
                clientSessionService.updateName(oldName, namesTextBoxValue, UserUtils.currentUser.getUserId(), new AsyncCallback<Void>() {
                    @Override
                    public void onFailure(Throwable caught) {

                    }

                    @Override
                    public void onSuccess(Void result) {
                        namesBox.removeItem(selectedName);
                        if (namesTextBoxValue != null) {
                            namesBox.addItem(namesTextBoxValue);
                        }
                        DecoratedPopupPanel decoratedPopupPanel = new DecoratedPopupPanel();
                        decoratedPopupPanel.center();
                        decoratedPopupPanel.setAutoHideEnabled(true);
                        decoratedPopupPanel.setWidget(new HTML("Имя изменено"));
                        decoratedPopupPanel.show();
                        eventBus.fireEvent(new UpdateNameEvent());
                    }
                });
                saveNameButton.setVisible(false);
            }
        });

        Button removeNameButton = new Button("Удалить");
        removeNameButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                final String selectedName = namesBox.getSelectedValue();
                clientSessionService.getFreePseudoNames(UserUtils.currentUser.getUserId(), new AsyncCallback<List<SessionPseudoName>>() {
                    @Override
                    public void onFailure(Throwable caught) {

                    }

                    @Override
                    public void onSuccess(List<SessionPseudoName> result) {
                        if (!result.contains(new SessionPseudoName(selectedName))) {
                            Window.alert("Это имя используется");
                        } else {
                            clientSessionService.removeName(selectedName, UserUtils.currentUser.getUserId(), new AsyncCallback<List<SessionPseudoName>>() {
                                @Override
                                public void onFailure(Throwable caught) {

                                }

                                @Override
                                public void onSuccess(List<SessionPseudoName> result) {
                                    namesTextBox.setText("");
                                    namesBox.clear();
                                    for (SessionPseudoName sessionPseudoName : result) {
                                        namesBox.addItem(sessionPseudoName.getName());
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });

//        add(namesBox);
        HorizontalPanel buttonsPanel = new HorizontalPanel();
        buttonsPanel.add(addNameButton);
        buttonsPanel.add(editNameButton);
        buttonsPanel.add(removeNameButton);
        pseudoNamesSettingsPanel.add(buttonsPanel);
        pseudoNamesSettingsPanel.setSpacing(10);
//        add(pseudoNamesSettingsPanel);
        addWest(pseudoNamesSettingsPanel, 350);

//        add(new Label("Время:"));

        VerticalPanel costSettingsPanel = new VerticalPanel();
        costSettingsPanel.setSpacing(10);
        Label costSettingsLabel = new Label("Настройки стоимости:");
        costSettingsPanel.add(costSettingsLabel);

        final VerticalPanel hoursCostPanel = new VerticalPanel();
        final Map<Long, HourCostModel> hourCostModelMap = new HashMap<Long, HourCostModel>();
//        HourCostModel hourCostModel = new HourCostModel();
//        hourCostModel.setHourOrder(1);
//        hourCostModel.setCostPerMinute(5l);
//        hourCostModel.setCostPerHour(2501);
//        hourCostModelMap.put(hourCostModel.getHourOrder(), hourCostModel);

        final TextBox firstPartLengthTextBox = new TextBox();
        firstPartLengthTextBox.addKeyPressHandler(new KeyPressHandler() {
            @Override
            public void onKeyPress(KeyPressEvent event) {
                String input = firstPartLengthTextBox.getText();
                if (!input.matches("[0-9]*")) {
                    // show some error
                    return;
                }
                // do your thang
            }
        });
//        add(firstPartLengthTextBox);
//        UserUtils.init();

        final TextBox firstPartSumAmountTextBox = new TextBox();
        firstPartSumAmountTextBox.addKeyPressHandler(new KeyPressHandler() {
            @Override
            public void onKeyPress(KeyPressEvent event) {
                String input = firstPartSumAmountTextBox.getText();
                if (!input.matches("[0-9]*")) {
                    // show some error
                    return;
                }
                // do your thang
            }
        });
//        clientSessionService.getCurrentUser();
//        add(firstPartSumAmountTextBox);

        final TextBox maxSessionLengthTextBox = new TextBox();
        firstPartSumAmountTextBox.addKeyPressHandler(new KeyPressHandler() {
            @Override
            public void onKeyPress(KeyPressEvent event) {
                String input = maxSessionLengthTextBox.getText();
                if (!input.matches("[0-9]*")) {
                    // show some error
                    return;
                }
                // do your thang
            }
        });
//        clientSessionService.getCurrentUser();
//        add(maxSessionLengthTextBox);

        Button saveButton = new Button("Сохранить настройки");
        saveButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
//                UserUtils.INSTANCE.getCurrentUser().getSettings().setFirstPartLength(Long.valueOf(firstPartLengthTextBox.getValue()));
//                UserUtils.INSTANCE.getCurrentUser().getSettings().setFirstPartSumAmount(Long.valueOf(firstPartSumAmountTextBox.getValue()));
//                UserUtils.INSTANCE.getCurrentUser().getSettings().setMaxSessionLength(Long.valueOf(maxSessionLengthTextBox.getValue()));
//                UserUtils.INSTANCE.getCurrentUser().getSettings().setUnlimitedCost(Long.valueOf(hourSettingsWidget.getUnlimCostTextBox().getValue()));
                UserUtils.INSTANCE.setHourCostModelMap(hourCostModelMap);
                if (!moreLessUnlimWidget.validate()) {
                    DecoratedPopupPanel decoratedPopupPanel = new DecoratedPopupPanel();
                    decoratedPopupPanel.center();
                    decoratedPopupPanel.setAutoHideEnabled(true);
                    decoratedPopupPanel.setWidget(new HTML("Не все поля заполнены"));
                    decoratedPopupPanel.show();
                    event.preventDefault();
                    event.stopPropagation();
                    return;
                }
                if(!moreLessUnlimWidget.validateOrder()) {
                    DecoratedPopupPanel decoratedPopupPanel = new DecoratedPopupPanel();
                    decoratedPopupPanel.center();
                    decoratedPopupPanel.setAutoHideEnabled(true);
                    decoratedPopupPanel.setWidget(new HTML("Количество часов указано не верно. Каждый последующий должен быть больше предыдущего"));
                    decoratedPopupPanel.show();
                    event.preventDefault();
                    event.stopPropagation();
                    return;
                }
                Map<Long, MoreLessUnlimModel> moreLessUnlimWidgetSettings = moreLessUnlimWidget.getSettings();
                UserUtils.INSTANCE.setMoreLessUnlimModelMap(moreLessUnlimWidgetSettings);
                UserUtils.currentUser.setMoreLessUnlimModelList(new ArrayList<MoreLessUnlimModel>(moreLessUnlimWidgetSettings.values()));
                clientSessionService.saveMoreLessModels(new ArrayList<MoreLessUnlimModel>(moreLessUnlimWidgetSettings.values()), UserUtils.currentUser.getUserId(), new AsyncCallback<Void>() {
                    @Override
                    public void onFailure(Throwable caught) {

                    }

                    @Override
                    public void onSuccess(Void result) {
                        DecoratedPopupPanel decoratedPopupPanel = new DecoratedPopupPanel();
                        decoratedPopupPanel.center();
                        decoratedPopupPanel.setAutoHideEnabled(true);
                        decoratedPopupPanel.setWidget(new HTML("Настройки системы подсчета обновлены"));
                        decoratedPopupPanel.show();
                    }
                });
            }
        });

//        Button testButton = new Button("Test ds");
//        testButton.addClickHandler(new ClickHandler() {
//            @Override
//            public void onClick(ClickEvent event) {
//                String serverString = args[0];
//                RemoteApiOptions options;
//                if (serverString.equals("localhost")) {
//                    options = new RemoteApiOptions().server(serverString,
//                            8080).useDevelopmentServerCredential();
//                } else {
//                    options = new RemoteApiOptions().server(serverString,
//                            443).useApplicationDefaultCredential();
//                }
//                RemoteApiInstaller installer = new RemoteApiInstaller();
//                installer.install(options);
//                try {
//                    DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
//                    System.out.println("Key of new entity is " + ds.put(new Entity("Hello Remote API!")));
//                } finally {
//                    installer.uninstall();
//                }
//            }
//        });

        HorizontalPanel southPanel = new HorizontalPanel();
        southPanel.add(saveButton);

        addSouth(southPanel, 30);
//        addNorth(deckLayoutPanel, 200);
        add(costSettingsVerticalPanel);
//        add(saveButton);

        Injector.INSTANCE.getEventBus().addHandler(UserLoggedInEvent.TYPE, new UserLoggedInHandler() {
            @Override
            public void userIsLoggedIn(UserLoggedInEvent userLoggedInEvent) {
                User currentUser = UserUtils.currentUser;
                if (UserUtils.getSettings().getCurrentCountStrategy() == SettingsHolder.countStrategy.MULTI_HOURS) {
                    moreLessUnlimWidget.showSettings(currentUser.getMoreLessUnlimModelList());
                }
            }
        });

//        firstPartLengthTextBox.setValue(String.valueOf(UserUtils.INSTANCE.getCurrentUser().getSettings().getFirstPartLength()));
//        firstPartSumAmountTextBox.setValue(String.valueOf(UserUtils.INSTANCE.getCurrentUser().getSettings().getFirstPartSumAmount()));
//        maxSessionLengthTextBox.setValue(String.valueOf(UserUtils.INSTANCE.getCurrentUser().getSettings().getMaxSessionLength()));
    }

}
