package com.client;

import com.client.events.AddSessionEvent;
import com.client.events.ChangeDatePointEvent;
import com.client.events.ToggleShowPayedEvent;
import com.client.events.ToggleShowRemovedEvent;
import com.client.events.UpdateNameEvent;
import com.client.events.UpdateNameEventHandler;
import com.client.events.UpdateNameOnSettingsEvent;
import com.client.events.UserLoggedInEvent;
import com.client.events.UserLoggedInHandler;
import com.client.gin.Injector;
import com.client.panels.ProfilePanel;
import com.client.panels.ReportsPanel;
import com.client.panels.SettingsPanel;
import com.client.service.ClientSessionService;
import com.client.service.ClientSessionServiceAsync;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.ScriptInjector;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DecoratedPopupPanel;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.visualization.client.VisualizationUtils;
import com.google.gwt.visualization.client.visualizations.PieChart;
import com.google.inject.Inject;
import com.shared.model.ClientSession;
import com.shared.model.DatePoint;
import com.shared.model.SessionPseudoName;
import com.shared.utils.UserUtils;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Dimon on 19.07.2016.
 */
public class MainTabPanel extends TabLayoutPanel {
//  @Inject
//  private SimpleEventBus eventBus;
  ToggleButton showRemovedButton;
  ToggleButton showPayedButton;
  SuggestBox suggestBox;
  MultiWordSuggestOracle oracle;
  //  Label sumLabel;
  ListBox datePointListBox;
  public List<SessionPseudoName> existingNames;
  private final ClientSessionServiceAsync clientSessionService = GWT.create(ClientSessionService.class);
  /**
   * Creates an empty tab panel.
   *
   */
  @Inject
  public MainTabPanel(final EventBus eventBus) {
    super(2.5, Style.Unit.EM);

    Injector.INSTANCE.getEventBus().addHandler(UpdateNameEvent.TYPE, new UpdateNameEventHandler() {
      @Override
      public void updateSum(UpdateNameEvent updateNameEvent) {
        loadNames();
      }
    });

    datePointListBox = new ListBox();
    for (DatePoint datePoint : DatePoint.values()) {
      datePointListBox.addItem(datePoint.getText());
    }
    datePointListBox.setSelectedIndex(0);
    datePointListBox.addChangeHandler(new ChangeHandler() {
      @Override
      public void onChange(ChangeEvent event) {
        ChangeDatePointEvent changeDatePointEvent = new ChangeDatePointEvent();
        changeDatePointEvent.setDatePoint(DatePoint.indexOf(datePointListBox.getSelectedIndex()));
        eventBus.fireEvent(changeDatePointEvent);
      }
    });
    showRemovedButton = new ToggleButton("Показывать удаленные");
    showPayedButton = new ToggleButton("Показывать оплаченные");
    // Create a tab panel
//    TabLayoutPanel tabPanel = new TabLayoutPanel(2.5, Style.Unit.EM);
    setAnimationDuration(301);
//    getElement().getStyle().setMarginBottom(10.0, Style.Unit.PX);
//    getElement().getStyle().setMarginLeft(300.0, Style.Unit.PX);
    setHeight("100%");
    setWidth("100%");
//    eventBus.addHandler(UpdateSumEvent.TYPE, new UpdateSumEventHandler() {
//      @Override
//      public void updateSum(UpdateSumEvent updateSumEvent) {
//       sumLabel.setText(getPrettyMoney(updateSumEvent.getSum()));
//      }
//    });
//    setHeight("100%");
//    setWidth("100%");
    // Add a home tab
    String[] tabTitles = {"Сессии", "Настройки", "Отчеты", "Профиль"};
//    ClientSessionGridPanel clientSessionGridPanel = new ClientSessionGridPanel(simpleEventBus);
    SplitLayoutPanel splitLayoutPanel = new SplitLayoutPanel();
    splitLayoutPanel.setSize("100%", "100%");

    showRemovedButton.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
      @Override
      public void onValueChange(ValueChangeEvent<Boolean> event) {
        UserUtils.getSettings().setIsToShowRemoved(event.getValue());
        ToggleShowRemovedEvent toggleShowRemovedEvent = new ToggleShowRemovedEvent();
        toggleShowRemovedEvent.setIsShowRemovedOn(event.getValue());
        toggleShowRemovedEvent.setIsShowPayedCurrentState(showPayedButton.getValue());
        eventBus.fireEvent(toggleShowRemovedEvent);
      }
    });
    showRemovedButton.setWidth("100px");
//    showRemovedButton.setHeight("40px");
    showRemovedButton.setDown(UserUtils.getSettings().isToShowRemoved());
    VerticalPanel eastButtonsPanel = new VerticalPanel();
    eastButtonsPanel.setWidth("100%");
    eastButtonsPanel.setHeight("100%");
    eastButtonsPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
    eastButtonsPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
    eastButtonsPanel.setSpacing(5);

    oracle = new MultiWordSuggestOracle();
//    oracle.setComparator(new Comparator<String>() {
//      @Override
//      public int compare(String o1, String o2) {
//        return containsIgnoreCase(o2, o1) ? 1 : -1;
//      }
//    });
    suggestBox = new SuggestBox(oracle);

    loadNames();

    Button addButton = new Button("Добавить сессию");
    addButton.setHeight("70px");
    addButton.setWidth("160px");
    addButton.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        final String value = suggestBox.getValue();
        if (value != null && isNameFree(value)) {
          final AddSessionEvent addSessionEvent = new AddSessionEvent();
          addSessionEvent.setClientPseudoName(value);
          Injector.INSTANCE.getEventBus().fireEvent(addSessionEvent);
        } else if (value != null && !value.isEmpty()) {
          SessionPseudoName sessionPseudoName = new SessionPseudoName();
          sessionPseudoName.setUserEntity(UserUtils.currentUser.getUserId());
          sessionPseudoName.setName(value);
          sessionPseudoName.setIsUsed(true);
          clientSessionService.addName(sessionPseudoName, new AsyncCallback<Void>() {
            @Override
            public void onFailure(Throwable caught) {

            }

            @Override
            public void onSuccess(Void result) {
              final AddSessionEvent addSessionEvent = new AddSessionEvent();
              addSessionEvent.setClientPseudoName(value);
              Injector.INSTANCE.getEventBus().fireEvent(addSessionEvent);
              DecoratedPopupPanel decoratedPopupPanel = new DecoratedPopupPanel();
              decoratedPopupPanel.center();
              decoratedPopupPanel.setAutoHideEnabled(true);
              decoratedPopupPanel.setWidget(new HTML("Имя добавлено"));
              decoratedPopupPanel.show();
            }
          });
        } else {
          DialogBox dialogBox = Injector.INSTANCE.getNameSelectionWidnow();
          dialogBox.show();

        }
        loadNames();
        suggestBox.setValue(null);
      }
    });

    HorizontalPanel namePanel = new HorizontalPanel();
    namePanel.add(new Label("Имя"));
    namePanel.add(suggestBox);
    eastButtonsPanel.add(namePanel);
    eastButtonsPanel.add(addButton);

    eastButtonsPanel.getElement().getStyle().setMargin(3, Style.Unit.PX);
    HTML html = new HTML("<div></div>");
    html.setHeight("10px");
    eastButtonsPanel.add(html);
    eastButtonsPanel.add(showRemovedButton);
    showPayedButton.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
      @Override
      public void onValueChange(ValueChangeEvent<Boolean> event) {
        UserUtils.getSettings().setIsToShowPayed(event.getValue());
        ToggleShowPayedEvent toggleShowPayedEvent = new ToggleShowPayedEvent();
        toggleShowPayedEvent.setIsShowPayedOn(event.getValue());
        toggleShowPayedEvent.setIsShowRemovedCurrentState(showRemovedButton.getValue());
        eventBus.fireEvent(toggleShowPayedEvent);
      }
    });
    showPayedButton.setWidth("100px");
//    showPayedButton.setHeight("40px");
    showPayedButton.setDown(UserUtils.getSettings().isToShowPayed());
    eastButtonsPanel.add(html);
    eastButtonsPanel.add(showPayedButton);

//    Label sumLabelLabel = new Label("Сумма:");
//    sumLabelLabel.getElement().getStyle().setLeft(20, Style.Unit.PX);
//    sumLabelLabel.getElement().getStyle().setTop(20, Style.Unit.PX);
//    eastButtonsPanel.add(sumLabelLabel);
//    sumLabel = new Label();
//    sumLabel.getElement().getStyle().setFontSize(20, Style.Unit.PX);
//    sumLabel.getElement().getStyle().setLeft(20, Style.Unit.PX);
//    sumLabel.getElement().getStyle().setTop(20, Style.Unit.PX);
//    eastButtonsPanel.add(html);
//    eastButtonsPanel.add(sumLabel);
    eastButtonsPanel.add(datePointListBox);


    final TextBox userNameBox = new TextBox();
    final Button addUserButton = new Button("Добавить пользователя");
    addUserButton.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        clientSessionService.addUser(userNameBox.getValue(), new AsyncCallback<Void>() {
          @Override
          public void onFailure(Throwable caught) {

          }

          @Override
          public void onSuccess(Void result) {
            Window.alert("User is created");
          }
        });
      }
    });
    final Button removeUserButton = new Button("Удалить пользователя");
    removeUserButton.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        clientSessionService.removeUser(userNameBox.getValue(), new AsyncCallback<Void>() {
          @Override
          public void onFailure(Throwable caught) {

          }

          @Override
          public void onSuccess(Void result) {
            Window.alert("User is removed");
          }
        });
      }
    });

    eastButtonsPanel.add(userNameBox);
    eastButtonsPanel.add(addUserButton);
    eastButtonsPanel.add(removeUserButton);
    Button logoutButton = new Button("Выход");
    logoutButton.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
       clientSessionService.logout(UserUtils.currentUser.getUserName(), new AsyncCallback<Void>() {
         @Override
         public void onFailure(Throwable caught) {

         }

         @Override
         public void onSuccess(Void result) {
//           Runnable runnable = new Runnable() {
//             @Override
//             public void run() {
           Window.Location.reload();
//               RootLayoutPanel.get().clear();
//               RootLayoutPanel.get().add(Injector.INSTANCE.getLoginPanel());
             }
//           };
//           VisualizationUtils.loadVisualizationApi(runnable, PieChart.PACKAGE);
//         }
       });
      }
    });
    eastButtonsPanel.add(logoutButton);
//    eastButtonsPanel.add(new HTML("<div class=\"pluso\" data-background=\"transparent\" data-options=\"small,round,line,horizontal,nocounter,theme=04\" data-services=\"vkontakte,odnoklassniki,facebook,twitter,google,moimir,email,print\"></div>"));
    userNameBox.setVisible(false);
    addUserButton.setVisible(false);
    removeUserButton.setVisible(false);

    eventBus.addHandler(UserLoggedInEvent.TYPE, new UserLoggedInHandler() {
      @Override
      public void userIsLoggedIn(UserLoggedInEvent userLoggedInEvent) {
        if (userLoggedInEvent.getUserName().equals("dik81")) {
          userNameBox.setVisible(true);
          addUserButton.setVisible(true);
          removeUserButton.setVisible(true);
        }
      }
    });
    splitLayoutPanel.addEast(eastButtonsPanel, 250);
//    eastButtonsPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
    eastButtonsPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
//    eastButtonsPanel.getElement().getStyle().setMargin(5, Style.Unit.PX);
//    eastButtonsPanel.getElement().getStyle().setPadding(5, Style.Unit.PX);
    add(splitLayoutPanel, tabTitles[0]);
    HorizontalPanel southPanel = new HorizontalPanel();
    southPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
    southPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
//    southPanel.getElement().getStyle().setMargin(10, Style.Unit.PX);
//    splitLayoutPanel.addSouth(southPanel, 60);
    splitLayoutPanel.add(Injector.INSTANCE.getClientSessionGridPanel());

    // Add a tab with an image
//    SimplePanel imageContainer = new SimplePanel();
//    imageContainer.setWidget(new Button("dfdfdf"));
    add(Injector.INSTANCE.getSettingsPanel(), tabTitles[1]);

    // Add a tab
//    HTML moreInfo = new HTML("some html");
    add(new ReportsPanel(), tabTitles[2]);

    add(new ProfilePanel(), tabTitles[3] + " - " + UserUtils.currentUser.getUserName());
    // Return the content
    selectTab(0);
//    ensureDebugId("cwTabPanel");

  }

  private boolean isNameExists(String value) {
    for (SessionPseudoName sessionPseudoName : existingNames) {
      if (value.equalsIgnoreCase(sessionPseudoName.getName())) {
        return true;
      }
    }
    return false;
  }

  private static boolean containsIgnoreCase(String src, String what) {
    final int length = what.length();
    if (length == 0)
      return true; // Empty string is contained

    final char firstLo = Character.toLowerCase(what.charAt(0));
    final char firstUp = Character.toUpperCase(what.charAt(0));

    for (int i = src.length() - length; i >= 0; i--) {
      // Quick check before calling the more expensive regionMatches() method:
      final char ch = src.charAt(i);
      if (ch != firstLo && ch != firstUp)
        continue;

      if (src.regionMatches(true, i, what, 0, length))
        return true;
    }

    return false;
  }

  private boolean isNameFree(String value) {
    for (SessionPseudoName sessionPseudoName : existingNames) {
      if (value.equalsIgnoreCase(sessionPseudoName.getName()) && !sessionPseudoName.isUsed()) {
        return true;
      }
    }
    return false;
  }

  private void loadNames() {
        clientSessionService.getAllPseudoNames(UserUtils.currentUser.getUserId(), new AsyncCallback<List<SessionPseudoName>>() {
          @Override
          public void onFailure(Throwable caught) {

          }

          @Override
          public void onSuccess(List<SessionPseudoName> result) {
            oracle.clear();
            for (SessionPseudoName sessionPseudoName : result) {
              if (!sessionPseudoName.isUsed()) {
                oracle.add(sessionPseudoName.getName());
              }
            }
            existingNames = result;
          }
        });
  }

  private DialogBox createDialogBox() {
    // Create a dialog box and set the caption text
    final DialogBox dialogBox = new DialogBox();

//        dialogBox.setWidth("400px");
//        dialogBox.setHeight("400px");
    dialogBox.ensureDebugId("cwDialogBox");
//        dialogBox.setText("dfd");

    // Create a table to layout the content
    VerticalPanel dialogContents = new VerticalPanel();
    dialogContents.setSpacing(5);
    dialogContents.setSize("300px", "300px");
    dialogContents.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
    dialogContents.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
    dialogBox.setWidget(dialogContents);

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
//        namesListBox.addItem("GREEN");
//        namesListBox.addItem("YELLOW");
//        namesListBox.addItem("BLACK");
    dialogContents.add(namesListBox);
    Button createButton = new Button("Создать");
    createButton.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent clickEvent) {
        final AddSessionEvent event = new AddSessionEvent();
        event.setClientPseudoName(namesListBox.getSelectedValue());
        Injector.INSTANCE.getEventBus().fireEvent(event);
        //To change body of implemented methods use File | Settings | File Templates.
//        dialogBox.hide();
      }
    });
    Button cancelButton = new Button("Отмена");
    cancelButton.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        dialogBox.hide();
      }
    });
    HorizontalPanel buttonContainer = new HorizontalPanel();
    buttonContainer.add(createButton);
    buttonContainer.add(cancelButton);
    dialogContents.add(buttonContainer);
//          Button addEntityButton = new Button("Создать client");
//          addEntityButton.addClickHandler(new ClickHandler() {
//            @Override
//            public void onClick(ClickEvent clickEvent) {
//              final ClientSession clientSession = new ClientSession(System.currentTimeMillis(),
//                      0, false, UserUtils.INSTANCE.getCurrentUser());
//              clientSessionService.saveClientSession(clientSession, new AsyncCallback<Long>() {
//                @Override
//                public void onFailure(Throwable throwable) {
//                  //To change body of implemented methods use File | Settings | File Templates.
//                }
//
//                @Override
//                public void onSuccess(Long id) {
//                  clientSession.setId(id);//To change body of implemented methods use File | Settings | File Templates.
//                }
//              });
//
//            }
//          });
//        dialogContents.add(addEntityButton);
//        if (LocaleInfo.getCurrentLocale().isRTL()) {
//            dialogContents.setCellHorizontalAlignment(
//                    closeButton, HasHorizontalAlignment.ALIGN_LEFT);
//
//        } else {
//            dialogContents.setCellHorizontalAlignment(
//                    closeButton, HasHorizontalAlignment.ALIGN_RIGHT);
//        }

    // Return the dialog box
    return dialogBox;
  }

//  public Label getSumLabel() {
//    return sumLabel;
//  }
//
//  public void setSumLabel(Label sumLabel) {
//    this.sumLabel = sumLabel;
//  }

  private String getPrettyMoney(long minPayment) {
    return new BigDecimal(minPayment).divide(new BigDecimal("100")).setScale(2, BigDecimal.ROUND_HALF_UP).toString();
  }

}
