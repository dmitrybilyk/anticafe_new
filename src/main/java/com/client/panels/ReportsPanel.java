package com.client.panels;

import com.client.service.ClientSessionService;
import com.client.service.ClientSessionServiceAsync;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.visualization.client.AbstractDataTable;
import com.google.gwt.visualization.client.VisualizationUtils;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.Selection;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.events.SelectHandler;
import com.google.gwt.visualization.client.visualizations.PieChart;
import com.google.gwt.visualization.client.visualizations.PieChart.Options;
import com.shared.model.ClientSession;
import com.shared.model.DatePoint;
import com.shared.utils.UserUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: dmitry
 * Date: 8/9/16
 * Time: 3:03 PM
 * To change this template use File | Settings | File Templates.
 */
public class ReportsPanel extends VerticalPanel{
    private final ClientSessionServiceAsync clientSessionService = GWT.create(ClientSessionService.class);
    Options options;
    public ReportsPanel() {
        setHeight("400px");
        setWidth("100%");
        options = createOptions();
//        createTable();
    }
    private Options createOptions() {
        Options options = Options.create();
        options.setWidth(800);
        options.setHeight(520);
        options.set3D(true);
        options.setTitle("Сессии:");
        return options;
    }

    private void createTable() {
        final DataTable data = DataTable.create();
        clientSessionService.getClientSessions(DatePoint.ALL, UserUtils.currentUser.getUserId(), true, true,
                new AsyncCallback<List<ClientSession>>() {
                    @Override
                    public void onFailure(Throwable caught) {

                    }

                    @Override
                    public void onSuccess(List<ClientSession> result) {
                        Map<Integer, List<ClientSession>> sessionStatusListMap = new HashMap<Integer, List<ClientSession>>();
                        int incrimentor = 0;
                        boolean isIncrimentorSet = false;
                        for (ClientSession.SESSION_STATUS status : ClientSession.SESSION_STATUS.values()) {
                            for (ClientSession session : result) {
                                if (status == session.getSessionStatus()) {
                                    List<ClientSession> clientSessions = sessionStatusListMap.get(status);
                                    if (clientSessions == null) {
                                        List<ClientSession> clientSessionList = new ArrayList<ClientSession>();
                                        clientSessionList.add(session);
                                        if (!isIncrimentorSet) {
                                            status.setOrder(incrimentor);
                                            incrimentor += 1;
                                            isIncrimentorSet = true;
                                        }
                                        sessionStatusListMap.put(status.getOrder(), clientSessionList);
                                    } else {
                                        clientSessions.add(session);
                                    }
                                }
                            }
                            isIncrimentorSet = false;
                        }
                        for (Integer statusOrder : sessionStatusListMap.keySet()) {
                            data.addColumn(ColumnType.STRING);
                            data.addColumn(ColumnType.NUMBER);
                        }
                        data.addRows(sessionStatusListMap.keySet().size());
//                        for (ClientSession.SESSION_STATUS status : sessionStatusListMap.keySet()) {
//                            for (int j = 0; j < data.getNumberOfRows(); j++) {
//                                if (j == 0) {
//                                    data.setValue(j, status.getOrder(),  status.getValue());
//                                } else {
//                                    data.setValue(j, status.getOrder(), sessionStatusListMap.get(status).size());
//                                }
//                            }
//                        }
//                        for (int i = 0; i < sessionStatusListMap.keySet().size(); i++) {
//                            for (int j = 0; j< data.getNumberOfColumns(); j++) {
////                            for (ClientSession.SESSION_STATUS status : sessionStatusListMap.keySet()) {
//                                if (j == 0) {
//                                    data.setValue(i, j, sessionStatusListMap.get(i).get(0).getSessionStatus().getValue());
//                                } else {
//                                    data.setValue(i, j, sessionStatusListMap.get(i).size());
//                                }
////                            }
//                            }
//                        }

                        PieChart pie = new PieChart(data, options);
                        add(pie);

//                        data.setValue(0, 0, "Work");
//                        data.setValue(0, 1, 14);
//                        data.setValue(1, 0, "Sleep");
//                        data.setValue(1, 1, 10);
                    }
                });
    }
}
