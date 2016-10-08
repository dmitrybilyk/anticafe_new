package com.client.gin;

import com.client.ClientSessionGridPanel;
import com.client.LoginPanel;
import com.client.MainTabPanel;
import com.client.NameSelectWindow;
import com.client.panels.SettingsPanel;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;

public class InjectorModule extends AbstractGinModule {
    @Override
    protected void configure() {
        bind(EventBus.class).to(SimpleEventBus.class).in(Singleton.class);
        bind(LoginPanel.class).in(Singleton.class);
        bind(MainTabPanel.class).in(Singleton.class);
        bind(ClientSessionGridPanel.class).in(Singleton.class);
        bind(NameSelectWindow.class).in(Singleton.class);
        bind(SettingsPanel.class).in(Singleton.class);
    }
}