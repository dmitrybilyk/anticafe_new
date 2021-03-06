package com.client.panels;

import com.client.events.UpdateNameEvent;
import com.client.events.UpdateNameEventHandler;
import com.client.events.UpdateNameOnSettingsEvent;
import com.client.events.UpdateNameOnSettingsEventHandler;
import com.client.events.UserLoggedInEvent;
import com.client.events.UserLoggedInHandler;
import com.client.gin.Injector;
import com.client.service.ClientSessionService;
import com.client.service.ClientSessionServiceAsync;
import com.client.widgets.HourSettingsWidget;
import com.client.widgets.MoreLessUnlimWidget;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
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
public class SettingsPanel extends DockPanel {
//    @Inject
//    private SimpleEventBus eventBus;
    private int selectedName = -1;
    private ClientSessionServiceAsync clientSessionService = GWT.create(ClientSessionService.class);
    HourSettingsWidget hourSettingsWidget;
    MoreLessUnlimWidget moreLessUnlimWidget;
    MultiWordSuggestOracle oracle;
    private String oldName;
    final ListBox namesBox = new ListBox();
//    private FormPanel formPanel = new FormPanel();
//    VerticalPanel mainPanel = new VerticalPanel();
    @Inject
    public SettingsPanel(final SimpleEventBus eventBus) {
        super();
//        super(Style.Unit.PX);
        VerticalPanel costSettingsVerticalPanel = new VerticalPanel();
        costSettingsVerticalPanel.setSpacing(5);
        costSettingsVerticalPanel.setWidth("100%");
        costSettingsVerticalPanel.setHeight("100%");
        costSettingsVerticalPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        costSettingsVerticalPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
//        costSettingsVerticalPanel.setSize("300px", "300px");
        FlowPanel radioBoxesPanel = new FlowPanel();
//        radioBoxesPanel.setHeight("30px");
//        radioBoxesPanel.setWidth("400px");
        Injector.INSTANCE.getEventBus().addHandler(UpdateNameOnSettingsEvent.TYPE, new UpdateNameOnSettingsEventHandler() {
            @Override
            public void updateSum(UpdateNameOnSettingsEvent updateNameEvent) {
                reloadPseudoNames();
            }
        });
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
        setHeight("80%");
//        add(new CheckBox("Some check"));

        VerticalPanel pseudoNamesSettingsPanel = new VerticalPanel();
        pseudoNamesSettingsPanel.getElement().getStyle().setBorderWidth(1, Style.Unit.PX);
        pseudoNamesSettingsPanel.getElement().getStyle().setBorderStyle(Style.BorderStyle.SOLID);
        pseudoNamesSettingsPanel.setBorderWidth(1);
        pseudoNamesSettingsPanel.setSpacing(5);
        pseudoNamesSettingsPanel.setWidth("70%");
        pseudoNamesSettingsPanel.setHeight("80%");
        pseudoNamesSettingsPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        pseudoNamesSettingsPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
        Label namesLabel = new Label("НАСТРОЙКИ ИМЕН:");
        pseudoNamesSettingsPanel.add(namesLabel);
        HorizontalPanel addNamePanel = new HorizontalPanel();
        addNamePanel.setSpacing(2);
        Label newNameLabel = new Label("Имя: ");
        oracle = new MultiWordSuggestOracle();
        final SuggestBox namesTextBox = new SuggestBox(oracle);

        final Button saveNameButton = new Button("Сохранить");
        final Button cancelButton = new Button("Отмена");

        addNamePanel.add(newNameLabel);
        addNamePanel.add(namesTextBox);
        addNamePanel.add(saveNameButton);
        addNamePanel.add(cancelButton);
        saveNameButton.setEnabled(false);
        cancelButton.setEnabled(false);
        pseudoNamesSettingsPanel.add(addNamePanel);
        HorizontalPanel existingNamesPanel = new HorizontalPanel();
        existingNamesPanel.setSpacing(5);
        Label existingNamesLabel = new Label("Имена: ");
//        namesBox.addChangeHandler(new ChangeHandler() {
//            @Override
//            public void onChange(ChangeEvent event) {
//             oldName = namesBox.getSelectedValue();
//            }
//        });
//        namesTextBox.addValueChangeHandler(new ValueChangeHandler<String>() {
//            @Override
//            public void onValueChange(ValueChangeEvent<String> event) {
//                String displayString = event.getValue();
////                namesBox.setItemSelected(getIndexByName(displayString), true);
//                namesBox.fireEvent(new SelectionEvent<>());
//                namesBox.setSelectedIndex(getIndexByName(displayString));
//            }
//            private int getIndexByName(String displayString) {
//                for (int i = 0; i < namesBox.getItemCount(); i++) {
//                    if (displayString.equals(namesBox.getItemText(i))) {
//                        return i;
//                    }
//                }
//                return 0;
//            }
//        });
//        namesTextBox.addSelectionHandler(new SelectionHandler<SuggestOracle.Suggestion>() {
//            @Override
//            public void onSelection(SelectionEvent<SuggestOracle.Suggestion> event) {
//                String displayString = event.getSelectedItem().getDisplayString();
//                namesBox.setValue(getIndexByName(displayString), displayString);
//            }
//
//            private int getIndexByName(String displayString) {
//                for (int i = 0; i < namesBox.getItemCount(); i++) {
//                    if (displayString.equals(namesBox.getItemText(i))) {
//                        return i;
//                    }
//                }
//                return 0;
//            }
//        });
        namesBox.setWidth("200px");
        existingNamesPanel.add(existingNamesLabel);
        existingNamesPanel.add(namesBox);
        pseudoNamesSettingsPanel.add(existingNamesPanel);
        reloadPseudoNames();
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
                                reloadPseudoNames();
                                UpdateNameEvent updateNameEvent = new UpdateNameEvent();
                                Injector.INSTANCE.getEventBus().fireEvent(updateNameEvent);
                                namesTextBox.setText("");
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
                String selectedValue = namesBox.getSelectedValue();
                oldName = selectedValue;
                namesTextBox.setValue(selectedValue);
                saveNameButton.setEnabled(true);
                cancelButton.setEnabled(true);
            }
        });

        saveNameButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(final ClickEvent event) {
//                String oldName = namesBox.getItemText(selectedName);
                final String namesTextBoxValue = namesTextBox.getValue();
                namesTextBox.setValue(null);
                clientSessionService.updateName(oldName, namesTextBoxValue, UserUtils.currentUser.getUserId(), new AsyncCallback<Void>() {
                    @Override
                    public void onFailure(Throwable caught) {

                    }

                    @Override
                    public void onSuccess(Void result) {
//                        namesBox.removeItem(selectedName);
//                        if (namesTextBoxValue != null) {
//                            namesBox.addItem(namesTextBoxValue);
//                        }
                        DecoratedPopupPanel decoratedPopupPanel = new DecoratedPopupPanel();
                        decoratedPopupPanel.center();
                        decoratedPopupPanel.setAutoHideEnabled(true);
                        decoratedPopupPanel.setWidget(new HTML("Имя изменено"));
                        decoratedPopupPanel.show();
                        UpdateNameEvent event1 = new UpdateNameEvent();
                        event1.setSum(0);
                        Injector.INSTANCE.getEventBus().fireEvent(event1);
                        reloadPseudoNames();
                    }
                });
                saveNameButton.setEnabled(false);
            }
        });
        cancelButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                saveNameButton.setEnabled(false);
                cancelButton.setEnabled(false);
                namesTextBox.setValue(null);
            }
        });
        namesTextBox.addSelectionHandler(new SelectionHandler<SuggestOracle.Suggestion>() {
            @Override
            public void onSelection(SelectionEvent<SuggestOracle.Suggestion> event) {
                oldName = event.getSelectedItem().getReplacementString();
                saveNameButton.setEnabled(true);
                cancelButton.setEnabled(true);
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
        buttonsPanel.setSpacing(5);
        buttonsPanel.add(addNameButton);
        buttonsPanel.add(editNameButton);
        buttonsPanel.add(removeNameButton);
        pseudoNamesSettingsPanel.add(buttonsPanel);
//        pseudoNamesSettingsPanel.setSpacing(10);
//        add(pseudoNamesSettingsPanel);
        add(pseudoNamesSettingsPanel, DockPanel.WEST);

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

        Button saveButton = new Button("Сохранить настройки стоимости");
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
        this.setWidth("100%");
        this.setHeight("10%");
        this.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        this.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
        southPanel.add(saveButton);

        add(southPanel, DockPanel.SOUTH);
//        addNorth(deckLayoutPanel, 200);
        add(costSettingsVerticalPanel, DockPanel.CENTER);
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

    private void reloadPseudoNames() {
        clientSessionService.getAllPseudoNames(UserUtils.currentUser.getUserId(), new AsyncCallback<List<SessionPseudoName>>() {
            @Override
            public void onFailure(Throwable throwable) {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public void onSuccess(List<SessionPseudoName> strings) {
                namesBox.clear();
                oracle.clear();
                for (SessionPseudoName name : strings) {
                    if (name  != null && name.getName() != null) {
                        oracle.add(name.getName());
                    }
                    namesBox.addItem(name.getName());
                }//To change body of implemented methods use File | Settings | File Templates.
            }
        });
    }

}
