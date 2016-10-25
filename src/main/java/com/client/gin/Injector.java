package com.client.gin;

import com.client.ClientSessionGridPanel;
import com.client.LoginPanel;
import com.client.MainPanel;
import com.client.MainTabPanel;
import com.client.NameSelectWindow;
import com.client.panels.SettingsPanel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.inject.client.GinModules;
import com.google.gwt.inject.client.Ginjector;

@GinModules(InjectorModule.class)
public interface Injector extends Ginjector {
    public static final Injector INSTANCE = GWT.create(Injector.class);

    public EventBus getEventBus();
    public LoginPanel getLoginPanel();
    public MainPanel getMainPanel();
    public MainTabPanel getMainTabPanel();
    public ClientSessionGridPanel getClientSessionGridPanel();
    public NameSelectWindow getNameSelectionWidnow();
    public SettingsPanel getSettingsPanel();
}

