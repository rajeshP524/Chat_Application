package com.gui;

import com.chatApp.Client;
import com.chatApp.HistoryListener;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class MainFrame extends JFrame {
    private Client client = null;
    private StatusPanel statusPanel;
    private ToolBar toolBar;
    private Controller controller;
    private ChatPanel chatPanel;
    private String username;

    public MainFrame(Client client, Controller controller){
        super("Chat Application");
        this.client = client;
        this.controller = controller;


        // set attributes
        setVisible(true);
        setSize(550, 320);
        //setResizable(false);
        //setMinimumSize(new Dimension(800,400));

        setLayout(new BorderLayout());

        // components
        statusPanel = controller.getStatusPanel();
        toolBar = controller.getToolBar();
        chatPanel = new ChatPanel(client);

        //set user icon name
        toolBar.setUserNameLabelText(client.getUser());

        //Add components to mainFrame
        add(statusPanel, BorderLayout.WEST);
        add(toolBar, BorderLayout.NORTH);
        add(chatPanel, BorderLayout.CENTER);

        // list of events that main frame is responsible for
        mainFrameAsAListener();

        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    private void mainFrameAsAListener() {

        // listens from statusPanel
        statusPanel.setStatusPanelListener(new StatusPanelListener() {
            @Override
            public void actionPerformed(String user) {
                username = user + "#";
                chatPanel.getChatToolBar().setHeadLabelText("Message : " + user);
                setChatArea(user);
            }
        });

        //listens history from server
        client.setHistoryListener(new HistoryListener() {
            @Override
            public void onHistory(String history) {
                chatPanel.getTextArea().append(history);
            }
        });

        //listens valid users from DMessageLister
        toolBar.setdMessageListener(new DMessageListener() {
            @Override
            public void onValidUser(String username) {
                chatPanel.listenUserNameFromMainFrame(username, MainFrame.this);
            }
        });

        //MainFrame as a listener for chatroom label setting
        toolBar.setChatroomLabelSetter(new ChatroomLabelSetter() {
            @Override
            public void setLabel() {
                chatPanel.listenChatroomLabelSetFromMainFrame(MainFrame.this);
            }
        });
    }

    //get history of a particular user/chatroom and append it to chatArea
    public void setChatArea(String user) {
        chatPanel.getTextArea().setText("");
        //request server for chatHistory
        String cmd = "history " + user + "\n";
        try {
            client.getHistory(cmd);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) throws UnknownHostException {

    }
}
