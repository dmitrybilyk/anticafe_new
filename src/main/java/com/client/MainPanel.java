package com.client;

import com.client.gin.Injector;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Created by dmitry on 21.10.16.
 */
public class MainPanel extends LayoutPanel {
  public MainPanel() {
//    super();
    setHeight("100%");
    setWidth("100%");
    DockPanel dockPanel = new DockPanel();
    HorizontalPanel upperPanel = new HorizontalPanel();
    upperPanel.add(new Button("Logout"));
    upperPanel.setHeight("30px");
    upperPanel.setWidth("100%");
//    upperPanel.setHorizontalAlignment(ALIGN_RIGHT);
    dockPanel.add(upperPanel, DockPanel.NORTH);
    dockPanel.add(Injector.INSTANCE.getMainTabPanel(), DockPanel.CENTER);
    add(dockPanel);
  }
}
