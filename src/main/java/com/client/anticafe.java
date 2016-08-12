package com.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.SimpleEventBus;
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
   * Create a remote service proxy to talk to the server-side Greeting service.
   */
  private final GreetingServiceAsync greetingService = GWT.create(GreetingService.class);

  /**
   * This is the entry point method.
   */
  public void onModuleLoad() {
      // Load the visualization api, passing the onLoadCallback to be called
      // when loading is done.
      Runnable runnable = new Runnable() {
          @Override
          public void run() {
              SimpleEventBus simpleEventBus = new SimpleEventBus();
              RootPanel.get().getElement().getStyle().setBackgroundColor("darkcyan");
              RootPanel.get().add(new LoginPanel(simpleEventBus));
          }
      };
      VisualizationUtils.loadVisualizationApi(runnable, PieChart.PACKAGE);

  }
}
