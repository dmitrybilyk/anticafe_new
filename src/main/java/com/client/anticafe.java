package com.client;

import com.client.gin.Injector;
import com.client.service.ClientSessionService;
import com.client.service.ClientSessionServiceAsync;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.visualization.client.VisualizationUtils;
import com.google.gwt.visualization.client.visualizations.PieChart;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class anticafe implements EntryPoint {
  /**
   * The message displayed to the user when the server cannot be reached or
   * returns an error.
   */
  private static final String SERVER_ERROR = "An error occurred while "
      + "attempting to contact the server. Please check your network "
      + "connection and try again.";

  /**
   * This is the entry point method.
   */
  public void onModuleLoad() {
      // Load the visualization api, passing the onLoadCallback to be called
      // when loading is done.
      final Injector injector = Injector.INSTANCE;
      Runnable runnable = new Runnable() {
          @Override
          public void run() {
            RootLayoutPanel.get().getElement().getStyle().setBackgroundColor("navajowhite");
            RootLayoutPanel.get().add(injector.getLoginPanel());
          }
      };
      VisualizationUtils.loadVisualizationApi(runnable, PieChart.PACKAGE);

  }
}
