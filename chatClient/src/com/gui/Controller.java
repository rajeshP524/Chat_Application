package com.gui;

import com.chatApp.Client;

public class Controller {
    Client client;
    StatusPanel statusPanel;
    ToolBar toolBar;
    public Controller(Client client){
        this.client = client;
        statusPanel = new StatusPanel(client);
        toolBar = new ToolBar(client);
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public StatusPanel getStatusPanel() {
        return statusPanel;
    }

    public void setStatusPanel(StatusPanel statusPanel) {
        this.statusPanel = statusPanel;
    }

    public ToolBar getToolBar() {
        return toolBar;
    }

    public void setToolBar(ToolBar toolBar) {
        this.toolBar = toolBar;
    }
}
