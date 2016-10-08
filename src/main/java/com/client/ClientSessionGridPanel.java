package com.client;

import com.client.bundles.Images;
import com.client.events.AddSessionEvent;
import com.client.events.AddSessionEventHandler;
import com.client.events.ChangeDatePointEvent;
import com.client.events.ChangeDatePointEventHandler;
import com.client.events.ToggleShowPayedEvent;
import com.client.events.ToggleShowPayedEventHandler;
import com.client.events.ToggleShowRemovedEvent;
import com.client.events.ToggleShowRemovedEventHandler;
import com.client.events.UpdateSumEvent;
import com.client.service.ClientSessionService;
import com.client.service.ClientSessionServiceAsync;
import com.google.common.collect.Range;
import com.google.gwt.cell.client.*;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.builder.shared.TableCellBuilder;
import com.google.gwt.dom.builder.shared.TableRowBuilder;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.i18n.shared.DateTimeFormat;
import com.google.gwt.media.client.Audio;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.text.shared.AbstractSafeHtmlRenderer;
import com.google.gwt.user.cellview.client.AbstractHeaderOrFooterBuilder;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.cellview.client.TextHeader;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import com.shared.model.ClientSession;
import com.shared.model.DatePoint;
import com.shared.model.HourCostModel;
import com.shared.model.MoreLessUnlimModel;
import com.shared.model.SessionPseudoName;
import com.shared.model.SettingsHolder;
import com.shared.utils.UserUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by dmitry on 26.07.16.
 */
public class ClientSessionGridPanel extends VerticalPanel {
  private SimpleEventBus simpleEventBus;
  long firstPartTimeLength = 60000;
  long firstPartSumAmount = 3500;
  DatePoint currentDatePointValue = DatePoint.TODAY;
  private SettingsHolder.countStrategy currentCountStrategy;
  private Column<ClientSession, String> manageButtonsColumn;
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
    clientSessionDataGrid.setFooterBuilder(new CustomFooterBuilder());
    Long firstPartLength = UserUtils.getSettings().getFirstPartLength();
    if (firstPartLength != null) {
      firstPartTimeLength = firstPartLength;
    }
    Long firstPartSumAmount = UserUtils.getSettings().getFirstPartSumAmount();
    if (firstPartSumAmount != null) {
      this.firstPartSumAmount = firstPartSumAmount;
    }
    simpleEventBus.addHandler(ChangeDatePointEvent.TYPE, new ChangeDatePointEventHandler() {
      @Override
      public void changeDatePoint(ChangeDatePointEvent changeDatePointEvent) {
        currentDatePointValue = changeDatePointEvent.getDatePoint();
        clientSessionService.getClientSessions(currentDatePointValue, UserUtils.INSTANCE.getCurrentUser(),
                UserUtils.getSettings().isToShowRemoved(),
                UserUtils.getSettings().isToShowPayed(), new AsyncCallback<List<ClientSession>>() {
                  @Override
                  public void onFailure(Throwable caught) {

                  }

                  @Override
                  public void onSuccess(List<ClientSession> result) {
                    updateListDataProviderOnSuccess(result);
//                    UpdateSumEvent updateSumEvent = new UpdateSumEvent();
//                    updateSumEvent.setSum(getSum(result));
//                    eventBus.fireEvent(updateSumEvent);
                  }
                });
      }
    });
//    simpleEventBus.addHandler(UserLoggedInEvent.TYPE, new UserLoggedInHandler() {
//      @Override
//      public void userIsLoggedIn(UserLoggedInEvent userLoggedInEvent) {
//        clientSessionService.getCurrentUser(userLoggedInEvent.getUserName(), userLoggedInEvent.getUserPassword(),
//                new AsyncCallback<User>() {
//                  @Override
//                  public void onFailure(Throwable caught) {
//
//                  }
//
//                  @Override
//                  public void onSuccess(User result) {
//                    UserUtils.INSTANCE.setCurrentUser(result);
//                    firstPartTimeLength = result.getSettings().getFirstPartLength();
//                    firstPartSumAmount = result.getSettings().getFirstPartSumAmount();
//                  }
//                });
//      }
//    });
    simpleEventBus.addHandler(ToggleShowRemovedEvent.TYPE, new ToggleShowRemovedEventHandler() {
      @Override
      public void toggleShowRemoved(ToggleShowRemovedEvent toggleShowRemovedEvent) {
        clientSessionService.getClientSessions(currentDatePointValue, UserUtils.INSTANCE.getCurrentUser(), toggleShowRemovedEvent.isShowRemovedOn(),
                toggleShowRemovedEvent.isShowPayedCurrentState(), new AsyncCallback<List<ClientSession>>() {
                  @Override
                  public void onFailure(Throwable caught) {

                  }

                  @Override
                  public void onSuccess(List<ClientSession> result) {
                    updateListDataProviderOnSuccess(result);
//                    clientSessionDataGrid.setVisibleRange(0, listDataProvider.getList().size());
                    long sum = 0l;
                    for (ClientSession clientSession : result) {
                      if (clientSession.getSessionStatus() == ClientSession.SESSION_STATUS.REMOVED) {
                        continue;
                      }
                      if (clientSession.getSessionStatus() == ClientSession.SESSION_STATUS.STOPPED ||
                              clientSession.getSessionStatus() == ClientSession.SESSION_STATUS.PAYED ||
                              clientSession.getSessionStatus() == ClientSession.SESSION_STATUS.STOPPED_UNLIMITED) {
                        sum += clientSession.getFinalSum();
                        continue;
                      }
                      long timeDifferenceLength;
                      if (clientSession.getStopTime() == 0) {
                        timeDifferenceLength = System.currentTimeMillis() - clientSession.getStartTime();
                      } else {
                        timeDifferenceLength = clientSession.getStopTime() - clientSession.getStartTime();
                      }
                      long timeDifferenceLengthInSeconds = getSeconds(timeDifferenceLength);
                      if (clientSession.getSessionStatus() == ClientSession.SESSION_STATUS.CREATED) {
                        sum += 0;
                      } else {
                        if (timeDifferenceLengthInSeconds <= getSeconds(firstPartTimeLength)) {
                          sum += ClientSessionGridPanel.this.firstPartSumAmount;
                        } else {
                          long totalSum = ClientSessionGridPanel.this.firstPartSumAmount + 50 * (timeDifferenceLength - firstPartTimeLength) / 1000 / 60;
                          sum += totalSum;
                        }
                      }
                    }
                    UpdateSumEvent updateSumEvent = new UpdateSumEvent();
                    updateSumEvent.setSum(sum);
                    eventBus.fireEvent(updateSumEvent);
                  }
                });
      }
    });
    simpleEventBus.addHandler(ToggleShowPayedEvent.TYPE, new ToggleShowPayedEventHandler() {
      @Override
      public void toggleShowPayed(ToggleShowPayedEvent toggleShowPayedEvent) {
        clientSessionService.getClientSessions(currentDatePointValue, UserUtils.INSTANCE.getCurrentUser(), toggleShowPayedEvent.isShowRemovedCurrentState(),
                toggleShowPayedEvent.isShowPayedOn(),
                new AsyncCallback<List<ClientSession>>() {
                  @Override
                  public void onFailure(Throwable caught) {

                  }

                  @Override
                  public void onSuccess(List<ClientSession> result) {
                    updateListDataProviderOnSuccess(result);
//                    clientSessionDataGrid.setVisibleRange(0, listDataProvider.getList().size());
                  }
                });
      }
    });
//      pseudoNamesList.addAll(Arrays.asList(new SessionPseudoName("BLACK"), new SessionPseudoName("RED"), new SessionPseudoName("YELLOW"),
//              new SessionPseudoName("WHITE"), new SessionPseudoName("GREEN")));
//    Long userId = UserUtils.INSTANCE.getCurrentUser().getUserEntity();
//    clientSessionService.addNames(Arrays.asList(new SessionPseudoName("BLACK", UserUtils.INSTANCE.getCurrentUser()), new SessionPseudoName("RED", UserUtils.INSTANCE.getCurrentUser()),
//            new SessionPseudoName("YELLOW", UserUtils.INSTANCE.getCurrentUser()), new SessionPseudoName("WHITE", UserUtils.INSTANCE.getCurrentUser()),
//            new SessionPseudoName("GREEN", UserUtils.INSTANCE.getCurrentUser())), new AsyncCallback<Void>() {
//      @Override
//      public void onFailure(Throwable throwable) {
//        //To change body of implemented methods use File | Settings | File Templates.
//      }
//
//      @Override
//      public void onSuccess(Void aVoid) {
//      }
//    });
    simpleEventBus.addHandler(AddSessionEvent.TYPE, new AddSessionEventHandler() {
      @Override
      public void addClientSession(AddSessionEvent addSessionEvent) {
        final ClientSession clientSession = new ClientSession();
        clientSessionService.markNameAsUsed(addSessionEvent.getClientPseudoName(), UserUtils.currentUser.getUserId(), new AsyncCallback<SessionPseudoName>() {
          @Override
          public void onFailure(Throwable caught) {

          }

          @Override
          public void onSuccess(SessionPseudoName result) {
            clientSession.setSessionPseudoName(result);
            clientSession.setCreationTime(System.currentTimeMillis());
            clientSessionService.saveClientSession(currentDatePointValue, clientSession, UserUtils.getSettings().isToShowRemoved(),
                    UserUtils.getSettings().isToShowPayed(), new AsyncCallback<List<ClientSession>>() {
                      @Override
                      public void onFailure(Throwable caught) {

                      }

                      @Override
                      public void onSuccess(List<ClientSession> result) {
//            clientSession.setId(result);
                        updateListDataProviderOnSuccess(result);
//                        clientSessionDataGrid.setVisibleRange(0, listDataProvider.getList().size());
                      }
                    });
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
    clientSessionDataGrid.setVisibleRange(0, 10);


    clientSessionDataGrid.setHeight("500px");
    clientSessionDataGrid.setWidth("100%");

    CellTable<ClientSession> clientSessionCellTable = new CellTable<>();
    clientSessionCellTable.insertColumn(0, new TextColumn<ClientSession>() {
      @Override
      public String getValue(ClientSession object) {
        return object.getSessionStatus().name();
      }
    });
//    clientSessionDataGrid.setHeaderBuilder(new AbstractHeaderOrFooterBuilder<ClientSession>(clientSessionCellTable, false) {
//      @Override
//      protected boolean buildHeaderOrFooterImpl() {
//        return false;
//      }
//    });

    SimplePager pager = new SimplePager(SimplePager.TextLocation.RIGHT, true, true) {
      @Override
      protected void onRangeOrRowCountChanged() {
//        for (int i = 0; i < ClientSessionGridPanel.this.clientSessionDataGrid.getVisibleItemCount(); i++) {
//          if (ClientSessionGridPanel.this.clientSessionDataGrid.getVisibleItem(i).getSessionStatus() == ClientSession.SESSION_STATUS.STARTED) {
//            ClientSessionGridPanel.this.clientSessionDataGrid.redraw();
//          }
//        }
        super.onRangeOrRowCountChanged();
      }
    };
    pager.setPageSize(10);
    pager.setRangeLimited(true);
    pager.setDisplay(clientSessionDataGrid);
    add(clientSessionDataGrid);
    pager.getElement().getStyle().setMarginBottom(70, Style.Unit.PX);
    pager.getElement().getStyle().setMarginLeft(100, Style.Unit.PX);
    add(pager);
    setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

    ButtonCellBase<String> stringButtonCellBase = new ButtonCellBase<>(new ButtonCellBase.DefaultAppearance<String>(new AbstractSafeHtmlRenderer<String>() {
      @Override
      public SafeHtml render(final String value) {
        return new SafeHtml() {
          @Override
          public String asString() {
            ClientSession.SESSION_STATUS sessionStatus = ClientSession.SESSION_STATUS.valueOf(value);
            if (sessionStatus == ClientSession.SESSION_STATUS.REMOVED) {
              return "<div style='pointer-events: none; opacity: 0.4; color:red;'>*********</div>";
            } else if (sessionStatus == ClientSession.SESSION_STATUS.PAYED) {
              return "<div style='pointer-events: none; opacity: 0.4; color:green;'>*********</div>";
            } else {
              return sessionStatus.getButtonText();  //To change body of implemented methods use File | Settings | File Templates.
            }
          }
        };  //To change body of implemented methods use File | Settings | File Templates.
      }
    }));

    Column<ClientSession, String> startTimeColumn = new Column<ClientSession, String>(new TextCell()) {
      @Override
      public String getValue(ClientSession object) {
        DateTimeFormat dateTimeFormat = DateTimeFormat.getFormat("dd-MM-yyyy HH:mm");
        Long startTime = object.getStartTime();
        return dateTimeFormat.format(new Date(startTime));
      }
    };

    clientSessionDataGrid.addColumn(startTimeColumn, new TextHeader("Начало"));

    Column<ClientSession, String> stopTimeColumn = new Column<ClientSession, String>(new TextCell()) {
      @Override
      public void render(Cell.Context context, ClientSession object, SafeHtmlBuilder sb) {
        if (object.getStopTime() != 0) {
          super.render(context, object, sb);
        }
      }

      @Override
      public String getValue(ClientSession object) {
        DateTimeFormat dateTimeFormat = DateTimeFormat.getFormat("dd-MM-yyyy HH:mm");
        Long startTime = object.getStopTime();
        return dateTimeFormat.format(new Date(startTime));
      }
    };

    clientSessionDataGrid.addColumn(stopTimeColumn, new TextHeader("Конец"));

    manageButtonsColumn = new Column<ClientSession, String>(stringButtonCellBase) {
      @Override
      public void render(Cell.Context context, ClientSession object, SafeHtmlBuilder sb) {
        super.render(context, object, sb);
      }

      @Override
      public String getValue(ClientSession clientSession) {
        return clientSession.getSessionStatus().name();
      }
    };
    manageButtonsColumn.setFieldUpdater(new FieldUpdater<ClientSession, String>() {
      @Override
      public void update(final int i, final ClientSession clientSession, String s) {
        if (clientSession.getSessionStatus() == ClientSession.SESSION_STATUS.CREATED) {
          clientSession.setStartTime(System.currentTimeMillis());
          clientSession.setStatus(ClientSession.SESSION_STATUS.STARTED);
          clientSession.setFinalSum(ClientSessionGridPanel.this.firstPartSumAmount);
          clientSessionService.startClientSession(currentDatePointValue, clientSession, UserUtils.getSettings().isToShowRemoved(),
                  UserUtils.getSettings().isToShowPayed(), new AsyncCallback<List<ClientSession>>() {
            @Override
            public void onFailure(Throwable caught) {

            }

            @Override
            public void onSuccess(List<ClientSession> result) {
              updateListDataProviderOnSuccess(result);
//              clientSessionDataGrid.setVisibleRange(0, listDataProvider.getList().size());
//              DecoratedPopupPanel decoratedPopupPanel = new DecoratedPopupPanel();
//              decoratedPopupPanel.center();
//              decoratedPopupPanel.setAutoHideEnabled(true);
//              decoratedPopupPanel.setWidget(new HTML(clientSession.getSessionPseudoName() + " стартовал"));
//              decoratedPopupPanel.show();
            }
          });
        } else if (clientSession.getSessionStatus() == ClientSession.SESSION_STATUS.STARTED) {
          clientSession.setStopTime(System.currentTimeMillis());
          clientSession.setStatus(ClientSession.SESSION_STATUS.STOPPED);
          long finalSum = 0;
          if (SettingsHolder.countStrategy.MULTI_HOURS == UserUtils.getSettings().getCurrentCountStrategy()) {
            finalSum = getMultiHoursSum(0, clientSession);
          } else if(SettingsHolder.countStrategy.MULTI_HOURS == UserUtils.getSettings().getCurrentCountStrategy()) {
            finalSum = getHourMinutestSum(0, clientSession, false);
          }
          clientSession.setFinalSum(((finalSum + 99) / 100 ) * 100);
          clientSessionService.stopClientSession(currentDatePointValue, clientSession, UserUtils.getSettings().isToShowRemoved(),
                  UserUtils.getSettings().isToShowPayed(), new AsyncCallback<List<ClientSession>>() {
            @Override
            public void onFailure(Throwable caught) {

            }

            @Override
            public void onSuccess(List<ClientSession> result) {
              updateListDataProviderOnSuccess(result);
//              clientSessionDataGrid.setVisibleRange(0, listDataProvider.getList().size());
              DecoratedPopupPanel decoratedPopupPanel = new DecoratedPopupPanel();
              decoratedPopupPanel.center();
              decoratedPopupPanel.setAutoHideEnabled(true);
//              decoratedPopupPanel.setWidget(new HTML(result + "is stopped"));
              decoratedPopupPanel.show();
            }
          });
        } else if (clientSession.getSessionStatus() == ClientSession.SESSION_STATUS.STOPPED ||
                clientSession.getSessionStatus() == ClientSession.SESSION_STATUS.STOPPED_UNLIMITED) {
          clientSession.setStatus(ClientSession.SESSION_STATUS.PAYED ) ;
          clientSessionService.payClientSession(currentDatePointValue, clientSession, UserUtils.getSettings().isToShowRemoved(),
                  UserUtils.getSettings().isToShowPayed(), new AsyncCallback<List<ClientSession>>() {
            @Override
            public void onFailure(Throwable caught) {

            }

            @Override
            public void onSuccess(List<ClientSession> result) {
              setNameFree(clientSession);
              updateListDataProviderOnSuccess(result);
//              clientSessionDataGrid.setVisibleRange(0, listDataProvider.getList().size());
//              DecoratedPopupPanel decoratedPopupPanel = new DecoratedPopupPanel();
//              decoratedPopupPanel.center();
//              decoratedPopupPanel.setAutoHideEnabled(true);
//              decoratedPopupPanel.setWidget(new HTML(clientSession.getSessionPseudoName().getName() + "Оплачена"));
//              decoratedPopupPanel.show();
            }
          });
        } else if (clientSession.getSessionStatus() == ClientSession.SESSION_STATUS.REMOVED ||
                clientSession.getSessionStatus() == ClientSession.SESSION_STATUS.PAYED) {
        }
      }
    });
    clientSessionDataGrid.addColumn(manageButtonsColumn, new TextHeader("Управление"));


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
        long timeDifferenceLengthInSeconds = getSeconds(timeDifferenceLength);
        long totalSum = 0;
        if (clientSession.getSessionStatus() == ClientSession.SESSION_STATUS.CREATED ||
                clientSession.getSessionStatus() == ClientSession.SESSION_STATUS.REMOVED) {
          return "0.00";
        } else if (clientSession.getSessionStatus() == ClientSession.SESSION_STATUS.STOPPED ||
                clientSession.getSessionStatus() == ClientSession.SESSION_STATUS.PAYED ||
                clientSession.getSessionStatus() == ClientSession.SESSION_STATUS.STOPPED_UNLIMITED) {
          return getPrettyMoney(clientSession.getFinalSum());
        } else if (UserUtils.getSettings().getCurrentCountStrategy() == SettingsHolder.countStrategy.HOUR_MINUTES){
          long startTimeInSeconds = getSeconds(clientSession.getStartTime());
          if (clientSession.getStopTime() == 0) {
            timeDifferenceLength = System.currentTimeMillis() - startTimeInSeconds;
            if (UserUtils.getSettings().getMaxSessionLength() > 0 &&
                    timeDifferenceLength > UserUtils.getSettings().getMaxSessionLength()) {
            }
          } else {
            timeDifferenceLength = clientSession.getStopTime() - startTimeInSeconds;
          }
          totalSum = getHourMinutestSum(0, clientSession, false);
        } else if (UserUtils.getSettings().getCurrentCountStrategy() == SettingsHolder.countStrategy.MULTI_HOURS) {
          totalSum = getMultiHoursSum(0, clientSession);
        }
        return getPrettyMoney(totalSum);
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
        } else if (clientSession.getSessionStatus() == ClientSession.SESSION_STATUS.STOPPED_UNLIMITED) {
          return Images.INSTANCE.stopped_unlimited();
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
//        clientSession.setStatus(ClientSession.SESSION_STATUS.REMOVED);
        clientSessionService.removeClientSession(currentDatePointValue, clientSession, UserUtils.getSettings().isToShowRemoved(),
                UserUtils.getSettings().isToShowPayed(), new AsyncCallback<List<ClientSession>>() {
          @Override
          public void onFailure(Throwable caught) {

          }

          @Override
          public void onSuccess(List<ClientSession> result) {
            setNameFree(clientSession);
            updateListDataProviderOnSuccess(result);
//            clientSessionDataGrid.setVisibleRange(0, listDataProvider.getList().size());
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

//    HorizontalPanel buttonsPanel = new HorizontalPanel();
//    buttonsPanel.add(addButton);
//    ToggleButton toggleButton = new ToggleButton();
//    toggleButton.setText("Показывать удаленные");
//    toggleButton.setDown("Показывать удаленные");
//    buttonsPanel.add(toggleButton);
//    add(buttonsPanel);
//    add(verticalPanel);
//    clientSessionGrid.setRowData(0, Collections.singletonList(new ClientSession(System.currentTimeMillis(),
//            System.currentTimeMillis(), false)));


    Timer t = new Timer() {
      public void run() {
//        com.google.gwt.view.client.Range visibleRange = clientSessionDataGrid.getVisibleRange();
//        for (int i = visibleRange.getStart() + 1; i < clientSessionDataGrid.getVisibleItemCount() +
//                visibleRange.getLength() - 1; i++) {
//          if (ClientSessionGridPanel.this.clientSessionDataGrid.getVisibleItem(i - 1).getSessionStatus() == ClientSession.SESSION_STATUS.STARTED) {
//            ClientSessionGridPanel.this.clientSessionDataGrid.redrawRow(i);
//          }
//        }
        ClientSessionGridPanel.this.clientSessionDataGrid.redraw();
//        long sum = 0;
//        for (int i = 0; i < ClientSessionGridPanel.this.clientSessionDataGrid.getRowCount(); i++) {
//          ClientSession clientSession = ClientSessionGridPanel.this.clientSessionDataGrid.getVisibleItem(i);
//            if (clientSession.getSessionStatus() == ClientSession.SESSION_STATUS.REMOVED) {
//              continue;
//            }
//          if (clientSession.getSessionStatus() == ClientSession.SESSION_STATUS.STOPPED ||
//                  clientSession.getSessionStatus() == ClientSession.SESSION_STATUS.PAYED ||
//                  clientSession.getSessionStatus() == ClientSession.SESSION_STATUS.STOPPED_UNLIMITED) {
//            sum += clientSession.getFinalSum();
//            continue;
//          }
//          sum = getSum(sum, clientSession);
//        }
//        UpdateSumEvent updateSumEvent = new UpdateSumEvent();
//        updateSumEvent.setSum(sum);
//        eventBus.fireEvent(updateSumEvent);
      }
    };

    // Schedule the timer to run once every second, 1000 ms.
    t.scheduleRepeating(1000);
    clientSessionService.getClientSessions(DatePoint.TODAY, UserUtils.INSTANCE.getCurrentUser(), UserUtils.getSettings().isToShowRemoved(),
            UserUtils.getSettings().isToShowPayed(), new AsyncCallback<List<ClientSession>>() {
      @Override
      public void onFailure(Throwable throwable) {
        //To change body of implemented methods use File | Settings | File Templates.
      }

      @Override
      public void onSuccess(List<ClientSession> clientSessions) {
        listDataProvider = new ListDataProvider<ClientSession>(clientSessions);
        listDataProvider.addDataDisplay(clientSessionDataGrid);
        updateListDataProviderOnSuccess(clientSessions);
//        ColumnSortEvent.ListHandler<ClientSession> sortHandler = new ColumnSortEvent.ListHandler<ClientSession>(clientSessions);
//        sortHandler.setComparator(manageButtonsColumn,
//                new Comparator<ClientSession>() {
//                  public int compare(ClientSession t1, ClientSession t2) {
//                    return t2.compareTo(t1);
//                  }
//                });
//        clientSessionDataGrid.setRowCount(100);
//        clientSessionDataGrid.setRowData(clientSessions);
//              clientSessionDataGrid.setVisibleRange(0, 1000);
      }
    });

  }

  private void updateListDataProviderOnSuccess(List<ClientSession> result) {
    listDataProvider.getList().clear();
    listDataProvider.getList().addAll(result);
    listDataProvider.refresh();
    clientSessionDataGrid.setVisibleRange(0, 10);
  }

  private long getSum(long sum, ClientSession clientSession) {
    long timeDifferenceLength;
    boolean isSessionOver = false;
    long startTimeInSeconds = getSeconds(clientSession.getStartTime());
    if (clientSession.getStopTime() == 0) {
      timeDifferenceLength = System.currentTimeMillis() - startTimeInSeconds;
      if (UserUtils.getSettings().getMaxSessionLength() > 0 &&
              timeDifferenceLength > UserUtils.getSettings().getMaxSessionLength()) {
        isSessionOver = true;
      }
    } else {
      timeDifferenceLength = clientSession.getStopTime() - startTimeInSeconds;
    }
    if (clientSession.getSessionStatus() == ClientSession.SESSION_STATUS.CREATED) {
      sum += 0;
    } else if (UserUtils.getSettings().getCurrentCountStrategy() == SettingsHolder.countStrategy.HOUR_MINUTES) {
      sum = getHourMinutestSum(sum, clientSession, isSessionOver);
    } else if (UserUtils.getSettings().getCurrentCountStrategy() == SettingsHolder.countStrategy.MULTI_HOURS) {
      sum = getMultiHoursSum(sum, clientSession);
    }
    return sum;
  }

  private long getHourMinutestSum(long sum, ClientSession clientSession, boolean isSessionOver) {
    long timeDifferenceLength = System.currentTimeMillis() - clientSession.getStartTime();
    long timeDifferenceLengthInSeconds = getSeconds(timeDifferenceLength);
    if (timeDifferenceLengthInSeconds <= getSeconds(firstPartTimeLength)) {
      sum += firstPartSumAmount;
    } else {
      long totalSum = firstPartSumAmount + 50 * (timeDifferenceLength - firstPartTimeLength) / 1000 / 60;
      sum += totalSum;
      if (isSessionOver) {
        clientSession.setFinalSum(totalSum);
        clientSessionService.stopClientSession(currentDatePointValue, clientSession, UserUtils.getSettings().isToShowRemoved(),
                UserUtils.getSettings().isToShowPayed(), new AsyncCallback<List<ClientSession>>() {
                  @Override
                  public void onFailure(Throwable caught) {

                  }

                  @Override
                  public void onSuccess(List<ClientSession> result) {
                    updateListDataProviderOnSuccess(result);
//                    clientSessionDataGrid.setVisibleRange(0, listDataProvider.getList().size());
                  }
                });
      }
    }
    return sum;
  }

  private long getMultiHoursSum(long sum, ClientSession clientSession) {
    List<MoreLessUnlimModel> moreLessUnlimModels = UserUtils.currentUser.getMoreLessUnlimModelList();
//    Long hourLength = 1000 * 60l + 1000 * 13;
    Long hourLength = 1000 * 60l * 60;

    long hoursSum = 0;
    long costPerMinute = 0;
    long unlimCost = 0;
    long hoursSet = 0;
    MoreLessUnlimModel moreLessUnlimModel = null;
    if (!moreLessUnlimModels.isEmpty()) {
      moreLessUnlimModel = moreLessUnlimModels.get(0);
      costPerMinute = moreLessUnlimModel.getCostPerMinute();
      unlimCost = moreLessUnlimModel.getUnlimCost();
    }
    long difference = System.currentTimeMillis() - clientSession.getStartTime();
    long hoursGone = (System.currentTimeMillis() - clientSession.getStartTime())/hourLength;
    for (MoreLessUnlimModel model : moreLessUnlimModels) {
      if (hoursGone >= model.getNumberOfHours()) {
        hoursSet = model.getNumberOfHours();
        hoursSum = model.getCostForHours();
        costPerMinute = model.getCostPerMinute();
      }
    }
    long leftMilliSeconds = 0;
    if (hoursSet != 0) {
      leftMilliSeconds = difference % (hourLength * hoursSet);
    } else {
      leftMilliSeconds = difference;
    }
    long totalSum = hoursSum + (leftMilliSeconds * costPerMinute) / 1000 / 60;
    if (totalSum > unlimCost) {
      clientSession.setStopTime(System.currentTimeMillis());
      if (moreLessUnlimModel != null) {
        clientSession.setFinalSum(moreLessUnlimModel.getUnlimCost());
      }
      clientSessionService.unlimClientSession(currentDatePointValue, clientSession, UserUtils.getSettings().isToShowRemoved(),
              UserUtils.getSettings().isToShowPayed(), new AsyncCallback<List<ClientSession>>() {
                @Override
                public void onFailure(Throwable caught) {

                }

                @Override
                public void onSuccess(List<ClientSession> result) {
                  updateListDataProviderOnSuccess(result);
//                  clientSessionDataGrid.setVisibleRange(0, listDataProvider.getList().size());
                  DecoratedPopupPanel decoratedPopupPanel = new DecoratedPopupPanel();
                  decoratedPopupPanel.center();
                  decoratedPopupPanel.setAutoHideEnabled(true);
//              decoratedPopupPanel.setWidget(new HTML(result + "is stopped"));
                  decoratedPopupPanel.show();
                }
              }
      );
      return -1;
    }
    return totalSum;
  }

  private long getMultiHoursMultiMinutesSum(long sum, ClientSession clientSession) {
    List<HourCostModel> hourCostModels = UserUtils.getOrderedHourCostModels();
    Long hourLength = UserUtils.getSettings().getHourLength();
    long hoursSum = 0;
    boolean isRangeFound = false;
    for (HourCostModel hourCostModel : hourCostModels) {
      Range<Long> range;
      range = Range.open(clientSession.getStartTime() + hourLength * (hourCostModel.getHourOrder() - 1), clientSession.getStartTime() + hourLength * hourCostModel.getHourOrder());
      if (range.contains(System.currentTimeMillis())) {
        sum = hourCostModel.getCostPerMinute() * (System.currentTimeMillis() - clientSession.getStartTime() -
                hourLength * (hourCostModel.getHourOrder() - 1)) / 1000 / 60;
        isRangeFound = true;
        return hoursSum + sum;
      } else {
        hoursSum += hourCostModel.getCostPerHour();
      }
    }
    if (!isRangeFound) {
      long difference = System.currentTimeMillis() - clientSession.getStartTime();
      HourCostModel lastHourCostModel = hourCostModels.get(hourCostModels.size() - 1);
      long numberOfHours = difference / hourLength;
      long leftMilliSeconds = difference % (numberOfHours*1000*60);
      long leftSum = (hoursSum + lastHourCostModel.getCostPerHour() * (numberOfHours - hourCostModels.size()) +
              lastHourCostModel.getCostPerMinute() * leftMilliSeconds) / 1000 / 60;
      return hoursSum + (lastHourCostModel.getCostPerHour() * (numberOfHours - hourCostModels.size())) + leftSum;

    }
    return hoursSum;
  }

  private void setNameFree(ClientSession clientSession) {
    clientSessionService.markNameAsFree(clientSession.getSessionPseudoName().getName(), UserUtils.currentUser.getUserId(),  new AsyncCallback<Void>() {
      @Override
      public void onFailure(Throwable caught) {

      }

      @Override
      public void onSuccess(Void result) {

      }
    });
  }

  private String getPrettyMoney(long minPayment) {
    if (minPayment == -1) {
      List<MoreLessUnlimModel> moreLessUnlimModels = UserUtils.currentUser.getMoreLessUnlimModelList();
      long unlimSum = moreLessUnlimModels.get(0).getUnlimCost();
      if (!moreLessUnlimModels.isEmpty()) {
        return "Безлимит - " + getPrettyMoney(unlimSum);
      }
    }
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
//        clientSessionService.markNameAsUsed(namesListBox.getSelectedValue(), UserUtils.currentUser.getUserEntity(), new AsyncCallback<Void>() {
//          @Override
//          public void onFailure(Throwable caught) {
//
//          }
//
//          @Override
//          public void onSuccess(Void result) {
//            simpleEventBus.fireEvent(event);
//          }
//        });
        simpleEventBus.fireEvent(event);
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

  private class CustomFooterBuilder extends AbstractHeaderOrFooterBuilder<ClientSession> {

    public CustomFooterBuilder() {
      super(clientSessionDataGrid, true);
    }

    @Override
    protected boolean buildHeaderOrFooterImpl() {
      String footerStyle = clientSessionDataGrid.getResources().style().footer();

      // Calculate the age of all visible contacts.
      String ageStr = "";
      List<ClientSession> items = clientSessionDataGrid.getVisibleItems();
      if (items.size() > 0) {
        int sum = getSum(items);
        ageStr = "Итого: " + getPrettyMoney(sum);
      }

      // Cells before age column.
      TableRowBuilder tr = startRow();
      tr.startTH().colSpan(4).className(footerStyle).endTH();

      // Show the average age of all contacts.
      TableCellBuilder th =
              tr.startTH().className(footerStyle).align(
                      HasHorizontalAlignment.ALIGN_CENTER.getTextAlignString());
      th.text(ageStr);
      th.endTH();

      // Cells after age column.
      tr.startTH().colSpan(2).className(footerStyle).endTH();
      tr.endTR();

      return true;
    }
  }

  private int getSum(List<ClientSession> items) {
    int sum = 0;
    for (ClientSession clientSession : items) {
      if (clientSession.getSessionStatus() == ClientSession.SESSION_STATUS.REMOVED) {
        continue;
      }
      if (clientSession.getSessionStatus() == ClientSession.SESSION_STATUS.STOPPED ||
              clientSession.getSessionStatus() == ClientSession.SESSION_STATUS.PAYED ||
              clientSession.getSessionStatus() == ClientSession.SESSION_STATUS.STOPPED_UNLIMITED) {
        sum += clientSession.getFinalSum();
        continue;
      }
      if (UserUtils.getSettings().getCurrentCountStrategy() == SettingsHolder.countStrategy.MULTI_HOURS){
        sum += getMultiHoursSum(0, clientSession);
      } else if (UserUtils.getSettings().getCurrentCountStrategy() == SettingsHolder.countStrategy.HOUR_MINUTES){
        sum += getHourMinutestSum(0, clientSession, false);
      }
    }
    return sum;
  }

}

