package com.client;

import com.client.bundles.Images;
import com.client.events.AddSessionEvent;
import com.client.events.AddSessionEventHandler;
import com.client.events.UserLoggedInEvent;
import com.client.events.UserLoggedInHandler;
import com.client.service.ClientSessionService;
import com.client.service.ClientSessionServiceAsync;
import com.google.gwt.cell.client.*;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.media.client.Audio;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.text.shared.AbstractSafeHtmlRenderer;
import com.google.gwt.user.cellview.client.AbstractHeaderOrFooterBuilder;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.TextHeader;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.Range;
import com.shared.model.ClientSession;
import com.shared.model.SessionPseudoName;
import com.shared.model.User;
import com.shared.utils.UserUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Created by dmitry on 26.07.16.
 */
public class ClientSessionGridPanel extends VerticalPanel {
  private SimpleEventBus simpleEventBus;
  long firstPartTimeLength = 60000;
  long firstPartSumAmount = 3500;
  ListDataProvider<ClientSession> listDataProvider;
  private final ClientSessionServiceAsync clientSessionService = GWT.create(ClientSessionService.class);
  //    private List<SessionPseudoName> pseudoNamesList = new ArrayList<>();
  final DataGrid<ClientSession> clientSessionDataGrid = new DataGrid<ClientSession>(10, new ProvidesKey<ClientSession>() {
    @Override
    public Object getKey(ClientSession item) {
      return ((ClientSession) item).getStartTime();
    }
  });

  public ClientSessionGridPanel(final SimpleEventBus eventBus) {
    this.simpleEventBus = eventBus;
    simpleEventBus.addHandler(UserLoggedInEvent.TYPE, new UserLoggedInHandler() {
      @Override
      public void userIsLoggedIn(UserLoggedInEvent userLoggedInEvent) {
        clientSessionService.getCurrentUser(userLoggedInEvent.getUserName(), userLoggedInEvent.getUserPassword(),
                new AsyncCallback<User>() {
                  @Override
                  public void onFailure(Throwable caught) {

                  }

                  @Override
                  public void onSuccess(User result) {
                    UserUtils.INSTANCE.setCurrentUser(result);
                    firstPartTimeLength = result.getSettings().getFirstPartLength();
                    firstPartSumAmount = result.getSettings().getFirstPartSumAmount();
                  }
                });
      }
    });
//      pseudoNamesList.addAll(Arrays.asList(new SessionPseudoName("BLACK"), new SessionPseudoName("RED"), new SessionPseudoName("YELLOW"),
//              new SessionPseudoName("WHITE"), new SessionPseudoName("GREEN")));
    clientSessionService.addNames(Arrays.asList(new SessionPseudoName("BLACK"), new SessionPseudoName("RED"), new SessionPseudoName("YELLOW"),
            new SessionPseudoName("WHITE"), new SessionPseudoName("GREEN")), new AsyncCallback<Void>() {
      @Override
      public void onFailure(Throwable throwable) {
        //To change body of implemented methods use File | Settings | File Templates.
      }

      @Override
      public void onSuccess(Void aVoid) {
      }
    });
    simpleEventBus.addHandler(AddSessionEvent.TYPE, new AddSessionEventHandler() {
      @Override
      public void addClientSession(AddSessionEvent addSessionEvent) {
        final ClientSession clientSession = new ClientSession();
        final SessionPseudoName sessionPseudoName = new SessionPseudoName(addSessionEvent.getClientPseudoName());
        clientSession.setSessionPseudoName(sessionPseudoName);
        clientSessionService.saveClientSession(clientSession, new AsyncCallback<Long>() {
          @Override
          public void onFailure(Throwable caught) {

          }

          @Override
          public void onSuccess(Long result) {
            clientSession.setId(result);
            listDataProvider.getList().add(clientSession);
            listDataProvider.refresh();
            clientSessionDataGrid.setVisibleRange(0, listDataProvider.getList().size());
//            for (int i = 0; i < clientSessionDataGrid.getVisibleItemCount(); i ++) {
//              clientSessionDataGrid.redrawRow(i);
//            }
//            clientSessionDataGrid.redraw();
//            listDataProvider.get
//            clientSessionDataGrid.setRowCount(clientSessionDataGrid.getVisibleItems().size() + 1, true);
//            clientSessionDataGrid.setRowData(clientSessionDataGrid.getVisibleItems().size() + 1, Arrays.asList(clientSession));
          }
        });
      }
    });

    setHeight("100%");
    setWidth("100%");

    Column<ClientSession, String> pseudoNameColumn = new Column<ClientSession, String>(new TextCell()) {
      @Override
      public String getValue(ClientSession object) {
        return object.getSessionPseudoName().getName();
      }
    };
    clientSessionDataGrid.setColumnWidth(pseudoNameColumn, 200, Style.Unit.PX);
    clientSessionDataGrid.addColumn(pseudoNameColumn, new TextHeader("Псевдоним"));


    clientSessionDataGrid.setHeight("500px");
    clientSessionDataGrid.setWidth("100%");
    add(clientSessionDataGrid);
    setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
    Button addButton = new Button("Добавить");
    addButton.setHeight("30px");
    addButton.setWidth("200px");
    addButton.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {

        DialogBox dialogBox = createDialogBox();
        dialogBox.center();
        dialogBox.setModal(true);
        dialogBox.setText("Выбор псевдонима");
        dialogBox.setSize("200px", "150px");
        firstPartTimeLength = UserUtils.INSTANCE.getCurrentUser().getSettings().getFirstPartLength();
        firstPartSumAmount = UserUtils.INSTANCE.getCurrentUser().getSettings().getFirstPartSumAmount();
        dialogBox.show();
      }
    });

    ButtonCellBase<String> stringButtonCellBase = new ButtonCellBase<>(new ButtonCellBase.DefaultAppearance<String>(new AbstractSafeHtmlRenderer<String>() {
      @Override
      public SafeHtml render(final String value) {
        return new SafeHtml() {
          @Override
          public String asString() {
            ClientSession.SESSION_STATUS sessionStatus = ClientSession.SESSION_STATUS.valueOf(value);
            if (sessionStatus == ClientSession.SESSION_STATUS.REMOVED) {
              return "<div style='pointer-events: none; opacity: 0.4; color:red;'>" + sessionStatus.getButtonText() + "</div>";
            } else {
              return sessionStatus.getButtonText();  //To change body of implemented methods use File | Settings | File Templates.
            }
          }
        };  //To change body of implemented methods use File | Settings | File Templates.
      }
    }));
    Column<ClientSession, String> startColumn = new Column<ClientSession, String>(stringButtonCellBase) {
      @Override
      public String getValue(ClientSession clientSession) {
        return clientSession.getSessionStatus().name();
      }
    };
    startColumn.setFieldUpdater(new FieldUpdater<ClientSession, String>() {
      @Override
      public void update(final int i, final ClientSession clientSession, String s) {
        if (clientSession.getSessionStatus() == ClientSession.SESSION_STATUS.CREATED) {
          clientSession.setStartTime(System.currentTimeMillis());
          clientSession.setStatus(ClientSession.SESSION_STATUS.STARTED);
          clientSessionDataGrid.redrawRow(i);
        } else if (clientSession.getSessionStatus() == ClientSession.SESSION_STATUS.STARTED) {
          clientSession.setStopTime(System.currentTimeMillis());
          clientSession.setStatus(ClientSession.SESSION_STATUS.STOPPED);
          clientSessionService.stopClientSession(clientSession, new AsyncCallback<Long>() {
            @Override
            public void onFailure(Throwable caught) {

            }

            @Override
            public void onSuccess(Long result) {
              DecoratedPopupPanel decoratedPopupPanel = new DecoratedPopupPanel();
              decoratedPopupPanel.center();
              decoratedPopupPanel.setAutoHideEnabled(true);
              decoratedPopupPanel.setWidget(new HTML(result + "is stopped"));
              decoratedPopupPanel.show();
              clientSessionDataGrid.redrawRow(i);
            }
          });
        } else if (clientSession.getSessionStatus() == ClientSession.SESSION_STATUS.STOPPED) {
          clientSession.setStatus(ClientSession.SESSION_STATUS.PAYED);
          clientSessionService.payClientSession(clientSession, new AsyncCallback<Long>() {
            @Override
            public void onFailure(Throwable caught) {

            }

            @Override
            public void onSuccess(Long result) {
              setNameFree(clientSession);
              DecoratedPopupPanel decoratedPopupPanel = new DecoratedPopupPanel();
              decoratedPopupPanel.center();
              decoratedPopupPanel.setAutoHideEnabled(true);
              decoratedPopupPanel.setWidget(new HTML(result + "Оплачена"));
              decoratedPopupPanel.show();
              clientSessionDataGrid.redrawRow(i);
            }
          });
        } else if (clientSession.getSessionStatus() == ClientSession.SESSION_STATUS.REMOVED) {
        }

      }
    });
    clientSessionDataGrid.addColumn(startColumn, new TextHeader("Управление"));


    Column<ClientSession, String> timeColumn = new Column<ClientSession, String>(new TextCell()) {
      @Override
      public String getValue(ClientSession clientSession) {
        if (clientSession.getSessionStatus() == ClientSession.SESSION_STATUS.CREATED || clientSession.getSessionStatus() == ClientSession.SESSION_STATUS.REMOVED) {
          return "00:00:00";
        } else if (clientSession.getStopTime() != 0) {
          return getMinutesString(clientSession.getStopTime() - clientSession.getStartTime());
        } else {
          return getMinutesString(System.currentTimeMillis() - clientSession.getStartTime());
        }
      }
    };
    clientSessionDataGrid.addColumn(timeColumn, new TextHeader("Время"));
    clientSessionDataGrid.addColumn(new Column<ClientSession, String>(new TextCell()) {
      @Override
      public String getValue(ClientSession clientSession) {
        long timeDifferenceLength = System.currentTimeMillis() - clientSession.getStartTime();
        long totalSumCurrentValue = 0;
        long timeDifferenceLengthInSeconds = getSeconds(timeDifferenceLength);
        if (clientSession.getSessionStatus() == ClientSession.SESSION_STATUS.CREATED) {
          return "0.00";
        } else {
          if (timeDifferenceLengthInSeconds <= getSeconds(firstPartTimeLength)) {
            return getPrettyMoney(firstPartSumAmount);
          } else {
            long totalSum = firstPartSumAmount + 50 * (timeDifferenceLength - firstPartTimeLength) / 1000 / 60;
            return getPrettyMoney(totalSum);
          }
        }
      }
    }, new TextHeader("Сумма"));

    clientSessionDataGrid.addColumn(new Column<ClientSession, String>(new TextCell(new AbstractSafeHtmlRenderer<String>() {
      @Override
      public SafeHtml render(final String value) {
        return new SafeHtml() {
          @Override
          public String asString() {
            ClientSession.SESSION_STATUS sessionStatus = ClientSession.SESSION_STATUS.valueOf(value);
            if (ClientSession.SESSION_STATUS.REMOVED == sessionStatus) {
              return "<div style=color:red;>" + sessionStatus.getValue() + "</div>";
            } else if (ClientSession.SESSION_STATUS.PAYED == sessionStatus) {
              return "<div style=color:green;>" + sessionStatus.getValue() + "</div>";  //To change body of implemented methods use File | Settings | File Templates.
            } else {
              return sessionStatus.getValue();
            }
          }
        };
      }
    })) {
      @Override
      public String getValue(ClientSession clientSession) {
        return clientSession.getSessionStatus().name();
      }
    }, new TextHeader("Статус"));

    clientSessionDataGrid.addColumn(new Column<ClientSession, ImageResource>(new ImageResourceCell()) {
      @Override
      public ImageResource getValue(ClientSession clientSession) {
        if (clientSession.getSessionStatus() == ClientSession.SESSION_STATUS.STARTED) {
          return Images.INSTANCE.progress();
        } else {
          return Images.INSTANCE.stopped();
        }
      }
    });

    ButtonCellBase<String> buttonCellBase = new ButtonCellBase<>(new ButtonCellBase.DefaultAppearance<String>(new AbstractSafeHtmlRenderer<String>() {
      @Override
      public SafeHtml render(final String value) {
        return new SafeHtml() {
          @Override
          public String asString() {
            return value;  //To change body of implemented methods use File | Settings | File Templates.
          }
        };  //To change body of implemented methods use File | Settings | File Templates.
      }
    }));
//    buttonCellBase.setIcon(Images.INSTANCE.remove());
    buttonCellBase.setDecoration(ButtonCellBase.Decoration.NEGATIVE);
    Column<ClientSession, String> removeColumn = new Column<ClientSession, String>(buttonCellBase) {
      @Override
      public void render(Cell.Context context, ClientSession object, SafeHtmlBuilder sb) {
        if (object.getSessionStatus() == ClientSession.SESSION_STATUS.REMOVED) {
          sb.appendHtmlConstant("<div style='pointer-events: none; opacity: 0.4;'>");
          super.render(context, object, sb);
          sb.appendHtmlConstant("</div>");
        } else {
          super.render(context, object, sb);
        }
      }

      @Override
      public String getValue(ClientSession clientSession) {
        return "Удалить";
      }
    };
    removeColumn.setFieldUpdater(new FieldUpdater<ClientSession, String>() {
      @Override
      public void update(final int index, final ClientSession clientSession, String value) {
        clientSessionService.removeClientSession(clientSession, new AsyncCallback<Void>() {
          @Override
          public void onFailure(Throwable caught) {

          }

          @Override
          public void onSuccess(Void result) {
            setNameFree(clientSession);
            clientSession.setStatus(ClientSession.SESSION_STATUS.REMOVED);
//              clientSession.setStopTime(System.currentTimeMillis());
            clientSessionDataGrid.redrawRow(index);
            Audio audio = Audio.createIfSupported();
            audio.setSrc(GWT.getHostPageBaseURL() + "sounds/7.wav");
            audio.play();
          }
        });
      }
    });
    clientSessionDataGrid.addColumn(removeColumn);

    DataGrid<ClientSession> clientSessionDataGridFooter = new DataGrid<>();
    clientSessionDataGridFooter.addColumn(new Column<ClientSession, String>(new AbstractSafeHtmlCell<String>(new AbstractSafeHtmlRenderer<String>() {
      @Override
      public SafeHtml render(final String object) {
        return new SafeHtml() {
          @Override
          public String asString() {
            return object;
          }
        };
      }
    }) {
      @Override
      public void render(Context context, String data, SafeHtmlBuilder sb) {
        super.render(context, data, sb);
      }

      @Override
      protected void render(Context context, SafeHtml data, SafeHtmlBuilder sb) {

      }
    }) {
      @Override
      public String getValue(ClientSession object) {
        return "dfdfd";
      }
    });
//          this.clientSessionDataGrid.setFooterBuilder(new AbstractHeaderOrFooterBuilder<ClientSession>(clientSessionDataGridFooter, true) {
//            @Override
//            protected boolean buildHeaderOrFooterImpl() {
//              return true;
//            }
//          });

    add(addButton);
//    add(verticalPanel);
//    clientSessionGrid.setRowData(0, Collections.singletonList(new ClientSession(System.currentTimeMillis(),
//            System.currentTimeMillis(), false)));


    Timer t = new Timer() {
      public void run() {
        for (int i = 0; i < ClientSessionGridPanel.this.clientSessionDataGrid.getVisibleItemCount(); i++) {
          if (ClientSessionGridPanel.this.clientSessionDataGrid.getVisibleItem(i).getSessionStatus() == ClientSession.SESSION_STATUS.STARTED) {
            ClientSessionGridPanel.this.clientSessionDataGrid.redrawRow(i);
          }
        }
      }
    };

    // Schedule the timer to run once every second, 1000 ms.
    t.scheduleRepeating(5000);
    clientSessionService.getClientSessions(UserUtils.INSTANCE.getCurrentUser(), new AsyncCallback<List<ClientSession>>() {
      @Override
      public void onFailure(Throwable throwable) {
        //To change body of implemented methods use File | Settings | File Templates.
      }

      @Override
      public void onSuccess(List<ClientSession> clientSessions) {
        listDataProvider = new ListDataProvider<ClientSession>(clientSessions);
        listDataProvider.addDataDisplay(clientSessionDataGrid);
//        clientSessionDataGrid.setRowCount(100);
        clientSessionDataGrid.setRowData(clientSessions);
//              clientSessionDataGrid.setVisibleRange(0, 1000);
      }
    });

  }


  private void setNameFree(ClientSession clientSession) {
    clientSessionService.markNameAsFree(clientSession.getSessionPseudoName(), new AsyncCallback<Void>() {
      @Override
      public void onFailure(Throwable caught) {

      }

      @Override
      public void onSuccess(Void result) {

      }
    });
  }

  private String getPrettyMoney(long minPayment) {
    return new BigDecimal(minPayment).divide(new BigDecimal("100")).setScale(2, BigDecimal.ROUND_HALF_UP).toString();
  }

  private long getMinutes(long timeLeft) {
    return (timeLeft / (1000 * 60)) % 60;
  }

  private long getSeconds(long timeLeft) {
    return (timeLeft / (1000));
  }

  private String getMinutesString(long timeLeft) {
    long minutes = (timeLeft / (1000 * 60)) % 60;
    String minutesString = padTimeValue(minutes);
    long hours = (timeLeft / (1000 * 60 * 60)) % 60;
    String hoursString = padTimeValue(hours);
    long seconds = (timeLeft / 1000) % 60;
    String secondsString = padTimeValue(seconds);
    return hoursString + ":" + minutesString + ":" + secondsString;
  }

  private String padTimeValue(long timeUnit) {
    return timeUnit < 10 ? "0" + timeUnit : String.valueOf(timeUnit);
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
    clientSessionService.getFreePseudoNames(new AsyncCallback<List<SessionPseudoName>>() {
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
        clientSessionService.markNameAsUsed(new SessionPseudoName(namesListBox.getSelectedValue()), new AsyncCallback<Void>() {
          @Override
          public void onFailure(Throwable caught) {

          }

          @Override
          public void onSuccess(Void result) {
            simpleEventBus.fireEvent(event);
          }
        });
        //To change body of implemented methods use File | Settings | File Templates.
        dialogBox.hide();
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
}
