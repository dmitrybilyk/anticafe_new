package com.client;

import com.client.events.AddSessionEvent;
import com.client.events.AddSessionEventHandler;
import com.client.service.ClientSessionService;
import com.client.service.ClientSessionServiceAsync;
import com.google.gwt.cell.client.*;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.text.shared.AbstractSafeHtmlRenderer;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.TextHeader;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import com.shared.model.ClientSession;
import com.shared.model.SessionPseudoName;

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
    private final ClientSessionServiceAsync clientSessionService = GWT.create(ClientSessionService.class);
    private List<SessionPseudoName> pseudoNamesList = new ArrayList<>();
    final DataGrid<ClientSession> clientSessionDataGrid = new DataGrid<ClientSession>(10, new ProvidesKey<ClientSession>() {
        @Override
        public Object getKey(ClientSession item) {
            return ((ClientSession)item).getStartTime();
        }
    });
  public ClientSessionGridPanel(final SimpleEventBus eventBus) {
      this.simpleEventBus = eventBus;
      pseudoNamesList.addAll(Arrays.asList(new SessionPseudoName("BLACK"), new SessionPseudoName("RED"), new SessionPseudoName("YELLOW"),
              new SessionPseudoName("WHITE"), new SessionPseudoName("GREEN")));
      clientSessionService.addNames(pseudoNamesList, new AsyncCallback<Void>() {
        @Override
        public void onFailure(Throwable throwable) {
          //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public void onSuccess(Void aVoid) {
//          final ClientSession clientSession = new ClientSession(new Date().getTime(), new Date().getTime(), false);
//          clientSessionService.getFreePseudoNames(new AsyncCallback<List<SessionPseudoName>>() {
//            @Override
//            public void onFailure(Throwable caught) {
//
//            }
//
//            @Override
//            public void onSuccess(List<SessionPseudoName> result) {
//              if (!result.isEmpty()) {
//                SessionPseudoName sessionPseudoName = result.get(0);
//                clientSession.setSessionPseudoName(sessionPseudoName);
//                clientSessionService.markNameAsUsed(sessionPseudoName, new AsyncCallback<Void>() {
//                  @Override
//                  public void onFailure(Throwable caught) {
//
//                  }
//
//                  @Override
//                  public void onSuccess(Void result) {
//
//                  }
//                });
//              }
//            }
//          });
//
//          clientSession.setId(0);
////    clientSession.setSessionPseudoName(new SessionPseudoName("GREEN"));
//          clientSessionDataGrid.setRowData(0, Arrays.asList(clientSession));//To change body of implemented methods use File | Settings | File Templates.
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
                clientSessionService.markNameAsUsed(sessionPseudoName, new AsyncCallback<Void>() {
                  @Override
                  public void onFailure(Throwable caught) {

                  }

                  @Override
                  public void onSuccess(Void result) {
                    clientSessionDataGrid.setRowData(clientSessionDataGrid.getVisibleItems().size(), Arrays.asList(clientSession));
                  }
                });
              }
            });
             }
      });
    setHeight("100%");
    setWidth("100%");
//    VerticalPanel verticalPanel = new VerticalPanel();
//    verticalPanel.setHeight("700px");
//    verticalPanel.setWidth("100%");

//    add(clientSessionGrid);

    final List<String> sessionPseudoNames = new ArrayList<>();
    for (SessionPseudoName name : pseudoNamesList) {
      sessionPseudoNames.add(name.getName());
    }
    SelectionCell pseudoNameCell = new SelectionCell(sessionPseudoNames) {
        @Override
        public void render(Context context, String value, SafeHtmlBuilder sb) {
//            if (clientSessionDataGrid.getVisibleItem(context.getIndex()).getSessionStatus() != ClientSession.SESSION_STATUS.CREATED) {
                sb.appendHtmlConstant("<div id='disabledCombo' style='pointer-events: none; opacity: 0.4;'>");
                super.render(context, value, sb);
                sb.appendHtmlConstant("</div>");
//            } else if(){
//                super.render(context, value, sb);
//            }
        }
    };
    Column<ClientSession, String> categoryColumn = new Column<ClientSession, String>(pseudoNameCell) {
      @Override
      public String getValue(ClientSession object) {
        return object.getSessionPseudoName().getName();
      }
    };
    clientSessionDataGrid.setColumnWidth(categoryColumn, 200, Style.Unit.PX);
//    clientSessionDataGrid.addColumn(categoryColumn, "Псевдоним");
    categoryColumn.setFieldUpdater(new FieldUpdater<ClientSession, String>() {
      @Override
      public void update(int index, ClientSession object, String value) {
        String releasedName = object.getSessionPseudoName().getName();
        for (String category : sessionPseudoNames) {
          if (category.equals(value)) {
            SessionPseudoName addedName = new SessionPseudoName(category);
            object.setSessionPseudoName(addedName);
          }
        }
        sessionPseudoNames.remove(new SessionPseudoName(value));
        sessionPseudoNames.add(releasedName);
//        ContactDatabase.get().refreshDisplays();
      }
    });
//    dataGrid.setColumnWidth(categoryColumn, 130, Unit.PX);
    clientSessionDataGrid.addColumn(categoryColumn, new TextHeader("Псевдоним"));

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
        dialogBox.show();
      }
    });

      Column<ClientSession, String> startColumn = new Column<ClientSession, String>(new ButtonCellBase<String>(new ButtonCellBase.DefaultAppearance<String>(new AbstractSafeHtmlRenderer<String>() {
          @Override
          public SafeHtml render(final String value) {
              return new SafeHtml() {
                  @Override
                  public String asString() {
                      return value;  //To change body of implemented methods use File | Settings | File Templates.
                  }
              };  //To change body of implemented methods use File | Settings | File Templates.
          }
      }))) {
          @Override
          public String getValue(ClientSession clientSession) {
              if (clientSession.getSessionStatus() == ClientSession.SESSION_STATUS.CREATED) {
                  return "Старт";
              } else if (clientSession.getSessionStatus() == ClientSession.SESSION_STATUS.STARTED){
                  return "Стоп";
              } else if (clientSession.getSessionStatus() == ClientSession.SESSION_STATUS.STOPPED){
                  return "Оплатить";
              } else if (clientSession.getSessionStatus() == ClientSession.SESSION_STATUS.REMOVED){
                return "Удалена";
              } else {
                  return "В архиве";
              }
          }
      };
    startColumn.setFieldUpdater(new FieldUpdater<ClientSession, String>() {
      @Override
      public void update(int i, ClientSession clientSession, String s) {
        if (clientSession.getSessionStatus() == ClientSession.SESSION_STATUS.CREATED) {
          clientSession.setStartTime(System.currentTimeMillis());
          clientSession.setStatus(ClientSession.SESSION_STATUS.STARTED);
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
              DecoratedPopupPanel decoratedPopupPanel = new DecoratedPopupPanel();
              decoratedPopupPanel.center();
              decoratedPopupPanel.setAutoHideEnabled(true);
              decoratedPopupPanel.setWidget(new HTML(result + "Оплачена"));
              decoratedPopupPanel.show();
            }
          });
        } else if (clientSession.getSessionStatus() == ClientSession.SESSION_STATUS.REMOVED) {
          Window.alert("Сессия удалена");
        }

//              clientSessionDataGrid.getVisibleItem(i).setStatus(ClientSession.SESSION_STATUS.STARTED);
        clientSessionDataGrid.redrawRow(i);
//              Window.alert("dfdf");//To change body of implemented methods use File | Settings | File Templates.
      }
    });
      clientSessionDataGrid.addColumn(startColumn, new TextHeader("Управление"));

    Column<ClientSession, String> removeColumn = new Column<ClientSession, String>(new ButtonCellBase<String>(new ButtonCellBase.DefaultAppearance<String>(new AbstractSafeHtmlRenderer<String>() {
      @Override
      public SafeHtml render(final String value) {
        return new SafeHtml() {
          @Override
          public String asString() {
            return value;  //To change body of implemented methods use File | Settings | File Templates.
          }
        };  //To change body of implemented methods use File | Settings | File Templates.
      }
    }))) {
      @Override
      public String getValue(ClientSession clientSession) {
        return "Удалить";
      }
    };
    clientSessionDataGrid.addColumn(removeColumn);

      removeColumn.setFieldUpdater(new FieldUpdater<ClientSession, String>() {
        @Override
        public void update(final int i, final ClientSession clientSession, String s) {
          clientSessionService.removeClientSession(clientSession, new AsyncCallback<Void>() {
            @Override
            public void onFailure(Throwable caught) {

            }

            @Override
            public void onSuccess(Void result) {
              clientSession.setStatus(ClientSession.SESSION_STATUS.REMOVED);
              clientSessionDataGrid.redrawRow(i);
            }
          });
        }
      });

      clientSessionDataGrid.addColumn(new Column<ClientSession, String>(new TextCell()) {
          @Override
          public String getValue(ClientSession clientSession) {
              if (clientSession.getSessionStatus() == ClientSession.SESSION_STATUS.CREATED) {
                  return "00:00:00";
              } else if (clientSession.getSessionStatus() == ClientSession.SESSION_STATUS.REMOVED ){
                return getMinutesString(clientSession.getStopTime() - clientSession.getStartTime());
              } else {
                return getMinutesString(System.currentTimeMillis() - clientSession.getStartTime());
              }
          }
      }, new TextHeader("Время"));

      clientSessionDataGrid.addColumn(new Column<ClientSession, String>(new TextCell()) {
        @Override
        public String getValue(ClientSession clientSession) {
          if (clientSession.getSessionStatus() == ClientSession.SESSION_STATUS.CREATED) {
            return "0.00";
          } else {
            return getPrettyMoney(3500);  //To change body of implemented methods use File | Settings | File Templates.
          }
        }
      }, new TextHeader("Сумма"));


    add(addButton);
//    add(verticalPanel);
//    clientSessionGrid.setRowData(0, Collections.singletonList(new ClientSession(System.currentTimeMillis(),
//            System.currentTimeMillis(), false)));


      Timer t = new Timer() {
          public void run() {
              for (int i = 0; i < clientSessionDataGrid.getVisibleItemCount(); i++) {
                     if (clientSessionDataGrid.getVisibleItem(i).getSessionStatus() == ClientSession.SESSION_STATUS.STARTED) {
                         clientSessionDataGrid.redrawRow(i);
                     }
              }
          }
      };

      // Schedule the timer to run once every second, 1000 ms.
      t.scheduleRepeating(5000);
  }

    private String getPrettyMoney(long minPayment) {
        return new BigDecimal(minPayment).divide(new BigDecimal("100")).setScale(2, BigDecimal.ROUND_HALF_UP).toString();
    }

    private long getMinutes(long timeLeft) {
        return (timeLeft/(1000*60))%60;
    }

    private long getSeconds(long timeLeft) {
        return (timeLeft/(1000));
    }

    private String getMinutesString(long timeLeft) {
        long minutes = (timeLeft / (1000 * 60)) % 60;
        String minutesString = padTimeValue(minutes);
        long hours = (timeLeft / (1000 * 60 * 60)) % 60;
        String hoursString = padTimeValue(hours);
        long seconds = (timeLeft / 1000) % 60;
        String secondsString = padTimeValue(seconds);
        return hoursString + ":"+ minutesString + ":" + secondsString;
    }

    private String padTimeValue(long timeUnit) {
        return timeUnit<10? "0"+timeUnit: String.valueOf(timeUnit);
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
            AddSessionEvent event = new AddSessionEvent();
            event.setClientPseudoName(namesListBox.getSelectedValue());
            clientSessionService.markNameAsUsed(new SessionPseudoName(namesListBox.getSelectedValue()), new AsyncCallback<Void>() {
              @Override
              public void onFailure(Throwable caught) {

              }

              @Override
              public void onSuccess(Void result) {

              }
            });
            simpleEventBus.fireEvent(event);//To change body of implemented methods use File | Settings | File Templates.
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
        Button addEntityButton = new Button("Создать client");
        addEntityButton.addClickHandler(new ClickHandler() {
          @Override
          public void onClick(ClickEvent clickEvent) {
            final ClientSession clientSession = new ClientSession(System.currentTimeMillis(),
                    0, false);
            clientSessionService.saveClientSession(clientSession, new AsyncCallback<Long>() {
              @Override
              public void onFailure(Throwable throwable) {
                //To change body of implemented methods use File | Settings | File Templates.
              }

              @Override
              public void onSuccess(Long id) {
                clientSession.setId(id);//To change body of implemented methods use File | Settings | File Templates.
              }
            });

          }
        });
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
